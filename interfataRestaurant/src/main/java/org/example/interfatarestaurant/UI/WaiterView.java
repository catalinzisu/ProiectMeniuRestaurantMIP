package org.example.interfatarestaurant.UI;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.example.interfatarestaurant.model.*;
import org.example.interfatarestaurant.service.OfferService;
import org.example.interfatarestaurant.service.RestaurantService;

import java.util.ArrayList;

public class WaiterView {
    private final RestaurantService service = new RestaurantService();
    private final OfferService offerService = new OfferService();
    private final User currentUser;
    private RestaurantTable currentTable = null;

    public WaiterView(User user) {
        this.currentUser = user;
    }

    public void show(Stage stage) {
        showTableSelection(stage);
    }

    // ECRAN 1: SELECTIE MESE
    private void showTableSelection(Stage stage) {
        FlowPane tablesPanel = new FlowPane(20, 20);
        tablesPanel.setPadding(new Insets(20));

        for (RestaurantTable t : service.getAllTables()) {
            Button btn = new Button(t.getName());
            btn.setPrefSize(100, 100);
            if (t.isOccupied()) {
                btn.setStyle("-fx-background-color: #ffcccc;"); // Rosu ocupat
                btn.setText(t.getName() + "\n(Ocupat)");
            } else {
                btn.setStyle("-fx-background-color: #ccffcc;"); // Verde liber
            }

            btn.setOnAction(e -> {
                currentTable = t;
                if (!t.isOccupied()) {
                    service.ocupaMasa(t);
                }
                showOrderScreen(stage);
            });
            tablesPanel.getChildren().add(btn);
        }

        BorderPane root = new BorderPane(tablesPanel);
        Button btnLogout = new Button("Logout");
        btnLogout.setOnAction(e -> new LoginView().show(stage));
        root.setTop(new ToolBar(new Label("Ospatar: " + currentUser.getUsername()), new Separator(), btnLogout));

        stage.setScene(new Scene(root, 600, 400));
    }

    // ECRAN 2: COMANDA
    private void showOrderScreen(Stage stage) {
        // Stanga: Meniu
        ProductTableView menuTable = new ProductTableView();
        menuTable.setItems(FXCollections.observableArrayList(service.getAllProducts()));

        // Dreapta: Cos (Folosim un TableView custom pentru OrderItem)
        TableView<OrderItem> basketTable = new TableView<>();
        TableColumn<OrderItem, String> colNume = new TableColumn<>("Produs");
        colNume.setCellValueFactory(new PropertyValueFactory<>("numeProdus"));
        TableColumn<OrderItem, Integer> colCant = new TableColumn<>("Cant.");
        colCant.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        TableColumn<OrderItem, Double> colPret = new TableColumn<>("Pret");
        colPret.setCellValueFactory(new PropertyValueFactory<>("pretUnitar"));

        basketTable.getColumns().addAll(colNume, colCant, colPret);

        ObservableList<OrderItem> basketItems = FXCollections.observableArrayList();
        basketTable.setItems(basketItems);

        // Adaugare in cos
        menuTable.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                Produs selected = menuTable.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    basketItems.add(new OrderItem(selected, 1));
                    updateTotal(basketItems);
                }
            }
        });

        Button btnFinalize = new Button("Finalizeaza Comanda & Elibereaza Masa");
        Label lblTotal = new Label("Total: 0.0 RON");

        btnFinalize.setOnAction(e -> {
            // Salvare in DB
            Order order = new Order(currentUser);
            double total = 0;
            for(OrderItem item : basketItems) {
                order.addItem(item);
                total += item.getProdus().getPret(); // Simplified calculation
            }
            order.setTotalAmount(total);
            service.saveOrder(order);
            service.elibereazaMasa(currentTable);

            new Alert(Alert.AlertType.INFORMATION, "Comanda Salvata!").showAndWait();
            showTableSelection(stage);
        });

        VBox rightPane = new VBox(10, new Label("Masa: " + currentTable.getName()), basketTable, lblTotal, btnFinalize);
        SplitPane split = new SplitPane(menuTable, rightPane);

        // Update Total Listener
        basketItems.addListener((javafx.collections.ListChangeListener<OrderItem>) c -> {
            // Recalculam ofertele
            // Eliminam reducerile anterioare vizuale
            basketItems.removeIf(i -> i.getProdus().getPret() < 0);

            // Calculam reduceri noi
            var discounts = offerService.calculateDiscounts(new ArrayList<>(basketItems));
            if(!discounts.isEmpty()) {
                // Adaugam reducerile in lista vizuala (fara a declansa loop infinit, atentie)
                // Aici e un trick: le adaugam doar la vizualizare finala sau in label
                // Pentru simplitate in demo, doar afisam total redus in label

                double rawTotal = basketItems.stream().mapToDouble(i -> i.getProdus().getPret()).sum();
                double discountTotal = discounts.stream().mapToDouble(i -> i.getProdus().getPret()).sum(); // e negativ

                lblTotal.setText(String.format("Total: %.2f (Reducere: %.2f)", (rawTotal + discountTotal), discountTotal));
            } else {
                double rawTotal = basketItems.stream().mapToDouble(i -> i.getProdus().getPret()).sum();
                lblTotal.setText(String.format("Total: %.2f", rawTotal));
            }
        });

        stage.setScene(new Scene(split, 900, 600));
    }

    // Helper pt Update Total
    private void updateTotal(ObservableList<OrderItem> items) {
        // Triggered by listener
    }
}