package net.aldisti.router;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class App {
    private static final Logger log = LoggerFactory.getLogger(App.class);

    public static final int PORT = 8042;

    public static void main(String[] args) {
        log.info("Router is starting");

        try {
            Server server = new Server(PORT);
            server.run();
        } catch (IOException e) {
            e.printStackTrace();
        }

        log.info("Router is shutting down");
    }
}
