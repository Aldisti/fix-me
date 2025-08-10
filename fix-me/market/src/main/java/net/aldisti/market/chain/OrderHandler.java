package net.aldisti.market.chain;

import net.aldisti.common.fix.constants.MsgType;
import net.aldisti.market.MessageBuilder;

public abstract class OrderHandler implements Handler {

    protected void executed(Request request, Response response) {
        response.setMessage(MessageBuilder.executed(
                request.getMessage(), request.getMarketName()
        ));
        response.getTransaction().put("response", MsgType.EXECUTED.name());
    }

    protected void rejected(Request request, Response response) {
        response.setMessage(MessageBuilder.rejected(
                request.getMessage(), request.getMarketName()
        ));
        response.getTransaction().put("response", MsgType.REJECTED.name());
    }
}
