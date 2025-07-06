package net.aldisti.common.network;

import net.aldisti.common.fix.Message;

import java.util.Queue;

/**
 * The SharedQueueClient is a type of {@link Client} that implements the {@link #receive(Message)}
 * method moving all the incoming messages into a queue.
 * By doing this, the queue can be shared with another {@link Thread} and
 * the messages can be handled asynchronously.
 */
public final class SharedQueueClient extends Client {
    private final Queue<Message> queue;

    public SharedQueueClient(String host, int port, Queue<Message> queue) throws InvalidClientConnection {
        super(host, port);
        this.queue = queue;
    }

    @Override
    protected void receive(Message msg) {
        if (!queue.offer(msg))
            log.info("Couldn't add message to queue: {}", msg);
    }
}
