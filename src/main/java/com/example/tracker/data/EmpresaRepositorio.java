package com.example.tracker.data;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

/**
 * Acceso a la tabla EMPRESA. Reutiliza la empresa si ya existe (comparando el
 * nombre sin distinción de mayúsculas) y la crea en caso contrario.
 */
public class EmpresaRepositorio {

    public long obtenerOCrear(Connection conn, String nombreEmpresa, LocalDate fechaRegistro) throws SQLException {
        if (nombreEmpresa == null || nombreEmpresa.isBlank()) {
            throw new IllegalArgumentException("El nombre de la empresa no puede estar vacío");
        }
        if (nombreEmpresa.length() > 150) {
            throw new IllegalArgumentException("El nombre de la empresa supera los 150 caracteres permitidos");
        }

        String buscar = "SELECT id FROM EMPRESA WHERE LOWER(nombre) = LOWER(?)";
        try (PreparedStatement ps = conn.prepareStatement(buscar)) {
            ps.setString(1, nombreEmpresa);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id");
                }
            }
        }

        String insertar = "INSERT INTO EMPRESA (nombre, sector, web, fechaRegistro) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insertar, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nombreEmpresa);
            ps.setString(2, "");
            ps.setString(3, "");
            ps.setDate(4, Date.valueOf(fechaRegistro));
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
        }

        throw new SQLException("No se pudo obtener ni crear la empresa: " + nombreEmpresa);
    }
}
