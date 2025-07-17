package net.aldisti.common.fix;

import lombok.*;
import net.aldisti.common.fix.constants.*;
import net.aldisti.common.fix.validators.ValidatorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * All the setters of this class perform validation checks.
 */
@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Message {
    private static final Logger log = LoggerFactory.getLogger(Message.class);
    /**
     * This is the separator used between each tag-value pair.
     */
    public static final String SEPARATOR = " "; // should be 
    /**
     * This is the separator used between the tag and value of each field.
     */
    public static final String TAGVALUE_SEPARATOR = "=";

    private final Map<Tag, String> attributes;

    public Message() {
        attributes = new HashMap<>();
    }

    public String get(Tag tag) {
        return attributes.get(tag);
    }

    public String getTagValue(Tag tag) {
        String attribute = attributes.get(tag);
        if (attribute == null)
            return "";
        return tag.value() + TAGVALUE_SEPARATOR + attribute;
    }

    public Integer getInt(Tag tag) {
        return Integer.parseInt(attributes.get(tag));
    }

    public Message add(Tag tag, String value) {
        attributes.put(tag, value);
        return this;
    }

    public Message remove(Tag tag) {
        attributes.remove(tag);
        return this;
    }

    public boolean isValid() {
        try {
            for (Map.Entry<Tag, String> entry : attributes.entrySet())
                if (entry.getKey().validator() != null)
                    entry.getKey().validator().validate(entry.getValue());
            return true;
        } catch (ValidatorException ve) {
            log.info(ve.getMessage());
            return false;
        }
    }
}
