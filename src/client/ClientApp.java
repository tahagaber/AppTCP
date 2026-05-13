package client;

import util.IDUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Level 1: Basic Client (Console)
 * Simply connects, sends a message, receives the first broadcast/response, and shuts down safely.
 */
public class ClientApp {
    public static void main(String[] args) {
        int port = IDUtils.generatePort("2220550");
        String host = "localhost";
        
        try (Socket socket = new Socket(host, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            
            System.out.println("Connected to server on port " + port);
            
            // Providing an initial username mapping so the Level 2/3 Server functions happily
            out.println("ConsoleClient");
            
            // Sending the desired Basic Client message
            String message = "Hello from the Basic Console Client!";
            out.println(message);
            System.out.println("Sent: " + message);
            
            // Receiving the server response
            String response;
            while ((response = in.readLine()) != null) {
                // Ignore background USERLIST broadcasts for this strict Basic Client implementation
                if (!response.startsWith("USERLIST:")) {
                    System.out.println("Server Response Received: " + response);
                    break;
                }
            }
            
            System.out.println("Basic Client operation completed. Closing connection...");
        } catch (Exception e) {
            System.err.println("Client exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
