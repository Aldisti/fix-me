package net.aldisti.broker;

import net.aldisti.common.fix.Message;
import net.aldisti.common.network.Client;
import net.aldisti.common.network.InvalidClientConnection;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;

public class BrokerClient extends Client {
    private final Queue<Message> queue;

    public BrokerClient(String host, int port, BlockingQueue<Message> queue) throws InvalidClientConnection {
        super(host, port);
        this.queue = queue;
    }

    @Override
    protected void receive(Message msg) {
        if (!queue.offer(msg))
            log.info("Couldn't add message to queue: {}", msg);
    }
}
