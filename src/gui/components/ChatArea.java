package gui.components;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class ChatArea extends BorderPane {

    private VBox chatBox;
    private ScrollPane chatScrollPane;
    private TextField inputField;
    private int messageCount = 0;

    public ChatArea(Runnable onSendAction) {
        getStyleClass().add("chat-area-container");

        // 1. Chat Header
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("chat-header");
        header.setPrefHeight(64);

        StackPane avatarStack = new StackPane();
        Circle avatar = new Circle(20, Color.web("#2d3449"));
        avatar.setStroke(Color.web("#3d494c"));
        Circle statusDot = new Circle(5, Color.web("#4cd7f6"));
        statusDot.setStroke(Color.web("#0b1326"));
        statusDot.setStrokeWidth(2);
        StackPane.setAlignment(statusDot, Pos.BOTTOM_RIGHT);
        avatarStack.getChildren().addAll(avatar, statusDot);

        VBox titleBox = new VBox(-2);
        Label channelName = new Label("Core_Dev_Team");
        channelName.setStyle("-fx-text-fill: -app-on-surface; -fx-font-size: 16px; -fx-font-weight: bold;");
        Label statusLabel = new Label("ONLINE");
        statusLabel.setStyle("-fx-text-fill: -app-primary; -fx-font-size: 10px; -fx-font-weight: bold; -fx-letter-spacing: 0.1em;");
        titleBox.getChildren().addAll(channelName, statusLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox rightIcons = new HBox(15);
        rightIcons.setAlignment(Pos.CENTER);
        String[] icons = {"📹", "📞", "|", "🔍", "⋯"};
        for (String icon : icons) {
            Label l = new Label(icon);
            l.setStyle("-fx-text-fill: -app-on-surface-variant; -fx-font-size: 18px; -fx-cursor: hand;");
            if (icon.equals("|")) l.setStyle("-fx-text-fill: -app-outline-variant; -fx-font-size: 18px;");
            rightIcons.getChildren().add(l);
        }

        header.getChildren().addAll(avatarStack, titleBox, spacer, rightIcons);
        setTop(header);

        // 2. Message List (Center)
        chatBox = new VBox(20);
        chatBox.getStyleClass().add("message-list");
        chatBox.setPadding(new Insets(24));
        
        chatScrollPane = new ScrollPane(chatBox);
        chatScrollPane.setFitToWidth(true);
        chatScrollPane.getStyleClass().add("chat-scroll-pane");
        VBox.setVgrow(chatScrollPane, Priority.ALWAYS);
        chatBox.heightProperty().addListener((observable, oldValue, newValue) -> chatScrollPane.setVvalue(1.0));
        setCenter(chatScrollPane);

        // 3. Input Area (Bottom)
        HBox bottomWrapper = new HBox();
        bottomWrapper.getStyleClass().add("chat-input-footer");
        bottomWrapper.setAlignment(Pos.CENTER);

        HBox inputContainer = new HBox(10);
        inputContainer.getStyleClass().add("input-container");
        HBox.setHgrow(inputContainer, Priority.ALWAYS);
        inputContainer.setAlignment(Pos.CENTER);

        Label addBtn = new Label("⊕");
        Label attachBtn = new Label("📎");
        addBtn.setStyle("-fx-text-fill: -app-on-surface-variant; -fx-font-size: 20px; -fx-cursor: hand;");
        attachBtn.setStyle("-fx-text-fill: -app-on-surface-variant; -fx-font-size: 20px; -fx-cursor: hand;");

        inputField = new TextField();
        inputField.setPromptText("Type a secure message...");
        inputField.setStyle("-fx-background-color: transparent; -fx-text-fill: -app-on-surface;");
        HBox.setHgrow(inputField, Priority.ALWAYS);

        Label moodBtn = new Label("😊");
        moodBtn.setStyle("-fx-text-fill: -app-on-surface-variant; -fx-font-size: 20px; -fx-cursor: hand;");

        Button sendBtn = new Button("➤");
        sendBtn.getStyleClass().add("send-button");
        sendBtn.setPrefSize(40, 40);

        inputContainer.getChildren().addAll(addBtn, attachBtn, inputField, moodBtn, sendBtn);
        bottomWrapper.getChildren().add(inputContainer);
        setBottom(bottomWrapper);

        // Action Handlers
        sendBtn.setOnAction(e -> onSendAction.run());
        inputField.setOnAction(e -> onSendAction.run());
    }

    public void setActiveMembersCount(int count) {
        // This is now handled in the Sidebar in ChatClientGUI
    }

    public String getInputText() {
        return inputField.getText();
    }

    public void clearInputText() {
        inputField.clear();
    }

    public void addMyMessage(String time, String message) {
        HBox messageCard = MessageBubble.create("YOU", time, message, true);
        chatBox.getChildren().add(messageCard);
        messageCount++;
    }

    public void addOtherMessage(String sender, String time, String message) {
        HBox messageCard = MessageBubble.create(sender, time, message, false);
        chatBox.getChildren().add(messageCard);
        messageCount++;
    }

    public void addServerMessage(String message) {
        HBox container = new HBox();
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(10, 0, 10, 0));

        Label content = new Label(message.toUpperCase());
        content.getStyleClass().add("message-server");

        container.getChildren().add(content);
        chatBox.getChildren().add(container);
    }
}
