package net.aldisti.router;

public enum ClientType {
    BROKER(5000),
    MARKET(5001);

    public final int port;

    ClientType(int port) {
        this.port = port;
    }
}
