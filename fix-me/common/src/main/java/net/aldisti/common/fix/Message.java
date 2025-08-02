package net.aldisti.common.fix;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.aldisti.common.fix.constants.Instrument;
import net.aldisti.common.fix.constants.MsgType;
import net.aldisti.common.fix.constants.Tag;
import net.aldisti.common.fix.validators.ValidatorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Message {
    private static final Logger log = LoggerFactory.getLogger(Message.class);
    /**
     * This is the separator used between each tag-value pair.
     */
    public static final String TAG_SEP = " "; // should be 
    /**
     * This is the separator used between the tag and value of each field.
     */
    public static final String TAGVALUE_SEP = "=";

    private final Map<Tag, String> attributes;

    public Message() {
        attributes = new HashMap<>();
        attributes.put(Tag.MESSAGE_ID, UUID.randomUUID().toString());
    }

    public String get(Tag tag) {
        return attributes.get(tag);
    }

    public Message add(Tag tag, String value) {
        attributes.put(tag, value);
        return this;
    }

    /**
     * If the tag is not present in the attributes map,
     * or the value is {@code null}, then an empty String is returned.
     *
     * @param tag The tag to retrieve.
     * @return A string composed of {@code tag.value() + TAGVALUE_SEPARATOR + attributes.get(tag)}
     */
    public String getTagValue(Tag tag) {
        String attribute = attributes.get(tag);
        if (attribute == null)
            return "";
        return tag.value() + TAGVALUE_SEP + attribute;
    }

    public Integer getInt(Tag tag) {
        return Integer.parseInt(attributes.get(tag));
    }

    public MsgType type() {
        String attr = attributes.get(Tag.TYPE);
        if (attr == null)
            return null;
        return MsgType.fromValue(attr);
    }

    public Instrument instrument() {
        String attr = attributes.get(Tag.INSTRUMENT);
        if (attr == null)
            return null;
        return Instrument.valueOf(attr);
    }

    public final Map<Tag, String> getAttributes() {
        return attributes;
    }

    public boolean isNotValid() {
        try {
            for (Map.Entry<Tag, String> entry : attributes.entrySet())
                if (entry.getKey().validator() != null)
                    entry.getKey().validator().validate(entry.getValue());
        } catch (ValidatorException ve) {
            log.warn("Invalid message: {}", ve.getMessage());
            return true;
        }
        return false;
    }
}
