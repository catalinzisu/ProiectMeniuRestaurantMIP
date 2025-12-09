package org.example.interfatarestaurant.UI;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.concurrent.Task;
import javafx.application.Platform;
import org.example.interfatarestaurant.model.Role;
import org.example.interfatarestaurant.model.User;
import org.example.interfatarestaurant.repository.UserRepository;

public class LoginView {
    private final UserRepository userRepo = new UserRepository();

    public void show(Stage stage) {
        // La prima rulare, creăm un admin dacă nu există
        if (userRepo.findByUsernameAndPassword("admin", "admin") == null) {
            userRepo.adaugaUser(new User("admin", "admin", Role.MANAGER));
            userRepo.adaugaUser(new User("osp", "123", Role.WAITER));
            userRepo.adaugaUser(new User("guest", "guest", Role.GUEST));
        }

        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);
        TextField userField = new TextField("admin");
        PasswordField passField = new PasswordField();
        passField.setText("admin");
        Button btnLogin = new Button("Login");

        btnLogin.setOnAction(event -> {
            Task<User> loginTask = new Task<User>() {
                @Override
                protected User call() throws Exception {
                    return userRepo.findByUsernameAndPassword(userField.getText(), passField.getText());
                }
            };

            loginTask.setOnSucceeded(workerStateEvent -> {
                User user = loginTask.getValue();
                if (user != null) {
                    Platform.runLater(() -> {
                        if (user.getRole() == Role.MANAGER) {
                            new ManagerView().show(stage);
                        } else {
                            new WaiterView(user).show(stage);
                        }
                    });
                } else {
                    Platform.runLater(() ->
                            new Alert(Alert.AlertType.ERROR, "Login failed").show()
                    );
                }
            });

            loginTask.setOnFailed(workerStateEvent ->
                    Platform.runLater(() ->
                            new Alert(Alert.AlertType.ERROR, "Login error").show()
                    )
            );

            new Thread(loginTask).start();
        });

        layout.getChildren().addAll(new Label("Restaurant Login"), userField, passField, btnLogin);
        stage.setScene(new Scene(layout, 300, 400));
        stage.show();
    }
}