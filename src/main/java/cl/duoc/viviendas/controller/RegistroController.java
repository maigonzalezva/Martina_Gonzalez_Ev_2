package cl.duoc.viviendas.controller;

import cl.duoc.viviendas.model.Casa;
import cl.duoc.viviendas.model.Departamento;
import cl.duoc.viviendas.model.Vivienda;
import cl.duoc.viviendas.repository.ViviendaRepository;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;

public class RegistroController {

    @FXML private ComboBox<String> cmbTipo;
    @FXML private TextField txtCodigo;
    @FXML private TextField txtSuperficie;
    @FXML private TextField txtHabitaciones;
    @FXML private TextField txtPiso;
    @FXML private CheckBox chkGastoComun;
    @FXML private CheckBox chkEstacionamiento;
    @FXML private CheckBox chkPatio;
    @FXML private Label lblMensaje;

    private ViviendaRepository repository;

    public void setRepository(ViviendaRepository repository) {
        this.repository = repository;
    }

    @FXML
    public void initialize() {
        cmbTipo.getItems().addAll("Departamento", "Casa");
        cmbTipo.setValue("Departamento");
        cambiarTipo();
    }

    @FXML
    private void cambiarTipo() {
        boolean esDepto = "Departamento".equals(cmbTipo.getValue());
        txtPiso.setDisable(!esDepto);
        chkGastoComun.setDisable(!esDepto);
        chkEstacionamiento.setDisable(!esDepto);
        chkPatio.setDisable(esDepto);
    }

    @FXML
    private void guardar() {
        String codigo = txtCodigo.getText().trim();
        if (codigo.isEmpty()) {
            mostrarError("El código es obligatorio.");
            return;
        }

        double superficie;
        try {
            superficie = Double.parseDouble(txtSuperficie.getText().trim());
        } catch (NumberFormatException e) {
            mostrarError("La superficie debe ser un número.");
            return;
        }

        int habitaciones;
        try {
            habitaciones = Integer.parseInt(txtHabitaciones.getText().trim());
        } catch (NumberFormatException e) {
            mostrarError("Las habitaciones deben ser un número entero.");
            return;
        }

        try {
            Vivienda vivienda;
            if ("Departamento".equals(cmbTipo.getValue())) {
                int piso;
                try {
                    piso = Integer.parseInt(txtPiso.getText().trim());
                } catch (NumberFormatException e) {
                    mostrarError("El piso debe ser un número entero.");
                    return;
                }
                if (piso < 1) {
                    mostrarError("El piso debe ser 1 o mayor.");
                    return;
                }
                Departamento depto = new Departamento(codigo, superficie, habitaciones,
                        piso, chkGastoComun.isSelected());
                if (chkEstacionamiento.isSelected()) {
                    depto.asignarEstacionamiento();
                }
                vivienda = depto;
            } else {
                vivienda = new Casa(codigo, superficie, habitaciones, chkPatio.isSelected());
            }

            repository.registrar(vivienda);
            volverAlListado("Vivienda " + codigo + " registrada.");
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        } catch (IllegalStateException e) {
            mostrarError("No se pudo guardar: " + e.getMessage());
        }
    }

    @FXML
    private void volver() {
        volverAlListado(null);
    }

    private void volverAlListado(String mensaje) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/cl/duoc/viviendas/view/main-view.fxml"));
            Parent root = loader.load();
            if (mensaje != null) {
                MainController controller = loader.getController();
                controller.mostrarMensaje(mensaje);
            }
            txtCodigo.getScene().setRoot(root);
        } catch (Exception e) {
            mostrarError("No se pudo volver: " + e.getMessage());
        }
    }

    private void mostrarError(String texto) {
        lblMensaje.setStyle("-fx-text-fill: red;");
        lblMensaje.setText(texto);
    }
}