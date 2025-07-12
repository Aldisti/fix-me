package net.aldisti.broker.fix;

import net.aldisti.broker.TradedAsset;
import net.aldisti.common.fix.Message;
import net.aldisti.common.fix.constants.MsgType;

import java.util.UUID;

public class MessageBuilder {
    private MessageBuilder() {}

    public static Message buy(final TradedAsset asset, Integer quantity, Integer price) {
        Message msg = newFromAsset(asset);
        msg.setType(MsgType.BUY.value);
        msg.setPrice(price.toString());
        msg.setQuantity(quantity.toString());
        return msg;
    }

    public static Message sell(final TradedAsset asset, Integer quantity, Integer price) {
        Message msg = newFromAsset(asset);
        msg.setType(MsgType.SELL.value);
        msg.setPrice(price.toString());
        msg.setQuantity(quantity.toString());
        return msg;
    }

    private static Message newFromAsset(final TradedAsset asset) {
        Message msg = new Message();
        msg.setTargetId(asset.getMarketId());
        msg.setAssetId(asset.getAsset().getId());
        msg.setInstrument(asset.getAsset().getInstrument().name());
        msg.setMessageId(UUID.randomUUID().toString());
        return msg;
    }
}
