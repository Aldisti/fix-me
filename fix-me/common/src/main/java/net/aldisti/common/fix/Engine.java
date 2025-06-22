package net.aldisti.common.fix;

import net.aldisti.common.fix.constants.Tag;

import java.util.ArrayList;
import java.util.List;

import static net.aldisti.common.fix.Message.TAGVALUE_SEPARATOR;

public class Engine {
    public static String serialize(Message message) throws InvalidFixMessage {
        List<String> pairs = new ArrayList<>();
        int length = 0;
        for (Tag tag : Tag.values()) {
            if (tag == Tag.BODY_LENGTH || tag == Tag.CHECKSUM)
                continue;
            String tagValue = EngineUtils.getTagValue(message, tag);
            length += tagValue.length() + 1; // the +1 is for the separator
            pairs.add(tagValue);
        }
        message.setBodyLength(Integer.toString(length));
        pairs.addFirst(EngineUtils.getTagValue(message, Tag.BODY_LENGTH));

        String rawMessage = String.join(Message.SEPARATOR, pairs);

        message.setChecksum(Integer.toString(EngineUtils.calculateChecksum(rawMessage)));

        return rawMessage + Message.SEPARATOR + EngineUtils.getTagValue(message, Tag.CHECKSUM);
    }

    public static Message deserialize(String msg) throws InvalidFixMessage {
        EngineUtils.verifyIntegrity(msg);
        Message message = new Message();
        String[] tagValues = msg.split(Message.SEPARATOR);
        for (String tagValue : tagValues) {
            if (tagValue == null || tagValue.isEmpty())
                throw new InvalidFixMessage("Empty or null tag/value pair");
            String[] pair = tagValue.split(TAGVALUE_SEPARATOR, 2);
            if (pair.length != 2)
                throw new InvalidFixMessage("Invalid tag/value pair: " + tagValue);
            EngineUtils.setTagValue(message, pair[0], pair[1]);
        }
        return message;
    }
}
