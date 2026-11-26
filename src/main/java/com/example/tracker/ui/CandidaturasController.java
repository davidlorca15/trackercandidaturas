package com.example.tracker.ui;

import com.example.tracker.model.Oferta;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controlador de la pantalla de candidaturas (candidaturas.fxml).
 */
public class CandidaturasController {

    @FXML
    private Label lblSubtitulo, lblTotal, lblPendientes, lblEntrevista, lblAceptadas;
    @FXML
    private TextField txtBuscar;
    @FXML
    private ComboBox<String> cmbEstado;
    @FXML
    private TableView<Oferta> tablaCandidaturas;
    @FXML
    private TableColumn<Oferta, Long> colId;
    @FXML
    private TableColumn<Oferta, String> colPuesto, colEmpresa, colFecha, colEstado;
    @FXML
    private TableColumn<Oferta, Void> colAcciones;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colPuesto.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaPostulacion"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        cmbEstado.getItems().addAll("Todos", "Pendiente", "Entrevista", "Rechazado", "Aceptado");
        cmbEstado.getSelectionModel().selectFirst();
    }

    @FXML
    private void onNavInicio() { }

    @FXML
    private void onNavCandidaturas() { }

    @FXML
    private void onNavEmpresas() { }

    @FXML
    private void onNavEntrevistas() { }

    @FXML
    private void onNavEstadisticas() { }

    @FXML
    private void onNavAjustes() { }

    @FXML
    private void onImportarCsv() { }

    @FXML
    private void onNuevaCandidatura() { }

    @FXML
    private void onFiltrar() { }
}