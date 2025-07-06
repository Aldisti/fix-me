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

    private Client client;

    public Market() {
        this.queue = new ConcurrentLinkedQueue<>();
        this.context = MarketContext.getInstance();
    }

    public void run() {
        Instant time = Instant.now();
        while (true) {
            Message msg = queue.poll();
            if (msg != null)
                handle(msg);

            if (Instant.now().getNano() - time.getNano() < 35000)
                continue;
            context.getAssetIds().forEach(id -> {
                Asset asset = context.updateAsset(id);
                client.send(MessageBuilder.notifyUpdate(asset));
            });
            time = Instant.now();
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
