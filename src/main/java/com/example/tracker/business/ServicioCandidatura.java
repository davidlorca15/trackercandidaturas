package com.example.tracker.business;

import com.example.tracker.data.OfertaRepositorio;
import com.example.tracker.model.Oferta;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Lógica de negocio: coordina las operaciones de la CLI con la base de datos
 * MySQL a través de {@link OfertaRepositorio} y {@link ImportadorCsv}.
 *
 * @author Rodrigo Franco, David Lorca
 * @version 1.0
 */
public class ServicioCandidatura {

    private final OfertaRepositorio repository = new OfertaRepositorio();
    private final ImportadorCsv importador = new ImportadorCsv();

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public void listar(String estado, String empresa) {
        try {
            List<Oferta> ofertas = repository.listar(estado, empresa);
            if (ofertas.isEmpty()) {
                System.out.println("No se encontraron candidaturas.");
            } else {
                ofertas.forEach(System.out::println);
            }
        } catch (Exception e) {
            System.out.println("Error al listar: " + e.getMessage());
        }
    }

    /** Importa un CSV a MySQL. La operación es atómica: o se insertan todas o ninguna. */
    public void importar(String csvPath) {
        Path path = Paths.get(csvPath);
        if (!Files.exists(path)) {
            System.out.println("Error: Archivo no encontrado - " + csvPath);
            return;
        }
        try {
            int insertadas = importador.importarDesdeCsv(path);
            System.out.println("Importación completada: " + insertadas + " ofertas insertadas en MySQL.");
        } catch (Exception e) {
            System.out.println("Error al importar: " + e.getMessage());
        }
    }

    public void añadir(String titulo, String empresa, String estado, String fechaStr) {
        try {
            LocalDate fecha = LocalDate.parse(fechaStr, DATE_FORMATTER);
            long id = repository.anadir(titulo, empresa, estado, fecha, "", "");
            System.out.println("Candidatura añadida con éxito con ID: " + id);
        } catch (Exception e) {
            System.out.println("Error al añadir: " + e.getMessage());
        }
    }

    public void ver(long id) {
        try {
            Optional<Oferta> oferta = repository.buscarPorId(id);
            if (oferta.isPresent()) {
                System.out.println(oferta.get());
            } else {
                System.out.println("Error: ID inexistente.");
            }
        } catch (Exception e) {
            System.out.println("Error al consultar: " + e.getMessage());
        }
    }

    public void borrar(long id) {
        try {
            if (repository.borrar(id)) {
                System.out.println("Candidatura " + id + " eliminada correctamente.");
            } else {
                System.out.println("Error: ID inexistente.");
            }
        } catch (Exception e) {
            System.out.println("Error al borrar: " + e.getMessage());
        }
    }
}
