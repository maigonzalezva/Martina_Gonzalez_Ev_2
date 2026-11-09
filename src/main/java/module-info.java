module cl.duoc.viviendas {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;

    exports cl.duoc.viviendas;
    exports cl.duoc.viviendas.model;

    opens cl.duoc.viviendas.controller to javafx.fxml;
    opens cl.duoc.viviendas.model to com.fasterxml.jackson.databind;
}
