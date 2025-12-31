package org.example.interfatarestaurant.controller;

import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.example.interfatarestaurant.RestaurantConfiguration;
import org.example.interfatarestaurant.RestaurantManager;
import org.example.interfatarestaurant.UI.*;
import org.example.interfatarestaurant.model.*;
import java.util.stream.Collectors;

public class RestaurantController {
    private final RestaurantModel model;
    private final Stage stage;
    private RestaurantConfiguration config;

    public RestaurantController(Stage stage) {
        this.stage = stage;
        this.model = new RestaurantModel();
        // Incarca configurarile la start
        this.config = RestaurantManager.incarcaConfigurare("appconfiguration.json");
        applyConfig();
        initLogin();
    }

    private void applyConfig() {
        model.setHappyHour(config.happyHourActive);
        model.setMealDeal(config.mealDealActive);
        model.setPartyPack(config.partyPackActive);
    }

    private void initLogin() {
        LoginView loginView = new LoginView();
        loginView.show(stage);
        loginView.getBtnLogin().setOnAction(e -> {
            String u = loginView.getUsername();
            String p = loginView.getPassword();
            if (model.login(u, p)) {
                User user = model.getCurrentUser();
                if (user.getRole() == Role.MANAGER) initManager();
                else if (user.getRole() == Role.WAITER) initWaiter();
            } else new Alert(Alert.AlertType.ERROR, "Date incorecte!").show();
        });
        loginView.getBtnGuest().setOnAction(e -> initGuest());
    }

    private void initGuest() {
        GuestView guestView = new GuestView();
        guestView.show(stage);
        guestView.getTable().getItems().setAll(model.getAllProducts());

        guestView.getTable().setOnMouseClicked(e -> {
            Produs p = guestView.getTable().getSelectionModel().getSelectedItem();
            if(p != null) {
                guestView.getLblName().setText(p.getNume());
                guestView.getLblPrice().setText(p.getPret() + " RON");
                String detalii = "";
                if(p instanceof Mancare) {
                    detalii = "Gramaj: " + ((Mancare)p).getGramaj() + "g";
                    if(((Mancare)p).isVegetarian()) detalii += " (Vegetarian)";
                } else if(p instanceof Bautura) {
                    detalii = "Volum: " + ((Bautura)p).getVolum() + "ml";
                    if(((Bautura)p).isAlcoholic()) detalii += " (Alcool)";
                }
                guestView.getLblGramaj().setText(detalii);
                guestView.getTxtDesc().setText(p.getDescriere() != null ? p.getDescriere() : "Fara descriere.");
            }
        });

        Runnable updateFilter = () -> {
            String searchText = guestView.getTxtSearch().getText().toLowerCase();
            boolean onlyVeg = guestView.getChkVeg().isSelected();
            boolean onlyDrink = guestView.getChkDrink().isSelected();
            var filtered = model.getAllProducts().stream()
                    .filter(p -> {
                        if (!p.getNume().toLowerCase().contains(searchText)) return false;
                        if (onlyDrink && !(p instanceof Bautura)) return false;
                        if (onlyVeg && p instanceof Mancare && !((Mancare)p).isVegetarian()) return false;
                        return true;
                    }).collect(Collectors.toList());
            guestView.getTable().getItems().setAll(filtered);
        };
        guestView.getTxtSearch().textProperty().addListener(o -> updateFilter.run());
        guestView.getChkVeg().selectedProperty().addListener(o -> updateFilter.run());
        guestView.getChkDrink().selectedProperty().addListener(o -> updateFilter.run());
        guestView.getBtnBack().setOnAction(e -> initLogin());
    }

    private void initWaiter() {
        WaiterView waiterView = new WaiterView(model.getCurrentUser());
        waiterView.setupTables(model.getAllTables(), table -> {
            model.occupyTable(table);
            waiterView.getMenuTable().getItems().setAll(model.getAllProducts());
            waiterView.getBasketTable().setItems(model.getCart());
            waiterView.updateTotal(0.0);
            waiterView.showOrderScreen(stage, table.getName());

            // DETALII SUS-DREAPTA LA OSPATAR
            waiterView.getMenuTable().setOnMouseClicked(e -> {
                Produs p = waiterView.getMenuTable().getSelectionModel().getSelectedItem();
                if(p != null) {
                    // Adaugare la dublu-click
                    if(e.getClickCount() == 2) {
                        model.addToCart(p);
                        waiterView.getBasketTable().setItems(model.getCart());
                        waiterView.updateTotal(model.calculateTotal());
                    }
                    // Afisare detalii
                    String info = p.getNume() + " | " + p.getPret() + " RON\n";
                    info += (p.getDescriere() != null ? p.getDescriere() : "");
                    waiterView.getTxtDetails().setText(info);
                }
            });

            waiterView.getSearchField().textProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal == null || newVal.isEmpty()) waiterView.getMenuTable().getItems().setAll(model.getAllProducts());
                else {
                    var filtered = model.getAllProducts().stream()
                            .filter(p -> p.getNume().toLowerCase().contains(newVal.toLowerCase()))
                            .collect(Collectors.toList());
                    waiterView.getMenuTable().getItems().setAll(filtered);
                }
            });
        });

        waiterView.getBtnRefreshHistory().setOnAction(e ->
                waiterView.getHistoryTable().getItems().setAll(model.getOrderHistory(model.getCurrentUser())));

        waiterView.getBtnLogout().setOnAction(e -> { model.logout(); initLogin(); });
        waiterView.getBtnRemove().setOnAction(e -> {
            OrderItem item = waiterView.getBasketTable().getSelectionModel().getSelectedItem();
            if (item != null) {
                model.getCart().remove(item);
                waiterView.updateTotal(model.calculateTotal());
            }
        });
        waiterView.getBtnFinalize().setOnAction(e -> {
            if (model.getCart().isEmpty()) new Alert(Alert.AlertType.WARNING, "Cos gol!").show();
            else {
                model.placeOrder();
                new Alert(Alert.AlertType.INFORMATION, "Trimis!").showAndWait();
                initWaiter();
            }
        });
        waiterView.getBtnFreeTable().setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Eliberezi masa?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.YES) { model.freeTable(); initWaiter(); }
        });
        waiterView.showTableSelection(stage);
    }

    private void initManager() {
        ManagerView managerView = new ManagerView();
        managerView.show(stage);

        // Sincronizam checkbox-urile cu config-ul incarcat
        managerView.getChkHappy().setSelected(config.happyHourActive);
        managerView.getChkMeal().setSelected(config.mealDealActive);
        managerView.getChkParty().setSelected(config.partyPackActive);

        managerView.getStaffList().getItems().setAll(model.getWaiters());
        managerView.getMenuList().getItems().setAll(model.getAllProducts());

        managerView.getBtnRefreshGlobalHistory().setOnAction(e ->
                managerView.getAllHistoryTable().getItems().setAll(model.getAllOrders()));

        managerView.getBtnAddStaff().setOnAction(e -> {
            try {
                model.addUser(new User(managerView.getTxtStaffUser().getText(), managerView.getTxtStaffPass().getText(), Role.WAITER));
                managerView.getStaffList().getItems().setAll(model.getWaiters());
            } catch (Exception ex) { new Alert(Alert.AlertType.ERROR, "Eroare user").show(); }
        });
        managerView.getBtnFireStaff().setOnAction(e -> {
            User u = managerView.getStaffList().getSelectionModel().getSelectedItem();
            if (u != null) { model.deleteUser(u); managerView.getStaffList().getItems().setAll(model.getWaiters()); }
        });
        managerView.getBtnAddProd().setOnAction(e -> {
            try {
                String n = managerView.getTxtProdName().getText();
                double p = Double.parseDouble(managerView.getTxtProdPrice().getText());
                String type = managerView.getTypeSelector().getValue();
                String extraText = managerView.getTxtGramaj().getText();
                String desc = managerView.getTxtDescriere().getText();
                boolean isExtra = managerView.getChkExtra().isSelected();
                Produs prod;
                if(type.equals("Mancare")) {
                    int gramaj = extraText.isEmpty() ? 0 : Integer.parseInt(extraText);
                    prod = new Mancare(n, p, Categorie.FelPrincipal, gramaj, isExtra);
                } else {
                    double volum = extraText.isEmpty() ? 0.0 : Double.parseDouble(extraText);
                    prod = new Bautura(n, p, Categorie.BauturaRacoritoare, volum, isExtra);
                }
                prod.setDescriere(desc);
                model.addProduct(prod);
                managerView.getMenuList().getItems().setAll(model.getAllProducts());
                new Alert(Alert.AlertType.INFORMATION, "Adaugat!").show();
            } catch(Exception ex) { new Alert(Alert.AlertType.ERROR, "Date invalide").show(); }
        });
        managerView.getBtnDeleteProd().setOnAction(e -> {
            Produs s = managerView.getMenuList().getSelectionModel().getSelectedItem();
            if (s != null) managerView.getMenuList().getItems().remove(s);
        });
        managerView.getBtnImportJson().setOnAction(e -> {
            model.importaDate();
            managerView.getMenuList().getItems().setAll(model.getAllProducts());
            new Alert(Alert.AlertType.INFORMATION, "Importat!").show();
        });
        managerView.getBtnExportJson().setOnAction(e -> {
            try { model.exportaDate(); new Alert(Alert.AlertType.INFORMATION, "Exportat!").show(); }
            catch (Exception ex) { new Alert(Alert.AlertType.ERROR, "Eroare export").show(); }
        });

        // SALVARE OFERTE
        managerView.getBtnSaveOffers().setOnAction(e -> {
            config.happyHourActive = managerView.getChkHappy().isSelected();
            config.mealDealActive = managerView.getChkMeal().isSelected();
            config.partyPackActive = managerView.getChkParty().isSelected();
            RestaurantManager.salveazaConfigurare(config, "appconfiguration.json");
            applyConfig(); // Aplica si in modelul curent
            new Alert(Alert.AlertType.INFORMATION, "Oferte salvate!").show();
        });

        managerView.getBtnResetTables().setOnAction(e -> { model.resetAllTables(); new Alert(Alert.AlertType.INFORMATION, "Mese Resetate!").show(); });
        managerView.getBtnLogout().setOnAction(e -> { model.logout(); initLogin(); });
    }
}