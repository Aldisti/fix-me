package net.aldisti.broker;

import lombok.extern.slf4j.Slf4j;
import net.aldisti.common.network.SharedQueueClient;
import net.aldisti.common.network.Client;

@Slf4j
public class App {
    public static void main(String[] args) {

        Broker broker = new Broker();

        Client client = new SharedQueueClient("127.0.0.1", 5000, broker.getQueue());
        log.info("Client {} created", client.getClientId());
    }
}
