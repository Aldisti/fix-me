package net.aldisti.market;

import lombok.Getter;
import net.aldisti.common.finance.Asset;
import net.aldisti.common.fix.Message;
import net.aldisti.common.network.Client;
import org.slf4j.Logger;

import java.time.Instant;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Market {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(Market.class);
    @Getter
    private final ConcurrentLinkedQueue<Message> queue;
    private final MarketContext context;
    /**
     * Interval in milliseconds between each market update.
     */
    private final int interval;

    private Client client;

    public Market() {
        this.queue = new ConcurrentLinkedQueue<>();
        this.context = MarketContext.getInstance();
        this.interval = 35;
    }

    public Market(Integer interval) {
        this.queue = new ConcurrentLinkedQueue<>();
        this.context = MarketContext.getInstance();
        this.interval = interval;
    }

    public void run() {
        var time = System.currentTimeMillis();
        log.info("Market starting with an update interval of {} ms", this.interval);
        while (true) {
            Message msg = queue.poll();
            if (msg != null)
                handle(msg);

            if (System.currentTimeMillis() - time < interval)
                continue;
            context.getAssetIds().forEach(id -> {
                Asset asset = context.updateAsset(id);
                if (asset.getPrice() != 0)
                    client.send(MessageBuilder.notifyUpdate(asset));
            });
            time = System.currentTimeMillis();
        }
    }

    private void handle(Message msg) {
        switch (msg.type()) {
            case BUY -> buy(msg);
            case SELL -> sell(msg);
            default -> error(msg);
        }
    }

    private void buy(Message msg) {
        if (context.buyAsset(msg.getAssetId(), msg.quantity(), msg.price()))
            client.send(MessageBuilder.executed(msg));
        else
            client.send(MessageBuilder.rejected(msg));
    }

    private void sell(Message msg) {
        if (context.sellAsset(msg.getAssetId(), msg.quantity(), msg.price()))
            client.send(MessageBuilder.executed(msg));
        else
            client.send(MessageBuilder.rejected(msg));
    }

    private void error(Message msg) {
        client.send(MessageBuilder.error(msg));
    }

    public void setClient(Client client) {
        if (this.client == null)
            this.client = client;
    }
}
