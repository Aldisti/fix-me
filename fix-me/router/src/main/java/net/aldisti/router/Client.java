package net.aldisti.router;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class Client extends Thread {
    private static final Logger log = LoggerFactory.getLogger(Client.class);

    private final Socket socket;
    private final int clientId;
    private final PrintWriter writer;
    private final BufferedReader reader;
    private Dispatcher dispatcher = null;

    public Client(Socket socket, int clientId) throws IOException {
        this.socket = socket;
        this.clientId = clientId;
        this.writer = new PrintWriter(this.socket.getOutputStream(), true);
        this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));

        log.info("Client#{}: created", clientId);
    }

    @Override
    public void run() {
        sendMessage("You are Client#" + clientId);
        try {
            routine();
        } catch (IOException e) {
            log.error("Error while running client {} routine", clientId, e);
        } finally {
            log.info("Closing connection with client {}", clientId);
            close();
        }
    }

    private void routine() throws IOException {
        log.info("Client#{}: starting routine", clientId);
        boolean running = true;
        while (running) {
            String line = reader.readLine();
            if (line == null) break;
            log.info("Client#{}: {}", clientId, line);

            List<String> commands = Arrays.stream(line.trim().split("\\s+", 3)).toList();
            if (commands.isEmpty()) continue;

            switch (commands.getFirst().toUpperCase()) {
                case "SEND":
                    sendCommand(commands);
                    break;
                case "EXIT":
                    running = false;
                    break;
                default:
                    sendMessage("You said: " + line);
            }
        }
    }

    private void sendCommand(List<String> commands) {
        int receiverId;
        try {
            receiverId = Integer.parseInt(commands.get(1));
        } catch (NumberFormatException e) {
            sendMessage("Invalid client id: '" + commands.get(1) + "'");
            return;
        }
        dispatcher.sendTo(receiverId, String.format(
                "Client#%d says: %s", clientId, commands.getLast()
        ));
    }

    public void sendMessage(String msg) {
        writer.println(msg);
    }

    public void close() {
        try {
            reader.close();
            writer.close();
            socket.close();
        } catch (IOException e) {
            log.error("Error while closing connection with client {}", clientId, e);
        }
        dispatcher.unregister(clientId);
    }

    public void setDispatcher(Dispatcher dispatcher) {
        if (this.dispatcher == null)
            this.dispatcher = dispatcher;
    }

    public int getClientId() {
        return this.clientId;
    }
}
