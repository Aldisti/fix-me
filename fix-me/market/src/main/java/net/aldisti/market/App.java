package net.aldisti.market;

import net.aldisti.common.network.Client;
import net.aldisti.common.network.SharedQueueClient;
import sun.misc.Signal;

public class App {
    public static void main(String[] args) {
        Market market = createMarket(args);

        Client client = new SharedQueueClient("localhost", 5001, market.getQueue());
        Thread.ofVirtual().start(client);

        Signal.handle(new Signal("INT"), (signal) -> market.stop());

        market.run(client);
    }

    private static Market createMarket(String[] args) {

        if (args.length == 0)
            return new Market(2500);

        if (args.length > 2) {
            System.out.println("Invalid number of arguments");
            System.exit(1);
        }

        if (!args[0].matches("\\d{1,6}")) {
            System.out.println("Invalid market interval");
            System.exit(2);
        }

        int interval = Integer.parseInt(args[0]);
        if (args.length == 2)
            return new Market(args[1], interval);
        return new Market(interval);
    }
}
