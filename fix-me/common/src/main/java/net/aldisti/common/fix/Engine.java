package net.aldisti.common.fix;

import net.aldisti.common.fix.constants.Tag;

import java.util.stream.Collectors;

import static net.aldisti.common.fix.Message.SEPARATOR;

public class Engine {
    public static String marshall(Message message) throws InvalidFixMessage {
        if (!message.isValid())
            throw new InvalidFixMessage("Invalid FIX message");

        String body = message.getAttributes().entrySet().stream()
                .filter(e -> e.getValue() != null)
                .filter(e -> e.getKey() != Tag.BODY_LENGTH && e.getKey() != Tag.CHECKSUM)
                .map(e -> message.getTagValue(e.getKey()))
                .collect(Collectors.joining(SEPARATOR));

        return EngineUtils.addChecksum(EngineUtils.addBodyLength(body));
    }

    public static String serialize(Message message) throws InvalidFixMessage {
        return marshall(message);
    }

    public static Message unmarshall(String msg) throws InvalidFixMessage {
        EngineUtils.verifyIntegrity(msg);
        Message message = new Message();
        String[] tagValues = msg.split(SEPARATOR);
        for (String rawTagValue : tagValues) {
            var tagValue = EngineUtils.extractTagValue(rawTagValue);
            message.add(tagValue.getKey(), tagValue.getValue());
        }
        if (!message.isValid())
            throw new InvalidFixMessage("Invalid FIX message");
        return message;
    }

    public static Message deserialize(String msg) throws InvalidFixMessage {
        return unmarshall(msg);
    }
}
