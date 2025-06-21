package net.aldisti.router;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class Client extends Thread {

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

        log("Connection created");
    }

    @Override
    public void run() {
        sendMessage("You are Client#" + clientId);
        try {
            routine();
        } catch (IOException e) {
            System.out.println("Error while communicating with client #" + clientId);
            System.out.println(e.getMessage());
        } finally {
            System.out.println("Closing connection with client #" + clientId);
            close();
        }
    }

    private void routine() throws IOException {
        log("Starting routine");
        boolean running = true;
        while (running) {
            String line = reader.readLine();
            if (line == null) break;
            log(line);

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

    public void log(String msg) {
        System.out.println("Client#" + clientId + ": " + msg);
    }

    public void close() {
        try {
            reader.close();
            writer.close();
            socket.close();
        } catch (IOException e) {
            System.out.println("Error closing connection with client #" + clientId);
            System.out.println(e.getMessage());
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
