package net.aldisti.router.fix;

import net.aldisti.common.fix.Message;
import net.aldisti.common.fix.constants.MsgType;

public class MessageBuilder {
    private MessageBuilder() {}

    public static Message invalidMessage(Integer clientId) {
        Message msg = new Message();
        msg.setSenderId(clientId.toString());
        msg.setTargetId(clientId.toString());
        msg.setType(MsgType.ERROR.getValue());
        return msg;
    }

    public static Message invalidSender(Message msg) {
        msg.setType(MsgType.INVALID_SENDER.getValue());
        return msg;
    }

    public static Message invalidTarget(Message msg) {
        msg.setType(MsgType.INVALID_TARGET.getValue());
        return msg;
    }
}
