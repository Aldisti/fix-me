package net.aldisti.router;

import java.io.IOException;
import java.net.ServerSocket;

public class Server {

    private ServerSocket socket;

    public Server(int port) {
        try {
            socket = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("Cannot open server on port " + port);
            System.exit(3);
        }
        System.out.println("Server listening at localhost:" + port);
    }

    public void run() throws IOException {
        while (true)
            new Client(socket.accept()).start();
    }
}
