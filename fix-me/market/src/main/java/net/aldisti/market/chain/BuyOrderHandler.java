package net.aldisti.market.chain;

import net.aldisti.common.fix.constants.Tag;
import net.aldisti.market.MarketContext;

public class BuyOrderHandler extends OrderHandler {

    private static final BuyOrderHandler INSTANCE = new BuyOrderHandler();

    private final MarketContext context;

    private BuyOrderHandler() {
        context = MarketContext.getInstance();
    }

    public static BuyOrderHandler getInstance() { return INSTANCE; }

    @Override
    public void handle(Request request, Response response) {

        boolean bought = context.buyAsset(
                request.getMessage().get(Tag.ASSET_ID),
                request.getMessage().getInt(Tag.QUANTITY),
                request.getMessage().getInt(Tag.PRICE)
        );

        if (bought) {
            executed(request, response);
        } else {
            rejected(request, response);
        }
    }
}
