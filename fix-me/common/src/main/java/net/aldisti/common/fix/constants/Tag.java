package net.aldisti.common.fix.constants;

import net.aldisti.common.fix.InvalidFixMessage;

public enum Tag {
    BODY_LENGTH("9", "bodyLength"),
    TYPE("11", "type"),
    SENDER_ID("13", "senderId"),
    TARGET_ID("15", "targetId"),
    MESSAGE_ID("17", "messageId"),
    INSTRUMENT("19", "instrument"),
    QUANTITY("21", "quantity"),
    MARKET("23", "market"),
    PRICE("25", "price"),
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
