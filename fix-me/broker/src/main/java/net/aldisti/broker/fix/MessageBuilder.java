package net.aldisti.broker.fix;

import net.aldisti.broker.TradedAsset;
import net.aldisti.common.fix.Message;
import net.aldisti.common.fix.constants.MsgType;
import net.aldisti.common.fix.constants.Tag;

import java.util.UUID;

public class MessageBuilder {
    private MessageBuilder() {}

    public static Message buy(final TradedAsset asset, Integer quantity, Integer price) {
        return newFromAsset(asset)
                .add(Tag.TYPE, MsgType.BUY.value)
                .add(Tag.PRICE, price.toString())
                .add(Tag.QUANTITY, quantity.toString());
    }

    public static Message sell(final TradedAsset asset, Integer quantity, Integer price) {
        return newFromAsset(asset)
                .add(Tag.TYPE, MsgType.SELL.value)
                .add(Tag.PRICE, price.toString())
                .add(Tag.QUANTITY, quantity.toString());
    }

    private static Message newFromAsset(final TradedAsset asset) {
        return new Message()
                .add(Tag.TARGET_ID, asset.getMarketId())
                .add(Tag.ASSET_ID, asset.getAsset().getId())
                .add(Tag.INSTRUMENT, asset.getAsset().getInstrument().name())
                .add(Tag.MESSAGE_ID, UUID.randomUUID().toString());
    }
}
