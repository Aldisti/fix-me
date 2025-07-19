package net.aldisti.broker;

import lombok.Getter;
import net.aldisti.broker.fix.MessageBuilder;
import net.aldisti.common.fix.Message;
import net.aldisti.common.fix.constants.Tag;
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
                .add(msg.getInt(Tag.QUANTITY), msg.getInt(Tag.PRICE));
        log.info("Bought {} shares of {} for {}", msg.get(Tag.QUANTITY), msg.get(Tag.ASSET_ID), msg.get(Tag.PRICE));
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
            case BUY -> balance += msg.getInt(Tag.QUANTITY) * msg.getInt(Tag.PRICE);
            case SELL -> balance -= msg.getInt(Tag.QUANTITY) * msg.getInt(Tag.PRICE);
        }
        log.info("Rejected order restored");
    }

    public void sell(final Message msg) {
        assets.get(getKey(msg))
                .subtract(msg.getInt(Tag.QUANTITY), msg.getInt(Tag.PRICE));
        balance += msg.getInt(Tag.PRICE) * msg.getInt(Tag.QUANTITY);
        log.info("Sold {} shares of {} for {} each", msg.get(Tag.QUANTITY), msg.get(Tag.ASSET_ID), msg.get(Tag.PRICE));
    }

    public final TradedAsset addOrUpdate(final Message msg) {
        return assets.compute(getKey(msg), (k, v) ->
                (v == null) ? new TradedAsset(msg) : v.update(msg.getInt(Tag.PRICE)));
    }

    public Integer getNetWorth() {
        return balance + assets.values().parallelStream()
                .reduce(0, (partial, asset) ->
                        partial + asset.getPrice() * asset.getQuantity(), Integer::sum);
    }

    private static String getKey(final Message msg) {
        return msg.get(Tag.SENDER_ID) + "|" + msg.get(Tag.ASSET_ID);
    }
}
