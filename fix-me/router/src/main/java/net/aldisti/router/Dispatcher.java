package net.aldisti.router;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class Dispatcher {
    private static final Logger log = LoggerFactory.getLogger(Dispatcher.class);

    private static final Dispatcher instance = new Dispatcher();

    /**
     * A map of all registered clients.
     */
    private final Map<Integer, Client> clients;

    private Dispatcher() {
        this.clients = new HashMap<>();
    }

    public static Dispatcher create() {
        return instance;
    }

    /**
     * Registers the client on the dispatcher and calls {@link Client#setDispatcher}.
     *
     * @implNote This method is idempotent.
     * @param client A new client.
     * @see #unregister(int)
     */
    public synchronized void register(Client client) {
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
     * @see #register(Client)
     */
    public synchronized void unregister(int clientId) {
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
     * @param raw The message to send.
     */
    public synchronized void sendTo(int clientId, String raw) {
        if (clients.containsKey(clientId))
            clients.get(clientId).sendMessage(raw);
    }

    /**
     * Sends a message to all Brokers registered to the dispatcher.
     *
     * @param raw The message to send.
     */
    public synchronized void sendAll(String raw) {
        clients.keySet().forEach(id -> {
            Client client = clients.get(id);
            if (client != null && client.getType() == ClientType.BROKER)
                clients.get(id).sendMessage(raw);
        });
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

    /**
     * Returns the {@link ClientType} of a specific client
     * if it exists, {@code null} otherwise.
     *
     * @param clientId A client id.
     * @return The type of the client.
     */
    public synchronized ClientType getClientType(Integer clientId) {
        Client client = clients.get(clientId);
        return (client != null) ? client.getType() : null;
    }
}
