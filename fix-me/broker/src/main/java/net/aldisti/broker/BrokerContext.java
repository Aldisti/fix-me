package net.aldisti.broker;

import net.aldisti.common.fix.Message;
import net.aldisti.common.fix.constants.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Broker strategy, implement this class to create a new one.
 */
public abstract class BrokerContext {
    private static final Logger log = LoggerFactory.getLogger(BrokerContext.class);

    static final Integer INITIAL_BALANCE = 10000;

    protected final Map<String, TradedAsset> assets;
    protected Integer balance;

    public BrokerContext() {
        assets = new HashMap<>();
        balance = INITIAL_BALANCE;
    }

    /**
     * Check if there is a need to buy or sell an asset.
     *
     * @return a message (with a buy or sell order) to send
     * to the client or null if there is nothing to do.
     */
    abstract Message checkForBuy(final TradedAsset asset);

    /**
     * Update context after successful buy order.
     */
    void executeBuy(final Message msg) {
        assets.get(getKey(msg))
                .add(msg.getInt(Tag.QUANTITY), msg.getInt(Tag.PRICE));
        log.info("Bought {} shares of {} for {}", msg.get(Tag.QUANTITY), msg.get(Tag.ASSET_ID), msg.get(Tag.PRICE));
    }

    /**
     * Update context after successful sell order.
     */
    void executeSell(final Message msg) {
        assets.get(getKey(msg))
                .subtract(msg.getInt(Tag.QUANTITY), msg.getInt(Tag.PRICE));
        balance += msg.getInt(Tag.PRICE) * msg.getInt(Tag.QUANTITY);
        log.info("Sold {} shares of {} for {} each", msg.get(Tag.QUANTITY), msg.get(Tag.ASSET_ID), msg.get(Tag.PRICE));
    }

    /**
     * Restore context after rejected order.
     */
    void restoreOrder(final Message msg) {
        switch (msg.type()) {
            case BUY -> balance += msg.getInt(Tag.QUANTITY) * msg.getInt(Tag.PRICE);
            case SELL -> balance -= msg.getInt(Tag.QUANTITY) * msg.getInt(Tag.PRICE);
        }
        log.warn("Rejected order restored");
    }

    /**
     * Updates an existing asset or creates one.
     */
    TradedAsset updateAsset(final Message msg) {
        return assets.compute(getKey(msg), (k, v) ->
                (v == null) ? new TradedAsset(msg) : v.update(msg.getInt(Tag.PRICE)));
    }

    /**
     * Calculate and return the net worth of the broker.
     */
    Integer getNetWorth() {
        return balance + assets.values().parallelStream()
                .reduce(0, (partial, asset) ->
                        partial + asset.getPrice() * asset.getQuantity(), Integer::sum);
    }

    static String getKey(final Message msg) {
        return msg.get(Tag.SENDER_ID) + "|" + msg.get(Tag.ASSET_ID);
    }
}
