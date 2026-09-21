# mto-backoffice

Backoffice web del dominio `MTO` (gestión de infraestructura ferroviaria de catenaria). Es la
primera línea de frontend del dominio: una aplicación **Spring Boot 4.1 / Java 25 + Vaadin Flow 25**
para el trabajo de gestión (catálogos, maestros de infraestructura, importaciones), pensada para un
equipo con perfil Java y solo con licencias libres (Vaadin core, Apache 2.0).

```
Navegador  ⇄  mto-backoffice (Vaadin, :8085)  ⇄  mto-gateway (:8090)  ⇄  mto-configuration (:8081)
           protocolo Vaadin                    REST + Bearer de la persona
           sesión en servidor                  servidor a servidor
```

El navegador **nunca llama a la API**: el token de la persona vive en la sesión de servidor y sale
solo hacia el gateway, que lo reenvía tal cual al servicio (cada servicio audita a la persona, no
a la aplicación). CORS no interviene.

Séptimo repositorio del dominio, hermano e independiente de
[`mto-configuration`](../mto-configuration), [`mto-stock`](../mto-stock),
[`mto-maintenance`](../mto-maintenance), [`mto-users`](../mto-users) y
[`mto-gateway`](../mto-gateway); la infraestructura local es de [`mto-platform`](../mto-platform).

## Estado: fase 3

- **Fase 0**: circuito completo con lo mínimo. Cliente `mto-backoffice` en el realm, login OIDC,
  marco con menú filtrado por roles y la pantalla de inicio con el diagnóstico del token.
- **Fase 1**: los 17 catálogos (listas de valores) con **una sola vista**, `LovCrudView`, que recibe
  el recurso en la ruta (`/catalogos/pole-types`, `/catalogos/profile-statuses`...). Los 17
  controladores de `mto-configuration` heredan los mismos ocho endpoints y el mismo DTO, así que
  una pantalla cubre 136 endpoints: listar (con filtro local: el servicio devuelve la lista entera),
  alta y modificación en un diálogo, borrado con confirmación (lógico en el servicio), alta múltiple
  pegando `CODIGO;Descripción` por línea (`POST /bulk`) y activar o desactivar la selección
  (`PUT /bulk`). Los errores del servicio se enseñan en su campo: `errors[{field, code, message}]`
  se vuelca sobre el formulario y lo que no se puede atribuir va a una notificación.

- **Fase 2**: los seis maestros de infraestructura (paquetes de ejecución, estaciones, vías,
  perfiles, seccionadores y aisladores de sección) bajo `infraestructura/*`, con **paginación en el
  servidor**: el grid pide cada página a `POST /{recurso}/filter` con la página, el tamaño, el orden
  de la columna y el texto de búsqueda (`searchText`, que el servicio aplica a varias columnas), y
  el recuento sale de `totalElements`. Nunca se trae el maestro entero: los perfiles son miles.
  Cada maestro tiene su editor con las referencias resueltas (paquete, estación, vía y empresa en
  desplegables; el perfil de un seccionador se busca en el servidor mientras se escribe) y las
  entradas de catálogo como desplegables. La edición sigue la regla de `README_API.md` §4 del
  servicio: **se edita sobre la fila leída y se devuelve entera**. Lo que la pantalla no conoce
  vuelve tal cual (`extras`), y las colecciones de hijos que no se editan aquí (vías y estaciones
  de un paquete, perfiles de una vía, ménsulas de un perfil, agujas de un aislador) van a `null`,
  que para el servicio es «de esta colección no digo nada». Para que eso fuera posible el backend
  cambió con la fase: las listas de paquetes, estaciones y vías van sin hijos, los filtros
  booleanos solo filtran si vienen y las empresas se pueden leer (`GET /business-entities`).

- **Fase 3**: los trabajos en segundo plano de `mto-configuration` (`README_ASYNC_JOBS.md`) en
  `trabajos`: exportar los perfiles de una vía a CSV, importar el maestro de perfiles
  (`profile-master.xlsx`) y el catálogo de LOV (`lov-master.xlsx`) con simulación (`dryRun`), y
  republicar los datos maestros. Lanzar es una llamada que responde 202 con el trabajo, o **429 con
  el trabajo ya rechazado** y un `Retry-After` cuando no hay cupo: se apunta igual, como rechazado,
  y se dice cuándo reintentar. Seguirlos es lo que hace `@Push`: mientras la pantalla está abierta,
  un hilo compartido consulta cada dos segundos los trabajos de la sesión que aún no han terminado y
  lleva el progreso al navegador con `UI.access()`. El fichero de un trabajo (el CSV, o el informe
  JSON de una importación, disponible también cuando terminó con errores) se descarga **a través de
  esta aplicación**, con el token de la persona: el navegador nunca habla con el gateway. La lista
  de trabajos es la de la sesión: el servicio solo permite consultar un trabajo por id, así que
  cerrar la sesión pierde la lista, no los trabajos.

| Acción sobre un trabajo | Roles de cliente de `mto-configuration-api` |
|---|---|
| Exportar perfiles, consultar, descargar | `config-read` |
| Importar el maestro de perfiles, republicar | `config-import` |
| Importar el catálogo de LOV | `config-import` + `lov-manage` |

| Acción sobre un maestro | Roles de cliente de `mto-configuration-api` |
|---|---|
| Ver la lista y buscar | `config-read` |
| Nuevo, modificar | `config-write` |
| Borrar (lógico) | `config-delete` |

Los botones siguen los permisos que aplica el servicio a cada catálogo:

| Acción | Roles de cliente de `mto-configuration-api` |
|---|---|
| Ver el catálogo | `config-read` |
| Nuevo, modificar | `config-write` + `lov-manage` |
| Borrar | `config-delete` + `lov-manage` |
| Alta múltiple, activar/desactivar seleccionados | `config-import` + `lov-manage` |

Esconder un botón es cortesía: la guarda real es `@RolesAllowed` en la vista y el 403 del servicio.
Con los usuarios de desarrollo, `config.responsable` (`mto-admin`) lo ve todo; `config.editor`
(`mto-editor`) ve los catálogos pero no puede tocarlos: le falta `lov-manage`, a propósito.

## Requisitos

- JDK 25. Node lo gestiona el plugin de Vaadin (usa uno global compatible o descarga el suyo).
- `mto-platform` levantado, con la entrada `127.0.0.1 auth.mto.local otel.mto.local` en `/etc/hosts`:
  el `iss` de los tokens es `http://auth.mto.local:8082/realms/mto` y el navegador va a ese mismo
  nombre para entrar.

## Arrancar en local

```bash
cd ../mto-platform && docker compose --profile all up -d && ./keycloak/apply-partials.sh
cd ../mto-backoffice
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

`http://localhost:8085` redirige a Keycloak; entra con un usuario de desarrollo (contraseña `local`):

| usuario | perfil | lo que ve |
|---|---|---|
| `config.responsable` | `mto-admin` | todo |
| `config.editor` | `mto-editor` | lectura; escritura de infraestructura pero no de catálogos (sin `lov-manage`) |
| `config.lector` | `mto-viewer` | solo lectura |
| `almacen.lector` | `mto-warehouse-viewer` | nada de configuración: el menú no ofrece las pantallas y la URL directa se deniega |

La pantalla **Inicio** muestra el principal, las autoridades y las cinco audiencias del access token
(`mto-configuration-api`, `mto-stock-api`, `mto-maintenance-api`, `mto-users-api`, `mto-gateway-api`):
si falta una, el servicio correspondiente responderá 401 aunque la persona tenga permisos.

Con `dev` el secreto del cliente ya viene puesto (`mto-backoffice-secret`, el que fija
`keycloak/mto-backoffice-dev.json`). El resto de variables:

| Variable | Qué | Por defecto |
|---|---|---|
| `SERVER_PORT` | Puerto | `8085` |
| `KEYCLOAK_ISSUER_URI` | Realm que emite los tokens | `http://auth.mto.local:8082/realms/mto` |
| `KEYCLOAK_CLIENT_ID` | Cliente confidencial de esta aplicación | `mto-backoffice` |
| `KEYCLOAK_CLIENT_SECRET` | Su secreto | vacío (`dev`: el local; `prod`: obligatorio) |
| `KEYCLOAK_ROLES_CLIENT_ID` | Cliente cuyos roles del access token son los permisos | `mto-configuration-api` |
| `MTO_GATEWAY_URL` | El gateway | `http://localhost:8090` |
| `MTO_GATEWAY_CONNECT_TIMEOUT` / `MTO_GATEWAY_READ_TIMEOUT` | Timeouts del cliente HTTP | `2s` / `15s` |

`.env.example` las recoge todas. Perfiles: `dev` (8085, abre el navegador, secreto local), `test`
(nadie escucha en Keycloak ni en el gateway) y `prod` (realm, secreto y gateway obligatorios).

## Seguridad

- Cliente **confidencial** `mto-backoffice` (Authorization Code con secreto) declarado en
  `keycloak/mto-backoffice-partial-import.json`, con los mismos cinco audience mapper que
  `mto-frontend`, que queda intacto y reservado a una futura SPA. Detalle en `keycloak/README.md`.
- Los permisos son los roles de cliente de `mto-configuration-api` (`config-read`, `config-write`,
  `config-delete`, `config-import`, `lov-manage`, `config-audit`), que llegan como
  `ROLE_CONFIG_READ`... y se comprueban con `@RolesAllowed` en cada vista. Los roles de realm
  (`mto-admin`, `mto-editor`...) llegan solo como `ROLE_REALM_*`: un perfil nunca se confunde con
  un permiso.
- Keycloak pone los roles en el **access token**, no en el ID token; por eso `BackofficeOidcUserService`
  verifica el access token y saca de él las autoridades. Sin eso, ninguna vista vería un rol.
- Los access token duran cinco minutos. `UserTokenProvider` los refresca desde cualquier hilo
  (también fuera de una petición HTTP: `UI.access()`, push, sondeo de trabajos), pidiéndolos por el
  nombre del principal. Cuando el refresh ya no vale (la sesión SSO ha caducado), la aplicación lo
  dice y ofrece volver a entrar.
- «Salir» cierra también la sesión de Keycloak (`end_session_endpoint`); volver a entrar pide
  credenciales.
- Un cambio de roles en Keycloak se aplica en el siguiente login: las autoridades se calculan al
  entrar.

## Cómo se habla con la API

- Un `RestClient` hacia el gateway (`/api/configuration/**` → `/api/v1/configuration/**`) con el
  Bearer de la persona y un `X-Correlation-Id` nuevo por llamada, que el gateway acepta y propaga:
  el mismo id sale en el log del gateway, en el de `mto-configuration` y en la notificación de
  error que ve la persona.
- Interfaces declarativas `@HttpExchange` escritas a mano, pantalla a pantalla (`client/`). Nada de
  `openapi-generator`: los `/v3/api-docs` no pasan por el gateway y no se van a hacer pantallas
  para los 300 endpoints.
- Los DTO son un **subconjunto**: solo las claves que usa la UI. La paginación es la forma DTO
  `{content, page:{size,number,totalElements,totalPages}}`, que `mto-configuration` fija con
  `spring.data.web.pageable.serialization-mode: via_dto`.
- Errores: `ApiErrorDecoder` entiende el `application/problem+json` de `mto-configuration` (`code`,
  `traceId`, `retryable`, `errors[{field,code,message}]`), el 401/403 del gateway (solo
  `correlationId`) y el 503 de su circuit breaker (`Retry-After`, `service`), y los convierte en
  `NotFoundApiException`, `ValidationApiException`, `ForbiddenApiException`,
  `SessionExpiredApiException`, `ConflictApiException` o `ServiceUnavailableApiException`.

## Pruebas

```bash
./mvnw test                      # todo en la JVM: sin Docker, sin Keycloak, sin gateway
./mvnw -B verify                 # lo mismo más el build de producción del frontend
```

Una clase por capa: `ClientLayerTest` (`MockRestServiceServer`), `SecurityLayerTest`,
`ViewLayerTest` (Karibu-Testing, Apache 2.0, sin navegador) y `MtoBackofficeApplicationTests`
(contexto completo). TestBench, el equivalente de Vaadin, es de pago y no se usa.

## Build de producción e imagen

En Vaadin 25 no hay perfil `production`: `./mvnw package` compila el frontend de producción
(`build-frontend` en `prepare-package`) y `vaadin-dev`, la herramienta de desarrollo, es `optional`
y no entra en el jar. `Dockerfile` construye esa imagen (la etapa de build no es Alpine porque el
Node que descarga el plugin necesita glibc) y `compose.yaml` la arranca contra `mto-platform`:

```bash
cp .env.example .env
docker compose up -d --build     # http://localhost:8085
```

El CI (`.github/workflows/ci.yml`, calcado del de `mto-gateway`) ejecuta `./mvnw -B verify`, sube
los informes de surefire, construye la imagen en cada push y la publica en GHCR al entrar en
`master`. Cuando la imagen exista, `mto-platform` podrá añadir el servicio `backoffice` a su
`compose.yaml` (perfil `backoffice`, puerto 8085).

## Puertos

| | |
|---|---|
| `mto-stock` | 8080 |
| `mto-configuration` | 8081 |
| Keycloak | 8082 |
| `mto-maintenance` | 8083 |
| `mto-users` | 8084 |
| **`mto-backoffice`** | **8085** |
| `mto-gateway` | 8090 |
