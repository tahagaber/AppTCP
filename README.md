# TCP - Socket Application 🚀

This project is a high-performance, real-time Chat Application built using **Java**, **JavaFX**, and **TCP/IP Sockets**. It features a premium "Pro Terminal" aesthetic and robust multithreaded architecture.

---

## 1. Key Features & Improvements 🌟
*   **TCP Branding:** Completely rebranded as a technical "TCP Pro Terminal" interface.
*   **Premium Light UI:** A state-of-the-art interface using **Geist** and **Hanken Grotesk** fonts, precision shadows, and smooth transitions.
*   **Intelligent Port Mapping:** Automatic port calculation based on Academic ID rules:
    - Uses last 5 digits of ID.
    - Caps at 65535 (uses last 4 if exceeded).
    - Ensures port > 1024 (adds 10000 if needed).
*   **Real-time Node Tracking:** A dynamic sidebar that updates automatically as users join or leave the network.
*   **Broadcast Engine:** Instant message distribution to all active nodes with integrated timestamps.

---

## 2. Server Architecture: How it Works 🧠

The server (`ServerApp.java`) acts as the central orchestrator for the entire network. Here is the step-by-step mechanism:

### A. Initialization
1.  **Port Calculation:** The server starts by calculating its listening port using the `IDUtils` utility.
2.  **Socket Opening:** It opens a `ServerSocket` on that port and enters an infinite `while(true)` loop.

### B. Connection Handling (The Multithreaded Core)
1.  **Acceptance:** When a client attempts to connect, `serverSocket.accept()` creates a unique `Socket` for that session.
2.  **Thread Dispatching:** To prevent the server from "freezing" while talking to one client, it wraps the connection in a `ClientHandler` and launches it in a **new Thread**.
3.  **Client Registry:** Every active connection is stored in a `synchronizedList` to ensure thread-safety during broadcast operations.

### C. Communication Protocol
1.  **Handshake:** Upon connection, the client sends its **Username** as the first piece of data.
2.  **Message Routing:** The `ClientHandler` listens for incoming text. Any message received is passed back to `ServerApp.broadcast()`.
3.  **Broadcasting:** The server iterates through all registered clients and writes the message to their output streams simultaneously.
4.  **Special Commands:** The server sends messages prefixed with `USERLIST:` to signal the GUI to update the sidebar list.

### D. Stability & Cleanup
-   **Graceful Exit:** If a client disconnects (closes the app or sends "EXIT"), the server catches the exception, removes the user from the list, and broadcasts a "Left the chat" notification to others.

---

## 3. How to Run 🏃‍♂️
1.  **Start the Server:** Run `ServerApp.java`. Look for the "Starting TCP Multithreaded Server" log.
2.  **Launch Clients:** Run `Main.java` (you can run it multiple times to simulate multiple users).
3.  **Interact:** Enter a message in the "TCP Pro Terminal" and see it broadcasted across all instances.

---
**Developed as a high-fidelity implementation of Network Programming and Modern UI/UX patterns.**
