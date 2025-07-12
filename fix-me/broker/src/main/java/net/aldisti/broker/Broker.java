package net.aldisti.broker;

import lombok.Getter;
import net.aldisti.broker.fix.MessageBuilder;
import net.aldisti.common.fix.Message;
import net.aldisti.common.network.Client;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class Broker {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(Broker.class);

    private boolean running = false;

    @Getter
    private final BlockingQueue<Message> queue;
    /**
     * Pending orders., the key is {@link Message#getMessageId() messageId}.
     */
    private final Map<String, Message> pending;
    private final BrokerContext context;

    private Client client;

    public Broker() {
        this.queue = new ArrayBlockingQueue<>(1024);
        this.pending = new HashMap<>();
        this.context = new BrokerContext();
    }

    public void run() {
        running = true;
        while (running) {
            try {
                Message msg = queue.poll(50, TimeUnit.MILLISECONDS);
                if (msg != null)
                    handle(msg);
            } catch (InterruptedException e) {
                log.info("Broker interrupted");
                break;
            }
        }
        client.close();
    }

    public void stop() {
        log.info("Broker shutting down...");
        running = false;
        int balance = context.getBalance();
        log.info("\nInitial balance: {}\nFinal balance: {}", BrokerContext.INITIAL_BALANCE, balance);
    }

    private void handle(Message msg) {
        switch (msg.type()) {
            case EXECUTED -> executed(msg);
            case REJECTED -> rejected(msg);
            case NOTIFY -> notify(msg);
        }
    }

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

    private void rejected(Message msg) {
        Message order = pending.remove(msg.getMessageId());
        if (order == null) {
            log.error("Non-existent order with id: {} has been rejected", msg.getMessageId());
            return;
        }
        context.restoreRejected(order);
        log.warn("Order {} has been rejected: {}", msg.getMessageId(), order);
    }

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

    public void setClient(Client client) {
        if (this.client == null)
            this.client = client;
    }

    private void send(Message msg) {
        if (msg == null) return;

        pending.put(msg.getMessageId(), msg);
        client.send(msg);
    }
}
