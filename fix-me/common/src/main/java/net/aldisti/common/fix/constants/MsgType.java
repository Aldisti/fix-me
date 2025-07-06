package net.aldisti.common.fix.constants;

import net.aldisti.common.fix.InvalidFixMessage;

public enum MsgType {
    ORDER("O"),
    BUY("BO"),
    SELL("SO"),
    EXECUTED("EX"),
    REJECTED("R"),
    INSTRUMENT("I"),
    NOTIFY("N"),
    ERROR("E"),
    INVALID_TARGET("IT"),
    INVALID_SENDER("IS"),;

    public final String value;

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
