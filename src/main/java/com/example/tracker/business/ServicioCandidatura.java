package com.example.tracker.business;

import com.example.tracker.data.ArchivoCandidatura;
import com.example.tracker.model.Oferta;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
/**
 * Servicio de lógica de negocio para gestionar las candidaturas y ofertas de empleo.
 * Se encarga de coordinar las operaciones de listado, importación, adición, consulta
 * y borrado interactuando con el repositorio de datos.
 *
 * @author Rodrigo Franco
 * @version 1.0
 */
public class ServicioCandidatura {

    /** Repositorio encargado de la persistencia y lectura de los datos de las candidaturas. */
    private final ArchivoCandidatura repository = new ArchivoCandidatura();

    /** Formateador de fechas estándar utilizado para parsear y estructurar las fechas en formato dd/MM/yyyy. */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Lista las ofertas de empleo almacenadas, permitiendo filtrarlas de forma opcional
     * por su estado y/o por el nombre de la empresa.
     *
     * @param estado El estado por el cual filtrar las ofertas (puede ser null).
     * @param empresa El nombre de la empresa por el cual filtrar las ofertas (puede ser null).
     */

    public void listar(String estado, String empresa) {
        List<Oferta> ofertas = repository.leerTodas();

        if (estado != null) {
            ofertas = ofertas.stream().filter(o -> o.getEstado().equalsIgnoreCase(estado)).collect(Collectors.toList());
        }
        if (empresa != null) {
            ofertas = ofertas.stream().filter(o -> o.getEmpresa().equalsIgnoreCase(empresa)).collect(Collectors.toList());
        }

        if (ofertas.isEmpty()) {
            System.out.println("No se encontraron candidaturas.");
        } else {
            ofertas.forEach(System.out::println);
        }
    }


    /**
     * Importa un lote de ofertas de empleo desde un archivo CSV externo,
     * asignándoles nuevos IDs secuenciales e integrándolas al repositorio existente.
     *
     * @param csvPath La ruta del archivo CSV que se desea importar.
     */

    public void importar(String csvPath) {
        Path path = Paths.get(csvPath);
        if (!Files.exists(path)) {
            System.out.println("Error: Archivo no encontrado - " + csvPath);
            return;
        }
        try {
            List<String> lineas = Files.readAllLines(path);
            List<Oferta> ofertasExistentes = repository.leerTodas();
            long maxId = ofertasExistentes.stream().mapToLong(Oferta::getId).max().orElse(0);

            for (int i = 1; i < lineas.size(); i++) {
                String[] p = lineas.get(i).split(";", -1);
                if (p.length >= 7) {
                    ofertasExistentes.add(new Oferta(++maxId, p[1], p[2], p[3], LocalDate.parse(p[4], DATE_FORMATTER), p[5], p[6]));
                }
            }
            repository.guardarTodas(ofertasExistentes);
            System.out.println("Importación completada con éxito desde " + csvPath);
        } catch (Exception e) {
            System.out.println("Error al importar: " + e.getMessage());
        }
    }

    /**
     * Añade una nueva candidatura al sistema con los datos básicos proporcionados
     * y genera automáticamente un ID secuencial único.
     *
     * @param titulo El título o puesto de la oferta.
     * @param empresa El nombre de la empresa.
     * @param estado El estado inicial de la postulación.
     * @param fechaStr La fecha de postulación en formato de texto (dd/MM/yyyy).
     */

    public void añadir(String titulo, String empresa, String estado, String fechaStr) {
        List<Oferta> ofertas = repository.leerTodas();
        long nextId = ofertas.stream().mapToLong(Oferta::getId).max().orElse(0) + 1;

        LocalDate fecha = LocalDate.parse(fechaStr, DATE_FORMATTER);
        Oferta nueva = new Oferta(nextId, titulo, empresa, estado, fecha, "", "");

        ofertas.add(nueva);
        repository.guardarTodas(ofertas);
        System.out.println("Candidatura añadida con éxito con ID: " + nextId);
    }

    public void ver(long id) {
        List<Oferta> ofertas = repository.leerTodas();
        Optional<Oferta> oferta = ofertas.stream().filter(o -> o.getId() == id).findFirst();

        if (oferta.isPresent()) {
            System.out.println(oferta.get().toString());
        } else {
            System.out.println("Error: ID inexistente.");
        }
    }

    /**
     * Elimina una candidatura del sistema basándose en su identificador único.
     *
     * @param id El identificador único de la oferta que se quiere borrar.
     */
    /**
     * * Elimina una candidatura del sistema basándose en su identificador único.
     * *
     * * @param id El identificador único de la oferta que se quiere borrar.
     *
     */

    public void borrar(long id) {
        List<Oferta> ofertas = repository.leerTodas();
        boolean removed = ofertas.removeIf(o -> o.getId() == id);

        if (removed) {
            repository.guardarTodas(ofertas);
            System.out.println("Candidatura " + id + " eliminada correctamente.");
        } else {
            System.out.println("Error: ID inexistente.");
        }
    }
}