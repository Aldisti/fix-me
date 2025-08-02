package net.aldisti.common.fix.constants;

public enum Instrument {
    STOCK(0.6f),
    ETF(0.4f),
    BOND(0.15f),
    CRYPTO(0.9f);

    public final float volatility;

    Instrument(float volatility) {
        this.volatility = volatility;
    }
}
