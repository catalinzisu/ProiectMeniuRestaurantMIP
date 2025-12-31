package org.example.interfatarestaurant.UI;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.example.interfatarestaurant.model.*;

public class ManagerView {
    private ListView<User> staffList = new ListView<>();
    private Button btnAddStaff = new Button("Angajează");
    private Button btnFireStaff = new Button("Concediază");
    private TextField txtStaffUser = new TextField();
    private PasswordField txtStaffPass = new PasswordField();

    private ListView<Produs> menuList = new ListView<>();
    private TextField txtProdName = new TextField();
    private TextField txtProdPrice = new TextField();
    private ComboBox<String> typeSelector = new ComboBox<>();

    private TextField txtGramaj = new TextField();
    private TextField txtDescriere = new TextField();
    private CheckBox chkExtra = new CheckBox("Vegetarian / Alcoolic");

    private Button btnAddProd = new Button("Adaugă Produs");
    private Button btnDeleteProd = new Button("Șterge Produs");
    private Button btnImportJson = new Button("Import JSON");
    private Button btnExportJson = new Button("Export JSON");

    private CheckBox chkHappy = new CheckBox("Happy Hour (-50% la a 2-a bautura)");
    private CheckBox chkMeal = new CheckBox("Meal Deal (-25% Pizza+Desert)");
    private CheckBox chkParty = new CheckBox("Party Pack (4 Pizza -> 1 Gratis)");
    private Button btnSaveOffers = new Button("SALVEAZA OFERTE");

    private Button btnResetTables = new Button("ELIBEREAZĂ TOATE MESELE");

    private HistoryView allHistoryTable = new HistoryView();
    private Button btnRefreshGlobalHistory = new Button("Reincarca Istoric");

    private Button btnLogout = new Button("Logout");

    public void show(Stage stage) {
        TabPane tabs = new TabPane();

        VBox staffBox = new VBox(10, new Label("Echipa:"), staffList, new HBox(5, txtStaffUser, txtStaffPass, btnAddStaff), btnFireStaff);
        staffBox.setPadding(new Insets(15));
        tabs.getTabs().add(new Tab("Personal", staffBox));

        typeSelector.getItems().addAll("Mancare", "Bautura");
        typeSelector.getSelectionModel().selectFirst();

        txtProdName.setPromptText("Nume");
        txtProdPrice.setPromptText("Pret");
        txtGramaj.setPromptText("Gramaj (g) / Volum (ml)");
        txtDescriere.setPromptText("Descriere produs...");

        GridPane formGrid = new GridPane();
        formGrid.setHgap(10); formGrid.setVgap(10);
        formGrid.add(new Label("Tip:"), 0, 0); formGrid.add(typeSelector, 1, 0);
        formGrid.add(new Label("Nume:"), 0, 1); formGrid.add(txtProdName, 1, 1);
        formGrid.add(new Label("Pret:"), 0, 2); formGrid.add(txtProdPrice, 1, 2);
        formGrid.add(new Label("Cantitate:"), 0, 3); formGrid.add(txtGramaj, 1, 3);
        formGrid.add(new Label("Descriere:"), 0, 4); formGrid.add(txtDescriere, 1, 4);
        formGrid.add(chkExtra, 1, 5);
        formGrid.add(btnAddProd, 1, 6);
        formGrid.add(btnDeleteProd, 0, 6);

        HBox topFileBox = new HBox(10, new Label("Lista Produse:"), new Region(), btnImportJson, btnExportJson);
        HBox.setHgrow(topFileBox.getChildren().get(1), Priority.ALWAYS);

        VBox menuBox = new VBox(10, topFileBox, menuList, new Separator(), new Label("Adaugare/Editare:"), formGrid);
        menuBox.setPadding(new Insets(15));
        tabs.getTabs().add(new Tab("Meniu & Detalii", menuBox));

        btnSaveOffers.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        VBox offersBox = new VBox(15, new Label("Strategii:"), chkHappy, chkMeal, chkParty, btnSaveOffers, new Separator(), btnResetTables);
        offersBox.setPadding(new Insets(30));
        tabs.getTabs().add(new Tab("Oferte", offersBox));

        VBox historyBox = new VBox(10, btnRefreshGlobalHistory, allHistoryTable);
        historyBox.setPadding(new Insets(15));
        tabs.getTabs().add(new Tab("Istoric Global", historyBox));

        BorderPane root = new BorderPane(tabs);
        root.setTop(new ToolBar(new Label("MANAGER ADMIN"), new Separator(), btnLogout));
        stage.setScene(new Scene(root, 1000, 750));
    }

    // Getters
    public ListView<User> getStaffList() { return staffList; }
    public Button getBtnAddStaff() { return btnAddStaff; }
    public Button getBtnFireStaff() { return btnFireStaff; }
    public TextField getTxtStaffUser() { return txtStaffUser; }
    public PasswordField getTxtStaffPass() { return txtStaffPass; }
    public ListView<Produs> getMenuList() { return menuList; }
    public TextField getTxtProdName() { return txtProdName; }
    public TextField getTxtProdPrice() { return txtProdPrice; }
    public ComboBox<String> getTypeSelector() { return typeSelector; }
    public TextField getTxtGramaj() { return txtGramaj; }
    public TextField getTxtDescriere() { return txtDescriere; }
    public CheckBox getChkExtra() { return chkExtra; }
    public Button getBtnAddProd() { return btnAddProd; }
    public Button getBtnDeleteProd() { return btnDeleteProd; }
    public Button getBtnImportJson() { return btnImportJson; }
    public Button getBtnExportJson() { return btnExportJson; }
    public CheckBox getChkHappy() { return chkHappy; }
    public CheckBox getChkMeal() { return chkMeal; }
    public CheckBox getChkParty() { return chkParty; }
    public Button getBtnSaveOffers() { return btnSaveOffers; }
    public Button getBtnResetTables() { return btnResetTables; }
    public HistoryView getAllHistoryTable() { return allHistoryTable; }
    public Button getBtnRefreshGlobalHistory() { return btnRefreshGlobalHistory; }
    public Button getBtnLogout() { return btnLogout; }
}