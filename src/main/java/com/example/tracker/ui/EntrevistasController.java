package com.example.tracker.ui;

import com.example.tracker.model.Entrevista;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Controlador de la pantalla de entrevistas (entrevistas.fxml).
 */
public class EntrevistasController {

    @FXML
    private VBox sidebar;
    @FXML
    private TableView<Entrevista> tablaEntrevistas;
    @FXML
    private TableColumn<Entrevista, String> colFecha, colTipo, colEstado;
    @FXML
    private TableColumn<Entrevista, Entrevista> colCandidatura;
    @FXML
    private TableColumn<Entrevista, Void> colAcciones;

    @FXML
    private void initialize() {
    }

    @FXML
    private void onAgendarEntrevista() {
    }

    @FXML
    private void onNavInicio() {
        irA("/com/example/tracker/candidaturas.fxml");
    }

    @FXML
    private void onNavCandidaturas() {
        irA("/com/example/tracker/candidaturas.fxml");
    }

    @FXML
    private void onNavEmpresas() {
        irA("/com/example/tracker/empresas.fxml");
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

    private void irA(String rutaFxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFxml));
            Parent raiz = loader.load();
            Stage stage = (Stage) sidebar.getScene().getWindow();
            stage.setScene(new Scene(raiz, 1440, 900));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}