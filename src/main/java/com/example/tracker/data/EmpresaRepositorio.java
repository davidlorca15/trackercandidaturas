package com.example.tracker.data;

import com.example.tracker.model.Empresa;

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
 * Acceso a la tabla EMPRESA. Reutiliza la empresa si ya existe (comparando el
 * nombre sin distinción de mayúsculas) y la crea en caso contrario.
 */
public class EmpresaRepositorio {

    /**
     * Busca una empresa por nombre (sin distinguir mayúsculas ni minúsculas) usando
     * la conexión recibida; si no existe, la crea con sector y web vacíos.
     *
     * @param conn          conexión JDBC activa (permite participar en transacciones externas)
     * @param nombreEmpresa nombre de la empresa a buscar o crear
     * @param fechaRegistro fecha de registro a asignar si se crea una nueva empresa
     * @return identificador ({@code id}) de la empresa existente o recién creada
     * @throws SQLException             si ocurre un error al consultar o insertar en {@code EMPRESA}
     * @throws IllegalArgumentException si el nombre es nulo, está en blanco o supera los 150 caracteres
     */

    public long obtenerOCrear(Connection conn, String nombreEmpresa, LocalDate fechaRegistro) throws SQLException {
        validar(nombreEmpresa);

        String buscar = "SELECT id FROM EMPRESA WHERE LOWER(nombre) = LOWER(?)";
        try (PreparedStatement ps = conn.prepareStatement(buscar)) {
            ps.setString(1, nombreEmpresa);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id");
                }
            }
        }

        /**
         * Crea una empresa nueva en una conexión propia. No permite repetir el nombre
         * sin distinción de mayúsculas.
         *
         * @param nombre        nombre de la empresa
         * @param sector        sector de actividad (opcional, máximo 100 caracteres)
         * @param web           sitio web de la empresa (opcional, máximo 255 caracteres)
         * @param fechaRegistro fecha de registro de la empresa
         * @return identificador ({@code id}) generado para la nueva empresa
         * @throws SQLException             si ya existe una empresa con ese nombre o falla la operación SQL
         * @throws IllegalArgumentException si el nombre no es válido o algún campo opcional excede su longitud máxima
         */

        return insertar(conn, nombreEmpresa, "", "", fechaRegistro);
    }

    /** Crea una empresa nueva. No permite repetir el nombre (sin distinguir mayúsculas). */
    public long anadir(String nombre, String sector, String web, LocalDate fechaRegistro) throws SQLException {
        validar(nombre);
        validarCamposOpcionales(sector, web);

        try (Connection conn = ConexionBD.getConnection()) {
            String buscar = "SELECT id FROM EMPRESA WHERE LOWER(nombre) = LOWER(?)";
            try (PreparedStatement ps = conn.prepareStatement(buscar)) {
                ps.setString(1, nombre);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        throw new SQLException("Ya existe una empresa con el nombre \"" + nombre + "\".");
                    }
                }
            }
            return insertar(conn, nombre, sector, web, fechaRegistro);
        }
    }

    /**
     * Lista las empresas ordenadas por nombre junto con el número de ofertas
     * ({@code OFERTA}) vinculadas a cada una, filtrando opcionalmente por nombre
     * o sector.
     *
     * @param filtro texto de búsqueda parcial para filtrar por nombre o sector; si es
     *               nulo o está en blanco, devuelve todas las empresas
     * @return lista de {@link Empresa} con su contador de candidaturas/ofertas
     * @throws SQLException si ocurre un error al ejecutar la consulta en base de datos
     */

    /** Lista las empresas con su número de candidaturas, opcionalmente filtradas por nombre o sector. */
    public List<Empresa> listar(String filtro) throws SQLException {
        StringBuilder sql = new StringBuilder("""
                SELECT e.id, e.nombre, e.sector, e.web, COUNT(o.id) AS candidaturas
                FROM EMPRESA e
                LEFT JOIN OFERTA o ON o.id_empresa = e.id
                """);
        List<String> parametros = new ArrayList<>();

        if (filtro != null && !filtro.isBlank()) {
            sql.append(" WHERE LOWER(e.nombre) LIKE LOWER(?) OR LOWER(e.sector) LIKE LOWER(?)");
            String patron = "%" + filtro.trim() + "%";
            parametros.add(patron);
            parametros.add(patron);
        }
        sql.append(" GROUP BY e.id, e.nombre, e.sector, e.web ORDER BY e.nombre");

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < parametros.size(); i++) {
                ps.setString(i + 1, parametros.get(i));
            }
            List<Empresa> empresas = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    empresas.add(new Empresa(
                            rs.getLong("id"),
                            rs.getString("nombre"),
                            rs.getString("sector"),
                            rs.getString("web"),
                            rs.getInt("candidaturas")));
                }
            }
            return empresas;
        }
    }

    /**
     * Inserta un nuevo registro en {@code EMPRESA} utilizando la conexión indicada
     * y recupera la clave primaria autogenerada.
     *
     * @param conn          conexión JDBC activa
     * @param nombre        nombre de la empresa
     * @param sector        sector de la empresa
     * @param web           URL o web de la empresa
     * @param fechaRegistro fecha de registro
     * @return clave primaria ({@code id}) generada por MySQL
     * @throws SQLException si falla la inserción o no se devuelve ninguna clave generada
     */

    private long insertar(Connection conn, String nombre, String sector, String web,
                          LocalDate fechaRegistro) throws SQLException {
        String insertar = "INSERT INTO EMPRESA (nombre, sector, web, fechaRegistro) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insertar, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nombre);
            ps.setString(2, sector);
            ps.setString(3, web);
            ps.setDate(4, Date.valueOf(fechaRegistro));
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
        }

        throw new SQLException("No se pudo obtener ni crear la empresa: " + nombre);
    }

    private static void validar(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la empresa no puede estar vacío");
        }
        if (nombre.length() > 150) {
            throw new IllegalArgumentException("El nombre de la empresa supera los 150 caracteres permitidos");
        }
    }

    /**
     * Valida que el nombre de la empresa no sea nulo, no esté en blanco y no supere
     * los 150 caracteres.
     *
     * @throws IllegalArgumentException si el nombre no cumple las restricciones
     */

    private static void validarCamposOpcionales(String sector, String web) {
        if (sector != null && sector.length() > 100) {
            throw new IllegalArgumentException("El sector supera los 100 caracteres permitidos");
        }
        if (web != null && web.length() > 255) {
            throw new IllegalArgumentException("La web supera los 255 caracteres permitidos");
        }
    }
}
