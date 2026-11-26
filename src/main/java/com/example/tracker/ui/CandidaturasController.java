package com.example.tracker.ui;

import com.example.tracker.business.ImportadorCsv;
import com.example.tracker.data.OfertaRepositorio;
import com.example.tracker.model.Oferta;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Controlador de la pantalla de candidaturas (candidaturas.fxml).
 * Conecta la vista con MySQL a través de {@link OfertaRepositorio}: carga la
 * tabla, aplica filtros, actualiza los indicadores y permite importar, añadir
 * y borrar candidaturas. Las consultas se ejecutan fuera del hilo de JavaFX.
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

    private final OfertaRepositorio repositorio = new OfertaRepositorio();
    private final ImportadorCsv importador = new ImportadorCsv();

    private final ObservableList<Oferta> ofertas = FXCollections.observableArrayList();
    private final FilteredList<Oferta> filtradas = new FilteredList<>(ofertas);

    private final ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "tracker-db-worker");
        t.setDaemon(true);
        return t;
    });

    @FXML
    private void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colPuesto.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colEmpresa.setCellValueFactory(new PropertyValueFactory<>("empresa"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaPostulacion"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colAcciones.setCellFactory(columna -> new TableCell<>() {
            private final Button botonBorrar = new Button("Borrar");

            {
                botonBorrar.setOnAction(evento -> {
                    Oferta oferta = getTableView().getItems().get(getIndex());
                    borrar(oferta);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : botonBorrar);
            }
        });

        tablaCandidaturas.setItems(filtradas);

        cmbEstado.getItems().addAll("Todos", "Pendiente", "Entrevista", "Rechazado", "Aceptado");
        cmbEstado.getSelectionModel().selectFirst();
        cmbEstado.getSelectionModel().selectedItemProperty()
                .addListener((obs, antes, ahora) -> cargarOfertas());

        txtBuscar.textProperty().addListener((obs, antes, ahora) -> aplicarFiltroTexto());

        cargarOfertas();
    }

    /** Trae las candidaturas de MySQL. "Todos" no envía filtro de estado. */
    private void cargarOfertas() {
        String estado = cmbEstado.getValue();
        String filtroEstado = (estado == null || "Todos".equals(estado)) ? null : estado;

        ejecutar(new Task<List<Oferta>>() {
            @Override
            protected List<Oferta> call() throws Exception {
                return repositorio.listar(filtroEstado, null);
            }

            @Override
            protected void succeeded() {
                ofertas.setAll(getValue());
                aplicarFiltroTexto();
                actualizarEstadisticas();
            }

            @Override
            protected void failed() {
                mostrarError("No se pudieron cargar las candidaturas de la base de datos.", getException());
            }
        });
    }

    /** Búsqueda local por puesto o empresa, sin distinguir mayúsculas. */
    private void aplicarFiltroTexto() {
        String texto = txtBuscar.getText() == null ? "" : txtBuscar.getText().trim().toLowerCase(Locale.ROOT);
        filtradas.setPredicate(oferta -> texto.isEmpty()
                || contiene(oferta.getTitulo(), texto)
                || contiene(oferta.getEmpresa(), texto));
    }

    private static boolean contiene(String valor, String texto) {
        return valor != null && valor.toLowerCase(Locale.ROOT).contains(texto);
    }

    private void actualizarEstadisticas() {
        long total = ofertas.size();
        lblTotal.setText(String.valueOf(total));
        lblPendientes.setText(String.valueOf(contar("Pendiente")));
        lblEntrevista.setText(String.valueOf(contar("Entrevista")));
        lblAceptadas.setText(String.valueOf(contar("Aceptado")));
        lblSubtitulo.setText(total + " candidaturas · base de datos");
    }

    private long contar(String estado) {
        return ofertas.stream().filter(o -> estado.equalsIgnoreCase(o.getEstado())).count();
    }

    @FXML
    private void onFiltrar() {
        cargarOfertas();
    }

    @FXML
    private void onImportarCsv() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Importar candidaturas (CSV)");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
        File archivo = chooser.showOpenDialog(tablaCandidaturas.getScene().getWindow());
        if (archivo == null) {
            return;
        }

        ejecutar(new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {
                return importador.importarDesdeCsv(archivo.toPath());
            }

            @Override
            protected void succeeded() {
                new Alert(Alert.AlertType.INFORMATION,
                        "Importadas " + getValue() + " candidaturas en MySQL.", ButtonType.OK).show();
                cargarOfertas();
            }

            @Override
            protected void failed() {
                mostrarError("No se pudo importar el CSV.", getException());
            }
        });
    }

    @FXML
    private void onNuevaCandidatura() {
        Dialog<Oferta> dialogo = new Dialog<>();
        dialogo.setTitle("Nueva candidatura");
        dialogo.setHeaderText("Introduce los datos de la candidatura");
        dialogo.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField txtTitulo = new TextField();
        txtTitulo.setPromptText("Ej. Desarrollador Java");
        TextField txtEmpresa = new TextField();
        txtEmpresa.setPromptText("Ej. Acme");
        ComboBox<String> cmbNuevoEstado = new ComboBox<>(
                FXCollections.observableArrayList("Pendiente", "Entrevista", "Rechazado", "Aceptado"));
        cmbNuevoEstado.getSelectionModel().selectFirst();
        DatePicker dpFecha = new DatePicker(LocalDate.now());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.add(new Label("Puesto:"), 0, 0);
        grid.add(txtTitulo, 1, 0);
        grid.add(new Label("Empresa:"), 0, 1);
        grid.add(txtEmpresa, 1, 1);
        grid.add(new Label("Estado:"), 0, 2);
        grid.add(cmbNuevoEstado, 1, 2);
        grid.add(new Label("Fecha:"), 0, 3);
        grid.add(dpFecha, 1, 3);
        dialogo.getDialogPane().setContent(grid);

        dialogo.setResultConverter(boton -> {
            if (boton == ButtonType.OK && !txtTitulo.getText().isBlank() && !txtEmpresa.getText().isBlank()) {
                return new Oferta(0, txtTitulo.getText().trim(), txtEmpresa.getText().trim(),
                        cmbNuevoEstado.getValue(), dpFecha.getValue(), "", "");
            }
            return null;
        });

        dialogo.showAndWait().ifPresent(oferta -> ejecutar(new Task<Long>() {
            @Override
            protected Long call() throws Exception {
                return repositorio.anadir(oferta.getTitulo(), oferta.getEmpresa(), oferta.getEstado(),
                        oferta.getFechaPostulacion(), "", "");
            }

            @Override
            protected void succeeded() {
                cargarOfertas();
            }

            @Override
            protected void failed() {
                mostrarError("No se pudo guardar la candidatura.", getException());
            }
        }));
    }

    private void borrar(Oferta oferta) {
        ejecutar(new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                repositorio.borrar(oferta.getId());
                return null;
            }

            @Override
            protected void succeeded() {
                cargarOfertas();
            }

            @Override
            protected void failed() {
                mostrarError("No se pudo borrar la candidatura " + oferta.getId() + ".", getException());
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
}
