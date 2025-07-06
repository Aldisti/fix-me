package net.aldisti.market;

import net.aldisti.common.finance.Asset;
import net.aldisti.common.fix.Message;
import net.aldisti.common.fix.constants.MsgType;

public class MessageBuilder {
    private MessageBuilder() {}

    public static Message notifyUpdate(Asset asset) {
        Message msg = new Message();
        msg.setType(MsgType.NOTIFY.value);
        msg.setInstrument(asset.getInstrument().name());
        msg.setAssetId(asset.getId());
        msg.setPrice(asset.getPrice().toString());
        msg.setQuantity(asset.getQuantity().toString());
        return msg;
    }

    public static Message executed(Message msg) {
        msg.setType(MsgType.EXECUTED.value);
        msg.setTargetId(msg.getSenderId());
        return msg;
    }

    public static Message rejected(Message msg) {
        msg.setType(MsgType.REJECTED.value);
        msg.setTargetId(msg.getSenderId());
        return msg;
    }

    public static Message error(Message msg) {
        msg.setType(MsgType.ERROR.value);
        msg.setTargetId(msg.getSenderId());
        return msg;
    }
}
