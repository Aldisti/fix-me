package net.aldisti.broker;

import net.aldisti.common.fix.Message;
import net.aldisti.common.fix.constants.Instrument;
import net.aldisti.common.fix.constants.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class TradedAssetTest {

    private Message msg;
    private TradedAsset asset;

    @BeforeEach
    void beforeAll() {
        msg = new Message();
        msg.add(Tag.ASSET_ID, "RKLB");
        msg.add(Tag.INSTRUMENT, Instrument.STOCK.name());
        msg.add(Tag.PRICE, "100");
        msg.add(Tag.SENDER_ID, "200204");
        msg.add(Tag.MARKET, "NASDAQ");

        asset = new TradedAsset(msg);
    }

    @Test
    void update() {
        Integer newPrice = 50;
        String newSenderId = "208225";
        msg.add(Tag.PRICE, newPrice.toString());
        msg.add(Tag.SENDER_ID, newSenderId);

        // tested method
        asset = asset.update(msg);

        assertEquals(newPrice, asset.getPrice());
        assertEquals(newSenderId, asset.getMarketId());
    }

    @Test
    void add() {
        int quantity = 10;
        int price = 50;

        int oldQuantity = asset.getQuantity();
        int oldPrice = asset.getPrice();
        int oldPaid = asset.getPaid();

        asset.add(quantity, price);

        assertNotEquals(oldQuantity, asset.getQuantity());
        assertNotEquals(oldPrice, asset.getPrice());
        assertNotEquals(oldPaid, asset.getPaid());

        assertEquals(oldPaid + quantity * price, asset.getPaid());
        assertEquals(oldQuantity + quantity, asset.getQuantity());

        assertEquals(price, asset.getPrice());
    }

    @Test
    void subtract() {
        int quantity = 10;
        int price = 50;

        int oldQuantity = asset.getQuantity();
        int oldPrice = asset.getPrice();
        int oldPaid = asset.getPaid();

        asset.subtract(quantity, price);

        assertNotEquals(oldQuantity, asset.getQuantity());
        assertNotEquals(oldPrice, asset.getPrice());
        assertNotEquals(oldPaid, asset.getPaid());

        assertEquals(oldPaid - quantity * price, asset.getPaid());
        assertEquals(oldQuantity - quantity, asset.getQuantity());

        assertEquals(price, asset.getPrice());
    }

    @Test
    void rateOfReturn() {
        assertEquals(0, asset.rateOfReturn());

        asset.add(10, 50);
        asset.update(msg);

        int ror = asset.rateOfReturn();

        assertNotEquals(0, ror);
        assertEquals(100, asset.getPrice());
    }
}