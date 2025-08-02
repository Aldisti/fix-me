package net.aldisti.broker;

import net.aldisti.broker.context.SimpleBrokerContext;
import net.aldisti.common.fix.Message;
import net.aldisti.common.fix.constants.Tag;
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
     * Pending orders., the key is the message id.
     */
    private final Map<String, Message> pending;
    /**
     * The broker context, where all the assets are managed.
     */
    private final SimpleBrokerContext context;

    private Client client;
    private boolean status;

    public Broker() {
        this.queue = new ArrayBlockingQueue<>(1024);
        this.pending = new HashMap<>();
        this.context = new SimpleBrokerContext();
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
            System.out.printf("\nInitial balance: %s\nFinal balance: %s\n",
                    SimpleBrokerContext.INITIAL_BALANCE, context.getNetWorth());
        }
    }

    public void stop() {
        if (!status) // do not call this method twice
            return;
        status = false;
        if (!queue.offer(new Message())) {
            log.error("Broker queue is full, cannot add empty message");
            System.exit(1); // this code should never execute
        }
    }

    private void handle(Message msg) {
        if (msg.type() == null) return;

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
        Message order = pending.remove(msg.get(Tag.MESSAGE_ID));
        if (order == null) {
            log.error("Non-existent order with id: {} has been executed", msg.get(Tag.MESSAGE_ID));
            return;
        }

        switch (order.type()) {
            case BUY -> context.executeBuy(msg);
            case SELL -> context.executeSell(msg);
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
        Message order = pending.remove(msg.get(Tag.MESSAGE_ID));
        if (order == null) {
            log.error("Non-existent order with id: {} has been rejected", msg.get(Tag.MESSAGE_ID));
            return;
        }
        context.restoreOrder(order);
        log.warn("Order {} has been rejected: {}", msg.get(Tag.MESSAGE_ID), order);
    }

    /**
     * Handles an incoming
     * {@link net.aldisti.common.fix.constants.MsgType#NOTIFY NOTIFY}
     * message.<br>
     * Here happens most of the buy/sell logic.
     */
    private void notify(Message msg) {
        final TradedAsset asset = context.updateAsset(msg);
        send(context.checkForBuy(asset));
    }

    /**
     * Puts a message in the pending list
     * and then sends it to the client.<br>
     * It's <b>null-safe</b>.
     */
    private void send(Message msg) {
        if (msg == null) return;

        pending.put(msg.get(Tag.MESSAGE_ID), msg);
        client.send(msg);
    }

    public BlockingQueue<Message> getQueue() {
        return this.queue;
    }
}
