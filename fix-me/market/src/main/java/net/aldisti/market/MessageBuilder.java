package net.aldisti.market;

import net.aldisti.common.finance.Asset;
import net.aldisti.common.fix.Message;
import net.aldisti.common.fix.constants.MsgType;
import net.aldisti.common.fix.constants.Tag;

public class MessageBuilder {
    private final String marketName;

    public MessageBuilder(String marketName) {
        this.marketName = marketName;
    }

    public Message notifyUpdate(Asset asset) {
        return new Message()
                .add(Tag.TYPE, MsgType.NOTIFY.value)
                .add(Tag.INSTRUMENT, asset.getInstrument().name())
                .add(Tag.ASSET_ID, asset.getId())
                .add(Tag.PRICE, asset.getPrice().toString())
                .add(Tag.QUANTITY, asset.getQuantity().toString())
                .add(Tag.MARKET, marketName);
    }

    public Message executed(Message msg) {
        return msg.add(Tag.TYPE, MsgType.EXECUTED.value)
                .add(Tag.TARGET_ID, msg.get(Tag.SENDER_ID))
                .add(Tag.MARKET, marketName);
    }

    public Message rejected(Message msg) {
        return msg.add(Tag.TYPE, MsgType.REJECTED.value)
                .add(Tag.TARGET_ID, msg.get(Tag.SENDER_ID))
                .add(Tag.MARKET, marketName);
    }

    public Message error(Message msg) {
        return msg.add(Tag.TYPE, MsgType.ERROR.value)
                .add(Tag.TARGET_ID, msg.get(Tag.SENDER_ID))
                .add(Tag.MARKET, marketName);
    }
}
