package org.example.interfatarestaurant.controller;

import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.example.interfatarestaurant.RestaurantConfiguration;
import org.example.interfatarestaurant.RestaurantManager;
import org.example.interfatarestaurant.UI.*;
import org.example.interfatarestaurant.model.*;
import org.example.interfatarestaurant.service.RestaurantService;
import java.util.stream.Collectors;

public class RestaurantController {
    private final RestaurantModel model;
    private final RestaurantService service;
    private final Stage stage;
    private RestaurantConfiguration config;

    public RestaurantController(Stage stage) {
        this.stage = stage;
        this.model = new RestaurantModel();
        this.service = new RestaurantService();
        // Incarca configurarile la start
        this.config = RestaurantManager.incarcaConfigurare("appconfiguration.json");
        applyOfferStateFromDatabase();
        initLogin();
    }

    /**
     * Încarcă starea ofertelor din baza de date (unde le-a salvat managerul)
     * Nu mai folosi doar config file, ci fetch din BD
     */
    private void applyOfferStateFromDatabase() {
        try {
            OfferState offerState = service.getOfferState();
            if (offerState != null) {
                model.setHappyHour(offerState.isHappyHourActive());
                model.setMealDeal(offerState.isMealDealActive());
                model.setPartyPack(offerState.isPartyPackActive());
            } else {
                // Fall back to config if no offer state in DB
                applyConfig();
            }
        } catch (Exception e) {
            // Fall back to config if DB fetch fails
            applyConfig();
        }
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

            waiterView.getMenuTable().setOnMouseClicked(e -> {
                Produs p = waiterView.getMenuTable().getSelectionModel().getSelectedItem();
                if(p != null) {
                    if(e.getClickCount() == 2) {
                        model.addToCart(p);
                        waiterView.getBasketTable().setItems(model.getCart());
                        waiterView.updateTotal(model.calculateTotal());
                    }
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
            if (model.getCurrentTable() == null) {
                new Alert(Alert.AlertType.ERROR, "Nu ati selectat nicio masa!").show();
                return;
            }

            String tableName = model.getCurrentTable().getName();
            Order activeOrder = model.findActiveOrderForTable(tableName);

            if (activeOrder == null || activeOrder.getItems().isEmpty()) {
                Alert confirmFree = new Alert(Alert.AlertType.CONFIRMATION,
                        "Nu exista comanda pentru aceasta masa. Doriti sa o eliberati?",
                        ButtonType.YES, ButtonType.NO);
                confirmFree.showAndWait();
                if (confirmFree.getResult() == ButtonType.YES) {
                    model.freeTable();
                    initWaiter();
                }
                return;
            }

            double exactTotal = model.calculateOrderTotal(activeOrder);
            String receiptText = model.generateReceiptText(activeOrder);

            Alert paymentAlert = new Alert(Alert.AlertType.INFORMATION);
            paymentAlert.setTitle("BON DE PLATĂ");
            paymentAlert.setHeaderText("Masa: " + tableName);
            paymentAlert.setContentText(receiptText);
            paymentAlert.showAndWait();

            Alert confirmFree = new Alert(Alert.AlertType.CONFIRMATION,
                    "Plata efectuată? Eliberezi masa " + tableName + "?",
                    ButtonType.YES, ButtonType.NO);
            confirmFree.showAndWait();

            if (confirmFree.getResult() == ButtonType.YES) {
                model.freeTable();
                model.clearCart();
                new Alert(Alert.AlertType.INFORMATION, "Masa eliberata!").showAndWait();
                initWaiter();
            }
        });
        waiterView.showTableSelection(stage);
    }

    private void initManager() {
        ManagerView managerView = new ManagerView();
        managerView.show(stage);

        new ManagerController(managerView, stage);

        managerView.getBtnLogout().setOnAction(e -> {
            model.logout();
            initLogin();
        });
    }
}

