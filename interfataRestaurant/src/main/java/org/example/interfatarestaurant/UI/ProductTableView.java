package org.example.interfatarestaurant.UI;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.interfatarestaurant.model.Produs;

public class ProductTableView extends TableView<Produs> {
    public ProductTableView() {
        TableColumn<Produs, String> colNume = new TableColumn<>("Nume");
        colNume.setCellValueFactory(new PropertyValueFactory<>("nume"));
        TableColumn<Produs, Double> colPret = new TableColumn<>("Preț");
        colPret.setCellValueFactory(new PropertyValueFactory<>("pret"));
        getColumns().addAll(colNume, colPret);
        setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
}