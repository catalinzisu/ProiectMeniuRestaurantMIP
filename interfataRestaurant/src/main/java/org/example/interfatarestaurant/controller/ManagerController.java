package org.example.interfatarestaurant.controller;

import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.concurrent.Task;
import org.example.interfatarestaurant.UI.ManagerView;
import org.example.interfatarestaurant.model.*;
import org.example.interfatarestaurant.service.RestaurantService;
import org.example.interfatarestaurant.util.AsyncTaskManager;
import org.example.interfatarestaurant.util.LoadingDialog;

import java.io.File;
import java.util.List;
import java.util.Optional;

/**
 * Controller pentru Manager - gestionează logica UI-ului manager
 * Delegă operațiile de business către RestaurantService
 */
public class ManagerController {
    private final ManagerView view;
    private final RestaurantService service;
    private final Stage stage;
    private Produs selectedProduct = null;

    public ManagerController(ManagerView view, Stage stage) {
        this.view = view;
        this.stage = stage;
        this.service = new RestaurantService();
        initializeEventHandlers();
        loadInitialData();
    }

    /**
     * Inițializează toți event handler-ii pentru butoane și componente UI
     */
    private void initializeEventHandlers() {
        // ===== SECȚIUNEA PERSONAL (STAFF) =====
        setupStaffSection();

        // ===== SECȚIUNEA MENIU =====
        setupMenuSection();

        // ===== SECȚIUNEA OFERTE =====
        setupOffersSection();

        // ===== BUTON LOGOUT =====
        view.getBtnLogout().setOnAction(e -> handleLogout());
    }

    /**
     * Configurează event handlers pentru secțiunea Personal
     */
    private void setupStaffSection() {
        // Buton: Adaugă Angajat
        view.getBtnAddStaff().setOnAction(e -> {
            String username = view.getTxtStaffUser().getText().trim();
            String password = view.getTxtStaffPass().getText().trim();

            if (username.isEmpty() || password.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Eroare", "Introduceți username și parolă!");
                return;
            }

            if (service.addStaff(username, password)) {
                showAlert(Alert.AlertType.INFORMATION, "Succes", "Angajat adăugat cu succes!");
                view.getTxtStaffUser().clear();
                view.getTxtStaffPass().clear();
                refreshStaffList();
            } else {
                showAlert(Alert.AlertType.ERROR, "Eroare", "Username-ul există deja sau a apărut o eroare!");
            }
        });

        // Buton: Concediază Angajat
        view.getBtnFireStaff().setOnAction(e -> {
            User selectedStaff = view.getStaffList().getSelectionModel().getSelectedItem();

            if (selectedStaff == null) {
                showAlert(Alert.AlertType.WARNING, "Eroare", "Selectați un angajat pentru ștergere!");
                return;
            }

            // Dialog de confirmare
            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("Confirmare Ștergere");
            confirmDialog.setHeaderText("Sunteți sigur?");
            confirmDialog.setContentText("Ștergerea angajatului \"" + selectedStaff.getUsername() +
                    "\" va șterge și toate comenzile sale din istoric.");

            Optional<ButtonType> result = confirmDialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (service.deleteStaff(selectedStaff)) {
                    showAlert(Alert.AlertType.INFORMATION, "Succes", "Angajat și comenzile asociate au fost șterse!");
                    refreshStaffList();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Eroare", "Nu s-a putut șterge angajatul!");
                }
            }
        });
    }

    /**
     * Configurează event handlers pentru secțiunea Meniu
     */
    private void setupMenuSection() {
        // SelectionListener pentru a încărca detaliile produsului selectat
        view.getMenuList().setOnMouseClicked(e -> {
            Produs selected = view.getMenuList().getSelectionModel().getSelectedItem();
            if (selected != null) {
                selectedProduct = selected;
                loadProductDetails(selected);
            }
        });

        // Buton: Adaugă Produs
        view.getBtnAddProd().setOnAction(e -> handleAddProduct());

        // Buton: Șterge Produs
        view.getBtnDeleteProd().setOnAction(e -> {
            if (selectedProduct == null) {
                showAlert(Alert.AlertType.WARNING, "Eroare", "Selectați un produs pentru ștergere!");
                return;
            }

            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("Confirmare Ștergere");
            confirmDialog.setHeaderText("Sunteți sigur?");
            confirmDialog.setContentText("Veți șterge produsul \"" + selectedProduct.getNume() + "\".");

            Optional<ButtonType> result = confirmDialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (service.deleteProduct(selectedProduct.getId())) {
                    showAlert(Alert.AlertType.INFORMATION, "Succes", "Produs șters!");
                    clearProductForm();
                    selectedProduct = null;
                    refreshMenuList();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Eroare", "Nu s-a putut șterge produsul!");
                }
            }
        });

        // Buton: Import JSON
        view.getBtnImportJson().setOnAction(e -> handleImportJson());

        // Buton: Export JSON
        view.getBtnExportJson().setOnAction(e -> handleExportJson());
    }

    /**
     * Configurează event handlers pentru secțiunea Oferte
     */
    private void setupOffersSection() {
        // Încarcă starea curentă a ofertelor
        loadOfferState();

        // Buton: Salvează Oferte
        view.getBtnSaveOffers().setOnAction(e -> {
            boolean happyHour = view.getChkHappy().isSelected();
            boolean mealDeal = view.getChkMeal().isSelected();
            boolean partyPack = view.getChkParty().isSelected();

            if (service.updateOfferState(happyHour, mealDeal, partyPack)) {
                showAlert(Alert.AlertType.INFORMATION, "Succes", "Ofertele au fost actualizate!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Eroare", "Nu s-a putut actualiza ofertele!");
            }
        });

        // Buton: Resetează Mese
        view.getBtnResetTables().setOnAction(e -> {
            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("Confirmare");
            confirmDialog.setHeaderText("Resetare Mese");
            confirmDialog.setContentText("Veți marca toate mesele ca libere.");

            Optional<ButtonType> result = confirmDialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (service.resetAllTables()) {
                    showAlert(Alert.AlertType.INFORMATION, "Succes", "Toate mesele au fost resetate!");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Eroare", "Nu s-a putut reseta mesele!");
                }
            }
        });
    }

    /**
     * Handler pentru adăugare produs (adăugare nouă sau update existent)
     */
    private void handleAddProduct() {
        String tip = view.getTypeSelector().getValue();
        String nume = view.getTxtProdName().getText().trim();
        String pretStr = view.getTxtProdPrice().getText().trim();
        String cantitateStr = view.getTxtGramaj().getText().trim();
        String descriere = view.getTxtDescriere().getText().trim();
        boolean isSpecial = view.getChkExtra().isSelected();

        // Validări
        if (tip == null || tip.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Eroare", "Selectați tipul produsului!");
            return;
        }
        if (nume.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Eroare", "Introduceți numele produsului!");
            return;
        }
        if (pretStr.isEmpty() || cantitateStr.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Eroare", "Introduceți preț și cantitate!");
            return;
        }

        try {
            double pret = Double.parseDouble(pretStr);
            int cantitate = Integer.parseInt(cantitateStr);

            if (pret <= 0 || cantitate <= 0) {
                showAlert(Alert.AlertType.WARNING, "Eroare", "Preț și cantitate trebuie să fie pozitive!");
                return;
            }

            boolean success;
            if (selectedProduct == null) {
                // Adăugare nouă
                success = service.addProduct(tip, nume, pret, cantitate, descriere, isSpecial);
                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Succes", "Produs adăugat cu succes!");
                }
            } else {
                // Update existent
                success = service.updateProduct(selectedProduct.getId(), tip, nume, pret, cantitate, descriere, isSpecial);
                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Succes", "Produs actualizat cu succes!");
                }
            }

            if (success) {
                clearProductForm();
                selectedProduct = null;
                refreshMenuList();
            } else {
                showAlert(Alert.AlertType.ERROR, "Eroare", "Nu s-a putut adăuga/actualiza produsul!");
            }

        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.WARNING, "Eroare", "Introduceți valori numerice corecte!");
        }
    }

    /**
     * Handler pentru export JSON cu threading
     */
    private void handleExportJson() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selectați locația pentru export");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        fileChooser.setInitialFileName("meniu_export.json");

        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            LoadingDialog loadingDialog = new LoadingDialog("Export", "Se exportă meniu-ul...");
            loadingDialog.show();

            Task<Boolean> exportTask = new Task<Boolean>() {
                @Override
                protected Boolean call() {
                    try {
                        return service.exportProductsToJson(file.getAbsolutePath());
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }
            };

            exportTask.setOnSucceeded(e -> {
                loadingDialog.close();
                if (exportTask.getValue()) {
                    showAlert(Alert.AlertType.INFORMATION, "Succes", "Meniu exportat în: " + file.getAbsolutePath());
                } else {
                    showAlert(Alert.AlertType.ERROR, "Eroare", "Nu s-a putut exporta meniu-ul!");
                }
            });

            exportTask.setOnFailed(e -> {
                loadingDialog.close();
                showAlert(Alert.AlertType.ERROR, "Eroare", "Export failed: " + exportTask.getException().getMessage());
            });

            AsyncTaskManager.executeTask(exportTask);
        }
    }

    /**
     * Handler pentru import JSON cu threading
     */
    private void handleImportJson() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selectați fișierul pentru import");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));

        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            LoadingDialog loadingDialog = new LoadingDialog("Import", "Se importă meniu-ul...");
            loadingDialog.show();

            Task<Boolean> importTask = new Task<Boolean>() {
                @Override
                protected Boolean call() {
                    try {
                        return service.importProductsFromJson(file.getAbsolutePath());
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }
            };

            importTask.setOnSucceeded(e -> {
                loadingDialog.close();
                if (importTask.getValue()) {
                    showAlert(Alert.AlertType.INFORMATION, "Succes", "Meniu importat cu succes!");
                    refreshMenuList();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Eroare", "Nu s-a putut importa meniu-ul!");
                }
            });

            importTask.setOnFailed(e -> {
                loadingDialog.close();
                showAlert(Alert.AlertType.ERROR, "Eroare", "Import failed: " + importTask.getException().getMessage());
            });

            AsyncTaskManager.executeTask(importTask);
        }
    }

    /**
     * Încarcă datele inițiale în ListView-uri
     */
    private void loadInitialData() {
        refreshStaffList();
        refreshMenuList();
        loadOfferState();
    }

    /**
     * Reîncarcă lista de angajați
     */
    private void refreshStaffList() {
        List<User> staff = service.getAllStaff();
        view.getStaffList().getItems().setAll(staff);
    }

    /**
     * Reîncarcă lista de produse
     */
    private void refreshMenuList() {
        List<Produs> products = service.getAllProducts();
        view.getMenuList().getItems().setAll(products);
    }

    /**
     * Încarcă starea ofertelor
     */
    private void loadOfferState() {
        OfferState state = service.getOfferState();
        view.getChkHappy().setSelected(state.isHappyHourActive());
        view.getChkMeal().setSelected(state.isMealDealActive());
        view.getChkParty().setSelected(state.isPartyPackActive());
    }

    /**
     * Încarcă detaliile unui produs selectat în formular
     */
    private void loadProductDetails(Produs product) {
        view.getTxtProdName().setText(product.getNume());
        view.getTxtProdPrice().setText(String.valueOf(product.getPret()));
        view.getTxtDescriere().setText(product.getDescriere() != null ? product.getDescriere() : "");

        if (product instanceof Mancare) {
            Mancare mancare = (Mancare) product;
            view.getTypeSelector().setValue("Mancare");
            view.getTxtGramaj().setText(String.valueOf(mancare.getGramaj()));
            view.getChkExtra().setSelected(mancare.isVegetarian());
        } else if (product instanceof Bautura) {
            Bautura bautura = (Bautura) product;
            view.getTypeSelector().setValue("Bautura");
            view.getTxtGramaj().setText(String.valueOf(bautura.getVolum()));
            view.getChkExtra().setSelected(bautura.isAlcoholic());
        }
    }

    /**
     * Golește formularul de produs
     */
    private void clearProductForm() {
        view.getTxtProdName().clear();
        view.getTxtProdPrice().clear();
        view.getTxtGramaj().clear();
        view.getTxtDescriere().clear();
        view.getTypeSelector().getSelectionModel().selectFirst();
        view.getChkExtra().setSelected(false);
    }

    /**
     * Handler pentru logout
     */
    private void handleLogout() {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Logout");
        confirmDialog.setHeaderText("Sunteți sigur?");
        confirmDialog.setContentText("Veți fi deconectați.");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Delegare către RestaurantController pentru logout
            // (va fi handled din RestaurantController)
            System.exit(0); // Pentru acum, închidem aplicația
        }
    }

    /**
     * Afișează o alert dialog cu mesaj
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

