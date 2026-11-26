package com.example.tracker.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;

public class EmpresasController {

    public TextField txtBuscar;
    public TableColumn<?, ?> colNombre;
    public TableColumn<?, ?> colSector;
    public TableColumn<?, ?> colWeb;
    public TableColumn<?, ?> colCandidaturas;
    public TableColumn<?, ?> colAcciones;
    public TableView<?> tablaEmpresas;
    @FXML
    private VBox sidebar;

    @FXML
    private void initialize() {
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
    }

    @FXML
    private void onNavEntrevistas() {
        irA("/com/example/tracker/entrevistas.fxml");
    }

    @FXML
    private void onNavEstadisticas() {
    }

    @FXML
    private void onNavAjustes() {
    }

    @FXML
    private void onBuscar() {
    }

    @FXML
    private void onNuevaEmpresa() {
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