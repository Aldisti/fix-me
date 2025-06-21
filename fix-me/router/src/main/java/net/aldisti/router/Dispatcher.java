package net.aldisti.router;

import java.util.HashMap;
import java.util.Map;

public class Dispatcher {

    private final Map<Integer, Client> clients;

    public Dispatcher() {
        this.clients = new HashMap<>();
    }

    public void register(Client client) {
        int clientId = client.getClientId();
        assert !clients.containsKey(clientId);

        sendAll(String.format("Dispatcher#0: Client#%d has registered", clientId));
        clients.put(clientId, client);
        client.setDispatcher(this);
    }

    public void unregister(int clientId) {
        assert clients.containsKey(clientId);

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
