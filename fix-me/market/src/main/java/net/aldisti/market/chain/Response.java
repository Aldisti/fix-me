package net.aldisti.market.chain;

import lombok.Getter;
import lombok.Setter;
import net.aldisti.common.fix.Message;
import net.aldisti.common.fix.constants.Tag;
import org.bson.Document;

import java.time.LocalDateTime;

@Getter
public class Response {
    /**
     * This message will be sent as response to a Broker.
     */
    @Setter
    private Message message;
    @Setter
    private boolean invalidRequest;
    /**
     * This transaction will be stored in the database.
     */
    private final Document transaction;

    public Response(Message msg) {
        this.transaction = toTransaction(msg);
        this.message = null;
        this.invalidRequest = false;
    }

    private static Document toTransaction(Message msg) {
        Document t = new Document();
        t.put("message_id", msg.get(Tag.MESSAGE_ID));
        t.put("client_id", msg.get(Tag.TARGET_ID));
        t.put("asset_id", msg.get(Tag.ASSET_ID));
        t.put("time", LocalDateTime.now());
        t.put("type", msg.type().name());
        t.put("price", msg.get(Tag.PRICE));
        t.put("quantity", msg.get(Tag.QUANTITY));
        t.put("instrument", msg.get(Tag.INSTRUMENT));
        return t;
    }
}
