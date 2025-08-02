package net.aldisti.broker;

import lombok.Getter;
import net.aldisti.common.fix.Message;
import net.aldisti.common.fix.constants.Instrument;
import net.aldisti.common.fix.constants.Tag;

@Getter
public class TradedAsset {
    private final String id;
    private final Instrument instrument;
    private final String marketName; // The market name, unique for the market.

    private String marketId; // The market id, assigned by the router.
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
     * @param msg The message containing new data about the asset.
     * @return The updated asset.
     */
    public TradedAsset update(Message msg) {
        this.price = msg.getInt(Tag.PRICE);
        this.marketId = msg.get(Tag.SENDER_ID);
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
