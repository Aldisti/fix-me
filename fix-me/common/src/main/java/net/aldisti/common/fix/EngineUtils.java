package net.aldisti.common.fix;

import lombok.extern.slf4j.Slf4j;
import net.aldisti.common.fix.constants.Tag;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static net.aldisti.common.fix.Message.TAGVALUE_SEPARATOR;
import static net.aldisti.common.fix.Message.SEPARATOR;

@Slf4j
class EngineUtils {
    /**
     * This regex checks if a FIX message starts with a BodyLength tag
     * and ends with a Checksum tag.
     */
    static final String INTEGRITY_REGEX = "^(" + Tag.BODY_LENGTH.value
            + TAGVALUE_SEPARATOR + "\\d+)" + SEPARATOR + "(.+)" + SEPARATOR
            + "(" + Tag.CHECKSUM.value + TAGVALUE_SEPARATOR + "\\d{3})$";

    static void setTagValue(Message message, String tagValue, String value) throws InvalidFixMessage {
        Tag tag = Tag.fromValue(tagValue);
        setTag(message, tag, value);
    }

    static String getTagValue(Message message, Tag tag) {
        String getterName = "get" + StringUtils.capitalize(tag.field);

        String value;
        try {
            value = (String) getMethod(message.getClass(), getterName).invoke(message);
        } catch (InvocationTargetException | IllegalAccessException e) {
            log.error("Error while invoking {}", getterName, e);
            throw new InvalidFixMessage("Cannot invoke getter method for field " + tag.field);
        }
        if (value == null)
            return null;
        return tag.value + TAGVALUE_SEPARATOR + value;
    }

    static int calculateChecksum(String rawMessage) {
        int sum = 0;
        for (Byte b : rawMessage.getBytes(StandardCharsets.UTF_8)) {
            sum += b;
        }
        return sum % 256;
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
     * Verifies the checksum of a full FIX message.
     * <br>
     * The message MUST contain the Checksum field and MUST have at least another field.
     *
     * @param rawMessage The FIX message to check, the Checksum field MUST be present.
     * @throws InvalidFixMessage If the checksum doesn't match.
     */
    static void verifyChecksum(String rawMessage) throws InvalidFixMessage {
        // search the last tag-value
        int lastSeparator = rawMessage.lastIndexOf(Message.SEPARATOR);
        // get the last tag-value and make sure it's the checksum
        String checksumTagValue = rawMessage.substring(lastSeparator + 1);
        // split it and check the value
        String[] pair = checksumTagValue.split(TAGVALUE_SEPARATOR);
        int value = Integer.parseInt(pair[1]);
        if (value != calculateChecksum(rawMessage.substring(0, lastSeparator)))
            throw new InvalidFixMessage("Invalid Checksum value");
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
    static void verifyBodyLength(String rawMessage) throws InvalidFixMessage {
        int lastSeparator = rawMessage.lastIndexOf(Message.SEPARATOR);
        rawMessage = rawMessage.substring(0, lastSeparator);
        int firstSeparator = rawMessage.indexOf(Message.SEPARATOR);
        String tagValue = rawMessage.substring(0, firstSeparator);
        int value = Integer.parseInt(tagValue.split(TAGVALUE_SEPARATOR)[1]);
        if (value != rawMessage.length() - tagValue.length())
            throw new InvalidFixMessage("Invalid BodyLength value");
    }

    static void setTag(Message message, Tag tag, String value) {
        String setterName = "set" + StringUtils.capitalize(tag.field);
        String getterName = "get" + StringUtils.capitalize(tag.field);

        Method setter = getMethod(message.getClass(), setterName);
        Method getter = getMethod(message.getClass(), getterName);

        try {
            if (getter.invoke(message) != null)
                throw new InvalidFixMessage("Duplicated tag: " + tag);
            setter.invoke(message, value);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("Cannot invoke getter or setter on {}", tag.field, e);
            throw new InvalidFixMessage("Cannot invoke method " + setterName);
        }
    }

    private static Method getMethod(Class<?> clazz, String name) {
        return Arrays.stream(clazz.getMethods())
                .filter(method -> method.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new InvalidFixMessage(
                        "Cannot find method " + name + " in class " + clazz.getName()
                ));
    }
}
