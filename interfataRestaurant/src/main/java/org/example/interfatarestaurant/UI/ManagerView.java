package org.example.interfatarestaurant.UI;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.interfatarestaurant.model.*;
import org.example.interfatarestaurant.service.OfferService;
import org.example.interfatarestaurant.service.RestaurantService;

import java.util.Optional;

public class ManagerView {
    private final RestaurantService service = new RestaurantService();

    public void show(Stage stage) {
        TabPane tabs = new TabPane();

        // TAB 1: STAFF
        Tab tabStaff = new Tab("Personal", createStaffView());

        // TAB 2: MENIU (Simplified CRUD)
        Tab tabMenu = new Tab("Meniu", createMenuView());

        // TAB 3: OFERTE
        Tab tabOffers = new Tab("Oferte", createOffersView());

        tabs.getTabs().addAll(tabStaff, tabMenu, tabOffers);

        Button btnLogout = new Button("Logout");
        btnLogout.setOnAction(e -> new LoginView().show(stage));

        BorderPane root = new BorderPane(tabs);
        root.setTop(new ToolBar(new Label("MANAGER ADMIN"), new Separator(), btnLogout));

        stage.setScene(new Scene(root, 800, 600));
    }

    private VBox createStaffView() {
        TableView<User> table = new TableView<>();
        TableColumn<User, String> colUser = new TableColumn<>("Username");
        colUser.setCellValueFactory(new PropertyValueFactory<>("username"));
        table.getColumns().add(colUser);

        table.setItems(FXCollections.observableArrayList(service.getAllStaff()));

        Button btnAdd = new Button("Adauga Ospatar");
        btnAdd.setOnAction(e -> {
            // Dialog simplu
            TextInputDialog dialog = new TextInputDialog();
            dialog.setHeaderText("Nume utilizator ospatar:");
            dialog.showAndWait().ifPresent(name -> {
                service.addStaff(new User(name, "1234", Role.WAITER));
                table.setItems(FXCollections.observableArrayList(service.getAllStaff()));
            });
        });

        Button btnDel = new Button("Concediaza (Sterge)");
        btnDel.setStyle("-fx-background-color: #ffcccc");
        btnDel.setOnAction(e -> {
            User sel = table.getSelectionModel().getSelectedItem();
            if (sel != null) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmare Stergere");
                alert.setContentText("Esti sigur? Se va sterge si istoricul comenzilor!");
                if (alert.showAndWait().get() == ButtonType.OK) {
                    service.deleteStaff(sel);
                    table.setItems(FXCollections.observableArrayList(service.getAllStaff()));
                }
            }
        });

        return new VBox(10, table, new HBox(10, btnAdd, btnDel));
    }

    private VBox createOffersView() {
        CheckBox ckHappy = new CheckBox("Happy Hour (Bauturi -50%)");
        ckHappy.setSelected(OfferService.HAPPY_HOUR_ACTIVE);
        ckHappy.setOnAction(e -> OfferService.HAPPY_HOUR_ACTIVE = ckHappy.isSelected());

        CheckBox ckMeal = new CheckBox("Meal Deal (Pizza + Desert Redus)");
        ckMeal.setSelected(OfferService.MEAL_DEAL_ACTIVE);
        ckMeal.setOnAction(e -> OfferService.MEAL_DEAL_ACTIVE = ckMeal.isSelected());

        CheckBox ckParty = new CheckBox("Party Pack (4 Pizza -> 1 Gratis)");
        ckParty.setSelected(OfferService.PARTY_PACK_ACTIVE);
        ckParty.setOnAction(e -> OfferService.PARTY_PACK_ACTIVE = ckParty.isSelected());

        VBox box = new VBox(20, ckHappy, ckMeal, ckParty);
        box.setPadding(new Insets(30));
        return box;
    }

    private VBox createMenuView() {
        // Implementare minimala existenta in iteratiile trecute
        return new VBox(new Label("Functionalitate Meniu CRUD (vezi iteratia 6)"));
    }
}