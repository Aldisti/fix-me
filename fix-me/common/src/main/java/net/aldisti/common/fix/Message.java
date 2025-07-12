package net.aldisti.common.fix;

import lombok.*;
import net.aldisti.common.fix.constants.*;
import net.aldisti.common.utils.StringUtils;

import java.util.UUID;

/**
 * All the setters of this class perform validation checks.
 */
@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Message {
    /**
     * This is the separator used between each tag-value pair.
     */
    public static final String SEPARATOR = " "; // should be 
    /**
     * This is the separator used between the tag and value of each field.
     */
    public static final String TAGVALUE_SEPARATOR = "=";

    private String bodyLength;
    private String type;
    private String senderId;
    private String targetId;
    private String messageId;
    private String instrument;
    @Setter
    private String assetId;
    private String quantity;
    @Setter
    private String market;
    private String price;
    private String checksum;

    public Message() {
        messageId = UUID.randomUUID().toString();
    }

    // Body Length ------------------------------

    public void setBodyLength(String bodyLength) throws InvalidFixMessage {
        if (bodyLength == null)
            return;
        if (!bodyLength.matches("^\\d+$"))
            throw new InvalidFixMessage("Invalid body length: " + bodyLength);
        this.bodyLength = bodyLength;
    }

    // Type -------------------------------------

    public MsgType type() {
        return MsgType.valueOf(type);
    }

    public String getType() {
        if (type == null)
            return null;
        return MsgType.valueOf(type).value;
    }

    public void setType(String type) throws InvalidFixMessage {
        if (type == null)
            return;
        this.type = MsgType.fromValue(type).name();
    }

    // Sender Id --------------------------------

    public void setSenderId(String senderId) throws InvalidFixMessage {
        if (senderId == null)
            return;
        if (!senderId.matches("^\\d{6}$"))
            throw new InvalidFixMessage("Invalid sender id: " + senderId);
        this.senderId = senderId;
    }

    // Target Id --------------------------------

    public void setTargetId(String targetId) throws InvalidFixMessage {
        if (targetId == null)
            return;
        if (!targetId.matches("^\\d{6}$"))
            throw new InvalidFixMessage("Invalid sender id: " + targetId);
        this.targetId = targetId;
    }

    // Message Id -------------------------------

    public void setMessageId(String messageId) throws InvalidFixMessage {
        if (messageId == null) {
            this.messageId = null;
            return;
        }
        try {
            UUID.fromString(messageId); // should throw if messageId is not a UUID
        } catch (IllegalArgumentException e) {
            throw new InvalidFixMessage("Invalid message id: " + messageId);
        }
        this.messageId = messageId;
    }

    // Instrument -------------------------------

    public Instruments instrument() {
        return Instruments.valueOf(instrument);
    }

    public void setInstrument(String instrument) throws InvalidFixMessage {
        try {
            this.instrument = Instruments.valueOf(instrument.toUpperCase()).name();
        } catch (IllegalArgumentException e) {
            throw new InvalidFixMessage("Invalid instrument: " + instrument);
        }
    }

    // Quantity ---------------------------------

    public Integer quantity() {
        return Integer.valueOf(quantity);
    }

    public void setQuantity(String quantity) throws InvalidFixMessage {
        if (quantity == null)
            return;
        if (!quantity.matches("^\\d+$"))
            throw new InvalidFixMessage("Invalid quantity: " + quantity);
        this.quantity = quantity;
    }

    // Price ------------------------------------

    public Integer price() {
        return Integer.valueOf(price);
    }

    public void setPrice(String price) throws InvalidFixMessage {
        if (price == null)
            return;
        if (!price.matches("^\\d+$"))
            throw new InvalidFixMessage("Invalid price: " + price);
        this.price = price;
    }

    // Checksum ---------------------------------

    public void setChecksum(String checksum) throws InvalidFixMessage {
        if (checksum == null)
            return;
        if (!checksum.matches("^\\d{1,3}$"))
            throw new InvalidFixMessage("Invalid checksum: " + checksum);
        this.checksum = StringUtils.leftPad(checksum, 3, '0');
    }
}
