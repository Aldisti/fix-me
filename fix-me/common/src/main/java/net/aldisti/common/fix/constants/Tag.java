package net.aldisti.common.fix.constants;

import net.aldisti.common.fix.InvalidFixMessage;
import net.aldisti.common.fix.validators.IsIntegerValidator;
import net.aldisti.common.fix.validators.IsEnum;
import net.aldisti.common.fix.validators.TagValueValidator;

public enum Tag {
    BODY_LENGTH(9, IsIntegerValidator.of()),
    TYPE(11),
    SENDER_ID(13, IsIntegerValidator.of(6)),
    TARGET_ID(15, IsIntegerValidator.of(6)),
    MESSAGE_ID(17),
    INSTRUMENT(19, IsEnum.of(Instruments.class)),
    ASSET_ID(20),
    QUANTITY(21, IsIntegerValidator.of()),
    MARKET(23),
    PRICE(25, IsIntegerValidator.of()),
    CHECKSUM(10, IsIntegerValidator.of(3));

    private final int value;
    private final TagValueValidator validator;

    Tag(int value) {
        this.value = value;
        this.validator = null;
    }

    Tag(int value, TagValueValidator validator) {
        this.value = value;
        this.validator = validator;
    }

    public int value() {
        return value;
    }

    public TagValueValidator validator() {
        return validator;
    }

    public static Tag fromValue(String value) {
        int needle;
        try {
            needle = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new InvalidFixMessage("Invalid tag, " + value + " is not an integer");
        }
        // BS could be used to search the value
        for (Tag tag : Tag.values()) {
            if (tag.value == needle)
                return tag;
        }
        throw new InvalidFixMessage("Invalid tag, " + value + " not found");
    }
}
