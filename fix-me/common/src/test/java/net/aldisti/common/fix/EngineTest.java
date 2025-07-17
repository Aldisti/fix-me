package net.aldisti.common.fix;

import net.aldisti.common.fix.constants.Instruments;
import net.aldisti.common.fix.constants.MsgType;
import net.aldisti.common.fix.constants.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EngineTest {
    @Test
    void integration() {
        var msg = new Message();
        msg.add(Tag.TYPE, MsgType.ORDER.value);
        msg.add(Tag.SENDER_ID, "220625");
        msg.add(Tag.TARGET_ID, "526022");
        msg.add(Tag.INSTRUMENT, Instruments.STOCK.name());
        msg.add(Tag.QUANTITY, "360");
        msg.add(Tag.MARKET, "Something?");
        msg.add(Tag.PRICE, "30");

        var serialized = Engine.serialize(msg);
        assertNotNull(serialized);

        var deserialized = Engine.deserialize(serialized);
        assertNotNull(deserialized);

        assertEquals(msg.get(Tag.TYPE), deserialized.get(Tag.TYPE));
        assertEquals(msg.get(Tag.SENDER_ID), deserialized.get(Tag.SENDER_ID));
        assertEquals(msg.get(Tag.TARGET_ID), deserialized.get(Tag.TARGET_ID));
        assertEquals(msg.get(Tag.INSTRUMENT), deserialized.get(Tag.INSTRUMENT));
        assertEquals(msg.get(Tag.QUANTITY), deserialized.get(Tag.QUANTITY));
        assertEquals(msg.get(Tag.MARKET), deserialized.get(Tag.MARKET));
        assertEquals(msg.get(Tag.PRICE), deserialized.get(Tag.PRICE));
        assertEquals(msg.get(Tag.MESSAGE_ID), deserialized.get(Tag.MESSAGE_ID));
    }
}
