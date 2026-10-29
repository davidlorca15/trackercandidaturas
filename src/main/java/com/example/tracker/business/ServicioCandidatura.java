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

public class ServicioCandidatura {
    private final ArchivoCandidatura repository = new ArchivoCandidatura();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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