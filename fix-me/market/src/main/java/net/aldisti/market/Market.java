package net.aldisti.market;

import net.aldisti.common.finance.Asset;
import net.aldisti.common.fix.Message;
import net.aldisti.common.network.Client;
import net.aldisti.market.chain.BasicHandler;
import net.aldisti.market.chain.Request;
import net.aldisti.market.chain.Response;
import org.slf4j.Logger;

import java.util.concurrent.ConcurrentLinkedQueue;

public class Market {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(Market.class);

    private final ConcurrentLinkedQueue<Message> queue;
    private final MarketContext context;
    /**
     * Interval in milliseconds between each market update.
     */
    private final int interval;
    private final String name;

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
        this.name = name;
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
                    client.send(MessageBuilder.notifyUpdate(asset, name));
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

        Response response = new Response(msg);
        Request request = new Request(msg, name);
        // starting the chain of responsibility
        BasicHandler.getInstance().handle(request, response);

        if (response.getMessage() != null)
            client.send(response.getMessage());
    }

    public ConcurrentLinkedQueue<Message> getQueue() {
        return this.queue;
    }
}
