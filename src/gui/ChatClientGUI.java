package gui;

import gui.components.ChatArea;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ScrollPane;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import util.IDUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatClientGUI extends Application {

    // --- Components ---
    private ChatArea chatArea;

    // --- Networking Components ---
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String username;

    // --- Sidebar Components ---
    private Label onlineBadge;
    private VBox userListContainer;

    @Override
    public void start(Stage primaryStage) {
        // Auto-generate username (e.g., "User_57")
        this.username = "User_" + (int) (Math.random() * 1000);
        initGUI(primaryStage);
        connectToServer();
    }

    private void initGUI(Stage stage) {
        VBox root = new VBox();
        root.getStyleClass().add("root");

        // 1. Top App Bar
        HBox topBar = createTopAppBar();
        
        // 2. Main Body Container
        HBox body = new HBox();
        VBox.setVgrow(body, Priority.ALWAYS);

        // 2a. Side Rail
        VBox sideRail = createSideRail();
        
        // 2b. User Sidebar
        VBox userSidebar = createUserSidebar();
        
        // 2c. Main Chat Area
        chatArea = new ChatArea(this::sendMessage);
        HBox.setHgrow(chatArea, Priority.ALWAYS);

        body.getChildren().addAll(sideRail, userSidebar, chatArea);
        root.getChildren().addAll(topBar, body);

        Scene scene = new Scene(root, 1280, 800);

        File cssFile = new File("src/gui/style.css");
        if (cssFile.exists()) {
            scene.getStylesheets().add(cssFile.toURI().toString());
        }

        stage.setTitle("DevChat v1.0");
        stage.setScene(scene);
        stage.setOnCloseRequest(e -> disconnect());
        stage.show();
    }

    private HBox createTopAppBar() {
        HBox topBar = new HBox(40);
        topBar.getStyleClass().add("top-app-bar");
        topBar.setPrefHeight(64);
        topBar.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("DevChat v1.0");
        title.getStyleClass().add("app-title");

        HBox nav = new HBox(24);
        nav.setAlignment(Pos.CENTER);
        Label link1 = new Label("Direct");
        Label link2 = new Label("Channels");
        Label link3 = new Label("Nodes");
        link1.getStyleClass().add("nav-link");
        link2.getStyleClass().addAll("nav-link", "nav-link-active");
        link3.getStyleClass().add("nav-link");
        nav.getChildren().addAll(link1, link2, link3);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox searchBox = new HBox(10);
        searchBox.getStyleClass().add("search-box");
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setPrefWidth(256);
        TextField searchField = new TextField();
        searchField.setPromptText("Search data points...");
        searchField.setStyle("-fx-background-color: transparent; -fx-text-fill: -app-on-surface;");
        Label searchIcon = new Label("🔍");
        searchIcon.setStyle("-fx-text-fill: -app-on-surface-variant;");
        searchBox.getChildren().addAll(searchField, searchIcon);

        HBox actions = new HBox(15);
        actions.setAlignment(Pos.CENTER);
        String[] icons = {"💻", "📡", "⋮"};
        for (String icon : icons) {
            Label l = new Label(icon);
            l.setStyle("-fx-text-fill: -app-on-surface-variant; -fx-font-size: 18px; -fx-cursor: hand;");
            actions.getChildren().add(l);
        }

        Circle avatar = new Circle(16, Color.web("#2d3449"));
        avatar.setStroke(Color.web("#3d494c"));

        topBar.getChildren().addAll(title, nav, spacer, searchBox, actions, avatar);
        return topBar;
    }

    private VBox createSideRail() {
        VBox rail = new VBox(32);
        rail.getStyleClass().add("side-rail");
        rail.setPrefWidth(80);
        rail.setAlignment(Pos.TOP_CENTER);

        ImageView profileImg = new ImageView(); // Placeholder for mockup image
        profileImg.setFitWidth(40);
        profileImg.setFitHeight(40);
        Circle clip = new Circle(20, 20, 20);
        profileImg.setClip(clip);

        VBox navGroup = new VBox(20);
        navGroup.setAlignment(Pos.CENTER);
        
        VBox btnChat = createRailButton("🗨", "Chats", true);
        VBox btnContacts = createRailButton("👥", "Contacts", false);
        VBox btnArchive = createRailButton("📦", "Archive", false);
        VBox btnSettings = createRailButton("⚙", "Settings", false);
        
        navGroup.getChildren().addAll(btnChat, btnContacts, btnArchive, btnSettings);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox btnSupport = createRailButton("❓", "Support", false);

        rail.getChildren().addAll(profileImg, navGroup, spacer, btnSupport);
        return rail;
    }

    private VBox createRailButton(String icon, String label, boolean active) {
        VBox box = new VBox(4);
        box.setAlignment(Pos.CENTER);
        box.getStyleClass().add("rail-button");
        if (active) box.getStyleClass().add("rail-button-active");
        
        Label lIcon = new Label(icon);
        lIcon.setStyle("-fx-font-size: 20px;");
        Label lText = new Label(label);
        lText.setStyle("-fx-font-size: 10px;");
        
        box.getChildren().addAll(lIcon, lText);
        return box;
    }

    private VBox createUserSidebar() {
        VBox sidebar = new VBox();
        sidebar.getStyleClass().add("user-sidebar");
        sidebar.setPrefWidth(320);

        HBox header = new HBox();
        header.getStyleClass().add("sidebar-header");
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Messages");
        title.getStyleClass().add("sidebar-title");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label editIcon = new Label("📝");
        editIcon.setStyle("-fx-text-fill: -app-primary; -fx-font-size: 18px;");
        header.getChildren().addAll(title, spacer, editIcon);

        VBox searchArea = new VBox();
        searchArea.getStyleClass().add("search-box-container");
        HBox searchBox = new HBox(10);
        searchBox.getStyleClass().add("search-box");
        searchBox.setAlignment(Pos.CENTER_LEFT);
        TextField searchField = new TextField();
        searchField.setPromptText("Search threads...");
        searchField.setStyle("-fx-background-color: transparent; -fx-text-fill: -app-on-surface;");
        Label filterIcon = new Label("≡");
        filterIcon.setStyle("-fx-text-fill: -app-on-surface-variant;");
        searchBox.getChildren().addAll(searchField, filterIcon);
        searchArea.getChildren().add(searchBox);

        userListContainer = new VBox(0);
        userListContainer.getChildren().addAll(
            createUserItem("Core_Dev_Team", "Pushing the latest API node...", true),
            createUserItem("Sec_Ops_Lead", "Unauthorized access detected...", false),
            createUserItem("Data_Viz_Specialist", "The quarterly metrics look solid.", false)
        );
        
        ScrollPane scrollPane = new ScrollPane(userListContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("chat-scroll-pane");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        sidebar.getChildren().addAll(header, searchArea, scrollPane);
        return sidebar;
    }


    private HBox createUserItem(String name, String status, boolean active) {
        HBox item = new HBox(12);
        item.getStyleClass().add("user-item");
        if (active) item.getStyleClass().add("user-item-active");
        item.setAlignment(Pos.CENTER_LEFT);

        StackPane avatarStack = new StackPane();
        Circle avatar = new Circle(20, Color.web("#171f33"));
        avatar.setStroke(Color.web("#3d494c"));
        
        if (active || name.equals("Core_Dev_Team")) {
            Circle statusDot = new Circle(5, Color.web("#4cd7f6"));
            statusDot.setStroke(Color.web("#060e20"));
            statusDot.setStrokeWidth(2);
            StackPane.setAlignment(statusDot, Pos.BOTTOM_RIGHT);
            avatarStack.getChildren().add(statusDot);
        }
        avatarStack.getChildren().add(0, avatar);

        VBox details = new VBox(2);
        HBox topRow = new HBox();
        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-text-fill: -app-on-surface; -fx-font-weight: 500; -fx-font-size: 14px;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label timeLabel = new Label(active ? "12:45" : "10:12");
        timeLabel.setStyle("-fx-text-fill: " + (active ? "-app-primary" : "-app-on-surface-variant") + "; -fx-font-size: 10px;");
        topRow.getChildren().addAll(nameLabel, spacer, timeLabel);

        Label statusLabel = new Label(status);
        statusLabel.setStyle("-fx-text-fill: -app-on-surface-variant; -fx-font-size: 11px;");
        details.getChildren().addAll(topRow, statusLabel);
        HBox.setHgrow(details, Priority.ALWAYS);

        item.getChildren().addAll(avatarStack, details);
        return item;
    }

    private void connectToServer() {
        int port = IDUtils.generatePort("2220550");
        String host = "localhost";

        try {
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println(username);

            Thread listenerThread = new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        handleIncomingMessage(message);
                    }
                } catch (IOException e) {
                    Platform.runLater(() -> chatArea.addServerMessage("Transmission Link Terminated."));
                }
            });
            listenerThread.setDaemon(true);
            listenerThread.start();

        } catch (IOException e) {
            Platform.runLater(() -> chatArea.addServerMessage("Quantum Link Failure at port " + port));
        }
    }

    private void handleIncomingMessage(String message) {
        if (message.startsWith("USERLIST:")) {
            parseAndDisplayUsers(message);
        } else {
            parseAndDisplayChat(message);
        }
    }

    private void parseAndDisplayUsers(String message) {
        String userListString = message.substring(9);
        String[] users = userListString.isEmpty() ? new String[0] : userListString.split(",");
        Platform.runLater(() -> {
            if (onlineBadge != null) onlineBadge.setText(users.length + " ONLINE");
            if (userListContainer != null) {
                userListContainer.getChildren().clear();
                for (String user : users) {
                    userListContainer.getChildren().add(createUserItem(user, "Active", false));
                }
            }
        });
    }

    private void parseAndDisplayChat(String message) {
        Platform.runLater(() -> {
            Pattern pattern = Pattern.compile("^\\[(.*?)\\] (.*?): (.*)$");
            Matcher matcher = pattern.matcher(message);

            if (matcher.matches()) {
                String rawTime = matcher.group(1);
                String sender = matcher.group(2);
                String content = matcher.group(3);

                String formattedTime = rawTime;
                try {
                    java.time.LocalTime t = java.time.LocalTime.parse(rawTime,
                            java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
                    formattedTime = t.format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a"));
                } catch (Exception ignored) {
                }

                if (sender.equals("Server")) {
                    chatArea.addServerMessage(content);
                } else if (sender.equals(username)) {
                    chatArea.addMyMessage(formattedTime, content);
                } else {
                    chatArea.addOtherMessage(sender, formattedTime, content);
                }
            } else {
                chatArea.addServerMessage(message);
            }
        });
    }

    private void sendMessage() {
        String message = chatArea.getInputText().trim();
        if (!message.isEmpty()) {
            if (out != null) {
                out.println(message);
                chatArea.clearInputText();
            } else {
                chatArea.addServerMessage("Offline. Pulse failed.");
            }
        }
    }

    private void disconnect() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
