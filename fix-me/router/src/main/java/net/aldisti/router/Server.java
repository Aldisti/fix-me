package net.aldisti.router;

import java.io.IOException;
import java.net.ServerSocket;

public class Server {

    private final ServerSocket socket;
    private final Dispatcher dispatcher;

    public Server(int port) throws IOException {
        this.socket = new ServerSocket(port);
        this.dispatcher = new Dispatcher();
        System.out.println("Server listening at localhost:" + port);
    }

    public void run() {
        int availableId = 1;

        while (true) {
            Client client;
            try {
                client = new Client(socket.accept(), availableId);
                availableId++;
            } catch (IOException e) {
                System.out.println("Couldn't accept client connection");
                System.out.println(e.getMessage());
                continue;
            }
            dispatcher.register(client);
            client.start();
        }
    }
}
