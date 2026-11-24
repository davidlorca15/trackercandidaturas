# Candia: CLI Tracker de Candidaturas

Aplicación de consola en Java para llevar el seguimiento de tus candidaturas de empleo. Los datos se guardan en **MySQL** (tablas `EMPRESA`, `OFERTA` y `ENTREVISTA`); el CSV solo se usa como origen de importaciones.

## Características

- Añadir, listar, consultar y borrar candidaturas desde la terminal.
- Filtrar el listado por estado y por empresa.
- Importar candidaturas en lote desde un CSV directamente a MySQL, en una sola transacción (o se insertan todas o ninguna).
- Las empresas se reutilizan automáticamente por nombre; si no existen se crean.

## Arquitectura

| Capa | Clase | Responsabilidad |
|------|-------|-----------------|
| Controlador | `Main.java` | Interpreta los argumentos de la línea de comandos. |
| Servicio | `ServicioCandidatura.java` | Lógica de negocio y coordinación con la BD. |
| Servicio | `ImportadorCsv.java` | Importación transaccional de CSV a MySQL. |
| Datos | `OfertaRepositorio.java`, `EmpresaRepositorio.java` | Consultas PreparedStatement y mapeo a entidades. |
| Datos | `ConexionBD.java` | Carga `config.properties` y abre la conexión. |
| Modelo | `model/Oferta.java` | Entidad de dominio. |

## Requisitos

- JDK 21 o superior.
- Maven 3.9 o superior, o el wrapper incluido (`mvnw` / `mvnw.cmd`).
- MySQL 8+ escuchando en `127.0.0.1:3307` (o ajustar la URL de conexión).

## Instalación y configuración

1. Ejecuta `src/main/resources/tracker_candidaturas.sql` en tu MySQL para crear la base de datos y las tablas.
2. Revisa `src/main/resources/config.properties`:

   ```properties
   db.url=jdbc:mysql://127.0.0.1:3307/tracker_candidaturas?useSSL=false&serverTimezone=UTC
   db.user=root
   db.password=root123
   ```

3. Compila:

   ```bash
   ./mvnw compile          # Linux / macOS
   mvnw.cmd compile        # Windows
   ```

   El driver JDBC (`com.mysql:mysql-connector-j:26.10.0`) se descarga automáticamente desde el `pom.xml`.

## Ejecución

```powershell
java -cp "target/classes;$env:USERPROFILE\.m2\repository\com\mysql\mysql-connector-j\26.10.0\mysql-connector-j-26.10.0.jar" com.example.tracker.Main -listar
```

También puedes ejecutar en modo módulo (el `module-info.java` ya declara `requires mysql.connector.j;`):

```powershell
java --module-path "target\classes;$env:USERPROFILE\.m2\repository\com\mysql\mysql-connector-j\26.10.0\mysql-connector-j-26.10.0.jar" -m com.example.tracker/com.example.tracker.Main -listar
```

## Comandos

| Comando | Descripción | Opciones |
|---------|-------------|----------|
| `-listar` | Muestra todas las candidaturas o las filtra. | `--estado <texto>`, `--empresa <texto>` |
| `-añadir` (o `-anadir`) | Registra una nueva candidatura. | `--titulo`, `--empresa`, `--estado`, `--fecha` (obligatorias) |
| `-ver <id>` | Muestra el detalle de una candidatura. | |
| `-borrar <id>` | Elimina una candidatura por su ID. | |
| `-importar <archivo.csv>` | Importa candidaturas desde un CSV a MySQL. | |

Los filtros comparan sin distinguir mayúsculas/minúsculas, pero el texto debe coincidir completo.

### Ejemplos

```powershell
java ... -añadir --titulo "Desarrollador Backend Java" --empresa "TechCorp" --estado "Pendiente" --fecha "30/10/2026"
java ... -listar
java ... -listar --estado Pendiente --empresa TechCorp
java ... -ver 1
java ... -borrar 1
java ... -importar candidaturas.csv
```

## Formato del CSV importado

Seis columnas útiles más el ID (la cabecera y la primera columna se ignoran). Separador: `;`.

```text
id;titulo;empresa;estado;fechaPostulacion;enlace;notas
0;Desarrollador Backend Java;TechCorp;Pendiente;30/10/2026;https://ejemplo.com/oferta;Entrevista el lunes
0;Analista de datos;DataSoft;Entrevista;15/10/2026;;
```

- Las fechas van en `dd/MM/yyyy`.
- Ningún campo puede contener `;`.
- Longitudes máximas: título 150, empresa 150, estado 30, enlace 255 (según el esquema SQL). Si se superan, la importación se cancela con un mensaje claro.
- Una fecha con formato incorrecto cancela toda la importación.

## Limitaciones conocidas

- `-añadir` no permite indicar enlace, notas ni salario; esos campos solo se rellenan al importar (el salario se inserta a `NULL`).
- El `estado` no se valida contra una lista, solo su longitud.
- Importar el mismo CSV dos veces duplica las ofertas (no hay control de duplicados).
- `ArchivoCandidatura.java` y `resources/com/example/demo/hello-view.fxml` son restos de la versión anterior y ya no se usan.
