package net.aldisti.common.fix.constants;

import lombok.Getter;
import net.aldisti.common.fix.InvalidFixMessage;

@Getter
public enum MsgType {
    ORDER("A"),
    EXECUTED("B"),
    REJECT("C"),
    INSTRUMENT("D"),;

    private final String value;

    MsgType(String value) {
        this.value = value;
    }

    public static MsgType fromValue(String value) {
        for (MsgType msgType : MsgType.values()) {
            if (msgType.value.equals(value))
                return msgType;
        }
        throw new InvalidFixMessage("Unknown MsgType: " + value);
    }
}
