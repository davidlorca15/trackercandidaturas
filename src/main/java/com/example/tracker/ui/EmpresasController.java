package com.example.tracker.ui;

import com.example.tracker.data.EmpresaRepositorio;
import com.example.tracker.model.Empresa;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Controlador de la pantalla de empresas (empresas.fxml). Carga el directorio
 * desde MySQL y permite dar de alta empresas nuevas desde el botón principal.
 */
public class EmpresasController {

    @FXML
    private VBox sidebar;
    @FXML
    private TextField txtBuscar;
    @FXML
    private TableColumn<Empresa, String> colNombre, colSector, colWeb;
    @FXML
    private TableColumn<Empresa, Number> colCandidaturas;
    @FXML
    private TableColumn<Empresa, Void> colAcciones;
    @FXML
    private TableView<Empresa> tablaEmpresas;

    private final EmpresaRepositorio repositorio = new EmpresaRepositorio();
    private final ObservableList<Empresa> empresas = FXCollections.observableArrayList();

    private final ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "tracker-empresas-worker");
        t.setDaemon(true);
        return t;
    });

    @FXML
    private void initialize() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colSector.setCellValueFactory(new PropertyValueFactory<>("sector"));
        colWeb.setCellValueFactory(new PropertyValueFactory<>("web"));
        colCandidaturas.setCellValueFactory(new PropertyValueFactory<>("candidaturasActivas"));

        colAcciones.setCellFactory(columna -> new TableCell<>() {
            private final Button botonVer = new Button("Ver");

            {
                botonVer.setOnAction(evento -> {
                    Empresa empresa = getTableView().getItems().get(getIndex());
                    new Alert(Alert.AlertType.INFORMATION,
                            empresa.getNombre() + "\nSector: " + empresa.getSector()
                                    + "\nWeb: " + empresa.getWeb()
                                    + "\nCandidaturas: " + empresa.getCandidaturasActivas(),
                            ButtonType.OK).show();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : botonVer);
            }
        });

        tablaEmpresas.setItems(empresas);
        cargarEmpresas(null);
    }

    /** Trae las empresas de MySQL; un filtro vacío muestra todas. */
    private void cargarEmpresas(String filtro) {
        ejecutar(new Task<List<Empresa>>() {
            @Override
            protected List<Empresa> call() throws Exception {
                return repositorio.listar(filtro);
            }

            @Override
            protected void succeeded() {
                empresas.setAll(getValue());
            }

            @Override
            protected void failed() {
                mostrarError("No se pudieron cargar las empresas de la base de datos.", getException());
            }
        });
    }

    @FXML
    private void onBuscar() {
        cargarEmpresas(txtBuscar.getText());
    }

    @FXML
    private void onNuevaEmpresa() {
        Dialog<ButtonType> dialogo = new Dialog<>();
        dialogo.setTitle("Nueva empresa");
        dialogo.setHeaderText("Introduce los datos de la empresa");
        dialogo.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Ej. Acme");
        TextField txtSector = new TextField();
        txtSector.setPromptText("Ej. Consultoría");
        TextField txtWeb = new TextField();
        txtWeb.setPromptText("Ej. https://www.acme.com");
        DatePicker dpFecha = new DatePicker(LocalDate.now());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.add(new Label("Nombre:"), 0, 0);
        grid.add(txtNombre, 1, 0);
        grid.add(new Label("Sector:"), 0, 1);
        grid.add(txtSector, 1, 1);
        grid.add(new Label("Web:"), 0, 2);
        grid.add(txtWeb, 1, 2);
        grid.add(new Label("Fecha registro:"), 0, 3);
        grid.add(dpFecha, 1, 3);
        dialogo.getDialogPane().setContent(grid);

        if (dialogo.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        String nombre = txtNombre.getText() == null ? "" : txtNombre.getText().trim();
        if (nombre.isBlank()) {
            new Alert(Alert.AlertType.WARNING, "El nombre de la empresa es obligatorio.", ButtonType.OK).show();
            return;
        }
        String sector = txtSector.getText() == null ? "" : txtSector.getText().trim();
        String web = txtWeb.getText() == null ? "" : txtWeb.getText().trim();
        LocalDate fecha = dpFecha.getValue() == null ? LocalDate.now() : dpFecha.getValue();

        ejecutar(new Task<Long>() {
            @Override
            protected Long call() throws Exception {
                return repositorio.anadir(nombre, sector, web, fecha);
            }

            @Override
            protected void succeeded() {
                cargarEmpresas(txtBuscar.getText());
                new Alert(Alert.AlertType.INFORMATION,
                        "Empresa \"" + nombre + "\" guardada en la base de datos.", ButtonType.OK).show();
            }

            @Override
            protected void failed() {
                mostrarError("No se pudo guardar la empresa.", getException());
            }
        });
    }

    private void ejecutar(Task<?> tarea) {
        executor.execute(tarea);
    }

    private void mostrarError(String mensaje, Throwable error) {
        Throwable causa = error.getCause() == null ? error : error.getCause();
        new Alert(Alert.AlertType.ERROR,
                mensaje + "\n\n" + causa.getMessage()
                        + "\n\nComprueba que MySQL esté arrancado y los datos de config.properties.",
                ButtonType.OK).show();
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
