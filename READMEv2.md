# Candia: Tracker de Candidaturas

Aplicación en Java para llevar el seguimiento de tus candidaturas de empleo. Los datos se guardan en **MySQL** (tablas `EMPRESA`, `OFERTA` y `ENTREVISTA`). Desde la versión 2.0 tiene una **interfaz gráfica JavaFX** diseñada con Scene Builder, además de la línea de comandos de la versión 1.0.

## Características

- Interfaz gráfica con barra lateral y pantallas de candidaturas, empresas y entrevistas.
- Listado de candidaturas con filtros por estado y búsqueda por puesto o empresa, e indicadores de totales.
- Importar candidaturas desde un CSV directamente a MySQL, en una sola transacción (o se insertan todas o ninguna).
- Añadir y borrar candidaturas desde la interfaz o desde la terminal.
- Listado de entrevistas con su estado (Pendiente, Superada, No superada) y borrado.
- Las empresas se reutilizan automáticamente por nombre; si no existen se crean.
- Las consultas a la base de datos se ejecutan fuera del hilo de la interfaz, para que la ventana no se bloquee.

## Arquitectura

| Capa | Clase | Responsabilidad |
|------|-------|-----------------|
| Punto de entrada gráfico | `MainGui.java` | Arranca la interfaz JavaFX y carga la pantalla de candidaturas. |
| Punto de entrada CLI | `Main.java` | Interpreta los argumentos de la línea de comandos. |
| Controladores | `ui/CandidaturasController.java`, `ui/EmpresasController.java`, `ui/EntrevistasController.java` | Gestionan los eventos de cada pantalla y llaman a los servicios y repositorios. |
| Servicio | `ServicioCandidatura.java` | Lógica de negocio y coordinación con la BD para la CLI. |
| Servicio | `ImportadorCsv.java` | Importación transaccional de CSV a MySQL. |
| Datos | `OfertaRepositorio.java`, `EmpresaRepositorio.java` | Consultas PreparedStatement y mapeo a entidades. |
| Datos | `EntrevistaRepositorio.java` | Listado de entrevistas con JOIN a OFERTA y EMPRESA, y borrado. |
| Datos | `ConexionBD.java` | Carga `config.properties` y abre la conexión. |
| Modelo | `model/Oferta.java`, `model/Empresa.java`, `model/Entrevista.java` | Entidades de dominio. |
| Vistas | `resources/com/example/tracker/*.fxml`, `candia.css` | Pantallas diseñadas en Scene Builder y estilos del manual de estilo. |

## Requisitos

- JDK 21 o superior.
- Maven 3.9 o superior, o el wrapper incluido (`mvnw` / `mvnw.cmd`).
- MySQL 8+ escuchando en `127.0.0.1:3307` (o ajustar la URL de conexión).
- Scene Builder, solo si vas a modificar los archivos FXML.

## Instalación y configuración

1. Ejecuta `src/main/resources/tracker_candidaturas.sql` en tu MySQL para crear la base de datos y las tablas.
2. Revisa `src/main/resources/config.properties`:

   ```properties
   db.url=jdbc:mysql://127.0.0.1:3307/tracker_candidaturas?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
   db.user=root
   db.password=tu_contraseña
   ```

   No subas al repositorio una contraseña real.

3. Compila:

   ```bash
   ./mvnw compile          # Linux / macOS
   mvnw.cmd compile        # Windows
   ```

   Las dependencias, incluidos JavaFX y el driver JDBC de MySQL, se descargan automáticamente desde el `pom.xml`.

## Ejecución

### Interfaz gráfica (v2.0)

Desde la carpeta del proyecto:

```bash
./mvnw javafx:run       # Linux / macOS
mvnw.cmd javafx:run     # Windows
```

En IntelliJ también puedes abrir `MainGui.java` y pulsar **Run**.

### Línea de comandos (v1.0)

```powershell
java -cp "target/classes;$env:USERPROFILE\.m2\repository\com\mysql\mysql-connector-j\26.10.0\mysql-connector-j-26.10.0.jar" com.example.tracker.Main -listar
```

También puedes ejecutar en modo módulo (el `module-info.java` ya declara `requires mysql.connector.j;`):

```powershell
java --module-path "target\classes;$env:USERPROFILE\.m2\repository\com\mysql\mysql-connector-j\26.10.0\mysql-connector-j-26.10.0.jar" -m com.example.tracker/com.example.tracker.Main -listar
```

## Pantallas de la interfaz

| Pantalla | Qué permite | Estado |
|----------|-------------|--------|
| Candidaturas | Indicadores, filtros por estado y texto, importar CSV, añadir y borrar | Funcional con MySQL |
| Empresas | Listado con búsqueda por nombre o sector, acciones por fila | Listado con datos de ejemplo |
| Entrevistas | Listado con estado en badges, borrado | Funcional con MySQL |

La barra lateral está en todas las pantallas. Cada botón carga su pantalla con `FXMLLoader` y la muestra en la misma ventana.

## Comandos

Estos comandos son de la versión CLI (`Main`).

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
- El formulario de alta de candidaturas en la interfaz es un diálogo creado en código; el formulario en FXML está pendiente.
- Todavía no hay pantalla de detalle de candidatura ni formularios de edición de candidaturas, empresas o entrevistas.
- La pantalla de empresas muestra datos de ejemplo hasta conectarla con la tabla `EMPRESA`.
- El `estado` no se valida contra una lista, solo su longitud.
- Importar el mismo CSV dos veces duplica las ofertas (no hay control de duplicados).
- `ArchivoCandidatura.java` y `resources/com/example/demo/hello-view.fxml` son restos de la versión anterior y ya no se usan.

## Novedades de la versión 2.0

- **Interfaz gráfica JavaFX** con pantallas diseñadas en Scene Builder (`candidaturas.fxml`, `empresas.fxml`, `entrevistas.fxml`).
- **Navegación entre pantallas** con barra lateral común y carga de FXML con `FXMLLoader`.
- **Hoja de estilos `candia.css`** con la paleta y la tipografía del manual de estilo, y badges de estado con texto y color.
- **Nuevo punto de entrada `MainGui`**. `Main` se mantiene como CLI.
- **Nueva entidad `Empresa`** y **nueva entidad `Entrevista`** en el modelo.
- **Nuevo repositorio `EntrevistaRepositorio`** con consulta JOIN entre ENTREVISTA, OFERTA y EMPRESA, y borrado.
- **Operaciones asíncronas**: las consultas se ejecutan en un hilo aparte, para que la interfaz no se bloquee.
- **Alertas de error** en la interfaz cuando falla la base de datos, con un mensaje que indica qué revisar.
- **Indicadores** de total, pendientes, en entrevista y aceptadas, que se actualizan al cargar los datos.

## Diferencias entre la versión 1.0 y la 2.0

| Aspecto | Versión 1.0 | Versión 2.0 |
|---------|-------------|-------------|
| Interfaz | Solo línea de comandos | Interfaz gráfica JavaFX y línea de comandos |
| Punto de entrada | `Main` | `MainGui` (interfaz) y `Main` (CLI) |
| Pantallas | Ninguna | Candidaturas, Empresas y Entrevistas |
| Navegación | No aplica | Barra lateral común, con `FXMLLoader` |
| Diseño de pantallas | No aplica | Scene Builder (FXML) y hoja `candia.css` |
| Modelo | `Oferta` | `Oferta`, `Empresa` y `Entrevista` |
| Repositorios | `OfertaRepositorio`, `EmpresaRepositorio` | Se añade `EntrevistaRepositorio` |
| Filtros | Por estado y empresa desde la terminal | Por estado y texto desde la pantalla |
| Entrevistas | No existían en la aplicación | Listado con estado y borrado |
| Ejecución | `java ... Main -listar` | `./mvnw javafx:run` o `MainGui` desde el IDE |
| Dependencias | `mysql-connector-j` | Además, `javafx-controls` y `javafx-fxml` |
| Módulos Java | Declaración mínima | `requires` de `java.sql`, `javafx.controls`, `javafx.fxml` y el driver de MySQL |
| Hilos | Todo en el hilo principal | Consultas a la base de datos en un hilo aparte |
| Documentación de la interfaz | No aplica | Tabla de pantallas y estado de cada una |