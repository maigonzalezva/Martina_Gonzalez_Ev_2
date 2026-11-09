package cl.duoc.viviendas.controller;

import cl.duoc.viviendas.dao.ViviendaDAOJson;
import cl.duoc.viviendas.model.Vivienda;
import cl.duoc.viviendas.repository.ViviendaRepository;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class MainController {

    @FXML private TextField txtBuscar;
    @FXML private Button btnNueva;
    @FXML private Button btnEliminar;
    @FXML private TableView<Vivienda> tabla;
    @FXML private TableColumn<Vivienda, String> colTipo;
    @FXML private TableColumn<Vivienda, String> colCodigo;
    @FXML private TableColumn<Vivienda, Double> colSuperficie;
    @FXML private TableColumn<Vivienda, Integer> colHabitaciones;
    @FXML private TableColumn<Vivienda, Double> colCosto;
    @FXML private Label lblContador;
    @FXML private Label lblMensaje;

    private ViviendaRepository repository;
    private final ObservableList<Vivienda> datos = FXCollections.observableArrayList();
    private FilteredList<Vivienda> filtradas;

    @FXML
    public void initialize() {
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colSuperficie.setCellValueFactory(new PropertyValueFactory<>("superficie"));
        colHabitaciones.setCellValueFactory(new PropertyValueFactory<>("habitaciones"));
        colCosto.setCellValueFactory(c ->
                new ReadOnlyObjectWrapper<>(c.getValue().calcularCostoArriendo()));

        filtradas = new FilteredList<>(datos, v -> true);
        SortedList<Vivienda> ordenadas = new SortedList<>(filtradas);
        ordenadas.comparatorProperty().bind(tabla.comparatorProperty());
        tabla.setItems(ordenadas);

        txtBuscar.textProperty().addListener((obs, viejo, nuevo) -> {
            String texto = nuevo == null ? "" : nuevo.trim().toLowerCase();
            filtradas.setPredicate(v -> texto.isEmpty()
                    || v.getCodigo().toLowerCase().contains(texto)
                    || v.getTipo().toLowerCase().contains(texto));
            actualizarContador();
        });

        try {
            repository = new ViviendaRepository(new ViviendaDAOJson());
            cargarTabla();
        } catch (IllegalStateException e) {
            mostrarError("Error al cargar los datos: " + e.getMessage());
            btnNueva.setDisable(true);
            btnEliminar.setDisable(true);
        }
    }

    private void cargarTabla() {
        datos.setAll(repository.listar());
        actualizarContador();
    }

    private void actualizarContador() {
        lblContador.setText("Mostrando " + filtradas.size() + " de " + datos.size());
    }

    @FXML
    private void nueva() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/cl/duoc/viviendas/view/registro-view.fxml"));
            Parent root = loader.load();
            RegistroController controller = loader.getController();
            controller.setRepository(repository);
            tabla.getScene().setRoot(root);
        } catch (Exception e) {
            mostrarError("No se pudo abrir el formulario: " + e.getMessage());
        }
    }

    @FXML
    private void eliminar() {
        Vivienda seleccionada = tabla.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarError("Selecciona una vivienda para eliminar.");
            return;
        }
        try {
            repository.eliminar(seleccionada.getCodigo());
            cargarTabla();
            mostrarMensaje("Vivienda " + seleccionada.getCodigo() + " eliminada.");
        } catch (IllegalStateException e) {
            mostrarError("No se pudo eliminar: " + e.getMessage());
        }
    }

    public void mostrarMensaje(String texto) {
        lblMensaje.setStyle("-fx-text-fill: green;");
        lblMensaje.setText(texto);
    }

    private void mostrarError(String texto) {
        lblMensaje.setStyle("-fx-text-fill: red;");
        lblMensaje.setText(texto);
    }
}