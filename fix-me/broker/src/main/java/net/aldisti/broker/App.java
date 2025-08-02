package net.aldisti.broker;

import net.aldisti.common.network.Client;
import net.aldisti.common.network.SharedQueueClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Signal;

public class App {
    private static final Logger log = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        Broker broker = new Broker();

        Client client = new SharedQueueClient("127.0.0.1", 5000, broker.getQueue());
        Thread.ofVirtual().start(client);

        Signal.handle(new Signal("INT"), (signal) -> broker.stop());

        broker.run(client);
    }
}
