package net.aldisti.market;

import net.aldisti.common.network.Client;
import net.aldisti.common.network.InvalidClientConnection;
import net.aldisti.common.network.SharedQueueClient;
import net.aldisti.market.db.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Signal;

public class App {
    private static final Logger log = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        Market market = createMarket(args);

        Database.create();
        Client client;
        try {
            client = new SharedQueueClient("localhost", 5001, market.getQueue());
        } catch (InvalidClientConnection e) {
            log.error("Client connection failed", e);
            Database.close();
            return;
        }
        Thread.ofVirtual().start(client);

        Signal.handle(new Signal("INT"), (signal) -> {
            market.stop();
            Database.close();
        });

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
