# FilmoTokio

Proyecto Java / Spring Boot multimódulo para la gestión de películas, artistas, usuarios, valoraciones REST y exportación Batch incremental.

## Descripción

FilmoTokio es una aplicación desarrollada con Spring Boot y Maven multimódulo. El proyecto combina:

- Aplicación WEB MVC con Thymeleaf.
- Seguridad WEB con usuarios y roles.
- API REST independiente para valoraciones.
- Seguridad REST mediante JWT.
- Integración WEB ↔ REST.
- Proceso Batch incremental para exportar películas a CSV.
- Persistencia con MySQL y JPA.
- Configuración centralizada y portable.

## Tecnologías principales

- Java 21
- Spring Boot
- Spring MVC / Thymeleaf
- Spring Security
- Spring Data JPA / Hibernate
- Spring Batch
- MySQL 8.x
- Maven
- Lombok
- JWT

## Estructura del proyecto

```text
FilmoTokio/
├── config/
│   └── application.properties
├── filmotokio-common/
├── filmotokio-web/
├── filmotokio-rest/
├── filmotokio-batch/
│   └── exports/
├── uploads/
│   └── posters/
└── pom.xml
```

### Módulos

- `filmotokio-common`: entidades y DTO compartidos.
- `filmotokio-web`: aplicación web MVC.
- `filmotokio-rest`: API REST de valoraciones.
- `filmotokio-batch`: proceso Batch de exportación incremental.

## Requisitos previos

Para ejecutar el proyecto es necesario disponer de:

- Java 21.
- Maven.
- MySQL 8.x.
- Una base de datos llamada `filmotokio`.
- Un usuario MySQL con permisos de lectura, escritura y creación/actualización de tablas.

SQL inicial recomendado:

```sql
CREATE DATABASE filmotokio
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;
```

El proyecto no incluye una base de datos embebida. Las tablas se crean o actualizan mediante Hibernate y Spring Batch.

## Configuración centralizada

Toda la configuración editable del proyecto se encuentra en un único archivo:

```text
config/application.properties
```

Los `application.properties` internos de cada módulo contienen únicamente la configuración técnica necesaria para traducir esos valores a Spring Boot.

Para que los tres módulos encuentren correctamente el archivo central y las rutas relativas, deben ejecutarse tomando como directorio de trabajo la raíz del proyecto:

```text
FilmoTokio/
```

En IntelliJ IDEA puede utilizarse:

```text
$PROJECT_DIR$
```

### Base de datos

Valores por defecto:

```properties
filmotokio.config.db.host=${FILMOTOKIO_DB_HOST:localhost}
filmotokio.config.db.port=${FILMOTOKIO_DB_PORT:3306}
filmotokio.config.db.name=${FILMOTOKIO_DB_NAME:filmotokio}
filmotokio.config.db.username=${FILMOTOKIO_DB_USER:root}
filmotokio.config.db.password=${FILMOTOKIO_DB_PASSWORD:root123}
```

Para ejecutar el proyecto en otro equipo, normalmente basta con revisar estos cinco valores en `config/application.properties`:

- host;
- puerto;
- nombre de la base de datos;
- usuario;
- contraseña.

El valor situado después de `:` es el utilizado por defecto. Por ejemplo:

```properties
filmotokio.config.db.password=${FILMOTOKIO_DB_PASSWORD:root123}
```

utiliza `root123` mientras no exista una variable de entorno llamada `FILMOTOKIO_DB_PASSWORD`.







### Puertos

```properties
filmotokio.config.web.port=${FILMOTOKIO_WEB_PORT:8080}
filmotokio.config.rest.port=${FILMOTOKIO_REST_PORT:8081}
```






### Comunicación WEB → REST

```properties
filmotokio.config.rest.host=${FILMOTOKIO_REST_HOST:localhost}
filmotokio.rest.client.username=${FILMOTOKIO_REST_CLIENT_USERNAME:filmotokio-web}
filmotokio.rest.client.password=${FILMOTOKIO_REST_CLIENT_PASSWORD:RestClient2026!}
```







### Seguridad JWT

```properties
filmotokio.rest.jwt.secret=${FILMOTOKIO_JWT_SECRET:FilmoTokioJwtSecret2026_DevelopmentOnly}
filmotokio.rest.jwt.expiration-seconds=${FILMOTOKIO_JWT_EXPIRATION_SECONDS:3600}
```






### Archivos

```properties
filmotokio.uploads.posters=${FILMOTOKIO_POSTERS_DIRECTORY:uploads/posters}
filmotokio.batch.export.directory=${FILMOTOKIO_BATCH_EXPORT_DIRECTORY:filmotokio-batch/exports}

spring.servlet.multipart.max-file-size=${FILMOTOKIO_UPLOAD_MAX_FILE_SIZE:5MB}
spring.servlet.multipart.max-request-size=${FILMOTOKIO_UPLOAD_MAX_REQUEST_SIZE:5MB}
```

Las rutas son relativas a la raíz del proyecto.








La validación funcional de pósteres limita las imágenes a 2 MB.

### Administrador inicial

El administrador inicial se crea automáticamente durante el primer arranque de WEB si todavía no existe un usuario con el nombre configurado.

```properties
filmotokio.admin.username=${FILMOTOKIO_ADMIN_USERNAME:admin}
filmotokio.admin.password=${FILMOTOKIO_ADMIN_PASSWORD:Admin2026!}
filmotokio.admin.email=${FILMOTOKIO_ADMIN_EMAIL:admin@filmotokio.local}
filmotokio.admin.name=${FILMOTOKIO_ADMIN_NAME:Administrador}
filmotokio.admin.surname=${FILMOTOKIO_ADMIN_SURNAME:FilmoTokio}
filmotokio.admin.birth-date=${FILMOTOKIO_ADMIN_BIRTH_DATE:1970-01-01}
```

Credenciales de demostración:

```text
Usuario: admin
Contraseña: Admin2026!
```

Estos valores están destinados al entorno académico/local y pueden cambiarse antes del primer arranque mediante el archivo central o variables de entorno.

Los usuarios estándar se crean desde `/register`.

## Variables de entorno

De forma opcional, los valores del archivo central pueden sobrescribirse mediante variables de entorno.

No es el método principal de configuración ni es necesario para una ejecución local o académica. Se mantiene únicamente como alternativa para otros entornos.

Ejemplo:

```powershell
$env:FILMOTOKIO_DB_PASSWORD="otra_password"
```

## Compilación

Desde la raíz del proyecto:

```powershell
mvn clean verify
```

o:

```powershell
mvn clean install
```

## Orden de arranque recomendado

1. Iniciar MySQL.
2. Crear la base de datos `filmotokio` si todavía no existe.
3. Revisar `config/application.properties`.
4. Arrancar `filmotokio-rest`.
5. Arrancar `filmotokio-web`.
6. Acceder a `http://localhost:8080`.
7. Ejecutar `filmotokio-batch` cuando se quiera lanzar la exportación incremental.

Para crear artistas o películas solo es necesario arrancar WEB.

Para consultar o enviar valoraciones desde una ficha de película deben estar arrancados WEB y REST. Si REST no está disponible, WEB muestra un aviso controlado y el resto de la ficha continúa funcionando.

## Módulo WEB

El módulo `filmotokio-web` permite:

- Registro y login de usuarios.
- Gestión de roles `USER` y `ADMIN`.
- Listado y consulta de artistas.
- Listado, búsqueda y consulta de películas.
- Creación de artistas y películas por usuarios autenticados.
- Edición y eliminación por administradores.
- Subida y visualización de pósteres.
- Consulta y envío de valoraciones a través del servicio REST.

### Rutas públicas

- `/login`
- `/register`

### Permisos

- `USER`: puede consultar, crear artistas y crear películas.
- `ADMIN`: puede consultar, crear, editar y eliminar artistas y películas.

## Gestión de pósteres

Los pósteres se guardan físicamente en:

```text
uploads/posters
```

Reglas principales:

- Póster obligatorio al crear una película.
- Póster opcional al editar.
- Si no se sube un póster nuevo al editar, se conserva el anterior.
- Tamaño máximo funcional: 2 MB.
- Formatos permitidos: JPG/JPEG, PNG y WEBP.
- En la base de datos se guarda únicamente el nombre del archivo.
- Si la imagen falta o no puede cargarse, se muestra un placeholder.

## Módulo REST

El módulo `filmotokio-rest` expone la API de valoraciones.

Puerto por defecto:

```text
8081
```

Endpoints principales:

```http
POST /authenticate
POST /ratings
GET /ratings/films/{filmId}/users/{userId}
GET /ratings-average/films/{filmId}
```

### Autenticación

Solicitud:

```http
POST /authenticate
Content-Type: application/x-www-form-urlencoded
Authorization: Basic <credenciales_base64>

grant_type=client_credentials
```

Respuesta:

```json
{
  "accessToken": "...",
  "tokenType": "bearer",
  "expiresIn": 3600
}
```

El resto de endpoints requieren:

```http
Authorization: Bearer <token>
```

La WEB reutiliza el token mientras siga siendo válido, evitando solicitar uno nuevo para cada operación.

### Errores REST

El servicio devuelve respuestas JSON controladas para:

- peticiones inválidas;
- validaciones;
- recursos inexistentes;
- `Content-Type` no soportado;
- ausencia de autenticación;
- token inválido o caducado;
- errores internos inesperados.

## Integración WEB ↔ REST

WEB no almacena directamente las valoraciones. Esa responsabilidad pertenece al módulo REST.

En la ficha de una película, WEB puede:

- consultar la valoración media;
- consultar la valoración del usuario autenticado;
- enviar una nueva valoración.

La contraseña del cliente REST debe coincidir para WEB y REST. Al estar centralizada, ambos módulos leen el mismo valor desde `config/application.properties`.

## Módulo Batch

El módulo `filmotokio-batch` exporta películas a CSV de forma incremental.

Funcionamiento:

1. Busca películas cuyo `id` todavía no aparece en `film_export`.
2. Genera un CSV asociado al `jobId` de Spring Batch.
3. Registra cada película exportada en `film_export`.
4. En ejecuciones posteriores no vuelve a exportar las mismas películas.

Carpeta de salida:

```text
filmotokio-batch/exports
```

Nombre de archivo:

```text
films-export-job-{jobId}.csv
```

Si no existen películas pendientes, el job termina en `COMPLETED` sin generar un CSV nuevo.

### Formato CSV

El writer:

- elimina espacios iniciales y finales;
- escapa comillas dobles;
- encierra entre comillas los campos que contienen comas, comillas o saltos de línea.

Esto evita que títulos con caracteres especiales rompan el CSV.

## Ejecución desde IntelliJ IDEA

Crear o revisar una configuración de ejecución para cada aplicación:

- `FilmotokioRestApplication`
- `FilmotokioWebApplication`
- `FilmotokioBatchApplication`

En las tres configuraciones:

```text
Working directory: $PROJECT_DIR$
```

Orden recomendado:

```text
1. FilmotokioRestApplication
2. FilmotokioWebApplication
3. FilmotokioBatchApplication cuando se quiera exportar
```

## Comprobaciones rápidas

### WEB

- Abre `http://localhost:8080`.
- Muestra la página de login.
- Permite entrar con `admin / Admin2026!`.
- Carga el listado de películas y sus pósteres.

### REST

En el log debe aparecer:

```text
Tomcat started on port 8081
Started FilmotokioRestApplication
```

### WEB ↔ REST

Entra en una ficha de película y comprueba que:

- aparece la media de valoraciones;
- puede guardarse una valoración;
- la valoración propia se muestra después de guardarla.

### BATCH

En el log debe aparecer:

```text
status: [COMPLETED]
```

Si no hay películas pendientes, no debe generarse un CSV nuevo.

## Resolución de problemas

### MySQL rechaza la conexión

Buscar al final del log:

```text
Access denied for user
```

Revisar:

- `filmotokio.config.db.username`
- `filmotokio.config.db.password`
- posibles variables de entorno que estén sobrescribiendo los valores.

### WEB no muestra los pósteres

Comprobar:

```properties
filmotokio.uploads.posters=uploads/posters
```

y verificar que el directorio de trabajo sea la raíz del proyecto.

### WEB no puede consultar valoraciones

Comprobar:

1. REST está arrancado en el host y puerto configurados.
2. WEB y REST leen la misma contraseña del cliente.
3. No existe una variable de entorno antigua sobrescribiendo la configuración central.

### Puerto ocupado

Modificar en `config/application.properties`:

```properties
filmotokio.config.web.port=8080
filmotokio.config.rest.port=8081
```

o utilizar las variables de entorno correspondientes.

## Valores de desarrollo

El repositorio incluye valores funcionales para facilitar la evaluación académica y el arranque local.

No son credenciales de producción. Para otro entorno deben sustituirse mediante variables de entorno o una configuración externa segura.

## Estado del proyecto

Proyecto funcional para demostración académica y portfolio:

- WEB MVC con seguridad y roles.
- REST de valoraciones protegido con JWT.
- Integración WEB ↔ REST.
- Gestión robusta de errores.
- Batch incremental a CSV.
- Configuración centralizada y portable.
- Interfaz renovada y navegación completa.
