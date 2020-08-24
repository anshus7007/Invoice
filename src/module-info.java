module Invoice.Generator {
    requires javafx.fxml;
    requires javafx.controls;
    requires java.sql;
    requires java.sql.rowset;
    requires mysql.connector.java;
    opens com.karam.invoice;
}