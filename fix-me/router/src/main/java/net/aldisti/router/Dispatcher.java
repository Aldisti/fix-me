package net.aldisti.router;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class Dispatcher {
    private static final Logger log = LoggerFactory.getLogger(Dispatcher.class);

    private final Map<Integer, Client> clients;

    public Dispatcher() {
        this.clients = new HashMap<>();
    }

    public void register(Client client) {
        int clientId = client.getClientId();
        assert !clients.containsKey(clientId);
        log.info("Registering client {}", clientId);
        sendAll(String.format("Dispatcher#0: Client#%d has registered", clientId));
        clients.put(clientId, client);
        client.setDispatcher(this);
    }

    public void unregister(int clientId) {
        assert clients.containsKey(clientId);
        log.info("Unregistering client {}", clientId);
        clients.remove(clientId);
        sendAll(String.format("Dispatcher#0: Client#%d has unregistered", clientId));
    }

    public synchronized void sendTo(int clientId, String msg) {
        Client client = clients.get(clientId);
        if (client != null)
            clients.get(clientId).sendMessage(msg);
    }

    private void sendAll(String msg) {
        clients.keySet().forEach(id -> {
            if (clients.containsKey(id))
                clients.get(id).sendMessage(msg);
        });
    }
}
