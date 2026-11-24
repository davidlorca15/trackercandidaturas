package com.example.tracker.data;

import com.example.tracker.model.Oferta;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Acceso a la tabla OFERTA de MySQL. Todas las consultas devuelven el nombre de
 * la empresa mediante un JOIN con EMPRESA para construir el modelo de dominio.
 */
public class OfertaRepositorio {

    private static final String SELECT_BASE = """
            SELECT o.id, o.titulo, e.nombre AS empresa, o.estado,
                   o.fechaPostulacion, o.enlace, o.notas
            FROM OFERTA o
            JOIN EMPRESA e ON e.id = o.id_empresa
            """;

    private final EmpresaRepositorio empresas = new EmpresaRepositorio();

    public List<Oferta> listar(String estado, String empresa) throws SQLException {
        StringBuilder sql = new StringBuilder(SELECT_BASE);
        List<String> parametros = new ArrayList<>();

        boolean hayEstado = estado != null && !estado.isBlank();
        boolean hayEmpresa = empresa != null && !empresa.isBlank();

        if (hayEstado) {
            sql.append(" WHERE LOWER(o.estado) = LOWER(?)");
            parametros.add(estado);
        }
        if (hayEmpresa) {
            sql.append(hayEstado ? " AND " : " WHERE ");
            sql.append("LOWER(e.nombre) = LOWER(?)");
            parametros.add(empresa);
        }
        sql.append(" ORDER BY o.id");

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < parametros.size(); i++) {
                ps.setString(i + 1, parametros.get(i));
            }
            List<Oferta> ofertas = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ofertas.add(mapear(rs));
                }
            }
            return ofertas;
        }
    }

    public Optional<Oferta> buscarPorId(long id) throws SQLException {
        String sql = SELECT_BASE + " WHERE o.id = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        }
    }

    /** Crea la empresa si hace falta y la oferta en una única transacción. */
    public long anadir(String titulo, String empresa, String estado,
                       LocalDate fecha, String enlace, String notas) throws SQLException {
        validar(titulo, estado, enlace);

        try (Connection conn = ConexionBD.getConnection()) {
            conn.setAutoCommit(false);
            try {
                long idEmpresa = empresas.obtenerOCrear(conn, empresa, fecha);
                long idOferta = insertar(conn, idEmpresa, titulo, estado, fecha, enlace, notas);
                conn.commit();
                return idOferta;
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    /** Inserta una oferta usando una conexión ya abierta (la usa el importador CSV). */
    public long insertar(Connection conn, long idEmpresa, String titulo, String estado,
                         LocalDate fecha, String enlace, String notas) throws SQLException {
        validar(titulo, estado, enlace);

        String sql = """
                INSERT INTO OFERTA (id_empresa, titulo, descripcion, estado, fechaPostulacion, enlace, salario, notas)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, idEmpresa);
            ps.setString(2, titulo);
            ps.setString(3, "");
            ps.setString(4, estado);
            ps.setDate(5, Date.valueOf(fecha));
            ps.setString(6, enlace);
            ps.setNull(7, Types.DECIMAL);
            ps.setString(8, notas);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
        }

        throw new SQLException("No se pudo insertar la oferta: " + titulo);
    }

    public boolean borrar(long id) throws SQLException {
        String sql = "DELETE FROM OFERTA WHERE id = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    /** Evita errores de la BD por campos más largos que las columnas VARCHAR. */
    public static void validar(String titulo, String estado, String enlace) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("El título no puede estar vacío");
        }
        if (titulo.length() > 150) {
            throw new IllegalArgumentException("El título supera los 150 caracteres permitidos");
        }
        if (estado == null || estado.isBlank()) {
            throw new IllegalArgumentException("El estado no puede estar vacío");
        }
        if (estado.length() > 30) {
            throw new IllegalArgumentException("El estado supera los 30 caracteres permitidos (VARCHAR(30))");
        }
        if (enlace != null && enlace.length() > 255) {
            throw new IllegalArgumentException("El enlace supera los 255 caracteres permitidos");
        }
    }

    private static Oferta mapear(ResultSet rs) throws SQLException {
        return new Oferta(
                rs.getLong("id"),
                rs.getString("titulo"),
                rs.getString("empresa"),
                rs.getString("estado"),
                rs.getDate("fechaPostulacion").toLocalDate(),
                rs.getString("enlace"),
                rs.getString("notas")
        );
    }
}
