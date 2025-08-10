package net.aldisti.market;

import net.aldisti.common.finance.Asset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class MarketContext {
    private static final Logger log = LoggerFactory.getLogger(MarketContext.class);
    private static final Random RANDOM = new Random();

    private static final MarketContext INSTANCE = new MarketContext();

    private final Map<String, Asset> assets;

    private MarketContext() {
        this.assets = new HashMap<>();
        StartingAssets.ASSETS.forEach(a -> this.assets.put(a.getId(), a));
    }

    public static MarketContext getInstance() {
        return INSTANCE;
    }

    public boolean buyAsset(String id, int quantity, int price) {
        Asset asset = assets.get(id);
        if (asset == null)
            return false;
        if (asset.getQuantity() < quantity)
            return false;
        if (asset.getPrice() > price)
            return false;
        log.info("Broker bought {} of {} for {}", quantity, id, price);
        // decrease available quantity
        asset.setQuantity(asset.getQuantity() - quantity);
        // update the price to the latest buy
        asset.setPrice(price);
        return true;
    }

    public boolean sellAsset(String id, int quantity, int price) {
        Asset asset = assets.get(id);
        if (asset == null)
            return false;
        if (asset.getPrice() < price)
            return false;
        log.info("Broker sold {} of {} for {}", quantity, id, price);
        // increase the quantity
        asset.setQuantity(asset.getQuantity() + quantity);
        return true;
    }

    /**
     * Updates an asset and returns it.
     *
     * @param id The asset id.
     * @return The updated asset.
     */
    public final Asset updateAsset(String id) {
        Asset asset = assets.get(id);
        if (asset != null && asset.getPrice() != 0)
            updateAsset(asset);
        return asset;
    }

    /**
     * @return All the assets' ids.
     */
    public Set<String> getAssetIds() {
        return assets.keySet();
    }

    private void updateAsset(Asset asset) {
        int percentage = RANDOM.nextInt(0, 50);
        boolean sign = RANDOM.nextBoolean();
        int price = asset.getPrice();
        // calculate the difference to add or subtract
        double diff = Math.ceil((double) price * ((double) percentage / 100) * asset.getInstrument().volatility);
        // update the price
        asset.setPrice(price + (int) ((sign) ? diff : -diff));
        log.info("Updated asset {} from {} to {}", asset.getId(), price, asset.getPrice());
    }
}
