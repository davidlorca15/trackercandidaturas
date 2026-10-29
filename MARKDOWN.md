#  Candia - CLI Tracker de Candidaturas

Aplicación de consola en Java basada en una **arquitectura de tres capas** (Controlador/Main, Servicio y Datos) para el seguimiento y gestión de ofertas de trabajo con almacenamiento y persistencia en archivos planos.

---


##  Arquitectura del Sistema

* **Capa Controlador (`Main.java`)**: Gestiona la entrada por línea de comandos, sanea argumentos y procesa banderas.
* **Capa Servicio (`ServicioCandidatura.java`)**: Contiene la lógica de negocio, filtros y operaciones de ordenación/validación.
* **Capa Datos (`ArchivoCandidatura.java`)**: Gestiona la lectura, escritura e importación del archivo plano mediante delimitadores de punto y coma (`;`).

---

##  Comandos Disponibles

 Comando  Descripción  Ejemplo de Uso 
| :--- | :--- | :--- 
| `-listar` | Muestra todas las candidaturas o las filtra por estado/empresa. | `-listar --estado Pendiente --empresa TechCorp` |
| `-añadir` | Registra una nueva candidatura en el sistema. | `-añadir --titulo "Java Dev" --empresa "Tech" --estado "Pendiente" --fecha "30/10/2026"` |
| `-ver` | Muestra el detalle completo de una candidatura por su ID. | `-ver 13` |
| `-borrar` | Elimina del archivo la candidatura especificada por su ID. | `-borrar 13` |
| `-importar` | Importa masivamente un conjunto de candidaturas desde un CSV. | `-importar candidaturas.csv` |

---

##  Formato del Archivo CSV (`candidaturas.csv`)

El archivo de almacenamiento y de importación utiliza el punto y coma (`;`) como delimitador con la siguiente estructura de columnas:

```text
Puesto/Título;Empresa;Estado;Fecha
Desarrollador Backend Java;TechCorp Solutions;Pendiente;30/10/2026