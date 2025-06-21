package net.aldisti.router;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class App {

    public static final int PORT = 8042;

    public static void main(String[] args) {
        System.out.println("Router is starting!");

        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Router is listening on port " + PORT);

            Socket client = server.accept();
            System.out.println("Client connection accepted!");

            var writer = new PrintWriter(client.getOutputStream(), true);
            var reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String line;

            do {
                line = reader.readLine();
                if ("exit".equalsIgnoreCase(line))
                    break;
                System.out.println("Client says: " + line);
                writer.println(line);
            } while (line != null);

            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Router is stopping!");
    }
}
