package net.aldisti.router;

import net.aldisti.common.fix.Engine;
import net.aldisti.common.fix.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class Dispatcher {
    private static final Logger log = LoggerFactory.getLogger(Dispatcher.class);

    /**
     * A map of all registered clients.
     */
    private final Map<Integer, Client> clients;

    public Dispatcher() {
        this.clients = new HashMap<>();
    }

    /**
     * Registers the client on the dispatcher and calls {@link Client#setDispatcher}.
     *
     * @implNote This method is idempotent.
     * @param client A new client.
     */
    public void register(Client client) {
        int clientId = client.getClientId();
        if (clients.containsKey(clientId)) {
            log.error("Trying to register already registered client {}", clientId);
            return;
        }
        log.info("Registering client {}", clientId);
        clients.put(clientId, client);
        client.setDispatcher(this);
    }

    /**
     * Unregisters a client from the dispatcher.
     * <br>
     * This method does not call {@link Client#close()}.
     *
     * @implNote This method is idempotent.
     * @param clientId A client id.
     */
    public void unregister(int clientId) {
        if (!clients.containsKey(clientId)) {
            log.error("Trying to unregister already unregistered client {}", clientId);
            return;
        }
        log.info("Unregistering client {}", clientId);
        clients.remove(clientId);
    }

    /**
     * Sends a message to another client.
     * <br>
     * A new line is appended to the message.
     *
     * @param clientId A client id.
     * @param msg The message to send.
     */
    public synchronized void sendTo(int clientId, String msg) {
        if (clients.containsKey(clientId))
            clients.get(clientId).sendMessage(msg);
    }

    /**
     * Serializes a message and sends it to another client.
     * <br>
     * A new line is appended to the message.
     *
     * @param clientId A client id.
     * @param msg The message to send.
     */
    public synchronized void sendTo(int clientId, Message msg) {
        if (exists(clientId))
            clients.get(clientId).sendMessage(Engine.serialize(msg));
    }

    /**
     * Checks whether a client exists or not.
     * <br>
     * Usually this is done before using {@link #sendTo}.
     *
     * @param clientId A client id.
     * @return {@code true} if the client is registered, {@code false} otherwise.
     */
    public synchronized boolean exists(Integer clientId) {
        if (clientId == null)
            return false;
        return clients.containsKey(clientId);
    }

    private void sendAll(String msg) {
        clients.keySet().forEach(id -> {
            if (clients.containsKey(id))
                clients.get(id).sendMessage(msg);
        });
    }

    private void sendAll(Message msg) {
        String raw = Engine.serialize(msg);
        clients.keySet().forEach(id -> {
            if (clients.containsKey(id))
                clients.get(id).sendMessage(raw);
        });
    }
}
