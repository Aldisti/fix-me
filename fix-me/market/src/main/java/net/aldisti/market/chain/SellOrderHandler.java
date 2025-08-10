package net.aldisti.market.chain;

import net.aldisti.common.fix.constants.Tag;
import net.aldisti.market.MarketContext;

public class SellOrderHandler extends OrderHandler {

    private static final SellOrderHandler INSTANCE = new SellOrderHandler();

    private final MarketContext context;

    private SellOrderHandler() {
        context = MarketContext.getInstance();
    }

    public static SellOrderHandler getInstance() {
        return INSTANCE;
    }

    @Override
    public void handle(Request request, Response response) {

        boolean sold = context.sellAsset(
                request.getMessage().get(Tag.ASSET_ID),
                request.getMessage().getInt(Tag.QUANTITY),
                request.getMessage().getInt(Tag.PRICE)
        );

        if (sold) {
            executed(request, response);
        } else {
            rejected(request, response);
        }
    }
}
