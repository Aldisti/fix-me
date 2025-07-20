package net.aldisti.common.fix;

import net.aldisti.common.fix.constants.Tag;
import net.aldisti.common.utils.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static net.aldisti.common.fix.Message.TAGVALUE_SEP;
import static net.aldisti.common.fix.Message.TAG_SEP;

class EngineUtils {
    private EngineUtils() {}

    /**
     * This regex checks if a FIX message starts with a BodyLength tag
     * and ends with a Checksum tag.
     */
    private static final String INTEGRITY_REGEX = "^(" + Tag.BODY_LENGTH.value()
            + TAGVALUE_SEP + "\\d+)" + TAG_SEP + "(.+)" + TAG_SEP
            + "(" + Tag.CHECKSUM.value() + TAGVALUE_SEP + "\\d{3})$";

    /**
     * Adds the body length tag-value to the beginning of a message.
     */
    static String addBodyLength(String body) {
        return Tag.BODY_LENGTH.value() + TAGVALUE_SEP + body.length() + TAG_SEP + body;
    }

    /**
     * Calculates and adds the checksum tag-value to the end of a message.
     */
    static String addChecksum(String body) {
        String checksum = StringUtils.leftPad(calculateChecksum(body).toString(), 3, '0');
        return body + TAG_SEP + Tag.CHECKSUM.value()
                + TAGVALUE_SEP + checksum;
    }

    /**
     * Extracts a pair of {@link Tag}-{@code String} from a tag-value representation.
     */
    static Map.Entry<Tag, String> extractTagValue(String tagValue) throws InvalidFixMessage {
        String[] parts = StringUtils.strip(tagValue, TAG_SEP).split(TAGVALUE_SEP, 2);
        if (parts.length != 2)
            throw new InvalidFixMessage("Invalid tag value, found " + parts.length + " parts");
        return Map.entry(Tag.fromValue(parts[0]), parts[1]);
    }

    /**
     * Verifies the integrity of a complete FIX message.
     * <br>
     * Checks both the checksum and the body length of the message.
     *
     * @param msg The full FIX message to verify.
     * @throws InvalidFixMessage If the message is malformed.
     */
    static void verifyIntegrity(String msg) throws InvalidFixMessage {
        if (msg == null || msg.isEmpty())
            throw new InvalidFixMessage("Empty or null message");
        if (!msg.matches(INTEGRITY_REGEX))
            throw new InvalidFixMessage("Invalid message format");
        verifyChecksum(msg);
        verifyBodyLength(msg);
    }

    /**
     * Verifies the body length of a full FIX message.
     * <br>
     * The message MUST contain the BodyLength, Checksum fields and,
     * at least, another field.
     *
     * @param rawMessage The FIX message to check, the BodyLength and Checksum fields
     *                   MUST be present.
     * @throws InvalidFixMessage If the body length doesn't match.
     */
    private static void verifyBodyLength(String rawMessage) throws InvalidFixMessage {
        int lastSeparator = rawMessage.lastIndexOf(Message.TAG_SEP);
        rawMessage = rawMessage.substring(0, lastSeparator);
        int firstSeparator = rawMessage.indexOf(Message.TAG_SEP);
        String tagValue = rawMessage.substring(0, firstSeparator);
        int value = Integer.parseInt(tagValue.split(TAGVALUE_SEP)[1]);
        if (value != rawMessage.length() - tagValue.length() - 1)
            throw new InvalidFixMessage("Invalid BodyLength value");
    }

    /**
     * Verifies the checksum of a full FIX message.
     * <br>
     * The message MUST contain the Checksum field and MUST have at least another field.
     *
     * @param rawMessage The FIX message to check, the Checksum field MUST be present.
     * @throws InvalidFixMessage If the checksum doesn't match.
     */
    private static void verifyChecksum(String rawMessage) throws InvalidFixMessage {
        // search the last tag-value
        int lastSeparator = rawMessage.lastIndexOf(Message.TAG_SEP);
        // get the last tag-value and make sure it's the checksum
        String checksumTagValue = rawMessage.substring(lastSeparator + 1);
        // split it and check the value
        String[] pair = checksumTagValue.split(TAGVALUE_SEP);
        int value = Integer.parseInt(pair[1]);
        if (value != calculateChecksum(rawMessage.substring(0, lastSeparator)))
            throw new InvalidFixMessage("Invalid Checksum value");
    }

    /**
     * Sums all the bytes in a string and returns the modulus by 256.
     */
    private static Integer calculateChecksum(String str) {
        int sum = 0;
        for (Byte b : str.getBytes(StandardCharsets.UTF_8))
            sum += b;
        return sum % 256;
    }
}
