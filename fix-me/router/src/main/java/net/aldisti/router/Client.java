package net.aldisti.router;

import lombok.Getter;
import net.aldisti.common.fix.Engine;
import net.aldisti.common.fix.Message;
import net.aldisti.common.fix.constants.MsgType;
import net.aldisti.common.providers.IdProvider;
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
    private final Socket socket;
    private final Queue<String> msgQueue;

    private Dispatcher dispatcher = null;

    public Client(Socket socket) throws IOException {
        this.socket = socket;
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
        } catch (InterruptedException e) {
            log.error("Error: interruption exception", e);
        } finally {
            log.info("Closing connection with client {}", clientId);
            close();
        }
    }

    private void routine(BufferedReader reader, PrintWriter writer) throws IOException, InterruptedException {
        String raw;
        while (socket.isConnected() && !socket.isInputShutdown() && !socket.isOutputShutdown()) {

            if (!msgQueue.isEmpty())
                writer.println(msgQueue.remove());

            raw = reader.readLine();
            if (raw != null) {
                Message msg = Engine.deserialize(raw);
                if (!clientId.toString().equals(msg.getSenderId())) {
                    log.error("Client {} sent message with wrong senderId {}", clientId, msg.getSenderId());
                    msg.setType(MsgType.ERROR.getValue());
                    writer.println(Engine.serialize(msg));
                }
                // forward message
                int targetId = Integer.parseInt(msg.getTargetId());
                if (dispatcher.exists(targetId))
                    dispatcher.sendTo(targetId, raw);
            }
            // TODO: maybe wait some millis?
        }
    }

    /**
     * Appends the message to a queue and it's sent to the client ASAP.
     *
     * @param msg A message to send.
     */
    public void sendMessage(String msg) {
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
