package net.aldisti.router;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client extends Thread {

    private final Socket socket;
    private final PrintWriter writer;
    private final BufferedReader reader;
    private final int port;

    public Client(Socket socket) throws IOException {
        this.socket = socket;
        this.port = socket.getPort();
        this.writer = new PrintWriter(this.socket.getOutputStream(), true);
        this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));

        System.out.println("Client #" + port + " connected");
    }

    @Override
    public void run() {
        String line;
        try {
            do {
                line = reader.readLine();
                if ("exit".equalsIgnoreCase(line))
                    break;
                System.out.println("Client#" + port + ": " + line);
                writer.println(line);
            } while (line != null);
        } catch (IOException e) {
            System.out.println("Error while communicating with client #" + port);
            System.out.println(e.getMessage());
        } finally {
            System.out.println("Closing connection with client #" + port);
            close();
        }
    }

    public void close() {
        try {
            reader.close();
            writer.close();
            socket.close();
        } catch (IOException e) {
            System.out.println("Error closing connection with client #" + port);
            System.out.println(e.getMessage());
        }
    }
}
