package net.aldisti.router.fix;

import net.aldisti.common.fix.Message;
import net.aldisti.common.fix.constants.MsgType;
import net.aldisti.common.fix.constants.Tag;

public class MessageBuilder {
    private MessageBuilder() {}

    public static Message invalidMessage(Integer clientId) {
        return new Message()
                .add(Tag.SENDER_ID, null)
                .add(Tag.TARGET_ID, clientId.toString())
                .add(Tag.TYPE, MsgType.ERROR.value);
    }

    public static Message invalidSender(Message msg, Integer clientId) {
        return msg.add(Tag.TARGET_ID, clientId.toString())
                .add(Tag.TYPE, MsgType.INVALID_SENDER.value);
    }

    public static Message invalidTarget(Message msg, Integer clientId) {
        return msg.add(Tag.TARGET_ID, clientId.toString())
                .add(Tag.TYPE, MsgType.INVALID_TARGET.value);
    }
}
