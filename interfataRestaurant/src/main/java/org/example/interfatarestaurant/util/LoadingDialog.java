package org.example.interfatarestaurant.util;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Loading Dialog - afișează o fereastră cu spinner și mesaj
 * Folosit pentru feedback vizual în timp de operații lungi
 */
public class LoadingDialog {
    private Stage stage;
    private Label messageLabel;

    public LoadingDialog(String title, String message) {
        stage = new Stage();
        stage.setTitle(title);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setResizable(false);
        stage.setWidth(300);
        stage.setHeight(150);

        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setPrefWidth(50);
        progressIndicator.setPrefHeight(50);

        messageLabel = new Label(message);
        messageLabel.setStyle("-fx-font-size: 14;");

        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPrefWidth(300);
        root.setPrefHeight(150);
        root.setStyle("-fx-background-color: #f5f5f5;");
        root.getChildren().addAll(progressIndicator, messageLabel);

        Scene scene = new Scene(root);
        stage.setScene(scene);
    }

    /**
     * Afișează dialog-ul (non-blocking)
     */
    public void show() {
        stage.show();
    }

    /**
     * Afișează dialog-ul și blochează alte ferestre
     */
    public void showAndWait() {
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    /**
     * Închide dialog-ul
     */
    public void close() {
        stage.close();
    }

    /**
     * Actualizează mesajul afișat
     */
    public void updateMessage(String newMessage) {
        messageLabel.setText(newMessage);
    }
}

