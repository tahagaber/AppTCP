package gui;

import gui.components.ChatArea;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
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
import java.util.Arrays;
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
        HBox root = new HBox();
        root.getStyleClass().add("root");

        // 1. Sidebar (Combined Header + Search + List + Footer)
        VBox userSidebar = createUserSidebar();
        
        // 2. Main Chat Area
        chatArea = new ChatArea(this::sendMessage);
        HBox.setHgrow(chatArea, Priority.ALWAYS);

        root.getChildren().addAll(userSidebar, chatArea);

        Scene scene = new Scene(root, 1280, 800);

        File cssFile = new File("src/gui/style.css");
        if (cssFile.exists()) {
            scene.getStylesheets().add(cssFile.toURI().toString());
        }

        stage.setTitle("TCP");
        stage.setScene(scene);
        stage.setOnCloseRequest(e -> disconnect());
        stage.show();
    }

    private VBox createUserSidebar() {
        VBox sidebar = new VBox();
        sidebar.getStyleClass().add("user-sidebar");
        sidebar.setPrefWidth(380); // Matching sidebar-width from mockup

        // Sidebar Header
        HBox header = new HBox(12);
        header.getStyleClass().add("sidebar-header");
        header.setAlignment(Pos.CENTER_LEFT);
        
        Circle profileImg = new Circle(20, Color.web("#eceef0"));
        profileImg.setStroke(Color.web("#06b6d4"));
        
        VBox titleGroup = new VBox(-2);
        Label mainTitle = new Label("TCP");
        mainTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: -app-primary;");
        Label subTitle = new Label("Pro Terminal");
        subTitle.setStyle("-fx-font-size: 11px; -fx-text-fill: -app-on-surface-variant; -fx-font-weight: bold;");
        titleGroup.getChildren().addAll(mainTitle, subTitle);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        HBox headerActions = new HBox(5);
        String[] icons = {"🌱", "🗨", "👥", "⋮"};
        for (String icon : icons) {
            Button btn = new Button(icon);
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: -app-on-surface-variant; -fx-font-size: 16px;");
            headerActions.getChildren().add(btn);
        }
        
        header.getChildren().addAll(profileImg, titleGroup, spacer, headerActions);

        // Search Bar Area
        VBox searchArea = new VBox();
        searchArea.setPadding(new Insets(12));
        HBox searchBox = new HBox(10);
        searchBox.getStyleClass().add("search-box");
        searchBox.setAlignment(Pos.CENTER_LEFT);
        Label searchIcon = new Label("🔍");
        TextField searchField = new TextField();
        searchField.setPromptText("Search or start new chat");
        searchField.getStyleClass().add("input-field-custom");
        HBox.setHgrow(searchField, Priority.ALWAYS);
        searchBox.getChildren().addAll(searchIcon, searchField);
        searchArea.getChildren().add(searchBox);

        // User List (Scrollable)
        userListContainer = new VBox(0);
        userListContainer.getChildren().addAll(
            createUserItem("Core_Dev_Team", "Pushing TCP optimizations...", true),
            createUserItem("Sec_Ops_Lead", "Firewall rules updated.", false),
            createUserItem("Database_Node_01", "Replication lag detected.", false),
            createUserItem("Archives_Network", "Sent a screenshot", false)
        );
        
        ScrollPane scrollPane = new ScrollPane(userListContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("chat-scroll-pane");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        // Sidebar Footer
        HBox footer = new HBox();
        footer.getStyleClass().add("sidebar-footer");
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(10));
        
        String[] footerLabels = {"Chats", "Archive", "Settings"};
        String[] footerIcons = {"🗨", "📦", "⚙"};
        for (int i = 0; i < footerLabels.length; i++) {
            VBox btn = createFooterButton(footerIcons[i], footerLabels[i], i == 0);
            HBox.setHgrow(btn, Priority.ALWAYS);
            footer.getChildren().add(btn);
        }

        sidebar.getChildren().addAll(header, searchArea, scrollPane, footer);
        return sidebar;
    }

    private VBox createFooterButton(String icon, String text, boolean active) {
        VBox box = new VBox(2);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(5));
        Label lIcon = new Label(icon);
        lIcon.setStyle("-fx-font-size: 20px;");
        Label lText = new Label(text.toUpperCase());
        lText.setStyle("-fx-font-size: 10px; -fx-font-weight: bold;");
        
        if (active) {
            lIcon.setStyle(lIcon.getStyle() + "-fx-text-fill: -app-primary;");
            lText.setStyle(lText.getStyle() + "-fx-text-fill: -app-primary;");
        } else {
            lIcon.setStyle(lIcon.getStyle() + "-fx-text-fill: -app-on-surface-variant;");
            lText.setStyle(lText.getStyle() + "-fx-text-fill: -app-on-surface-variant;");
        }
        
        box.getChildren().addAll(lIcon, lText);
        return box;
    }

    private HBox createUserItem(String name, String status, boolean active) {
        HBox item = new HBox(12);
        item.getStyleClass().add("user-item");
        if (active) item.getStyleClass().add("user-item-active");
        item.setAlignment(Pos.CENTER_LEFT);

        StackPane avatarStack = new StackPane();
        Circle avatar = new Circle(18, Color.web("#eceef0"));
        avatar.setStroke(Color.web("#bcc9cd", 0.5));
        Circle statusDot = new Circle(5, Color.web("#1bbd85"));
        statusDot.setStroke(Color.web("#ffffff"));
        statusDot.setStrokeWidth(2);
        StackPane.setAlignment(statusDot, Pos.BOTTOM_RIGHT);
        avatarStack.getChildren().addAll(avatar, statusDot);

        VBox details = new VBox(2);
        HBox topRow = new HBox();
        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-text-fill: -app-on-surface; -fx-font-weight: bold; -fx-font-size: 13px;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label timeLabel = new Label("14:20");
        timeLabel.setStyle("-fx-text-fill: -app-primary; -fx-font-weight: bold; -fx-font-size: 11px;");
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
