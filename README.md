# FilmoTokio

Proyecto Java / Spring Boot multimódulo para gestión de películas, artistas, usuarios, valoraciones REST y exportación Batch incremental.

## Descripción

FilmoTokio es una aplicación desarrollada con Spring Boot y Maven multimódulo. El proyecto combina:

* Aplicación WEB MVC con Thymeleaf.
* Seguridad WEB con usuarios y roles.
* API REST independiente para valoraciones.
* Seguridad REST con JWT.
* Integración WEB ↔ REST.
* Proceso Batch incremental para exportar películas a CSV.
* Persistencia con MySQL/MariaDB y JPA.

## Tecnologías principales

* Java 21
* Spring Boot
* Spring MVC / Thymeleaf
* Spring Security
* Spring Data JPA / Hibernate
* Spring Batch
* MySQL / MariaDB
* Maven
* Lombok
* JWT

## Estructura del proyecto

```text
FilmoTokio/
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

* `filmotokio-common`: entidades y DTOs compartidos.
* `filmotokio-web`: aplicación web MVC.
* `filmotokio-rest`: API REST de valoraciones.
* `filmotokio-batch`: proceso Batch de exportación incremental.

## Requisitos previos

Para ejecutar el proyecto es necesario disponer de:

* Java 21.
* Maven.
* MySQL 8.x o MariaDB.
* Una base de datos llamada `filmotokio`.
* Un usuario MySQL con permisos de lectura, escritura y creación/actualización de tablas.

SQL inicial recomendado:

```sql
CREATE DATABASE filmotokio
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;
```

El proyecto no incluye una base de datos embebida. Las tablas se crean o actualizan mediante Hibernate y Spring Batch.

## Configuración

Los módulos que acceden a base de datos tienen su propio `application.properties`:

* `filmotokio-web/src/main/resources/application.properties`
* `filmotokio-rest/src/main/resources/application.properties`
* `filmotokio-batch/src/main/resources/application.properties`

La conexión se configura mediante variables de entorno con valores por defecto:

```properties
spring.datasource.url=${FILMOTOKIO_DB_URL:jdbc:mysql://localhost:3306/filmotokio}
spring.datasource.username=${FILMOTOKIO_DB_USER:root}
spring.datasource.password=${FILMOTOKIO_DB_PASSWORD:change_me}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

Variables principales:

* `FILMOTOKIO_DB_URL`
* `FILMOTOKIO_DB_USER`
* `FILMOTOKIO_DB_PASSWORD`
* `FILMOTOKIO_REST_CLIENT_PASSWORD`
* `FILMOTOKIO_JWT_SECRET`

Ejemplo en PowerShell:

```powershell
$env:FILMOTOKIO_DB_PASSWORD="tu_password_mysql"
$env:FILMOTOKIO_REST_CLIENT_PASSWORD="tu_password_cliente_rest"
$env:FILMOTOKIO_JWT_SECRET="una_clave_larga_y_segura"
```

También se pueden editar directamente los `application.properties` para una ejecución local sencilla.

## Credenciales y placeholders

El repositorio no incluye credenciales reales de producción.

Valores como `change_me` o `change_me_super_secret_key` son placeholders de desarrollo. Deben sustituirse mediante variables de entorno o configuración local antes de ejecutar el proyecto en otro entorno.

### Administrador inicial

En el primer arranque, `InitialDataLoader` crea automáticamente un usuario administrador si todavía no existe el nombre de usuario configurado.

Los datos iniciales se encuentran en:

```text
filmotokio-web/src/main/resources/application.properties
```

Valores por defecto:

```properties
filmotokio.admin.username=admin
filmotokio.admin.password=Admin2026!
filmotokio.admin.email=admin@filmotokio.local
filmotokio.admin.name=Administrador
filmotokio.admin.surname=FilmoTokio
filmotokio.admin.birth-date=1970-01-01
```

Para probar la aplicación como administrador:

- Usuario: `admin`
- Contraseña: `Admin2026!`

Estos valores pueden cambiarse antes del primer arranque sin modificar el código Java. Son credenciales de demostración académica y no deben utilizarse así en producción.

Los usuarios estándar se crean desde la opción de registro de la aplicación (`/register`).

## Orden de arranque recomendado

1. Arrancar MySQL/MariaDB.
2. Crear la base de datos `filmotokio` si no existe.
3. Arrancar `filmotokio-rest`.
4. Arrancar `filmotokio-web`.
5. Acceder a la WEB en:

```text
http://localhost:8080
```

6. Ejecutar `filmotokio-batch` cuando se quiera lanzar la exportación incremental.

Para crear artistas o películas solo es necesario arrancar `filmotokio-web`.

Para que la ficha de película muestre valoraciones, debe estar arrancado `filmotokio-rest`. Si REST no está disponible, la WEB muestra un mensaje controlado y el resto de datos de la película sigue funcionando.

## Módulo WEB

El módulo `filmotokio-web` permite:

* Registro y login de usuarios.
* Gestión de roles `USER` y `ADMIN`.
* CRUD de artistas.
* CRUD de películas.
* Subida y visualización de posters.
* Listado y búsqueda de películas.
* Detalle de película.
* Consulta y envío de valoraciones mediante el REST.

Rutas públicas:

* `/login`
* `/register`

Rutas de administración protegidas por rol `ADMIN`:

* edición y borrado de películas;
* edición y borrado de artistas.

## Gestión de posters

Los posters se guardan físicamente en:

```properties
filmotokio.uploads.posters=uploads/posters
```

Reglas principales:

* Poster obligatorio al crear película.
* Poster opcional al editar.
* Si al editar no se sube poster nuevo, se conserva el anterior.
* Tamaño máximo funcional: 2 MB.
* Formatos permitidos: JPG/JPEG, PNG y WEBP.
* En base de datos se guarda solo el nombre del archivo.

## Módulo REST

El módulo `filmotokio-rest` expone la API de valoraciones.

Puerto por defecto:

```properties
server.port=8081
```

Endpoints principales:

```http
POST /authenticate
POST /ratings
GET /ratings/films/{filmId}/users/{userId}
GET /ratings-average/films/{filmId}
```

El endpoint `/authenticate` permite obtener un JWT mediante Client Credentials. El resto de endpoints están protegidos y requieren Bearer Token.

## Seguridad REST / JWT

La WEB solicita un token al REST usando:

```http
POST /authenticate
Content-Type: application/x-www-form-urlencoded
Authorization: Basic <credenciales_base64>

grant_type=client_credentials
```

Respuesta esperada:

```json
{
  "accessToken": "...",
  "tokenType": "bearer",
  "expiresIn": 3600
}
```

Después, la WEB usa el token para consultar medias, consultar valoración propia y crear nuevas valoraciones.

## Integración WEB ↔ REST

La WEB no almacena directamente las valoraciones. Esa responsabilidad pertenece al módulo REST.

En la ficha de película, la WEB consulta:

* valoración media;
* valoración propia del usuario autenticado.

Si el usuario no ha votado, se muestra formulario de valoración.

Si el REST está apagado o no responde, la ficha muestra un mensaje controlado y no se produce Whitelabel.

## Módulo Batch

El módulo `filmotokio-batch` exporta películas a CSV de forma incremental.

Funcionamiento:

1. Busca películas cuyo `id` no aparezca en `film_export`.
2. Genera un CSV asociado al `jobId` de Spring Batch.
3. Registra cada película exportada en `film_export`.
4. En ejecuciones posteriores, esas películas no vuelven a exportarse.

Archivo generado:

```text
filmotokio-batch/exports/films-export-job-{jobId}.csv
```

Ejemplo de CSV:

```csv
filmId,title,releaseYear
15,Prueba Pelicula Batch,2026
```

Propiedad relevante:

```properties
spring.batch.job.enabled=false
```

El job se lanza mediante un runner propio con parámetro `startedAt`, evitando repetir una ejecución ya completada con los mismos parámetros.

## Notas sobre CSV

El writer del Batch protege los títulos exportados:

* elimina espacios iniciales/finales con `strip()`;
* escapa comillas dobles;
* encierra entre comillas campos con comas, comillas o saltos de línea.

Esto evita que títulos como `Matrix, Reloaded` rompan el formato CSV.

## Ejecución rápida

Desde la raíz del proyecto:

```powershell
mvn clean install
```

Después arrancar los módulos desde el IDE o mediante Maven según el módulo que se quiera probar.

Orden recomendado:

```text
1. filmotokio-rest
2. filmotokio-web
3. filmotokio-batch cuando se quiera exportar
```

## Comprobaciones habituales

Si algo no arranca, revisar:

1. MySQL/MariaDB está iniciado.
2. Existe la base de datos `filmotokio`.
3. Usuario y contraseña de base de datos son correctos.
4. Los puertos `8080` y `8081` están libres.
5. Las variables de entorno no están sobrescribiendo una configuración correcta.
6. REST está arrancado si se quieren probar valoraciones desde la WEB.

## Estado del proyecto

Proyecto funcional para demostración académica y portfolio:

* WEB MVC con seguridad y roles.
* REST de valoraciones protegidas con JWT.
* Integración WEB ↔ REST.
* Batch incremental a CSV.
* Configuración portable mediante properties y variables de entorno.
