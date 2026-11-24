# Candia - CLI Tracker de Candidaturas

Aplicación de consola Java con **arquitectura de tres capas** para el seguimiento de ofertas de empleo. La persistencia está en MySQL (`EMPRESA`, `OFERTA`, `ENTREVISTA`); el CSV se usa únicamente para importar datos.

## Arquitectura del sistema

- **Controlador (`Main.java`)**: procesa los argumentos de la línea de comandos.
- **Servicio (`ServicioCandidatura.java`, `ImportadorCsv.java`)**: lógica de negocio, filtros e importación transaccional.
- **Datos (`OfertaRepositorio.java`, `EmpresaRepositorio.java`, `ConexionBD.java`)**: acceso a MySQL mediante `PreparedStatement`.
- **Modelo (`model/Oferta.java`)**: entidad de dominio.

## Comandos disponibles

| Comando | Descripción | Ejemplo de uso |
| :--- | :--- | :--- |
| `-listar` | Lista o filtra por estado y empresa. | `-listar --estado Pendiente --empresa TechCorp` |
| `-añadir` | Registra una candidatura. | `-añadir --titulo "Java Dev" --empresa "Tech" --estado "Pendiente" --fecha "30/10/2026"` |
| `-ver <id>` | Muestra el detalle de una candidatura. | `-ver 1` |
| `-borrar <id>` | Elimina la candidatura indicada. | `-borrar 1` |
| `-importar` | Importa un CSV a MySQL en una transacción. | `-importar candidaturas.csv` |

## Formato del CSV (`candidaturas.csv`)

Delimitador `;`, con cabecera. La primera columna (ID) se ignora:

```text
id;titulo;empresa;estado;fechaPostulacion;enlace;notas
0;Desarrollador Backend Java;TechCorp;Pendiente;30/10/2026;https://ejemplo.com/oferta;Entrevista el lunes
```

Fechas en `dd/MM/yyyy`. Longitudes máximas: título 150, empresa 150, estado 30, enlace 255.
