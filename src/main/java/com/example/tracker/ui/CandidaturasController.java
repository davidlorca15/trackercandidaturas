package com.example.tracker.ui;

package com.example.tracker;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

public class CandidaturasController {

    public VBox sidebar;
    @FXML
    private Label lblSubtitulo, lblTotal, lblPendientes, lblEntrevista, lblAceptadas;
    @FXML
    private TextField txtBuscar;
    @FXML
    private ComboBox<String> cmbEstado;
    @FXML
    private TableView<Candidatura> tablaCandidaturas;
    @FXML
    private TableColumn<Candidatura, Integer> colId;
    @FXML
    private TableColumn<Candidatura, String> colPuesto, colEmpresa, colFecha, colEstado;
    @FXML
    private TableColumn<Candidatura, Void> colAcciones;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colPuesto.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colEmpresa.setCellValueFactory(new PropertyValueFactory<>("empresa"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaPostulacion"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        cmbEstado.getItems().addAll("Todos", "Pendiente", "Entrevista", "Rechazado", "Aceptado");
        cmbEstado.getSelectionModel().selectFirst();

        // Aquí cargarás los datos del servicio/repositorio
    }

    @FXML
    private void onNavInicio() {
    }

    @FXML
    private void onNavCandidaturas() {
    }

    @FXML
    private void onNavEmpresas() {
    }

    @FXML
    private void onNavEntrevistas() {
    }

    @FXML
    private void onNavEstadisticas() {
    }

    @FXML
    private void onNavAjustes() {
    }

    @FXML
    private void onImportarCsv() {
    }

    @FXML
    private void onNuevaCandidatura() {
    }

    @FXML
    private void onFiltrar() {
    }
}