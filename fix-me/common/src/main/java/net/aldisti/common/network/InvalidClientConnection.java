package net.aldisti.common.network;

public class InvalidClientConnection extends RuntimeException {
    public InvalidClientConnection() {
        super("Client connection error");
    }

    public InvalidClientConnection(String message) {
        super(message);
    }
}
