package org.example.interfatarestaurant;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.interfatarestaurant.controller.RestaurantController;
import org.example.interfatarestaurant.util.AsyncTaskManager;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) {
        new RestaurantController(stage);
    }

    @Override
    public void stop() {
        // Shutdown graceful al thread pool când aplicația se închide
        AsyncTaskManager.shutdown();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}