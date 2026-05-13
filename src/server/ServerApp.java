package server;

import util.IDUtils;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

/**
 * Level 1/2: Multithreaded Server App
 * Upgraded Server that runs infinitely, handling concurrent connections via threads.
 */
public class ServerApp {
    // Thread-safe list to store connections
    private static final List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());
    
    public static void main(String[] args) {
        // Use Port utility as instructed
        int port = IDUtils.generatePort("2220550");
        System.out.println("Starting TCP Multithreaded Server on port " + port + "...");
        
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is running and waiting for clients...");
            
            while (true) {
                // Accept client connection -> loop blocks until a client connects
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());
                
                // Dispatch client logic onto a separate thread
                ClientHandler handler = new ClientHandler(clientSocket);
                clients.add(handler);
                
                new Thread(handler).start();
            }
        } catch (IOException e) {
            System.err.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Sends a message to ALL currently connected clients
     */
    public static void broadcast(String message) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                client.sendMessage(message);
            }
        }
    }
    
    /**
     * Removes client dynamically and immediately refreshes remaining users
     */
    public static void removeClient(ClientHandler client) {
        clients.remove(client);
        broadcastUserList(); // Synchronize users list upon disconnection
    }
    
    /**
     * Crafts and sends the 'USERLIST:...' message dynamically keeping the clients aligned
     */
    public static void broadcastUserList() {
        StringBuilder userList = new StringBuilder("USERLIST:");
        synchronized (clients) {
            for (int i = 0; i < clients.size(); i++) {
                userList.append(clients.get(i).getUsername());
                if (i < clients.size() - 1) {
                    userList.append(",");
                }
            }
        }
        broadcast(userList.toString());
    }
}
