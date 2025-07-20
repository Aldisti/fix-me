package net.aldisti.market;

import net.aldisti.common.network.Client;
import net.aldisti.common.network.SharedQueueClient;
import sun.misc.Signal;

public class App {
    public static void main(String[] args) {
        Market market = (args.length == 1) ? new Market(Integer.parseInt(args[0])) : new Market(5000);

        Client client = new SharedQueueClient("localhost", 5001, market.getQueue());
        Thread.ofVirtual().start(client);

        Signal.handle(new Signal("INT"), (signal) -> market.stop());

        market.run(client);
    }
}
