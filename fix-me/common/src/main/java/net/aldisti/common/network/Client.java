package net.aldisti.common.network;

import net.aldisti.common.fix.Engine;
import net.aldisti.common.fix.InvalidFixMessage;
import net.aldisti.common.fix.Message;
import net.aldisti.common.fix.constants.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class Client extends Thread {
    protected static final Logger log = LoggerFactory.getLogger(Client.class);

    private final Socket socket;
    protected final Queue<String> queue;
    private String clientId;

    public Client(String host, int port) throws InvalidClientConnection {
        try {
            socket = new Socket(host, port);
        } catch (IOException e) {
            throw new InvalidClientConnection("Could not connect to " + host + ":" + port);
        }
        queue = new ConcurrentLinkedQueue<>();
    }

    @Override
    public void run() {
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
                PrintWriter writer = new PrintWriter(this.socket.getOutputStream(), true);
        ) {
            clientId = reader.readLine();
            log.info("Connected as client {}", clientId);
            setName("Client#" + clientId);
            routine(reader, writer);
        } catch (IOException ioe) {
            log.error("Error: cannot send or receive messages", ioe);
        } finally {
            log.info("Closing connection");
            close();
        }
    }

    private void routine(BufferedReader reader, PrintWriter writer) throws IOException {
        String raw;
        while (socket.isConnected() && !socket.isOutputShutdown() && !socket.isInputShutdown()) {
            if (!queue.isEmpty())
                writer.println(queue.poll());
            // read incoming message, if there is one
            if (!reader.ready() || (raw = reader.readLine()) == null)
                continue;
            // deserialize message, validate it and use it
            receive(deserialize(raw));
        }
    }

    /**
     * This is method is called every time a new message is received.
     *
     * @param msg A received message.
     */
    protected abstract void receive(Message msg);

    /**
     * This method pushes a message into a queue, and
     * then it's sent to the router ASAP.
     *
     * @param msg A message to send.
     */
    public void send(Message msg) {
        msg.add(Tag.SENDER_ID, getClientId());
        queue.add(Engine.serialize(msg));
    }

    private Message deserialize(String raw) {
        try { // handle deserialization errors
            return Engine.deserialize(raw);
        } catch (InvalidFixMessage e) {
            log.error("Received invalid fix message in {}", getName(), e);
            log.info("Message: {}", raw);
            return null;
        }
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            log.error("Error while closing connection", e);
        }
        log.info("Connection closed");
    }

    public String getClientId() {
        return this.clientId;
    }
}
