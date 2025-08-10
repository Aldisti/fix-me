package net.aldisti.market.chain;

import net.aldisti.market.MessageBuilder;

public class BasicHandler implements Handler {

    private static final BasicHandler INSTANCE = new BasicHandler();

    private BasicHandler() { }

    public static BasicHandler getInstance() {
        return INSTANCE;
    }

    @Override
    public void handle(Request request, Response response) {
        switch (request.getMessage().type()) {
            case BUY -> BuyOrderHandler.getInstance().handle(request, response);
            case SELL -> SellOrderHandler.getInstance().handle(request, response);
            default -> response.setInvalidRequest(true);
        };

        if (response.isInvalidRequest()) {
            response.setMessage(MessageBuilder.error(
                    request.getMessage(), request.getMarketName()
            ));
        } else {
            SaveTransactionHandler.getInstance().handle(request, response);
        }
    }
}
