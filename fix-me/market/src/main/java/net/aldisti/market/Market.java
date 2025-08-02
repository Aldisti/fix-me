package net.aldisti.market;

import net.aldisti.common.finance.Asset;
import net.aldisti.common.fix.Message;
import net.aldisti.common.fix.constants.Tag;
import net.aldisti.common.network.Client;
import org.slf4j.Logger;

import java.util.Scanner;
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
        if (context.buyAsset(msg.get(Tag.ASSET_ID), msg.getInt(Tag.QUANTITY), msg.getInt(Tag.PRICE)))
            client.send(builder.executed(msg));
        else
            client.send(builder.rejected(msg));
    }

    private void sell(Message msg) {
        if (context.sellAsset(msg.get(Tag.ASSET_ID), msg.getInt(Tag.QUANTITY), msg.getInt(Tag.PRICE)))
            client.send(builder.executed(msg));
        else
            client.send(builder.rejected(msg));
    }

    private void error(Message msg) {
        client.send(builder.error(msg));
    }

    public ConcurrentLinkedQueue<Message> getQueue() {
        return this.queue;
    }
}
