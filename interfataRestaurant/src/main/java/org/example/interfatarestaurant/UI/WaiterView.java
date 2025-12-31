package org.example.interfatarestaurant.UI;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.example.interfatarestaurant.model.Order;
import org.example.interfatarestaurant.model.OrderItem;
import org.example.interfatarestaurant.model.RestaurantTable;
import org.example.interfatarestaurant.model.User;
import java.util.List;
import java.util.function.Consumer;

public class WaiterView {
    private final User currentUser;
    private FlowPane tablesPanel;
    private Button btnLogout = new Button("Logout");

    private ProductTableView menuTable = new ProductTableView();
    private TextField searchField = new TextField();
    private TableView<OrderItem> basketTable = new TableView<>();
    private Button btnFinalize = new Button("Trimite Comanda");
    private Button btnFreeTable = new Button("Nota / Elibereaza Masa");
    private Button btnRemove = new Button("Sterge Selectat");
    private Label lblTotal = new Label("Total: 0.0 RON");

    // ISTORIC CU TABEL
    private HistoryView historyTable = new HistoryView();
    private Button btnRefreshHistory = new Button("Actualizeaza Istoric");

    // DETAILS VIEW
    private TextArea txtDetails = new TextArea();

    public WaiterView(User user) {
        this.currentUser = user;
        initBasketColumns();
    }

    private void initBasketColumns() {
        TableColumn<OrderItem, String> colNume = new TableColumn<>("Produs");
        colNume.setCellValueFactory(new PropertyValueFactory<>("numeProdus"));
        TableColumn<OrderItem, Integer> colCant = new TableColumn<>("Cant.");
        colCant.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        TableColumn<OrderItem, Double> colPret = new TableColumn<>("Pret");
        colPret.setCellValueFactory(new PropertyValueFactory<>("pretUnitar"));
        basketTable.getColumns().addAll(colNume, colCant, colPret);
        basketTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    public void setupTables(List<RestaurantTable> tables, Consumer<RestaurantTable> onSelectAction) {
        tablesPanel = new FlowPane(20, 20);
        tablesPanel.setPadding(new Insets(20));
        for (RestaurantTable t : tables) {
            Button btn = new Button(t.getName());
            btn.setPrefSize(100, 100);
            if (t.isOccupied()) {
                btn.setStyle("-fx-background-color: #ffcccc; -fx-border-color: red;");
                btn.setText(t.getName() + "\n(Ocupat)");
            } else {
                btn.setStyle("-fx-background-color: #ccffcc; -fx-border-color: green;");
            }
            btn.setOnAction(e -> onSelectAction.accept(t));
            tablesPanel.getChildren().add(btn);
        }
    }

    public void showTableSelection(Stage stage) {
        BorderPane root = new BorderPane(tablesPanel);
        root.setTop(new ToolBar(new Label("Ospatar: " + currentUser.getUsername()), new Separator(), btnLogout));
        stage.setScene(new Scene(root, 950, 650));
    }

    public void showOrderScreen(Stage stage, String tableName) {
        TabPane tabs = new TabPane();

        // --- TAB 1: COMANDA ---
        Label lblMeniu = new Label("MENIU PRINCIPAL");
        lblMeniu.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        searchField.setPromptText("🔍 Caută produs...");

        // Detalii sus-dreapta (mic)
        txtDetails.setPrefHeight(60);
        txtDetails.setEditable(false);
        txtDetails.setPromptText("Click pe un produs pentru detalii...");

        VBox leftPane = new VBox(5, lblMeniu, searchField, txtDetails, menuTable);
        leftPane.setPadding(new Insets(10));
        VBox.setVgrow(menuTable, Priority.ALWAYS);

        btnFinalize.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        btnFinalize.setMaxWidth(Double.MAX_VALUE);
        btnFreeTable.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold;");
        btnFreeTable.setMaxWidth(Double.MAX_VALUE);

        VBox rightPane = new VBox(10, new Label("Masa: " + tableName), basketTable, btnRemove, new Separator(), lblTotal, btnFinalize, new Separator(), btnFreeTable);
        rightPane.setPadding(new Insets(10));
        rightPane.setPrefWidth(350);

        SplitPane split = new SplitPane(leftPane, rightPane);
        split.setDividerPositions(0.65);

        tabs.getTabs().add(new Tab("Comanda Masa " + tableName, split));

        // --- TAB 2: ISTORIC ---
        VBox historyBox = new VBox(10, btnRefreshHistory, historyTable);
        historyBox.setPadding(new Insets(15));
        tabs.getTabs().add(new Tab("Istoric Comenzile Mele", historyBox));

        stage.setScene(new Scene(tabs, 1000, 700));
    }

    public void updateTotal(double val) { lblTotal.setText(String.format("Total: %.2f RON", val)); }
    public Button getBtnLogout() { return btnLogout; }
    public ProductTableView getMenuTable() { return menuTable; }
    public TextField getSearchField() { return searchField; }
    public TableView<OrderItem> getBasketTable() { return basketTable; }
    public Button getBtnFinalize() { return btnFinalize; }
    public Button getBtnFreeTable() { return btnFreeTable; }
    public Button getBtnRemove() { return btnRemove; }
    public HistoryView getHistoryTable() { return historyTable; }
    public Button getBtnRefreshHistory() { return btnRefreshHistory; }
    public TextArea getTxtDetails() { return txtDetails; }
}