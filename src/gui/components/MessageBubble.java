package gui.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class MessageBubble {

    public static HBox create(String sender, String time, String content, boolean isMe) {
        HBox wrapper = new HBox();
        wrapper.setAlignment(isMe ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        wrapper.setPadding(new Insets(5, 0, 5, 0));

        VBox bubble = new VBox(4);
        bubble.setMaxWidth(500);
        bubble.getStyleClass().add(isMe ? "message-bubble-outgoing" : "message-bubble-incoming");

        if (!isMe) {
            Label nameLabel = new Label(sender);
            nameLabel.setStyle("-fx-text-fill: -app-primary; -fx-font-weight: bold; -fx-font-size: 11px;");
            bubble.getChildren().add(nameLabel);
        }

        Label contentLabel = new Label(content);
        contentLabel.setWrapText(true);
        contentLabel.getStyleClass().add(isMe ? "message-content-me" : "message-content");
        
        HBox meta = new HBox(5);
        meta.setAlignment(isMe ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        Label timeLabel = new Label(time);
        timeLabel.setStyle("-fx-text-fill: -app-on-surface-variant; -fx-font-size: 10px;");
        meta.getChildren().add(timeLabel);
        
        if (isMe) {
            Label check = new Label("✓✓");
            check.setStyle("-fx-text-fill: #003640; -fx-font-size: 10px;");
            meta.getChildren().add(check);
        }

        bubble.getChildren().addAll(contentLabel, meta);
        wrapper.getChildren().add(bubble);
        return wrapper;
    }

    public static HBox createSystemMessage(String content) {
        HBox wrapper = new HBox();
        wrapper.setAlignment(Pos.CENTER);
        wrapper.setPadding(new Insets(10, 0, 10, 0));
        
        Label l = new Label(content.toUpperCase());
        l.setStyle("-fx-background-color: #222a3d; -fx-text-fill: -app-on-surface-variant; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 4 12; -fx-background-radius: 20; -fx-border-color: -app-outline-variant; -fx-border-radius: 20; -fx-letter-spacing: 0.1em;");
        
        wrapper.getChildren().add(l);
        return wrapper;
    }
}
