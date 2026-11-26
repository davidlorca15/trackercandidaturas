package com.example.tracker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Punto de entrada de la interfaz gráfica JavaFX.
 * Carga la pantalla de candidaturas diseñada con Scene Builder.
 * EL inicio de la v1.0 sigue en "Main".
 */
public class MainGui extends Application {

    private static final String VISTA_CANDIDATURAS = "/com/example/tracker/candidaturas.fxml";

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(VISTA_CANDIDATURAS));
        Parent raiz = loader.load();

        stage.setTitle("Candia");
        stage.setScene(new Scene(raiz, 1440, 900));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}