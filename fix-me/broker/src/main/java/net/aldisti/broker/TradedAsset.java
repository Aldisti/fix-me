package net.aldisti.broker;

import lombok.Getter;
import net.aldisti.common.fix.Message;
import net.aldisti.common.fix.constants.Instruments;
import net.aldisti.common.fix.constants.Tag;

@Getter
public class TradedAsset {
    private final String id;
    private final Instruments instrument;
    private final String marketName;
    private String marketId;
    private Integer price; // The market price value.
    private Integer quantity; // The bought quantity.
    private Integer paid; // The amount paid for the quantity.

    public TradedAsset(Message msg) {
        this.id = msg.get(Tag.ASSET_ID);
        this.instrument = msg.instrument();
        this.price = msg.getInt(Tag.PRICE);
        this.marketId = msg.get(Tag.SENDER_ID);
        this.marketName = msg.get(Tag.MARKET);
        this.paid = 0;
        this.quantity = 0;
    }

    /**
     * @param price Updates the asset current price.
     * @return the updated instance of the asset.
     */
    public TradedAsset update(Integer price) {
        this.price = price;
        return this;
    }

    public TradedAsset update(String marketId) {
        this.marketId = marketId;
        return this;
    }

    /**
     * @param quantity The amount of shares to add to the asset.
     * @param price The price of each new added share.
     * @return the updated instance of the asset.
     */
    public void add(Integer quantity, Integer price) {
        this.quantity += quantity;
        this.paid += price * quantity;
        this.price = price; // this should be the latest price
    }

    /**
     * @param quantity The amount of shares to subtract from the asset.
     * @param price The amount of each sold share.
     * @return the updated instance of the asset.
     */
    public void subtract(Integer quantity, Integer price) {
        add(-quantity, -price);
    }

    /**
     * Calculates the Rate of Return of the asset.
     *
     * @return The positive or negative percentage.
     */
    public Integer rateOfReturn() {
        if (paid == 0)
            return 0;
        return (price * quantity - paid) / paid * 100;
    }
}
