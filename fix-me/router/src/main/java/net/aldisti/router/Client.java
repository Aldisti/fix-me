package net.aldisti.router;

import lombok.Getter;
import net.aldisti.common.fix.Engine;
import net.aldisti.common.fix.InvalidFixMessage;
import net.aldisti.common.fix.Message;
import net.aldisti.common.fix.constants.MsgType;
import net.aldisti.common.providers.IdProvider;
import net.aldisti.router.fix.MessageBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Client extends Thread {
    private static final Logger log = LoggerFactory.getLogger(Client.class);

    @Getter
    private final Integer clientId;
    @Getter
    private final ClientType type;
    private final Socket socket;
    private final Queue<String> msgQueue;

    private Dispatcher dispatcher = null;

    public Client(Socket socket, ClientType type) throws IOException {
        this.socket = socket;
        this.type = type;
        this.clientId = IdProvider.generate();
        this.msgQueue = new ConcurrentLinkedQueue<>();
        log.info("Client {} initialized", clientId);
    }

    @Override
    public void run() {
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
                PrintWriter writer = new PrintWriter(this.socket.getOutputStream(), true);
            ) {
            writer.println(clientId);
            routine(reader, writer);
        } catch (IOException ioe) {
            log.error("Error: cannot send or receive messages from client {}", clientId, ioe);
        } finally {
            log.info("Closing connection with client {}", clientId);
            close();
        }
    }

    private void routine(BufferedReader reader, PrintWriter writer) throws IOException {
        String raw;
        Message msg;
        while (socket.isConnected() && !socket.isInputShutdown() && !socket.isOutputShutdown()) {

            if (!msgQueue.isEmpty())
                writer.println(msgQueue.remove());

            // read incoming message
            if ((raw = reader.readLine()) == null)
                continue;

            // deserialize message and validate it
            if ((msg = deserialize(raw)) == null) {
                writer.println(Engine.serialize(MessageBuilder.invalidMessage(clientId)));
                continue;
            }

            handle(msg, raw);
        }
    }

    private void handle(Message msg, final String raw) {
        if (!clientId.toString().equals(msg.getSenderId())) { // validate sender id
            log.warn("Client {} sent message with wrong senderId {}", clientId, msg.getSenderId());
            sendMessage(Engine.serialize(MessageBuilder.invalidSender(msg, clientId)));
            return;
        }
        if (type == ClientType.MARKET && msg.getTargetId() == null) {
            log.info("Client {} sent message to all brokers", clientId);
            dispatcher.sendAll(raw);
            return;
        } else if (msg.getTargetId() == null) {
            log.warn("Client {} sent message with no target", clientId);
            sendMessage(Engine.serialize(MessageBuilder.invalidTarget(msg, clientId)));
            return;
        }

        int targetId = Integer.parseInt(msg.getTargetId());

        if (!dispatcher.exists(targetId)) {
            log.warn("Client {} is trying to send message to non-existing targetId {}", clientId, targetId);
            sendMessage(Engine.serialize(MessageBuilder.invalidTarget(msg, clientId)));
        } else if (type == ClientType.BROKER && dispatcher.getClientType(targetId) == ClientType.BROKER) {
            log.warn("Client {} is trying to send message to a {}", clientId, ClientType.BROKER.name());
            sendMessage(Engine.serialize(MessageBuilder.invalidMessage(clientId)));
        } else { // forward message
            dispatcher.sendTo(targetId, raw);
        }
    }

    private Message deserialize(String raw) {
        try {
            return Engine.deserialize(raw);
        } catch (InvalidFixMessage e) { // handle deserialization errors
            log.error("Client {} sent invalid message", clientId, e);
            log.info("Message: {}", raw);
            return null;
        }
    }

    /**
     * Appends the message to a queue so that it is sent to the client ASAP.
     *
     * @param msg A message to send.
     */
    public synchronized void sendMessage(String msg) {
        msgQueue.offer(msg);
    }

    /**
     * Closes the client socket and unregisters the client from the {@link Dispatcher}
     */
    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            log.error("Error while closing connection with client {}", clientId, e);
        }
        if (dispatcher != null)
            dispatcher.unregister(clientId);
        log.info("Client {} closed with {} non-sent messages", clientId, msgQueue.size());
    }

    /**
     * Set the {@link Dispatcher} instance that manages this client.
     * <br>
     * Works only the first time.
     *
     * @param dispatcher A dispatcher instance.
     */
    public void setDispatcher(Dispatcher dispatcher) {
        if (this.dispatcher == null)
            this.dispatcher = dispatcher;
    }
}
