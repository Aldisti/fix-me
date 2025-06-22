package net.aldisti.common.fix.constants;

import net.aldisti.common.fix.InvalidFixMessage;

public enum Tag {
    BODY_LENGTH("9", "bodyLength"),
    TYPE("13", "type"),
    SENDER_ID("11", "senderId"),
    TARGET_ID("12", "targetId"),
    INSTRUMENT("14", "instrument"),
    QUANTITY("15", "quantity"),
    MARKET("16", "market"),
    PRICE("17", "price"),
    CHECKSUM("10", "checksum");

    public final String value;
    public final String field;

    Tag(String value, String field) {
        this.value = value;
        this.field = field;
    }

    public static Tag fromValue(String value) throws InvalidFixMessage {
        for (Tag tag : Tag.values()) {
            if (tag.value.equalsIgnoreCase(value))
                return tag;
        }
        throw new InvalidFixMessage("Unknown tag: " + value);
    }
}
