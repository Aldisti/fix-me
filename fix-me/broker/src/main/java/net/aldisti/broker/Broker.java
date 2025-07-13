package net.aldisti.broker;

import net.aldisti.broker.fix.MessageBuilder;
import net.aldisti.common.fix.Message;
import net.aldisti.common.network.Client;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Broker {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(Broker.class);

    /**
     * A queue for all incoming messages.
     */
    private final BlockingQueue<Message> queue;
    /**
     * Pending orders., the key is {@link Message#getMessageId() messageId}.
     */
    private final Map<String, Message> pending;
    /**
     * The broker context, where all the assets are managed.
     */
    private final BrokerContext context;
    private Client client;
    private boolean status;

    public Broker() {
        this.queue = new ArrayBlockingQueue<>(1024);
        this.pending = new HashMap<>();
        this.context = new BrokerContext();
        this.status = true;
    }

    public void run(Client client) {
        this.client = client;

        try {
            while (status)
                handle(queue.take());
        } catch (InterruptedException e) {
            log.warn("Broker interrupted");
        } finally {
            log.info("Shutting down");
            client.close();
            int balance = context.getBalance();
            System.out.printf("\nInitial balance: %s\nFinal balance: %s\n", BrokerContext.INITIAL_BALANCE, context.getNetWorth());
        }
    }

    public void stop() {
        status = false;
        if (!queue.offer(new Message())) {
            log.error("Broker queue is full, cannot add empty message");
            System.exit(1); // this code should never execute
        }
    }

    private void handle(Message msg) {
        if (msg.getType() == null) return;

        switch (msg.type()) {
            case EXECUTED -> executed(msg);
            case REJECTED -> rejected(msg);
            case NOTIFY -> notify(msg);
            default -> log.warn("Unknown message type: {}", msg.type());
        }
    }

    /**
     * Handles an incoming
     * {@link net.aldisti.common.fix.constants.MsgType#EXECUTED EXECUTED}
     * message.
     */
    private void executed(Message msg) {
        Message order = pending.remove(msg.getMessageId());
        if (order == null) {
            log.error("Non-existent order with id: {} has been executed", msg.getMessageId());
            return;
        }

        switch (order.type()) {
            case BUY -> context.buy(msg);
            case SELL -> context.sell(msg);
            default -> {
                log.error("Executed order refers to invalid message type: {}", order.type());
            }
        }
    }

    /**
     * Handles an incoming
     * {@link net.aldisti.common.fix.constants.MsgType#REJECTED REJECTED}
     * message.
     */
    private void rejected(Message msg) {
        Message order = pending.remove(msg.getMessageId());
        if (order == null) {
            log.error("Non-existent order with id: {} has been rejected", msg.getMessageId());
            return;
        }
        context.restoreRejected(order);
        log.warn("Order {} has been rejected: {}", msg.getMessageId(), order);
    }

    /**
     * Handles an incoming
     * {@link net.aldisti.common.fix.constants.MsgType#NOTIFY NOTIFY}
     * message.<br>
     * Here happens most of the buy/sell logic.
     */
    private void notify(Message msg) {
        final TradedAsset asset = context.addOrUpdate(msg);

        if (asset.getQuantity() == 0) {
            // check current asset allocation before buying
            send(context.buyOrder(asset, 10));
            return;
        }

        int ror = asset.rateOfReturn();
        if (ror >= 5 && asset.getQuantity() / 4 > 0) {
            send(MessageBuilder.sell(asset, asset.getQuantity() / 4, asset.getPrice()));
        } else if (ror <= -5) {
            int quantity = (asset.getQuantity() / 4 > 0) ? asset.getQuantity() / 4 : 10;
            send(context.buyOrder(asset, quantity));
        }
    }

    /**
     * Puts a message in the pending list
     * and then sends it to the client.<br>
     * It's <b>null-safe</b>.
     */
    private void send(Message msg) {
        if (msg == null) return;

        pending.put(msg.getMessageId(), msg);
        client.send(msg);
    }

    public BlockingQueue<Message> getQueue() {
        return this.queue;
    }
}
