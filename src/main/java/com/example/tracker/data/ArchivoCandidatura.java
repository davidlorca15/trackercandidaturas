package com.example.tracker.data;

import com.example.tracker.model.Oferta;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ArchivoCandidatura {
    private static final String FILE_PATH = "candidaturas.csv";
    private static final String SEPARATOR = ";";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public List<Oferta> leerTodas() {
        List<Oferta> ofertas = new ArrayList<>();
        Path path = Paths.get(FILE_PATH);

        if (!Files.exists(path)) return ofertas;

        try {
            List<String> lineas = Files.readAllLines(path);
            for (int i = 1; i < lineas.size(); i++) {
                String[] partes = lineas.get(i).split(SEPARATOR, -1);
                if (partes.length >= 7) {
                    Oferta oferta = new Oferta(
                            Long.parseLong(partes[0]), partes[1], partes[2], partes[3],
                            LocalDate.parse(partes[4], DATE_FORMATTER), partes[5], partes[6]
                    );
                    ofertas.add(oferta);
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo de candidaturas: " + e.getMessage());
        }
        return ofertas;
    }

    public void guardarTodas(List<Oferta> ofertas) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
            writer.println("id;titulo;empresa;estado;fechaPostulacion;enlace;notas");
            for (Oferta o : ofertas) {
                writer.printf("%d;%s;%s;%s;%s;%s;%s%n",
                        o.getId(), o.getTitulo(), o.getEmpresa(), o.getEstado(),
                        o.getFechaPostulacion().format(DATE_FORMATTER),
                        o.getEnlace(), o.getNotas());
            }
        } catch (IOException e) {
            System.err.println("Error al guardar en el archivo: " + e.getMessage());
        }
    }
}