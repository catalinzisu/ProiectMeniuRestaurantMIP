module org.example.interfatarestaurant {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires java.sql;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;

    exports org.example.interfatarestaurant to javafx.graphics;

    opens org.example.interfatarestaurant to javafx.fxml;
    opens org.example.interfatarestaurant.UI to javafx.fxml;
    opens org.example.interfatarestaurant.model to org.hibernate.orm.core, javafx.base, com.fasterxml.jackson.databind;

    opens org.example.interfatarestaurant.repository to org.hibernate.orm.core;
}