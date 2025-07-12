package net.aldisti.broker;

import lombok.Getter;
import net.aldisti.broker.fix.MessageBuilder;
import net.aldisti.common.fix.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class BrokerContext {
    private static final Logger log = LoggerFactory.getLogger(BrokerContext.class);
    public static final int INITIAL_BALANCE = 10000;

    /**
     * The key is {@code <marketId>|<assetId>}
     */
    private final Map<String, TradedAsset> assets;
    @Getter
    private Integer balance;


    public BrokerContext() {
        assets = new HashMap<>();
        balance = INITIAL_BALANCE;
    }

    public void buy(final Message msg) {
        assets.get(getKey(msg))
                .add(msg.quantity(), msg.price());
        log.info("Bought {} shares of {} for {}", msg.getQuantity(), msg.getAssetId(), msg.getPrice());
    }

    public Message buyOrder(final TradedAsset asset, int quantity) {
        int cost = asset.getPrice() * quantity;
        if (cost <= 0 || cost > balance)
            return null;
        balance -= cost;
        return MessageBuilder.buy(asset, quantity, asset.getPrice());
    }

    public void restoreRejected(final Message msg) {
        switch (msg.type()) {
            case BUY -> balance += msg.quantity() * msg.price();
            case SELL -> balance -= msg.quantity() * msg.price();
        }
        log.info("Rejected order restored");
    }

    public void sell(final Message msg) {
        assets.get(getKey(msg))
                .subtract(msg.quantity(), msg.price());
        balance += msg.price() * msg.quantity();
        log.info("Sold {} shares of {} for {} each", msg.getQuantity(), msg.getAssetId(), msg.getPrice());
    }

    public final TradedAsset addOrUpdate(final Message msg) {
        return assets.compute(getKey(msg), (k, v) ->
                (v == null) ? new TradedAsset(msg) : v.update(msg.price()));
    }

    private static String getKey(final Message msg) {
        return msg.getSenderId() + "|" + msg.getAssetId();
    }
}
