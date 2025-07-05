package net.aldisti.router;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;

public class Server {
    private static final Logger log = LoggerFactory.getLogger(Server.class);

    private final ServerSocket socket;
    private final Dispatcher dispatcher;

    public Server(int port) throws IOException {
        this.socket = new ServerSocket(port);
        this.dispatcher = new Dispatcher();
        log.info("Server started on port {}", port);
    }

    public void run() {
        while (!socket.isClosed()) {
            Client client;
            try {
                client = new Client(socket.accept());
            } catch (IOException e) {
                log.error("Couldn't accept client connection", e);
                continue;
            }
            dispatcher.register(client);
            Thread.ofVirtual().start(client);
        }
    }
}
