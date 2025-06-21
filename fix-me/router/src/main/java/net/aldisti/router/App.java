package net.aldisti.router;

import java.io.*;

public class App {

    public static final int PORT = 8042;

    public static void main(String[] args) {
        System.out.println("Router is starting!");

        try {
            Server server = new Server(PORT);
            server.run();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Router is stopping!");
    }
}
