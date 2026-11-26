package com.example.tracker.data;

import com.example.tracker.model.Entrevista;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a la tabla ENTREVISTA. La consulta une OFERTA y EMPRESA para mostrar
 * la candidatura y la empresa de cada entrevista.
 */
public class EntrevistaRepositorio {

    private static final String SELECT_BASE = """
            SELECT en.id, o.titulo, e.nombre AS empresa, en.fecha, en.tipo, en.notas, en.resultado
            FROM ENTREVISTA en
            JOIN OFERTA o ON o.id = en.id_oferta
            JOIN EMPRESA e ON e.id = o.id_empresa
            """;

    public List<Entrevista> listar() throws SQLException {
        String sql = SELECT_BASE + " ORDER BY en.fecha DESC, en.id DESC";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Entrevista> entrevistas = new ArrayList<>();
            while (rs.next()) {
                entrevistas.add(mapear(rs));
            }
            return entrevistas;
        }
    }

    /** Agenda una entrevista asociada a una candidatura (OFERTA) existente. */
    public long anadir(long idOferta, LocalDate fecha, String tipo,
                       String notas, String resultado) throws SQLException {
        validar(idOferta, fecha, tipo, resultado);

        String sql = """
                INSERT INTO ENTREVISTA (id_oferta, fecha, tipo, notas, resultado)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, idOferta);
            ps.setDate(2, Date.valueOf(fecha));
            ps.setString(3, tipo.trim());
            ps.setString(4, notas == null ? "" : notas.trim());
            ps.setString(5, resultado);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
        }

        throw new SQLException("No se pudo agendar la entrevista para la candidatura " + idOferta);
    }

    public boolean borrar(long id) throws SQLException {
        String sql = "DELETE FROM ENTREVISTA WHERE id = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private static void validar(long idOferta, LocalDate fecha, String tipo, String resultado) {
        if (idOferta <= 0) {
            throw new IllegalArgumentException("Selecciona una candidatura válida");
        }
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha de la entrevista es obligatoria");
        }
        if (tipo == null || tipo.isBlank()) {
            throw new IllegalArgumentException("El tipo de entrevista no puede estar vacío");
        }
        if (tipo.length() > 50) {
            throw new IllegalArgumentException("El tipo supera los 50 caracteres permitidos");
        }
        if (resultado == null || resultado.isBlank()) {
            throw new IllegalArgumentException("El resultado no puede estar vacío");
        }
        if (resultado.length() > 30) {
            throw new IllegalArgumentException("El resultado supera los 30 caracteres permitidos");
        }
    }

    private static Entrevista mapear(ResultSet rs) throws SQLException {
        return new Entrevista(
                rs.getLong("id"),
                rs.getString("titulo"),
                rs.getString("empresa"),
                rs.getDate("fecha").toLocalDate(),
                rs.getString("tipo"),
                rs.getString("notas"),
                rs.getString("resultado"));
    }
}
