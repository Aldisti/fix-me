package net.aldisti.common.fix;

import lombok.*;
import net.aldisti.common.fix.constants.*;
import org.apache.commons.lang3.StringUtils;

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
    @Setter
    private String instrument;
    private String quantity;
    @Setter
    private String market;
    private String price;
    private String checksum;

    public Message() {
        messageId = UUID.randomUUID().toString();
    }

    public void setBodyLength(String bodyLength) throws InvalidFixMessage {
        if (bodyLength == null)
            return;
        if (!bodyLength.matches("^\\d+$"))
            throw new InvalidFixMessage("Invalid body length: " + bodyLength);
        this.bodyLength = bodyLength;
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

    public void setSenderId(String senderId) throws InvalidFixMessage {
        if (senderId == null)
            return;
        if (!senderId.matches("^\\d{6}$"))
            throw new InvalidFixMessage("Invalid sender id: " + senderId);
        this.senderId = senderId;
    }

    public void setTargetId(String targetId) throws InvalidFixMessage {
        if (targetId == null)
            return;
        if (!targetId.matches("^\\d{6}$"))
            throw new InvalidFixMessage("Invalid sender id: " + targetId);
        this.targetId = targetId;
    }

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

    public void setQuantity(String quantity) throws InvalidFixMessage {
        if (quantity == null)
            return;
        if (!quantity.matches("^\\d+$"))
            throw new InvalidFixMessage("Invalid quantity: " + quantity);
        this.quantity = quantity;
    }

    public void setPrice(String price) throws InvalidFixMessage {
        if (price == null)
            return;
        if (!price.matches("^\\d+$"))
            throw new InvalidFixMessage("Invalid price: " + price);
        this.price = price;
    }

    public void setChecksum(String checksum) throws InvalidFixMessage {
        if (checksum == null)
            return;
        if (!checksum.matches("^\\d{1,3}$"))
            throw new InvalidFixMessage("Invalid checksum: " + checksum);
        this.checksum = StringUtils.leftPad(checksum, 3, '0');
    }
}
