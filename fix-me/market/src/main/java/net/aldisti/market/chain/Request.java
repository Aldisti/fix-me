package net.aldisti.market.chain;

import lombok.Getter;
import net.aldisti.common.fix.Message;

@Getter
public class Request {
    private final Message message;
    private final String marketName;

    public Request(Message msg, String marketName) {
        this.message = msg;
        this.marketName = marketName;
    }
}
