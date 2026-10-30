# Candia: CLI Tracker de Candidaturas

Aplicación de consola en Java para llevar el seguimiento de tus candidaturas de empleo. Los datos se guardan en un archivo CSV local, sin base de datos.

## Características

- Añadir, listar, consultar y borrar candidaturas desde la terminal.
- Filtrar el listado por estado y por empresa.
- Importar candidaturas en lote desde un archivo CSV.
- Persistencia en un único fichero, `candidaturas.csv`, creado en el directorio desde el que ejecutas la aplicación.

## Arquitectura

El proyecto sigue una arquitectura de tres capas:

| Capa | Clase | Responsabilidad |
|------|-------|-----------------|
| Controlador | `Main.java` | Interpreta los argumentos de la línea de comandos y llama al servicio. |
| Servicio | `ServicioCandidatura.java` | Lógica de negocio: filtros, validaciones, importación y gestión de IDs. |
| Datos | `ArchivoCandidatura.java` | Lectura y escritura de `candidaturas.csv`. |

El modelo de dominio es `model/Oferta.java`.

```
src/main/java/
├── module-info.java
└── com/example/tracker/
    ├── Main.java
    ├── business/ServicioCandidatura.java
    ├── data/ArchivoCandidatura.java
    └── model/Oferta.java
```

## Requisitos

- JDK 21
- Maven 3.9 o superior, o el wrapper incluido (`mvnw` / `mvnw.cmd`)

## Instalación

```bash
git clone https://github.com/davidlorca15/trackercandidaturas.git
cd trackercandidaturas
```

Antes de compilar, revisa el `pom.xml`: en la sección `maven-compiler-plugin` las propiedades `<source>` y `<target>` están a `27`, versión que no existe. Cámbialas a `21`.

Compila el proyecto con:

```bash
./mvnw compile          # Linux / macOS
mvnw.cmd compile        # Windows
```

## Ejecución

La aplicación se ejecuta directamente con la clase `Main`:

```bash
java -cp target/classes com.example.tracker.Main -listar
```

Sustituye `-listar` por cualquiera de los comandos de la siguiente sección. El archivo `candidaturas.csv` se crea en la carpeta desde la que lanzas el comando.

## Comandos

| Comando | Descripción | Opciones |
|---------|-------------|----------|
| `-listar` | Muestra todas las candidaturas o las filtra. | `--estado <texto>`, `--empresa <texto>` |
| `-añadir` | Registra una nueva candidatura. | `--titulo`, `--empresa`, `--estado`, `--fecha` (todas obligatorias) |
| `-ver <id>` | Muestra el detalle de una candidatura. | |
| `-borrar <id>` | Elimina una candidatura por su ID. | |
| `-importar <archivo.csv>` | Importa candidaturas desde un CSV. | |

Los filtros de `-listar` comparan sin distinguir mayúsculas y minúsculas, pero el texto debe coincidir completo.

### Ejemplos

```bash
# Añadir una candidatura (la fecha va en formato dd/MM/yyyy)
java -cp target/classes com.example.tracker.Main -añadir --titulo "Desarrollador Backend Java" --empresa "TechCorp" --estado "Pendiente" --fecha "30/10/2026"

# Listar todas
java -cp target/classes com.example.tracker.Main -listar

# Listar solo las pendientes de una empresa
java -cp target/classes com.example.tracker.Main -listar --estado Pendiente --empresa TechCorp

# Ver el detalle de la candidatura con ID 13
java -cp target/classes com.example.tracker.Main -ver 13

# Borrar la candidatura con ID 13
java -cp target/classes com.example.tracker.Main -borrar 13

# Importar desde un CSV
java -cp target/classes com.example.tracker.Main -importar candidaturas.csv
```

Los IDs se asignan automáticamente: cada nueva candidatura recibe el siguiente número disponible.

## Formato del archivo CSV

### Almacenamiento (`candidaturas.csv`)

La aplicación gestiona este archivo sola. Usa `;` como separador y tiene esta cabecera:

```text
id;titulo;empresa;estado;fechaPostulacion;enlace;notas
1;Desarrollador Backend Java;TechCorp;Pendiente;30/10/2026;https://ejemplo.com/oferta;
```

Las fechas usan el formato `dd/MM/yyyy`.

### Importación (`-importar`)

El fichero a importar debe tener siete columnas por fila. La primera columna se ignora, porque el ID se reasigna al importar, y la primera línea (cabecera) también se salta:

```text
id;titulo;empresa;estado;fechaPostulacion;enlace;notas
0;Desarrollador Backend Java;TechCorp;Pendiente;30/10/2026;https://ejemplo.com/oferta;Entrevista el lunes
0;Analista de datos;DataSoft;Entrevista;15/10/2026;;
```

Ten en cuenta que:

- Las filas con menos de siete columnas se ignoran sin avisar.
- Si una fecha no tiene el formato correcto, la importación se cancela entera y no se guarda nada.
- Ningún campo puede contener `;`, porque rompería la estructura del archivo.

## Limitaciones conocidas

- `-añadir` no permite indicar enlace ni notas. Esos campos solo se pueden rellenar importando un CSV.
- El campo `estado` no se valida: acepta cualquier texto.
- Los campos que contienen `;` corrompen el CSV.
- El `pom.xml` incluye dependencias de JavaFX y una configuración de `javafx-maven-plugin` que apuntan a clases que no existen en el proyecto. No se usan y pueden eliminarse.
- Cada ejecución imprime una línea `DEBUG - Comando recibido` que debería quitarse.
- `candidaturas.csv` contiene datos personales. Conviene añadirlo a `.gitignore` para no subirlo al repositorio.

## Autores

- David Lorca
- Rodrigo Franco
