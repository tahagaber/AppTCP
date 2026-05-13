package gui.components;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class MessageBubble {
    public static VBox create(String name, String time, String message, boolean isMe) {
        VBox container = new VBox(5);
        container.getStyleClass().add("message-item");
        container.setAlignment(isMe ? Pos.TOP_RIGHT : Pos.TOP_LEFT);

        HBox horizontalLayout = new HBox(12);
        horizontalLayout.setAlignment(isMe ? Pos.TOP_RIGHT : Pos.TOP_LEFT);

        // Avatar
        Circle avatar = new Circle(18);
        avatar.setFill(isMe ? Color.web("#eceef0") : Color.web("#06b6d4"));
        avatar.setStroke(Color.web("#bcc9cd", 0.5));

        VBox contentBox = new VBox(4);
        contentBox.setMaxWidth(600);

        // Header: Name and Time
        HBox header = new HBox(8);
        header.setAlignment(Pos.BOTTOM_LEFT);
        
        Label nameLabel = new Label(isMe ? "You" : name);
        nameLabel.getStyleClass().add("message-sender");
        if (isMe) nameLabel.setStyle("-fx-text-fill: -app-primary;");
        
        Label timeLabel = new Label(time);
        timeLabel.getStyleClass().add("message-time");
        
        header.getChildren().addAll(nameLabel, timeLabel);

        // Message Bubble
        Label content = new Label(message);
        content.setWrapText(true);
        content.getStyleClass().add("message-content");
        if (isMe) content.getStyleClass().add("message-content-me");

        VBox bubble = new VBox(content);
        bubble.getStyleClass().add(isMe ? "message-bubble-outgoing" : "message-bubble-incoming");

        contentBox.getChildren().addAll(header, bubble);

        if (isMe) {
            horizontalLayout.getChildren().addAll(contentBox, avatar);
        } else {
            horizontalLayout.getChildren().addAll(avatar, contentBox);
        }

        container.getChildren().add(horizontalLayout);
        return container;
    }
}
