package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Level 2: Multithreaded Server (ClientHandler)
 * Handles individual client connections, reading messages, and executing broadcast updates.
 */
public class ClientHandler implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String username;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public String getUsername() {
        return username;
    }

    /**
     * Send a message to this specific client's output stream
     */
    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Read username as the very first input from the client
            username = in.readLine();
            if (username == null || username.trim().isEmpty()) {
                username = "Guest_" + (System.currentTimeMillis() % 1000);
            }
            
            System.out.println("User joined: " + username);
            
            // Broadcast the updated online users list
            ServerApp.broadcastUserList();
            
            // Broadcast join message to everyone
            ServerApp.broadcast(formatMessage("Server", username + " has joined the chat!"));

            String message;
            // Loop until client disconnects
            while ((message = in.readLine()) != null) {
                if (message.equalsIgnoreCase("EXIT")) {
                    break;
                }
                
                System.out.println("Received from " + username + ": " + message);
                
                // Broadcast the received message to all connected clients
                ServerApp.broadcast(formatMessage(username, message));
            }
            
        } catch (IOException e) {
            System.err.println("Connection error for user " + username + ": " + e.getMessage());
        } finally {
            closeConnections();
        }
    }

    /**
     * Adds timestamp to the chat messages
     */
    private String formatMessage(String sender, String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        return "[" + timestamp + "] " + sender + ": " + message;
    }

    /**
     * Safely cleanup connections and alert other clients when a user disconnects
     */
    private void closeConnections() {
        // Remove client from the list
        ServerApp.removeClient(this);
        
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.err.println("Error closing connection for handler: " + e.getMessage());
        }
        
        if (username != null) {
            ServerApp.broadcast(formatMessage("Server", username + " has left the chat."));
        }
        System.out.println("User disconnected: " + username);
    }
}
