package net.aldisti.market;

import net.aldisti.common.finance.Asset;

import java.util.List;

import static net.aldisti.common.fix.constants.Instrument.*;

public class StartingAssets {
    public static final List<Asset> ASSETS = List.of(
            Asset.builder()
                    .id("APPL").name("APPLE")
                    .instrument(STOCK)
                    .quantity(1000000).price(100).build(),
            Asset.builder()
                    .id("RKLB").name("Rocket Lab")
                    .instrument(STOCK)
                    .quantity(500000).price(35).build(),
            Asset.builder()
                    .id("SP5").name("S&P500")
                    .instrument(ETF)
                    .quantity(10000000).price(15).build(),
            Asset.builder()
                    .id("US30").name("USA 2030")
                    .instrument(BOND)
                    .quantity(1000000).price(1000).build(),
            Asset.builder()
                    .id("BTC").name("Bitcoin")
                    .instrument(CRYPTO)
                    .quantity(50000).price(35000).build()
    );
}
