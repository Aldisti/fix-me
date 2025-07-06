package net.aldisti.router.fix;

import net.aldisti.common.fix.Message;
import net.aldisti.common.fix.constants.MsgType;

public class MessageBuilder {
    private MessageBuilder() {}

    public static Message invalidMessage(Integer clientId) {
        Message msg = new Message();
        msg.setSenderId("0");
        msg.setTargetId(clientId.toString());
        msg.setType(MsgType.ERROR.value);
        return msg;
    }

    public static Message invalidSender(Message msg, Integer clientId) {
        msg.setTargetId(clientId.toString());
        msg.setType(MsgType.INVALID_SENDER.value);
        return msg;
    }

    public static Message invalidTarget(Message msg, Integer clientId) {
        msg.setTargetId(clientId.toString());
        msg.setType(MsgType.INVALID_TARGET.value);
        return msg;
    }
}
