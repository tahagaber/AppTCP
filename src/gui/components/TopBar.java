package gui.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class TopBar extends HBox {
    
    public TopBar(String username) {
        super(20);
        setAlignment(Pos.CENTER_LEFT);
        setPadding(new Insets(15, 25, 15, 25));
        getStyleClass().add("header-bar");
        
        Label appLogoTxt = new Label("Network Programming");
        appLogoTxt.setStyle("-fx-font-weight: 800; -fx-font-size: 20px; -fx-text-fill: -app-primary;");
        
        Region topSpacer1 = new Region();
        HBox.setHgrow(topSpacer1, Priority.ALWAYS);
        
        Label myTopAvatar = AvatarUtil.createAvatar(username);
        myTopAvatar.setPrefSize(40, 40);
        myTopAvatar.getStyleClass().add("avatar");
        myTopAvatar.setStyle("-fx-font-size: 14px; -fx-background-radius: 8;");
        
        getChildren().addAll(appLogoTxt, topSpacer1, myTopAvatar);
    }

}
