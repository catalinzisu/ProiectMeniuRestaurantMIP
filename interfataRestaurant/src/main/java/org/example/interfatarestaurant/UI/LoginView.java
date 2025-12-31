package org.example.interfatarestaurant.UI;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginView {
    private TextField userField;
    private PasswordField passField;
    private Button btnLogin;
    private Button btnGuest;

    public void show(Stage stage) {
        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);

        userField = new TextField();
        userField.setPromptText("Username");

        passField = new PasswordField();
        passField.setPromptText("Password");

        btnLogin = new Button("Login Staff");
        btnGuest = new Button("Intra ca Vizitator (Guest)");
        btnGuest.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

        layout.getChildren().addAll(new Label("Restaurant Login"), userField, passField, btnLogin, new Separator(), btnGuest);
        stage.setScene(new Scene(layout, 300, 400));
        stage.show();
    }

    public String getUsername() { return userField.getText(); }
    public String getPassword() { return passField.getText(); }
    public Button getBtnLogin() { return btnLogin; }
    public Button getBtnGuest() { return btnGuest; }
}