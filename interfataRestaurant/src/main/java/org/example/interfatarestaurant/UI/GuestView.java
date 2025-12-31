package org.example.interfatarestaurant.UI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class GuestView {
    private ProductTableView table = new ProductTableView();
    private TextField txtSearch = new TextField();
    private CheckBox chkVeg = new CheckBox("Vegetarian");
    private CheckBox chkDrink = new CheckBox("Bauturi");
    private Button btnBack = new Button("Inapoi la Login");

    // ELEMENTS FOR DETAILS VIEW
    private Label lblName = new Label("Selecteaza un produs");
    private Label lblPrice = new Label("");
    private Label lblGramaj = new Label("");
    private TextArea txtDesc = new TextArea();

    public void show(Stage stage) {
        txtSearch.setPromptText("Cauta...");

        VBox filters = new VBox(10, new Label("Filtre:"), txtSearch, chkVeg, chkDrink, new Separator(), btnBack);
        filters.setPadding(new Insets(10));

        // CONSTRUCTIA DETAILS VIEW (DREAPTA)
        VBox detailsBox = new VBox(15);
        detailsBox.setPadding(new Insets(20));
        detailsBox.setAlignment(Pos.TOP_CENTER);
        detailsBox.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #ccc;");

        lblName.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        lblPrice.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        lblPrice.setStyle("-fx-text-fill: green;");

        txtDesc.setEditable(false);
        txtDesc.setWrapText(true);
        txtDesc.setPrefHeight(100);

        detailsBox.getChildren().addAll(new Label("DETALII PRODUS"), new Separator(), lblName, lblPrice, lblGramaj, new Label("Descriere:"), txtDesc);

        SplitPane split = new SplitPane();
        split.getItems().addAll(table, detailsBox);
        split.setDividerPositions(0.7);

        BorderPane root = new BorderPane();
        root.setLeft(filters);
        root.setCenter(split);

        stage.setScene(new Scene(root, 1000, 600));
    }

    public ProductTableView getTable() { return table; }
    public TextField getTxtSearch() { return txtSearch; }
    public CheckBox getChkVeg() { return chkVeg; }
    public CheckBox getChkDrink() { return chkDrink; }
    public Button getBtnBack() { return btnBack; }

    public Label getLblName() { return lblName; }
    public Label getLblPrice() { return lblPrice; }
    public Label getLblGramaj() { return lblGramaj; }
    public TextArea getTxtDesc() { return txtDesc; }
}