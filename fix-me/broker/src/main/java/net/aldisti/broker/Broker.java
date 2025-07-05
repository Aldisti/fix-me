package net.aldisti.broker;

import lombok.Getter;
import net.aldisti.common.fix.Message;
import org.slf4j.Logger;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class Broker {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(Broker.class);

    @Getter
    private final BlockingQueue<Message> queue;

    public Broker() {
        this.queue = new ArrayBlockingQueue<>(1024);
    }

    public void run() {
        Message msg;
        while (true) {
            try {
                msg = queue.poll(5, TimeUnit.MILLISECONDS);
                if (msg != null)
                    handle(msg);
            } catch (InterruptedException e) {
                log.info("Broker interrupted");
                break;
            }
            // TODO: do something else in meantime
        }
    }

    public void handle(Message msg) {
        // TODO: do something with the message
    }
}
