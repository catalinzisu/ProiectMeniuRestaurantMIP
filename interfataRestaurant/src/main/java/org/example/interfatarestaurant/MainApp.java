package org.example.interfatarestaurant;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.interfatarestaurant.UI.LoginView;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) {
        // Pornim direct cu Login-ul
        new LoginView().show(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}