package com.example.tracker;

import com.example.tracker.business.ServicioCandidatura;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Error: Faltan argumentos. Operaciones válidas: -listar, -añadir, -ver, -borrar, -importar");
            return;
        }

        ServicioCandidatura service = new ServicioCandidatura();
        String comando = args[0];

        try {
            System.out.println("DEBUG - Comando recibido: [" + comando + "]");
            String comandoLimpio = comando.trim().toLowerCase();

            if (comandoLimpio.endsWith("listar") || comandoLimpio.contains("listar")) {
                String estado = getArgValue(args, "--estado");
                String empresa = getArgValue(args, "--empresa");
                service.listar(estado, empresa);

            } else if (comandoLimpio.endsWith("añadir") || comandoLimpio.endsWith("anadir")) {
                String titulo = getArgValue(args, "--titulo");
                String emp = getArgValue(args, "--empresa");
                String est = getArgValue(args, "--estado");
                String fecha = getArgValue(args, "--fecha");
                if (titulo == null || emp == null || est == null || fecha == null) {
                    System.out.println("Error: Faltan argumentos obligatorios para -añadir (--titulo, --empresa, --estado, --fecha)");
                    return;
                }
                service.añadir(titulo, emp, est, fecha);

            } else if (comandoLimpio.contains("ver")) {
                if (args.length < 2) {
                    System.out.println("Error: Falta el ID. Uso: -ver <id>");
                    return;
                }
                service.ver(Long.parseLong(args[1]));

            } else if (comandoLimpio.contains("borrar")) {
                if (args.length < 2) {
                    System.out.println("Error: Falta el ID. Uso: -borrar <id>");
                    return;
                }
                service.borrar(Long.parseLong(args[1]));

            } else if (comandoLimpio.contains("importar")) {
                if (args.length < 2) {
                    System.out.println("Error: Falta el archivo. Uso: -importar <archivo.csv>");
                    return;
                }
                service.importar(args[1]);

            } else {
                System.out.println("Error: Comando no reconocido.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: El ID debe ser un número válido.");
        } catch (Exception e) {
            System.out.println("Error de ejecución: " + e.getMessage());
        }
    }

    private static String getArgValue(String[] args, String flag) {
        for (int i = 0; i < args.length - 1; i++) {
            if (args[i].equals(flag)) {
                return args[i + 1];
            }
        }
        return null;
    }
}