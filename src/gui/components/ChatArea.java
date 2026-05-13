package gui.components;

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
    private Label activeMembersLabel;
    private int messageCount = 0;

    public ChatArea(Runnable onSendAction) {
        getStyleClass().add("chat-area-container");

        // 1. Chat Header
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("chat-header");
        header.setPrefHeight(64);

        Circle avatar = new Circle(20, Color.web("#eceef0"));
        avatar.setStroke(Color.web("#06b6d4"));

        VBox titleBox = new VBox(-2);
        Label channelName = new Label("Core_Dev_Team");
        channelName.getStyleClass().add("chat-channel-name");
        
        HBox statusBox = new HBox(5);
        statusBox.setAlignment(Pos.CENTER_LEFT);
        Circle statusDot = new Circle(4, Color.web("#1bbd85"));
        Label statusLabel = new Label("ONLINE");
        statusLabel.setStyle("-fx-text-fill: -app-tertiary; -fx-font-size: 10px; -fx-font-weight: bold;");
        statusBox.getChildren().addAll(statusDot, statusLabel);
        
        titleBox.getChildren().addAll(channelName, statusBox);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox rightIcons = new HBox(15);
        rightIcons.setAlignment(Pos.CENTER);
        String[] icons = {"📹", "📞", "|", "🔍", "⋮"};
        for (String icon : icons) {
            Label l = new Label(icon);
            l.setStyle("-fx-text-fill: -app-primary; -fx-font-size: 18px; -fx-cursor: hand;");
            if (icon.equals("|")) l.setStyle("-fx-text-fill: -app-outline-variant; -fx-font-size: 18px;");
            rightIcons.getChildren().add(l);
        }

        header.getChildren().addAll(avatar, titleBox, spacer, rightIcons);
        setTop(header);

        // 2. Message List (Center)
        chatBox = new VBox(20);
        chatBox.getStyleClass().add("message-list");
        chatBox.setStyle("-fx-background-color: transparent;");

        chatScrollPane = new ScrollPane(chatBox);
        chatScrollPane.setFitToWidth(true);
        chatScrollPane.getStyleClass().add("chat-scroll-pane");
        chatScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        chatBox.heightProperty().addListener((observable, oldValue, newValue) -> chatScrollPane.setVvalue(1.0));

        setCenter(chatScrollPane);

        // 3. Input Area (Bottom)
        HBox bottomWrapper = new HBox(15);
        bottomWrapper.getStyleClass().add("chat-input-footer");
        bottomWrapper.setPadding(new Insets(15, 25, 15, 25));
        bottomWrapper.setAlignment(Pos.CENTER);

        HBox toolGroup = new HBox(10);
        toolGroup.setAlignment(Pos.CENTER);
        Label addIcon = new Label("+");
        addIcon.setStyle("-fx-text-fill: -app-on-surface-variant; -fx-font-size: 24px; -fx-cursor: hand;");
        Label moodIcon = new Label("😊");
        moodIcon.setStyle("-fx-text-fill: -app-on-surface-variant; -fx-font-size: 20px; -fx-cursor: hand;");
        toolGroup.getChildren().addAll(addIcon, moodIcon);

        inputField = new TextField();
        inputField.setPromptText("Type a message or paste code snippet...");
        inputField.getStyleClass().add("input-field-custom");
        HBox.setHgrow(inputField, Priority.ALWAYS);
        
        inputField.setStyle("-fx-background-color: -app-surface-container-lowest; -fx-border-color: -app-outline-variant; -fx-padding: 10 15; -fx-background-radius: 4; -fx-border-radius: 4;");

        Button sendBtn = new Button("➤");
        sendBtn.getStyleClass().add("send-button");
        sendBtn.setPrefSize(48, 48);
        sendBtn.setStyle("-fx-background-color: -app-primary; -fx-text-fill: white; -fx-font-size: 18px; -fx-background-radius: 4;");

        bottomWrapper.getChildren().addAll(toolGroup, inputField, sendBtn);
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
        VBox messageCard = MessageBubble.create("YOU", time, message, true);
        chatBox.getChildren().add(messageCard);
        messageCount++;
    }

    public void addOtherMessage(String sender, String time, String message) {
        VBox messageCard = MessageBubble.create(sender, time, message, false);
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
