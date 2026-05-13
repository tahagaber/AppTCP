# DevChat v1.0 - Advanced TCP Socket Application 🚀

This project is a high-performance, real-time Chat Application built using **Java**, **JavaFX**, and **TCP/IP Sockets**. It features a premium "DevChat v1.0" dark-themed interface and a robust multithreaded architecture.

---

## 1. Key Features & Improvements 🌟
*   **DevChat v1.0 Design System:** A sophisticated dark-themed UI inspired by modern terminal IDEs and communication platforms.
*   **Sophisticated Layout:**
    *   **Side Rail:** Quick navigation for Chats, Contacts, and Settings.
    *   **Messages Sidebar:** Real-time tracking of active threads and users.
    *   **Chat Engine:** Smooth message bubbles with integrated timestamps and "Seen" indicators.
*   **Intelligent Port Automation:** Automatic port calculation based on Academic ID (e.g., `2220550` -> Port `22550`).
*   **Real-time Node Tracking:** Dynamic sidebar updates as users join or leave the network.
*   **High-Fidelity Broadcasting:** Instant message distribution to all active nodes.

---

## 2. Technical Architecture 🧠

### A. The Multithreaded Server (`ServerApp.java`)
The server acts as the central orchestrator, managing simultaneous connections without blocking.
1.  **Thread-Per-Client:** Each connecting user is assigned a dedicated `ClientHandler` thread.
2.  **Thread-Safe Registry:** Uses `Collections.synchronizedList` to manage active sockets safely across multiple threads.
3.  **Broadcast Protocol:** 
    *   Receives data from one client thread.
    *   Distributes it to all other active threads.
    *   Handles `USERLIST:` commands to synchronize UI states across the network.

### B. The Modern GUI (`ChatClientGUI.java` & `ChatArea.java`)
The interface is built to handle high-frequency data streams smoothly.
1.  **Background Networking:** A dedicated listener thread handles incoming socket data to keep the UI responsive.
2.  **Thread Integration:** Uses `Platform.runLater()` to safely push network data into the JavaFX UI thread, preventing concurrency crashes.
3.  **CSS Design System:** Fully customized `style.css` using modern CSS variables (Design Tokens) for consistent colors, spacing, and animations.

---

## 3. Communication Logic 📡
1.  **Handshake:** Upon connection, the client sends its unique username to the server.
2.  **State Sync:** The server immediately broadcasts an updated `USERLIST:` to all clients.
3.  **Messaging:** 
    *   `[Time] Sender: Message` format for standard communication.
    *   Automatic parsing of timestamps and sender names for custom bubble rendering.
4.  **Resilience:** Automatic detection of connection drops with "Transmission Link Terminated" feedback in the GUI.

---

## 4. How to Run 🏃‍♂️
1.  **Start the Server:** Execute `ServerApp.java`. It will open a port based on the utility rules.
2.  **Launch Clients:** Execute `Main.java`. You can launch multiple instances to simulate a full chat network.
3.  **Chat:** Enter messages in the input bar. Watch as they populate across all connected instances in real-time.

---

## 5. Technology Stack 🛠
- **Core:** Java 17+
- **UI Framework:** JavaFX (Graphics, Layouts, CSS)
- **Networking:** Java Sockets (TCP/IP)
- **Concurrency:** Java Threads & Runnables
- **Styling:** Vanilla CSS 3 with Custom Properties

---
**Developed as a high-fidelity implementation of Network Programming and Modern UI/UX patterns.**
