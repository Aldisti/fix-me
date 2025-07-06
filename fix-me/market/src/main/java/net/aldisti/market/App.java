package net.aldisti.market;

import net.aldisti.common.network.Client;
import net.aldisti.common.network.SharedQueueClient;

public class App {
    public static void main(String[] args) {
        System.out.println("Market is starting!");

        Market market = new Market();

        Client client = new SharedQueueClient("localhost", 5001, market.getQueue());
        Thread.ofVirtual().start(client);

        market.setClient(client);
        market.run();
    }
}
