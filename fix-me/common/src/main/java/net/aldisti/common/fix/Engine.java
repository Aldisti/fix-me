package net.aldisti.common.fix;

import net.aldisti.common.fix.constants.Tag;

import java.util.stream.Collectors;

import static net.aldisti.common.fix.Message.TAG_SEP;

public class Engine {
    /**
     * Marshalls a {@link Message} into a string representation of it.
     */
    public static String marshall(Message message) throws InvalidFixMessage {
        if (message.isNotValid())
            throw new InvalidFixMessage("Invalid FIX message");

        String body = message.getAttributes().keySet().stream()
                .filter(s -> s != Tag.BODY_LENGTH && s != Tag.CHECKSUM)
                .map(message::getTagValue)
                .collect(Collectors.joining(TAG_SEP));

        return EngineUtils.addChecksum(EngineUtils.addBodyLength(body));
    }

    /**
     * Unmarshalls a string representation into a {@link Message} object and
     * validates it.
     */
    public static Message unmarshall(String msg) throws InvalidFixMessage {
        EngineUtils.verifyIntegrity(msg);
        Message message = new Message();
        String[] tagValues = msg.split(TAG_SEP);
        for (String rawTagValue : tagValues) {
            var tagValue = EngineUtils.extractTagValue(rawTagValue);
            message.add(tagValue.getKey(), tagValue.getValue());
        }
        if (message.isNotValid())
            throw new InvalidFixMessage("Invalid FIX message");
        return message;
    }
}
