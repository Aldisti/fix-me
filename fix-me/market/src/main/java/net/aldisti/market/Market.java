package net.aldisti.market;

import net.aldisti.common.finance.Asset;
import net.aldisti.common.fix.Message;
import net.aldisti.common.fix.constants.MsgType;
import net.aldisti.common.fix.constants.Tag;
import net.aldisti.common.network.Client;
import net.aldisti.market.db.Database;
import org.bson.Document;
import org.slf4j.Logger;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Market {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(Market.class);

    private final ConcurrentLinkedQueue<Message> queue;
    private final MarketContext context;
    /**
     * Interval in milliseconds between each market update.
     */
    private final int interval;
    private final MessageBuilder builder;

    private Client client;
    private boolean status = true;

    public Market() {
        this("Default", 35);
    }

    public Market(int interval) {
        this("Default", interval);
    }

    public Market(String name, int interval) {
        this.queue = new ConcurrentLinkedQueue<>();
        this.context = MarketContext.getInstance();
        this.interval = interval;
        this.builder = new MessageBuilder(name);
    }

    public void run(Client client) {
        log.info("Market starting with an update interval of {} ms", this.interval);
        this.client = client;
        long time = System.currentTimeMillis();
        while (status) {
            handle(queue.poll());

            if (System.currentTimeMillis() - time < interval)
                continue;
            context.getAssetIds().forEach(id -> {
                Asset asset = context.updateAsset(id);
                if (asset.getPrice() != 0)
                    client.send(builder.notifyUpdate(asset));
            });
            time = System.currentTimeMillis();
        }
        client.close();
    }

    public void stop() {
        status = false;
        log.info("Gracefully stopping");
    }

    private void handle(Message msg) {
        if (msg == null) return;

        switch (msg.type()) {
            case BUY -> buy(msg);
            case SELL -> sell(msg);
            default -> error(msg);
        }
    }

    private void buy(Message msg) {
        Message response;
        Document transaction = createTransaction(msg);
        if (context.buyAsset(msg.get(Tag.ASSET_ID), msg.getInt(Tag.QUANTITY), msg.getInt(Tag.PRICE))) {
            response = MessageBuilder.executed(msg);
            transaction.put("response", MsgType.EXECUTED.name());
        } else {
            response = MessageBuilder.rejected(msg);
            transaction.put("response", MsgType.REJECTED.name());
        }
        Database.save(transaction);
        client.send(response);
    }

    private void sell(Message msg) {
        Message response;
        Document transaction = createTransaction(msg);
        if (context.sellAsset(msg.get(Tag.ASSET_ID), msg.getInt(Tag.QUANTITY), msg.getInt(Tag.PRICE))) {
            response = MessageBuilder.executed(msg);
            transaction.put("response", MsgType.EXECUTED.name());
        } else {
            response = MessageBuilder.rejected(msg);
            transaction.put("response", MsgType.REJECTED.name());
        }
        Database.save(transaction);
        client.send(response);
    }

    private void error(Message msg) {
        client.send(builder.error(msg));
    }

    private static Document createTransaction(Message msg) {
        Document t = new Document();
        t.put("message_id", msg.get(Tag.MESSAGE_ID));
        t.put("client_id", msg.get(Tag.TARGET_ID));
        t.put("asset_id", msg.get(Tag.ASSET_ID));
        t.put("time", LocalDateTime.now());
        t.put("type", msg.type().name());
        t.put("price", msg.get(Tag.PRICE));
        t.put("quantity", msg.get(Tag.QUANTITY));
        t.put("instrument", msg.get(Tag.INSTRUMENT));
        return t;
    }

    public ConcurrentLinkedQueue<Message> getQueue() {
        return this.queue;
    }
}
