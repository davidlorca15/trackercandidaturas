package com.example.tracker;

import com.example.tracker.business.ServicioCandidatura;

import java.util.Locale;

/**
 * Punto de entrada de la CLI. Interpreta los argumentos y delega en
 * {@link ServicioCandidatura}, que trabaja contra la base de datos MySQL.
 *
 * @author Rodrigo Franco y David Lorca
 * @version 1.0
 */
public class Main {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Error: Faltan argumentos. Operaciones válidas: -listar, -añadir, -ver, -borrar, -importar");
            return;
        }

        ServicioCandidatura service = new ServicioCandidatura();
        String comando = args[0].trim().toLowerCase(Locale.ROOT);

        try {
            switch (comando) {
                case "-listar" -> service.listar(getArgValue(args, "--estado"), getArgValue(args, "--empresa"));

                case "-añadir", "-anadir" -> {
                    String titulo = getArgValue(args, "--titulo");
                    String empresa = getArgValue(args, "--empresa");
                    String estado = getArgValue(args, "--estado");
                    String fecha = getArgValue(args, "--fecha");
                    if (titulo == null || empresa == null || estado == null || fecha == null) {
                        System.out.println("Error: Faltan argumentos obligatorios para -añadir (--titulo, --empresa, --estado, --fecha)");
                        return;
                    }
                    service.añadir(titulo, empresa, estado, fecha);
                }

                case "-ver" -> {
                    if (args.length < 2) {
                        System.out.println("Error: Falta el ID. Uso: -ver <id>");
                        return;
                    }
                    service.ver(Long.parseLong(args[1]));
                }

                case "-borrar" -> {
                    if (args.length < 2) {
                        System.out.println("Error: Falta el ID. Uso: -borrar <id>");
                        return;
                    }
                    service.borrar(Long.parseLong(args[1]));
                }

                case "-importar" -> {
                    if (args.length < 2) {
                        System.out.println("Error: Falta el archivo. Uso: -importar <archivo.csv>");
                        return;
                    }
                    service.importar(args[1]);
                }

                default -> System.out.println("Error: Comando no reconocido: " + args[0]);
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: El ID debe ser un número válido.");
        } catch (Exception e) {
            System.out.println("Error de ejecución: " + e.getMessage());
        }
    }

    /** Devuelve el valor situado después de una bandera, o {@code null} si no está. */
    private static String getArgValue(String[] args, String flag) {
        for (int i = 0; i < args.length - 1; i++) {
            if (args[i].equals(flag)) {
                return args[i + 1];
            }
        }
        return null;
    }
}
