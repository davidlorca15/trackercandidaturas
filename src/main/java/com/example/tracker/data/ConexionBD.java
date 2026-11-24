package com.example.tracker.data;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConexionBD {

    private static final Properties props = new Properties();

    static {
        try (InputStream input = ConexionBD.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("No se encontró config.properties en resources");
            }
            props.load(input);
        } catch (Exception e) {
            throw new RuntimeException("Error cargando config.properties: " + e.getMessage(), e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                props.getProperty("db.url"),
                props.getProperty("db.user"),
                props.getProperty("db.password")
        );
    }
}