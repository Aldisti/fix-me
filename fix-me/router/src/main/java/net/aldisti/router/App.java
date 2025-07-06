package net.aldisti.router;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class App {
    private static final Logger log = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws IOException {
        log.info("Router is starting...");
        List<Thread> servers = new ArrayList<>();

        for (ClientType type : ClientType.values()) {
            servers.add(Thread.ofVirtual().start(new Server(type)));
        }

        for (Thread server : servers) {
            try {
                server.join();
            } catch (InterruptedException e) {
                log.warn("Server {} interrupted while joining", server.getName(), e);
            }
        }
        log.info("Router is shutting down...");
    }
}
