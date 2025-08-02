package net.aldisti.common.fix;

import net.aldisti.common.fix.constants.Instrument;
import net.aldisti.common.fix.constants.MsgType;
import net.aldisti.common.fix.constants.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EngineTest {
    @Test
    void integration() {
        Message msg = new Message()
                .add(Tag.TYPE, MsgType.ORDER.value)
                .add(Tag.SENDER_ID, "220625")
                .add(Tag.TARGET_ID, "526022")
                .add(Tag.INSTRUMENT, Instrument.STOCK.name())
                .add(Tag.QUANTITY, "360")
                .add(Tag.MARKET, "Something?")
                .add(Tag.PRICE, "30");

        String serialized = Engine.marshall(msg);
        assertNotNull(serialized);

        Message deserialized = Engine.unmarshall(serialized);
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
