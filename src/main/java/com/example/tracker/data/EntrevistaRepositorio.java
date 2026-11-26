package com.example.tracker.data;

import com.example.tracker.model.Entrevista;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    public boolean borrar(long id) throws SQLException {
        String sql = "DELETE FROM ENTREVISTA WHERE id = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
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
