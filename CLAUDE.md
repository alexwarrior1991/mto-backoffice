# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Proyecto

`mto-backoffice`: backoffice web del dominio `MTO` (infraestructura ferroviaria de catenaria).
Aplicación **Spring Boot 4.1 / Java 25 + Vaadin Flow 25** que consume las APIs del dominio a través
de `mto-gateway` con el token de la persona, servidor a servidor. Es un **cliente**: sin base de
datos, sin broker, sin lógica de negocio. Lo que una pantalla necesita y la API no da bien se
arregla en el servicio (`mto-configuration`), no aquí. `README.md` es la referencia funcional y
operativa; `keycloak/README.md`, la del cliente OIDC en el realm.

⚠️ `mto-configuration`, `mto-stock`, `mto-maintenance`, `mto-users` y `mto-gateway` son **repos
hermanos independientes**. La infraestructura local (Keycloak, el gateway, los servicios) la levanta
`mto-platform`, cuyo `keycloak/apply-partials.sh` aplica `keycloak/mto-backoffice-partial-import.json`
y `keycloak/mto-backoffice-dev.json` de este repositorio. `compose.yaml` aquí trae **solo la aplicación**.

⚠️ Solo Vaadin **core** (Apache 2.0). Charts, Maps, Dashboard, CRUD, GridPro y TestBench son de pago
y no entran; `MtoBackofficeApplicationTests` falla si aparecen en el classpath. Nada de React/Hilla
ni de offline.

## Comandos

```bash
./mvnw compile
./mvnw test                                        # todo en JVM: ni Docker, ni Keycloak, ni gateway
./mvnw test -Dtest=ClientLayerTest                 # una clase
./mvnw test -Dtest='ViewLayerTest#theCatalogueOfTheRouteIsListedAndTheFilterIsLocal'
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev   # http://localhost:8085, abre el navegador
./mvnw -B verify                                   # incluye el build de producción del frontend
```

Entorno local: `cd ../mto-platform && docker compose --profile all up -d && ./keycloak/apply-partials.sh`
(con `127.0.0.1 auth.mto.local otel.mto.local` en `/etc/hosts`). Perfiles `dev`, `test`, `prod`.
Puerto **8085** (`dev`); el contenedor escucha en 8080 y se publica en 8085. Usuarios de desarrollo:
`config.responsable` (todo), `config.lector` (solo lectura), contraseña `local`.

Vaadin 25: `mvn package`/`verify` **es** el build de producción (`build-frontend` va ligado a
`prepare-package` y `vaadin-dev` es `optional`); no hay perfil `production`. El plugin usa un Node
global compatible o descarga el suyo en `~/.vaadin`. Lo que genera (`src/main/frontend/generated`,
`package.json`, `vite.generated.ts`...) está en `.gitignore`.

## Arquitectura

Paquetes bajo `com.alejandro.mtobackoffice`:

- `configuration/security` — `SecurityConfiguration` (cadena con `VaadinSecurityConfigurer` y
  `oauth2LoginPage("/oauth2/authorization/keycloak")`; sondas de salud abiertas),
  `KeycloakClientRegistrations` (el registro OIDC **construido a mano** desde `app.keycloak.*`),
  `BackofficeOidcUserService` (los roles se leen del **access token**), `KeycloakRoleMapper`
  (claims → `ROLE_*`), `BackofficeUser` (el usuario con las audiencias del access token),
  `CurrentPrincipal` (nombre del principal desde cualquier hilo), `PrincipalSessionRecorder`,
  `SecurityRoles`, `SecurityAuthorityPrefixes`, `JwtClaimNames`, `KeycloakProperties`.
- `configuration/client` — `GatewayClientConfiguration` (un `RestClient` hacia el gateway con dos
  interceptores, `BearerTokenInterceptor` y `CorrelationIdInterceptor`, y `ApiErrorDecoder` como
  manejador de estado; `HttpServiceProxyFactory` para las interfaces `@HttpExchange`),
  `UserTokenProvider` (`AuthorizedClientServiceOAuth2AuthorizedClientManager` con `refreshToken()`),
  `GatewayProperties`, `CorrelationProperties`.
- `client` — las interfaces `@HttpExchange` por servicio: `client/configuration/LovClient` (los
  ocho endpoints de `AbstractLovController` parametrizados por recurso; `LovResource`, los 17
  catálogos con su ruta y su título), `MasterClient<D>` (lo que comparten los maestros de
  `CRUDController`: `POST /filter` paginado, `GET/POST/PUT/DELETE`) con seis subinterfaces vacías
  que solo ponen la ruta y el tipo (`StationClient`, `TrackClient`...; Spring resuelve el genérico
  contra la subinterfaz), `BusinessEntityClient` (solo lectura), `MasterFilters` (cuerpo y orden
  del `/filter`) y `JobsClient` (los trabajos en segundo plano: importaciones multipart con
  `@RequestPart Resource`, exportación, republicado, la lista de todas las familias (`GET /jobs`,
  paginada y filtrable por tipo y estado), estado y fichero por familia, `JobFamily`). Los DTO
  (`client/dto`): `LovDto` es un record con solo las claves que usa la UI y
  `@JsonInclude(NON_NULL)`; los maestros (`client/dto/master`) son **clases mutables** que heredan
  de `MasterDto` (ver la regla de abajo), con `LovRef` para las referencias a catálogo y los hijos
  tipados que los editores gestionan (`CantileverDto` con su `SteadyArmDto` 1:1,
  `SectionInsulatorSwitchDto`; `DisconnectorDto` trae además `profileCode`/`profileKp`, solo de
  salida); `PageResponse<T>` con la forma `{content, page}`; los trabajos (`client/dto/jobs`:
  `JobDto`, la unión de las tres respuestas del servicio, `JobStatus`, `JobType`, `UploadedFile`). Los
  errores en `client/error` (`ApiProblem`, `ApiErrorDecoder` y la jerarquía
  `BackofficeApiException`, con `TooManyRequestsApiException` llevando el cuerpo del 429).
- `ui` — `MainLayout` (AppLayout; el menú lo dan las vistas anotadas con `@Menu`, filtradas por
  `AccessAnnotationChecker`; las de `infraestructura/*` se agrupan bajo «Infraestructura» y los
  catálogos se listan a mano porque su vista lleva el recurso en la ruta), `ui/views/HomeView`,
  `ui/lov` (`LovCrudView` en `catalogos/:resource`, `LovEditorDialog` con `Binder` sobre el modelo
  mutable `LovForm`, `LovBulkCreateDialog` con su parser de líneas), `ui/master` (`MasterView<D>`,
  la lista paginada en el servidor con `grid.setItems(fetch, count)` sobre `POST /filter`;
  `MasterEditorDialog<D>`, el `Binder` sobre el DTO leído; una vista y un editor por maestro;
  `ReferenceCatalog` y `LovCatalog`, los nombres y las entradas de catálogo cargados una vez por
  pantalla; `Pickers`, desplegables y conversores; `EnabledFilter`, el filtro de tres estados;
  `ChildrenEditor<C>`, la tabla de hijos dentro del editor del padre, con `CantileverDialog` y
  `SwitchDialog`), `ui/jobs` (`JobsView` en `trabajos`: los lanzadores y la lista del servicio,
  paginada y filtrada; `JobLog`, lo que solo sabe la sesión de sus trabajos —la etiqueta y el
  último estado— en la `VaadinSession`; `JobPolling`, el hilo compartido que vuelve a pedir la
  página mientras hay algo en curso; `JobErrorsDialog`), `ui/support` (`UiErrors`: excepción →
  `Notification`; `ServerValidation`: `errors[]` del servicio → campos del `Binder`).
- `configuration/vaadin` — `BackofficeSystemMessages`, los mensajes de sistema de Vaadin en
  castellano y con el aviso de sesión caducada apagado (recarga → login → SSO).

### Reglas que no se rompen

- **El token nunca llega al navegador.** El navegador habla el protocolo de Vaadin con esta
  aplicación; el access token vive en el `OAuth2AuthorizedClientService` del servidor y solo sale
  hacia el gateway. CORS no interviene y no hay que tocar los orígenes del gateway.
- **Los roles se leen del access token, no del ID token.** Keycloak pone `resource_access` solo en el
  access token (todos los audience mapper del realm son `id.token.claim=false`), y `oauth2Login`
  construye las autoridades desde el ID token. `BackofficeOidcUserService` verifica el access token
  (firma y emisor) y aplica `KeycloakRoleMapper`. Un `GrantedAuthoritiesMapper` no sirve: no ve el
  access token.
- **Los roles de realm se emiten solo como `ROLE_REALM_*`, nunca como `ROLE_*`.** Los permisos que
  comprueban las vistas (`@RolesAllowed("CONFIG_READ")`) son roles de **cliente** de
  `mto-configuration-api`. Si un rol de realm se emitiera con `ROLE_`, quien administre el realm
  podría crear un rol llamado como un permiso y concederlo a cualquiera (`SecurityLayerTest`).
- **Sin descubrimiento OIDC en el arranque.** `KeycloakClientRegistrations` deriva los endpoints del
  issuer y añade `end_session_endpoint` a los metadatos; con `issuer-uri` en YAML la aplicación no
  arrancaría sin Keycloak, y con él tampoco arrancarían los tests de contexto. El JWK Set se pide al
  primer login.
- **El token se pide por nombre de principal desde cualquier hilo.** Boot guarda el cliente
  autorizado por principal (`AuthenticatedPrincipalOAuth2AuthorizedClientRepository` sobre
  `InMemoryOAuth2AuthorizedClientService`); `UserTokenProvider` lo refresca sin petición HTTP. En un
  hilo de fondo el principal se fija con `CurrentPrincipal.runAs(nombre, tarea)`; en una petición o
  dentro de `UI.access()` lo da el `SecurityContextHolder` (estrategia Vaadin-aware). Sin principal,
  ninguna llamada sale (`SessionExpiredApiException`).
- **Cada llamada saliente lleva un `X-Correlation-Id` nuevo** (UUID). El gateway lo acepta y lo
  propaga al servicio; es la referencia que enseñan las notificaciones de error.
- **Los DTO son un subconjunto**: solo las claves que la UI usa. Un campo nuevo en el servicio no
  rompe nada aquí; lo desconocido se ignora.
- **Los errores se tipan en la capa de cliente**, no en las vistas. `ApiErrorDecoder` tolera los
  tres formatos que llegan (el `problem+json` de `mto-configuration` con `code`/`traceId`/`errors`,
  el 401/403 del gateway solo con `correlationId`, y el 503 del fallback del gateway con
  `Retry-After` y `service`) y las vistas solo conocen `BackofficeApiException` y sus subclases.
- **La paginación es la forma DTO** `{content, page:{size,number,totalElements,totalPages}}`, fijada
  en `mto-configuration` con `spring.data.web.pageable.serialization-mode: via_dto` y pinada allí
  por test. `PageResponse<T>` la lee (y tolera `first`/`last` de stock y maintenance).
- **El menú no es una guarda.** `MainLayout` esconde lo que la persona no puede abrir; quien manda
  es `@RolesAllowed` en la vista y el 403 del servicio. Dentro de una vista pasa lo mismo: los
  botones de `LovCrudView` siguen los permisos del servicio (`config-write`+`lov-manage` para crear
  y modificar, `config-delete`+`lov-manage` para borrar, `config-import`+`lov-manage` para los
  lotes) con `AuthenticationContext.hasAllRoles`, y un 403 igualmente se traduce a notificación.
- **Una vista por familia de endpoints, no por recurso.** Los 17 catálogos comparten controlador
  base y DTO en `mto-configuration`; aquí son una `LovCrudView` con el recurso en la ruta. Un
  catálogo nuevo allí es una constante más en `LovResource`, nada más.
- **La validación de negocio vive en el servicio.** El formulario solo exige lo evidente (código y
  descripción obligatorios, longitud de columna) y vuelca `errors[{field, code, message}]` campo a
  campo con `ServerValidation`. Un `code` repetido llega como 409 y un cuerpo sin `code` como 400
  desde que `RestExceptionHandler` los mapea (antes eran 500).
- **Un maestro se edita sobre la fila leída y se devuelve entero** (`README_API.md` §4 de
  `mto-configuration`: lee, modifica sobre lo leído, devuélvelo entero). Por eso los DTO de
  `client/dto/master` son clases mutables y no records, y por eso tienen lo que un DTO «solo con
  las claves que usa la UI» no tendría: `extras` (`@JsonAnySetter`/`@JsonAnyGetter`), que devuelve
  tal cual lo que el servicio mandó y aquí no tiene campo, y `forgetChildren()`, que pone a `null`
  las colecciones de hijos que el editor no toca porque para el servicio `null` es «no digo nada»
  y una lista vacía u omitida borra a los hijos. Esas clases **no** llevan
  `@JsonInclude(NON_NULL)`: el `null` tiene que viajar. `versionNumber` vuelve como se leyó: es el
  bloqueo optimista, y un cambio concurrente llega como 409. Se edita una **copia** de la fila
  (por Jackson, para que lleve los `extras`), para que cancelar o un rechazo del servicio no
  cambien lo que enseña el Grid. `profiles.disconnector` (1:1, `null` = desvincular) no se toca.
- **Las listas de maestros se paginan en el servidor.** `MasterView` pide cada página a
  `POST /{recurso}/filter` con `page`, `size`, `sort=campo,asc` y `searchText`; el recuento es
  `totalElements`. Nada de `findAll` en memoria como en los catálogos: los perfiles son miles.
  Las filas de paquetes, estaciones y vías llegan sin hijos y los filtros booleanos solo filtran
  si vienen; las dos cosas se arreglaron en `mto-configuration` para esta fase, no aquí.
- **Un trabajo se lanza y se sigue; no se espera.** Lanzar responde 202 con el trabajo, o 429 con
  el trabajo ya rechazado y un `Retry-After` (`TooManyRequestsApiException` trae ese cuerpo, y
  `JobsView` lo apunta como rechazado en vez de tratarlo como un fallo). El progreso lo trae
  `@Push`: `JobPolling` consulta desde un hilo propio, con el principal fijado por
  `CurrentPrincipal.callAs`, y `JobsView` lo lleva a la pantalla con `UI.access()`; sin pantalla
  abierta, o sin nada en curso, no se consulta nada. La lista es la del servicio (`GET /jobs`):
  `JobLog` solo guarda la etiqueta con la que esta sesión lanzó cada trabajo y su último estado,
  para pintarla encima y avisar cuando uno termina; un trabajo de la sesión que no esté en la
  página se consulta por su familia. Las filas no traen los errores por elemento: `JobErrorsDialog`
  los pide al detalle. El `downloadUrl` del servicio no se usa: es su ruta interna, y la descarga
  se pide por familia e id a través del gateway.
- **El fichero de un trabajo se descarga a través de esta aplicación.** `DownloadHandler` pide el
  fichero al servicio con el token de la persona y lo sirve en la misma respuesta; un `Anchor` al
  gateway no serviría porque el navegador no tiene token (primera regla). Una exportación solo se
  descarga `COMPLETED`; una importación también `COMPLETED_WITH_ERRORS`, porque su fichero es el
  informe de esos errores (`JobDto.isDownloadable`).
- **Una colección de hijos que el editor gestiona va entera o no va.** `ChildrenEditor` trabaja
  sobre una lista propia y `edited()` solo la devuelve si alguien la tocó; `prepare(dto)` del
  editor la pone entonces en el DTO, ya después de `forgetChildren()`, y si no, se queda el
  `null` que deja al servicio sin decir nada. Media lista no existe: para el servicio la
  colección que llega es el estado final y el hijo que falta se borra (`README_API.md` §4). El 1:1
  (`profiles.disconnector`, `cantilevers.steadyArm`) se manda entero para vincular o mantener y
  `null` para desvincular, y por eso el seccionador del editor de perfiles no pasa por el `Binder`:
  lo que viaja es el objeto leído del servicio, no un id.
- **Un reinicio no deja un diálogo muerto.** Sesión y tokens viven en memoria y se pierden al
  reiniciar; `BackofficeSystemMessages` deja apagado el aviso de sesión caducada para que Vaadin
  recargue en cuanto lo detecte y la cadena de seguridad reentre por el SSO de Keycloak. No se
  persisten las sesiones (serializar una sesión de Vaadin es frágil y el almacén de tokens se
  perdería igual) ni se introduce una base de datos para los tokens: es un cliente.
- **Nada de componentes de pago.** `vaadin-spring-boot-starter` trae solo `vaadin-core-internal`.

### Tests

Una clase por capa; se añaden métodos, no clases: `ClientLayerTest` (interfaces `@HttpExchange` y
`RestClient` reales contra `MockRestServiceServer`: prefijo del gateway, Bearer y correlación, forma
de página, `problem+json` de configuration, 401/403 y 503 del gateway, cuerpo no JSON; los
maestros: resolución del genérico, parámetros de página y orden del `/filter`, `extras` e hijos a
`null` en un `PUT`, referencias a catálogo como `{id, code}`, las ménsulas tipadas con su brazo y
el seccionador 1:1 en un `PUT`; los trabajos: la importación como parte multipart con `dryRun` en
la query, el 429 con el trabajo rechazado y el `Retry-After`, la lista paginada con sus filtros,
el estado por familia y el fichero con sus cabeceras, qué es descargable),
`SecurityLayerTest` (mapeo de roles, registro OIDC sin descubrimiento, roles desde el access token,
`CurrentPrincipal`), `ViewLayerTest` (Karibu-Testing 2.7.3 sobre el contexto de Spring: el catálogo
de la ruta y su filtro local, menú por roles, controles de escritura ocultos sin permiso, alta por
diálogo, errores del servicio campo a campo, borrado con confirmación, lote sobre la selección,
parser del alta múltiple, notificación de error, diagnóstico de audiencias; los maestros: lista
paginada, ordenada y filtrada contra el cliente simulado, nombres de referencias en las columnas,
edición sobre una copia que vuelve con `extras` e hijos a `null`, errores del servicio sobre un
desplegable, borrado confirmado, alta de un perfil con sus referencias, KP no válido, las
ménsulas a `null` sin tocar y enteras al tocarlas, las agujas en su diálogo y enteras al guardar,
el perfil legible en la lista de seccionadores, los mensajes de sistema; los trabajos: subir y
lanzar una importación, el progreso llegando por `pollOnce()` + `UI.access()` hasta el enlace de
descarga y el botón de errores (que pide el detalle), el 429 apuntado como rechazado con su aviso,
un trabajo propio fuera de la página seguido por su familia, la lista paginada y filtrada en el
servicio, los lanzadores según permisos) y
`MtoBackofficeApplicationTests` (contexto completo sin Keycloak ni gateway; redirección al login;
sonda de salud; ausencia de artefactos comerciales). Todo corre en la JVM sin Docker.
