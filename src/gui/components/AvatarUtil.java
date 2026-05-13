package gui.components;

import javafx.scene.control.Label;

public class AvatarUtil {
    public static Label createAvatar(String name) {
        String initials = name.length() >= 2 ? name.substring(0, 2).toUpperCase() : name.toUpperCase();
        Label avatar = new Label(initials);
        avatar.getStyleClass().add("avatar");
        avatar.setMinSize(36, 36);
        avatar.setMaxSize(36, 36);
        return avatar;
    }
}
