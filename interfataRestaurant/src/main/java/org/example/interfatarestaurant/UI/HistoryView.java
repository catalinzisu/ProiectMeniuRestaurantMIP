package org.example.interfatarestaurant.UI;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.interfatarestaurant.model.Order;
import java.time.format.DateTimeFormatter;

public class HistoryView extends TableView<Order> {

    public HistoryView() {
        TableColumn<Order, Long> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(50);

        TableColumn<Order, String> colMasa = new TableColumn<>("Masa");
        colMasa.setCellValueFactory(new PropertyValueFactory<>("tableName"));

        TableColumn<Order, String> colData = new TableColumn<>("Data");
        colData.setCellValueFactory(cell -> {
            if (cell.getValue().getOrderDate() != null) {
                return new SimpleStringProperty(cell.getValue().getOrderDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
            }
            return new SimpleStringProperty("-");
        });
        colData.setPrefWidth(150);

        TableColumn<Order, Double> colPret = new TableColumn<>("Total (RON)");
        colPret.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));

        TableColumn<Order, String> colOspatar = new TableColumn<>("Ospatar");
        colOspatar.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getUser().getUsername()));

        this.getColumns().addAll(colId, colMasa, colData, colPret, colOspatar);
        this.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
}