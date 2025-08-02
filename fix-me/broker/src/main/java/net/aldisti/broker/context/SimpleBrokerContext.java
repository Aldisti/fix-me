package net.aldisti.broker.context;

import net.aldisti.broker.TradedAsset;
import net.aldisti.broker.fix.MessageBuilder;
import net.aldisti.common.fix.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleBrokerContext extends BrokerContext {
    private static final Logger log = LoggerFactory.getLogger(SimpleBrokerContext.class);

    @Override
    public Message checkForBuy(final TradedAsset asset) {
        if (asset.getQuantity() == 0) {
            // check current asset allocation before buying
            return buyOrder(asset, 10);
        }

        int ror = asset.rateOfReturn();
        if (ror >= 5 && asset.getQuantity() / 4 > 0) {
            return MessageBuilder.sell(asset, asset.getQuantity() / 4, asset.getPrice());
        } else if (ror <= -5) {
            int quantity = (asset.getQuantity() / 4 > 0) ? asset.getQuantity() / 4 : 10;
            return buyOrder(asset, quantity);
        } else {
            return null;
        }
    }

    private Message buyOrder(final TradedAsset asset, int quantity) {
        int cost = asset.getPrice() * quantity;
        if (cost <= 0 || cost > balance)
            return null;
        balance -= cost;
        return MessageBuilder.buy(asset, quantity, asset.getPrice());
    }
}
