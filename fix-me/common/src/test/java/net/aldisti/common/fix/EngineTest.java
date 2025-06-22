package net.aldisti.common.fix;

import net.aldisti.common.fix.constants.MsgType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EngineTest {
    @Test
    void integration() {
        var msg = new Message();
        msg.setType(MsgType.ORDER.getValue());
        msg.setSenderId("220625");
        msg.setTargetId("526022");
        msg.setInstrument("RKLB");
        msg.setQuantity("360");
        msg.setMarket("Something?");
        msg.setPrice("30");

        var serialized = Engine.serialize(msg);
        assertNotNull(serialized);

        var deserialized = Engine.deserialize(serialized);
        assertNotNull(deserialized);

        assertEquals(msg.getType(), deserialized.getType());
        assertEquals(msg.getSenderId(), deserialized.getSenderId());
        assertEquals(msg.getTargetId(), deserialized.getTargetId());
        assertEquals(msg.getInstrument(), deserialized.getInstrument());
        assertEquals(msg.getQuantity(), deserialized.getQuantity());
        assertEquals(msg.getMarket(), deserialized.getMarket());
        assertEquals(msg.getPrice(), deserialized.getPrice());
    }
}
