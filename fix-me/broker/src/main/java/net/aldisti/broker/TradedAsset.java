package net.aldisti.broker;

import lombok.Getter;
import net.aldisti.common.finance.Asset;
import net.aldisti.common.fix.Message;
import net.aldisti.common.fix.constants.Tag;

@Getter
public class TradedAsset {
    private final Asset asset;
    private final String marketId;
    /**
     * The bought quantity.
     */
    private Integer quantity;
    /**
     * The amount paid for the {@link #quantity}
     */
    private Integer paid;
    /**
     * The market price value.
     */
    private Integer price;

    public TradedAsset(Message msg) {
        this.asset = Asset.builder()
                .id(msg.get(Tag.ASSET_ID))
                .name("N/A")
                .instrument(msg.instrument())
                .build();
        this.marketId = msg.get(Tag.SENDER_ID);
        this.paid = 0;
        this.quantity = 0;
        this.price = msg.getInt(Tag.PRICE);
    }

    /**
     * @param price Updates the asset current price.
     * @return the updated instance of the asset.
     */
    public TradedAsset update(Integer price) {
        this.price = price;
        return this;
    }

    /**
     * @param quantity The amount of shares to add to the asset.
     * @param price The price of each new added share.
     * @return the updated instance of the asset.
     */
    public void add(Integer quantity, Integer price) {
        this.quantity += quantity;
        this.paid += price;
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
