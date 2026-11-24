package com.example.tracker.business;

import com.example.tracker.data.ConexionBD;
import com.example.tracker.data.EmpresaRepositorio;
import com.example.tracker.data.OfertaRepositorio;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Importa candidaturas desde un CSV delimitado por ';' directamente a MySQL.
 * Toda la importación se realiza en una única transacción: si falla una fila,
 * no se guarda ninguna.
 */
public class ImportadorCsv {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final EmpresaRepositorio empresas = new EmpresaRepositorio();
    private final OfertaRepositorio ofertas = new OfertaRepositorio();

    public int importarDesdeCsv(Path rutaCsv) throws Exception {
        if (!Files.exists(rutaCsv)) {
            throw new IllegalArgumentException("No se encontró el archivo CSV: " + rutaCsv);
        }

        List<String> lineas = Files.readAllLines(rutaCsv, StandardCharsets.UTF_8);
        int insertadas = 0;

        try (Connection conn = ConexionBD.getConnection()) {
            conn.setAutoCommit(false);
            try {
                for (int i = 1; i < lineas.size(); i++) {
                    String linea = lineas.get(i).trim();
                    if (linea.isEmpty()) {
                        continue;
                    }

                    String[] cols = linea.split(";", -1);
                    if (cols.length < 7) {
                        throw new IllegalArgumentException("Línea " + (i + 1) + " con formato inválido: " + linea);
                    }

                    String titulo = cols[1].trim();
                    String nombreEmpresa = cols[2].trim();
                    String estado = cols[3].trim();
                    LocalDate fechaPostulacion = LocalDate.parse(cols[4].trim(), FORMATO_FECHA);
                    String enlace = cols[5].trim();
                    String notas = cols[6].trim();

                    long idEmpresa = empresas.obtenerOCrear(conn, nombreEmpresa, fechaPostulacion);
                    ofertas.insertar(conn, idEmpresa, titulo, estado, fechaPostulacion, enlace, notas);
                    insertadas++;
                }

                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }

        return insertadas;
    }
}
