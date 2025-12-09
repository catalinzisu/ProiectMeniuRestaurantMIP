package org.example.interfatarestaurant.UI;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.interfatarestaurant.model.*;
import org.example.interfatarestaurant.service.RestaurantService;

import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

public class GuestView {
    private final RestaurantService service = new RestaurantService();

    public void show(Stage stage) {
        List<Produs> allProducts = service.getAllProducts();
        ProductTableView table = new ProductTableView();
        table.setItems(FXCollections.observableArrayList(allProducts));

        // --- FILTRE (Streams API) ---
        CheckBox chkVeg = new CheckBox("Doar Vegetariene");
        ComboBox<String> cmbType = new ComboBox<>(FXCollections.observableArrayList("Toate", "Mancare", "Bautura"));
        cmbType.setValue("Toate");
        TextField txtPrice = new TextField();
        txtPrice.setPromptText("Pret maxim...");
        Button btnFilter = new Button("Aplica Filtre");

        btnFilter.setOnAction(e -> {
            double maxPrice = txtPrice.getText().isEmpty() ? Double.MAX_VALUE : Double.parseDouble(txtPrice.getText());
            boolean vegOnly = chkVeg.isSelected();
            String type = cmbType.getValue();

            List<Produs> filtered = allProducts.stream()
                    .filter(p -> p.getPret() <= maxPrice)
                    .filter(p -> !vegOnly || (p instanceof Mancare && ((Mancare) p).isVegetarian()))
                    .filter(p -> type.equals("Toate") ||
                            (type.equals("Mancare") && p instanceof Mancare) ||
                            (type.equals("Bautura") && p instanceof Bautura))
                    .collect(Collectors.toList());

            table.setItems(FXCollections.observableArrayList(filtered));
        });

        // --- CAUTARE (Optional) ---
        TextField txtSearch = new TextField();
        txtSearch.setPromptText("Cauta produs...");
        Button btnSearch = new Button("Cauta");

        btnSearch.setOnAction(e -> {
            String term = txtSearch.getText().toLowerCase();
            Optional<Produs> found = allProducts.stream()
                    .filter(p -> p.getNume().toLowerCase().contains(term))
                    .findFirst();

            found.ifPresentOrElse(
                    p -> {
                        table.getSelectionModel().select(p);
                        table.scrollTo(p);
                    },
                    () -> new Alert(Alert.AlertType.INFORMATION, "Produsul nu a fost gasit.").show()
            );
        });

        VBox leftPane = new VBox(10, new Label("Filtre"), chkVeg, cmbType, txtPrice, btnFilter, new Separator(), new Label("Cautare"), txtSearch, btnSearch);
        leftPane.setPadding(new Insets(10));

        // --- DETALII ---
        Label lblDetails = new Label("Selecteaza un produs");
        table.getSelectionModel().selectedItemProperty().addListener((obs, old, p) -> {
            if (p != null) lblDetails.setText(p.toString() + "\nPret: " + p.getPret());
        });

        BorderPane root = new BorderPane();
        root.setCenter(table);
        root.setLeft(leftPane);
        root.setRight(new VBox(10, new Label("Detalii:"), lblDetails));

        // Fixed Logout Button
        Button btnLogout = new Button("Logout");
        btnLogout.setOnAction(e -> new LoginView().show(stage));
        root.setTop(new ToolBar(btnLogout));

        stage.setScene(new Scene(root, 900, 600));
        stage.setTitle("Meniu Guest");
    }
}