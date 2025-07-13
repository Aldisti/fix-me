package net.aldisti.router;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;

public class Server extends Thread {
    private static final Logger log = LoggerFactory.getLogger(Server.class);

    private final ServerSocket socket;
    private final Dispatcher dispatcher;
    private final ClientType type;

    public Server(ClientType type) throws IOException {
        this.socket = new ServerSocket(type.port);
        this.type = type;
        this.dispatcher = Dispatcher.create();
        log.info("{} server started on port {}", type.name(), type.port);
    }

    @Override
    public void run() {
        while (!socket.isClosed()) {
            Client client;
            try {
                client = new Client(socket.accept(), type);
            } catch (IOException e) {
                if (!e.getMessage().contains("interrupt"))
                    log.error("Couldn't accept client connection", e);
                continue;
            }
            dispatcher.register(client);
            Thread.ofVirtual().start(client);
        }
        log.info("{} server stopped", type.name());
    }
}
