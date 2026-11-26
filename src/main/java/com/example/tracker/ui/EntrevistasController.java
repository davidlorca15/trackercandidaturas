package com.example.tracker.ui;

import com.example.tracker.data.EntrevistaRepositorio;
import com.example.tracker.model.Entrevista;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Controlador de la pantalla de entrevistas (entrevistas.fxml).
 * Las consultas se ejecutan fuera del hilo de JavaFX.
 */
public class EntrevistasController {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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

    private final EntrevistaRepositorio repositorio = new EntrevistaRepositorio();
    private final ObservableList<Entrevista> entrevistas = FXCollections.observableArrayList();
    private final ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "tracker-db-entrevistas");
        t.setDaemon(true);
        return t;
    });

    @FXML
    private void initialize() {
        colFecha.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getFecha().format(FORMATO_FECHA)));
        colTipo.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getTipo()));
        colEstado.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getResultado()));
        colCandidatura.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue()));

        colCandidatura.setCellFactory(columna -> new TableCell<>() {
            @Override
            protected void updateItem(Entrevista entrevista, boolean vacia) {
                super.updateItem(entrevista, vacia);
                if (vacia || entrevista == null) {
                    setGraphic(null);
                    return;
                }
                Label titulo = new Label(entrevista.getTitulo());
                titulo.getStyleClass().add("texto-negrita");
                Label empresa = new Label(entrevista.getEmpresa());
                empresa.getStyleClass().add("texto-pequeno");
                setGraphic(new VBox(2, titulo, empresa));
            }
        });

        colEstado.setCellFactory(columna -> new TableCell<>() {
            @Override
            protected void updateItem(String resultado, boolean vacia) {
                super.updateItem(resultado, vacia);
                if (vacia || resultado == null) {
                    setGraphic(null);
                    return;
                }
                Label badge = new Label(etiquetaEstado(resultado));
                badge.getStyleClass().add("badge-" + claseEstado(resultado));
                setGraphic(badge);
            }
        });

        colAcciones.setCellFactory(columna -> new AccionesCell());

        tablaEntrevistas.setItems(entrevistas);
        cargarEntrevistas();
    }

    private void cargarEntrevistas() {
        executor.execute(new Task<List<Entrevista>>() {
            @Override
            protected List<Entrevista> call() throws Exception {
                return repositorio.listar();
            }

            @Override
            protected void succeeded() {
                entrevistas.setAll(getValue());
            }

            @Override
            protected void failed() {
                mostrarError("No se pudieron cargar las entrevistas.", getException());
            }
        });
    }

    private void borrar(Entrevista entrevista) {
        executor.execute(new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                repositorio.borrar(entrevista.getId());
                return null;
            }

            @Override
            protected void succeeded() {
                cargarEntrevistas();
            }

            @Override
            protected void failed() {
                mostrarError("No se pudo borrar la entrevista.", getException());
            }
        });
    }

    /** Celda de la columna Acciones con el botón Borrar. */
    private class AccionesCell extends TableCell<Entrevista, Void> {

        private final Button btnBorrar = new Button("Borrar");

        AccionesCell() {
            btnBorrar.setOnAction(e -> borrar(getTableView().getItems().get(getIndex())));
        }

        @Override
        protected void updateItem(Void item, boolean vacia) {
            super.updateItem(item, vacia);
            setGraphic(vacia ? null : btnBorrar);
        }
    }

    private static String etiquetaEstado(String resultado) {
        return switch (resultado) {
            case "positivo" -> "Superada";
            case "negativo" -> "No superada";
            default -> "Pendiente";
        };
    }

    private static String claseEstado(String resultado) {
        return switch (resultado) {
            case "positivo" -> "superada";
            case "negativo" -> "no-superada";
            default -> "pendiente";
        };
    }

    private void mostrarError(String mensaje, Throwable error) {
        new Alert(Alert.AlertType.ERROR,
                mensaje + "\n\n" + error.getMessage()
                        + "\n\nComprueba que MySQL esté arrancado y los datos de config.properties.",
                ButtonType.OK).show();
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