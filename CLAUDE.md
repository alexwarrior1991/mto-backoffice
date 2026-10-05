# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Proyecto

`mto-backoffice`: backoffice web del dominio `MTO` (infraestructura ferroviaria de catenaria).
Aplicación **Spring Boot 4.1 / Java 25 + Vaadin Flow 25** que consume las APIs del dominio a través
de `mto-gateway` con el token de la persona, servidor a servidor. Es un **cliente**: sin base de
datos, sin broker, sin lógica de negocio. Lo que una pantalla necesita y la API no da bien se
arregla en el servicio (`mto-configuration`, `mto-users`, `mto-stock` o `mto-maintenance`), no aquí. `README.md` es la referencia funcional y
operativa; `keycloak/README.md`, la del cliente OIDC en el realm.

⚠️ **Convive con `mto-frontend`**, la SPA en React del dominio: las dos aplicaciones se usan
indistintamente, con las mismas pantallas, las mismas rutas y las mismas reglas, y entran por el
mismo SSO. Se mantienen a la par: un cambio de comportamiento en una (una regla, un fallo arreglado,
una llamada distinta, lo que se ofrece, cómo se dice un error) se lleva a la otra en el mismo
cambio; los textos no tienen que coincidir (aquí siguen sin tildes). «Abrir en mto-frontend», en la
barra, lleva a la misma pantalla allí, y la SPA tiene el enlace de vuelta.

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
../mto-platform/scripts/e2e.sh                     # el e2e de mto-frontend contra la plataforma entera, como el job e2e
```

Entorno local: `cd ../mto-platform && docker compose --profile all up -d && ./keycloak/apply-partials.sh`
(con `127.0.0.1 auth.mto.local otel.mto.local` en `/etc/hosts`; levanta también `mto-frontend`, en el 4200). Perfiles `dev`, `test`, `prod`.
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
  (claims → `ROLE_*` para los clientes de `app.keycloak.roles-client-ids`, más el sinónimo
  `ROLE_CLIENT_<CLIENTE>_*`), `BackofficeUser` (el usuario con las audiencias del access token),
  `CurrentPrincipal` (nombre del principal desde cualquier hilo), `PrincipalSessionRecorder`,
  `SecurityRoles`, `UserRoles`, `StockRoles`, `MaintenanceRoles` y `NotificationRoles` (los
  permisos de `mto-configuration-api`, `mto-users-api`, `mto-stock-api`, `mto-maintenance-api` y
  `mto-notification-api`), `SecurityAuthorityPrefixes`, `JwtClaimNames`, `KeycloakProperties`.
- `configuration/client` — `GatewayClientConfiguration` (un `RestClient` hacia el gateway con dos
  interceptores, `BearerTokenInterceptor` y `CorrelationIdInterceptor`, y `ApiErrorDecoder` como
  manejador de estado; `HttpServiceProxyFactory` para las interfaces `@HttpExchange`),
  `UserTokenProvider` (`AuthorizedClientServiceOAuth2AuthorizedClientManager` con `refreshToken()`),
  `GatewayProperties`, `CorrelationProperties`.
- `client` — las interfaces `@HttpExchange` por servicio: `client/configuration/LovClient` (los
  ocho endpoints de `AbstractLovController` parametrizados por recurso; `LovResource`, los 17
  catálogos con su ruta y su título), `MasterClient<D>` (lo que comparten los maestros de
  `CRUDController`: `POST /filter` paginado, `GET/POST/PUT/DELETE`) con seis subinterfaces vacías (salvo `TrackClient`, que añade `schematic`: el esquema de la vía en una llamada)
  que solo ponen la ruta y el tipo (`StationClient`, `TrackClient`...; Spring resuelve el genérico
  contra la subinterfaz), `BusinessEntityClient` (solo lectura), `MasterFilters` (cuerpo y orden
  del `/filter`) y `JobsClient` (los trabajos en segundo plano: importaciones multipart con
  `@RequestPart Resource`, exportación, republicado, la lista de todas las familias (`GET /jobs`,
  paginada y filtrable por tipo y estado), estado y fichero por familia, `JobFamily`);
  `client/users/UsersClient` (toda la API de `mto-users` bajo `/api/users`: búsqueda paginada
  `first`/`max`, alta, modificación parcial, activar, borrado, contraseña temporal, correo de
  acciones, sesiones normales y offline, credenciales, roles de cliente —quitar es un `DELETE`
  con cuerpo—, perfiles y sus miembros; sus DTO son records en `client/dto/users`, con
  `UsersPage` para `{content, first, max, total}`); `client/stock` (la API de `mto-stock` bajo
  `/api/stock`: `StockCatalogueClient<D, C, U>`, lo que comparten los cinco catálogos —lista con
  `search`/`active` y `Pageable`, lectura, alta, modificación con `active`, historial— con
  `WarehouseClient`, `SupplierClient`, `ProjectClient`, `MaterialClient` (además existencias,
  bajo mínimo y libro por material) y `AssemblyClient` (además disponibilidad) resolviendo el
  genérico contra la subinterfaz; `MovementClient` (entradas, salidas, ajustes, transferencias y
  el libro) y `ReservationClient` (alta, modificación, cancelar con un `DELETE` que devuelve
  cuerpo, liberar, consumir); DTO como records en `client/dto/stock`, con enumerados con etiqueta
  y tolerantes —ver las reglas—;
  el historial usa `RevisionDto<T>`, `RevisionMetadataDto`, `RevisionOperation` y `AuditDto`, que
  viven en `client/dto` porque mantenimiento los comparte); `client/maintenance` (la API de
  `mto-maintenance` bajo `/api/maintenance`: `OrderClient` —órdenes, sus transiciones, su historial
  de estados, sus tareas, que solo existen dentro de una orden o de un turno, y sus líneas de
  material—, `AssetClient`, `MaintenanceCatalogClient` —equipos, tipos de tarea, plantillas de
  inspección—, `ShiftClient` —turnos, sus tareas y perfiles, asignar una tarea—, `InspectionClient`,
  `DefectClient` y `ReportClient` —cada informe en JSON y como fichero `ResponseEntity<byte[]>`—,
  todos con `revisions` salvo los catálogos y los informes; DTO como records en
  `client/dto/maintenance`: filtros (`OrderFilter`, `AssetFilter`...), peticiones `*Request`, las
  modificaciones como `MergePatch<*UpdateRequest>` (lo cambiado, lo vaciado y la versión leída) y
  enumerados tolerantes —ver las reglas—); `client/notification/NotificationClient` (la API de
  `mto-notification` bajo `/api/notifications`: mi bandeja paginada con sus filtros, el contador
  acotado, marcar una o todas, el registro con sus filtros y el detalle de una línea, y los
  accesos; DTO como records en `client/dto/notification`, con `payload` como `Map` y los
  enumerados tolerantes `ActivityCategory`, `ActivitySeverity`, `ActorKind` y `AccessOutcome`).
  Los DTO
  (`client/dto`): `LovDto` es un record con solo las claves que usa la UI (con `versionNumber`,
  que vuelve como se leyó) y `@JsonInclude(NON_NULL)`; los maestros (`client/dto/master`) son
  **clases mutables** que heredan de `MasterDto` (ver la regla de abajo), con `LovRef` para las referencias a catálogo y los hijos
  tipados que los editores gestionan (`CantileverDto` con su `SteadyArmDto` 1:1,
  `SectionInsulatorSwitchDto`; `DisconnectorDto` trae además `profileCode`/`profileKp`, solo de
  salida); `PageResponse<T>` con la forma `{content, page}`; los trabajos (`client/dto/jobs`:
  `JobDto`, la unión de las tres respuestas del servicio, `JobStatus`, `JobType`, `UploadedFile`);
  `ClientEnums`, lo que comparten los enumerados tolerantes (`parse` y `selectable`). Los
  errores en `client/error` (`ApiProblem`, `ApiErrorDecoder` y la jerarquía
  `BackofficeApiException`, con `TooManyRequestsApiException` llevando el cuerpo del 429).
- `ui` — `MainLayout` (AppLayout, con «Abrir en mto-frontend» en la barra; el menú lo dan las vistas anotadas con `@Menu`, filtradas por
  `AccessAnnotationChecker`; las rutas con prefijo conocido (`infraestructura/*`, `usuarios/*`,
  `almacen/*`, `mantenimiento/*`) se agrupan por prefijo (`MainLayout.GROUPS`), una entrada cuya ruta es el propio prefijo es el
  nodo del grupo, y los catálogos se listan a mano porque su vista lleva el recurso en la ruta),
  `ui/views/HomeView`,
  `ui/lov` (`LovCrudView` en `catalogos/:resource`, `LovEditorDialog` con `Binder` sobre el modelo
  mutable `LovForm`, `LovBulkCreateDialog` con su parser de líneas), `ui/master` (`MasterView<D>`,
  la lista paginada en el servidor con `LazyPages` (una petición por página) sobre `POST /filter`;
  `MasterEditorDialog<D>`, el `Binder` sobre el DTO leído; una vista y un editor por maestro;
  `ReferenceCatalog` y `LovCatalog`, los nombres y las entradas de catálogo cargados una vez por
  pantalla; `Pickers`, desplegables y conversores; `EnabledFilter`, el filtro de tres estados;
  `ChildrenEditor<C>`, la tabla de hijos dentro del editor del padre, con `CantileverDialog` y
  `SwitchDialog`; `TrackSchematicDialog`, la ventana con el esquema de una vía, y `SchematicDrawing`,
  el SVG que la dibuja en Java puro; `MasterView.addRowActions` y `rowButton`, el gancho de acciones
  de fila con el que `TracksView` pone el botón «Esquema»), `ui/jobs` (`JobsView` en `trabajos`: los lanzadores y la lista del servicio,
  paginada y filtrada; `JobLog`, lo que solo sabe la sesión de sus trabajos —la etiqueta y el
  último estado— en la `VaadinSession`; `JobErrorsDialog`), `ui/users` (`UsersView` en `usuarios`:
  la lista paginada en el servidor con `LazyPages` sobre `GET /api/users` y
  `first`/`max`; `UserEditorDialog`, el `Binder` sobre el modelo mutable `UserForm`, cuyas
  propiedades se llaman como los campos del servicio para `ServerValidation`; `UserAttributes`,
  los atributos como texto `clave=valor` por línea; `UserDetailView` en `usuarios/:userId`, la
  ficha con su cabecera, su botonera por permiso y un `TabSheet` de paneles `LazyPanel` (en
  `ui/support`), que piden sus datos la primera vez que se abren: `UserProfilesPanel`, `UserRolesPanel`,
  `UserSessionsPanel` (normales y offline) y `UserCredentialsPanel`; `ResetPasswordDialog` y
  `ExecuteActionsEmailDialog`, cada uno con su `Binder` sobre un `Form` con los nombres del
  servicio; `TakeOut`, las tres llamadas de «sacar a la persona» en su orden, parando en el
  primer fallo; `UserProfilesView` en `usuarios/perfiles` y `ClientRolesView` en `usuarios/roles`,
  los dos catálogos de solo lectura con filtro local, y `MembersPanel`, los miembros de un perfil
  o de un rol paseados sin total), `ui/stock` (`StockRoutes`, las rutas del módulo de almacén bajo
  `almacen`; `StockCatalogueView<D>`, la lista paginada en el servidor sobre
  `StockCatalogueClient.search` con búsqueda, estado y orden de columna, con `MaterialsView`,
  `WarehousesView`, `SuppliersView` y `ProjectsView` poniendo columnas y editor;
  `CatalogueEditorDialog` con `Binder` sobre `CatalogueForm` para almacenes, proveedores y
  proyectos, y `MaterialEditorDialog` sobre `MaterialForm`; `StockView` en `almacen`, la entrada
  «Almacén» del menú y a la vez el nodo del grupo: las cifras de un material en un almacén
  (`GET /materials/{id}/stock`), su libro y los materiales bajo mínimo; `MovementsView` en
  `almacen/movimientos`, el libro entero con filtros; `MovementDialog` (un `Kind` por operación:
  entrada, salida, transferencia, ajuste) con `Binder` sobre `MovementForm`, cuyas propiedades se
  llaman como los campos de la petición aunque guarden el resumen elegido; `StockOperations`, los
  botones por permiso; `StockPickers`, los desplegables que buscan en el servidor; `MovementGrid`,
  las columnas del libro; `ReservationsView` en `almacen/reservas`, la lista paginada con sus
  filtros y, en cada fila activa, modificar, salida con la reserva, consumir, liberar y cancelar;
  `ReservationDialog` con `Binder` sobre `ReservationForm`; `AssembliesView` en
  `almacen/conjuntos`, un catálogo más cuyo editor (`AssemblyEditorDialog`) lleva la lista de
  materiales entera en `BomEditor` y cuya fila ofrece, también a quien solo lee,
  `AssemblyAvailabilityDialog`, la disponibilidad por almacén que calcula el servicio;
  el historial de cada fila, en `RevisionsDialog`, abierto desde el botón de historial que cada
  catálogo y cada reserva ofrecen a quien puede leer; `StockClients` y `StockFormats`),
  `ui/maintenance` (`MaintenanceRoutes`, las rutas bajo `mantenimiento`; `MaintenanceClients`, los
  clientes de sus pantallas, de mantenimiento y de los otros dos servicios; `MaintenanceNames`, los
  nombres de lo que el servicio solo guarda como id —vías, estaciones y paquetes de
  `mto-configuration`, almacenes y proyectos de `mto-stock`—; `OrdersView` en `mantenimiento`, la
  entrada «Mantenimiento» del menú y el nodo del grupo, y `OrderDetailView` en
  `mantenimiento/ordenes/:orderId`, la ficha con los botones que su estado admite
  (`OrderTransitionDialog`, un `Kind` por transición; `ReasonDialog` para cancelar) y pestañas
  `LazyPanel`: `OrderTasksPanel` (con `TaskEditorDialog` y `GenerateTasksDialog`),
  `OrderMaterialsPanel` (`MaterialUsageDialog`), `OrderDefectsPanel`, `OrderInspectionsPanel` y
  `StatusHistoryPanel`; `AssetsView` en `mantenimiento/activos` (con `AssetOrdersDialog`);
  `ShiftsView` y `ShiftDetailView` en `mantenimiento/turnos` (`ShiftTransitionDialog`,
  `AssignTasksDialog`, `ShiftTasksPanel`, `ShiftProfilesPanel` y `ShiftReportPanel`, el parte);
  `CompleteTaskDialog`, completar una tarea con sus defectos en línea y el material gastado, y
  `CheckItemsDialog`, el checklist de una tarea o de una inspección; `InspectionsView` e
  `InspectionDetailView` (`InspectionOutcomeDialogs`: el defecto y la orden correctiva que genera),
  `DefectsView` y `DefectDetailView` (`DefectTransitionDialogs`); `ReportsView` en
  `mantenimiento/informes`; `TeamsView`, `TaskTypesView` e `InspectionTemplatesView`, los
  catálogos; un `*EditorDialog` con `Binder` sobre un `*Form` mutable por recurso, cuyas
  propiedades se llaman como los campos de la petición para `ServerValidation`;
  `MaintenanceHistory`, el botón «Historial» y la línea de cada recurso en `RevisionsDialog`;
  `MaintenanceUi`, `MaintenancePickers`, `MaintenanceCatalogs` y `MaintenanceFormats`, lo
  compartido), `ui/notification` (`NotificationRoutes`: `notificaciones`, `actividad` y
  `actividad/accesos`; `InboxBell`, la campana de la barra, que `MainLayout` pone solo con
  `notification-inbox` y que pide el contador al entrar y cada 30 s desde `SharedPolling` con
  `CurrentPrincipal.callAs` + `UI.access()`; `NotificationsView`, la bandeja paginada en el
  servidor, que abre con las no leídas, marca al abrir y sigue el enlace con `NotificationLinks`
  (una ruta de esta aplicación con sus parámetros, o una pestaña nueva si es absoluto);
  `ActivityView`, el registro con los filtros del servicio y los de la URL (`BeforeEnterObserver`);
  `AccessView`, los accesos con su permiso aparte; `EventDetailDialog`, una línea entera con su
  `payload` clave a clave), `ui/support` (`UiErrors`: excepción →
  `Notification`; `ServerValidation`: `errors[]` del servicio → campos del `Binder`;
  `OffsetPager`: anteriores/siguientes para una lista `first`/`max` sin total, donde una página
  llena es la única señal de que hay más; `RevisionsDialog<D>`, el historial de cualquier fila
  (paginado, la más reciente primero; el 404 es «sin historial»); `LazyPanel`, la pestaña que
  pide sus datos al abrirse; `Downloads`, el fichero de un servicio servido a través de esta
  aplicación con `DownloadHandler`, que notifica en la pantalla un fallo; `Formats`, cantidades y
  fechas; `SharedPolling`, el hilo compartido que vuelve a preguntar al servicio mientras hay una
  pantalla abierta: los trabajos en curso y el contador de la campana; `PageVisibility`, si la
  pestaña del navegador se ve, para no preguntar con ella oculta; `TextMatching`, el orden natural
  y la comparación sin mayúsculas ni tildes de una lista que ya está entera en pantalla;
  `Required`, lo obligatorio que no admite solo espacios; `LazyPages`, la lista paginada en el
  servidor con una sola petición por página; `RowActions`, cómo se abre una fila: doble clic o su
  botón; `Numbers`, las comprobaciones de un número antes de llamar, con y sin `Binder`).
- `configuration/vaadin` — `BackofficeSystemMessages`, los mensajes de sistema de Vaadin en
  castellano y con el aviso de sesión caducada apagado (recarga → login → SSO); `FrontendProperties`
  (`app.frontend.url`, la SPA: `linkTo(ruta)` da el enlace de «Abrir en mto-frontend», o nada si no
  es una dirección `http(s)`) y `FrontendConfiguration`, que la registra. El tema no vive
  aquí sino en `MtoBackofficeApplication`, el `AppShellConfigurator`: `@StyleSheet(Lumo.STYLESHEET)`
  y `@StyleSheet(Lumo.UTILITY_STYLESHEET)`. En Vaadin 25 un shell sin ellos deja la aplicación con
  la letra y los colores del navegador, y las clases de `LumoUtility` sin efecto.

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
  comprueban las vistas (`@RolesAllowed("CONFIG_READ")`, `@RolesAllowed("USERS_READ")`) son roles
  de **cliente** de `mto-configuration-api`, `mto-users-api`, `mto-stock-api`,
  `mto-maintenance-api` y `mto-notification-api` (`app.keycloak.roles-client-ids`).
  Si un rol de realm se emitiera con `ROLE_`, quien administre el realm podría crear un rol llamado
  como un permiso y concederlo a cualquiera (`SecurityLayerTest`). El mapeo emite `ROLE_X` para
  los cinco clientes, así que sus nombres de rol no pueden solaparse (`config-*` y `lov-manage`,
  `users-*`, `stock-*`, `maintenance-*` y `notification-*`; `SecurityLayerTest` lo comprueba), y además
  `ROLE_CLIENT_<CLIENTE>_X`.
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
  cinco formatos que llegan (el `problem+json` de `mto-configuration` con `code`/`traceId`/`errors`,
  el de `mto-users` con `errorCode`/`validationErrors[{field,message}]` —alias en `ApiProblem`, sin
  código por campo—, el JSON de `mto-stock`, que no es `problem+json` y trae `error` y `message`
  —alias en `title` y `detail`— y que `mto-maintenance` manda igual, con `path` y `method` de más,
  el 401/403 del gateway solo con `correlationId`, y el 503 del
  fallback del gateway con `Retry-After` y `service`) y las vistas solo conocen
  `BackofficeApiException` y sus subclases. Un 502 no es transitorio y su notificación lleva el
  detalle; un 409 `STK-001` es falta de stock y un 422 sin errores por campo es una regla de negocio
  (`UiErrors` los dice así, no como «conflicto» ni «petición no válida»). Los dos 409 de
  `mto-configuration` tampoco se dicen igual: `CON-001` es una versión vieja («recarga y vuelve a
  intentarlo») y `BUS-002`, un valor único repetido o una entrada en uso, que recargar no arregla.
  Los de estado de `mto-maintenance` no piden recargar: `TRN-001` (el estado no admite la transición), `SHF-001` (el
  turno no admite ese trabajo), `MAT-001` (la línea de material), `AST-001` (activo desactivado, o
  un dato que manda `mto-configuration`) y `AST-409`/`TEA-409` (código repetido); `INS-001` es un
  422 de la inspección y su checklist, y el 503 `STK-503`, el almacén caído al sincronizar o quitar
  una línea, que se queda como estaba. Al sincronizar, el almacén también puede decir que no: 409
  `STK-001` sin existencias, que se dice como en almacén, o 422 `STK-422` por otro motivo («el
  almacén ha rechazado la operación», con el motivo de stock).
- **La paginación es la forma DTO** `{content, page:{size,number,totalElements,totalPages}}`, fijada
  en `mto-configuration` con `spring.data.web.pageable.serialization-mode: via_dto` y pinada allí
  por test. `PageResponse<T>` la lee (y tolera `first`/`last` de stock y maintenance). La API de
  usuarios pagina al estilo de Keycloak (`first`/`max` con `max` ≤ 200, `UsersPage<T>`), y las
  listas de miembros de un perfil o de un rol no traen total. La de almacén es el `Pageable` de
  Spring por parámetros (`page`, `size`, `sort=campo,asc`; solo atributos de la entidad, o el
  servicio responde 500) con la misma página anidada, que `PageResponse<T>` ya lee. La de
  mantenimiento es la misma (un `sort` desconocido allí es 400 `REQ-400`), y lo que el servicio
  calcula (el próximo preventivo, el avance de una orden) no se ordena. Sus listas anidadas (tareas
  de una orden o de un turno, líneas de material, equipos, tipos de tarea) llegan enteras. Como en
  mto-frontend, toda lista de almacén y de mantenimiento lleva el id para desempatar al final de su
  orden (`MasterFilters.withTieBreak` con `BY_ID`; en mantenimiento, dentro de los `default search`
  de cada cliente), y las de `mto-notification` lo que el servicio deja ordenar: la bandeja
  `createdAt,desc` (no admite el id), el registro y los accesos `seq,desc`. Sin desempate, dos filas
  iguales en la columna (los dos apuntes de una transferencia) podrían salir en dos páginas o en
  ninguna.
- **Un usuario se modifica con lo que cambió, y la lista se pide como la pide Keycloak.** El
  `PUT /api/users/{id}` de `mto-users` es parcial: `null` es «no tocar» y la cadena vacía, «vaciar»,
  así que `UserForm.toUpdateRequest(original)` compara con lo leído y solo manda lo distinto, y sin
  cambios no se llama (`UpdateUserRequest.changesNothing()`); el nombre de usuario no viaja nunca y
  la contraseña temporal del alta viaja tal como se escribió. La lista pide cada tramo con `first`/`max` en trozos de como
  mucho 200 (`UsersView.MAX_PAGE`, el tope del servicio) y no ordena porque la API no ordena. La
  búsqueda por texto y el filtro por atributo se excluyen en la pantalla porque el servicio los
  rechaza juntos (`SEARCH-400`): lo deshabilitado no viaja, y un atributo mal formado no pide nada
  (la lista sigue con el último filtro bien formado, también al recargar). Nada de esto se arregla aquí con
  lógica propia: si la lista necesita orden u otro filtro, se pide en `mto-users`. En la ficha,
  asignar y quitar perfiles o roles y modificar **pintan lo que devuelve el servicio** (que lo relee
  de Keycloak antes de contestar), sin releer; cambiar un perfil relee los roles de la persona
  en su pestaña si se abrió, pero no el catálogo (los roles de cada cliente se piden una vez por
  pantalla, también en `usuarios/roles`), fijar una contraseña relee la cabecera y las credenciales, y las sesiones normales y las
  offline se piden por separado; y las rutas estáticas del módulo (`usuarios/perfiles`, `usuarios/roles`) ganan a
  `usuarios/:userId` porque Vaadin resuelve antes los segmentos literales.
- **Un catálogo de almacén no se borra: se retira.** `mto-stock` no tiene `DELETE` de maestros
  (`stock_movement` y `reservation` los referencian); el editor de modificación lleva `active` y
  desmarcarlo es retirar. El alta no lleva `active` (el servicio lo crea activo). Un proyecto con
  `synchronizedFromMasterData` es de `mto-configuration`: la vista enseña su origen y no ofrece
  modificarlo, porque el servicio lo rechaza con 422 `PRJ-001`; no se reimplementa esa regla aquí,
  solo se evita ofrecer lo que va a fallar.
- **Las cifras del almacén son del servicio.** Físico, reservado, disponible y «bajo mínimo» vienen
  de `GET /materials/{id}/stock`; la pantalla no suma movimientos ni resta reservas. Un movimiento
  se registra y el servicio decide: sin disponible es 409 `STK-001`, un material o almacén
  retirado es 400/422, y la notificación lo dice. Lo único que el diálogo exige es lo evidente
  (material, almacén, cantidad positiva, destino distinto del origen).
- **Solo una reserva activa cambia, y lo decide el servicio.** Modificar (`PUT`, sin el material),
  liberar y consumir (`POST` sin cuerpo) y cancelar (`DELETE`, que devuelve la reserva cancelada y
  pide `stock-delete`) son llamadas distintas y no se funden: la pantalla solo las ofrece en las
  filas activas porque en las demás el servicio responde 422 `RES-001`, y si aun así llega, la
  notificación lo dice. «Salida con esta reserva» es la salida de movimientos con `reservationId`:
  material, almacén y cantidad van fijos porque el servicio exige que coincidan exactamente con lo
  reservado; referencia y notas son lo que el consumo directo no lleva.
- **Un conjunto no tiene stock: su disponibilidad la calcula el servicio.** La lista de materiales
  va entera en el alta y en la modificación (la que llega sustituye a la anterior) y no puede ir
  vacía; `BomEditor` no permite dos líneas del mismo material porque añadir uno que ya está
  sustituye su cantidad. Cuántos se pueden montar en un almacén y qué componente limita es
  `GET /assemblies/{id}/availability?warehouseId` (el almacén es obligatorio porque el stock es
  por almacén): aquí no se divide nada. La disponibilidad es una consulta, así que la fila la
  ofrece con `stock-read`.
- **El historial de almacén es el de Envers en `mto-stock`, y el de mantenimiento, el de
  `mto-maintenance`, con la misma forma.** `GET /{recurso}/{id}/revisions` (materiales, almacenes,
  proveedores, proyectos, conjuntos y reservas; activos, órdenes, turnos, inspecciones y defectos),
  paginado y la más reciente primero, con `source` (`HTTP`, `MESSAGING`, `SYSTEM` o `BASELINE`, la foto inicial) y
  `correlationId` tal cual; `entity.audit` viene vacío a propósito y no se enseña. Sin revisiones
  el servicio responde 404 y `RevisionsDialog` lo dice como «sin historial todavía», no como error.
  Que un proyecto cambiado por un evento de datos maestros no deje revisión es del servicio (allí
  es SQL nativo), y se enseña lo que hay; lo mismo un activo de mantenimiento que solo ha llegado
  por datos maestros, que no tiene ninguna y da 404. La columna de acciones existe siempre: el
  historial es lectura, como la lista. En mantenimiento lo abre el botón «Historial» de cada ficha
  (y de cada fila de activos), y `MaintenanceHistory` pone la línea de cada recurso. No es la
  pestaña «Estados» de una orden o un defecto: esa es `/history`, las transiciones con su
  comentario, que el servicio guarda aparte.
- **En mantenimiento, una transición solo se ofrece en su estado de origen, y la decide el
  servicio.** Los estados de cada recurso (`MaintenanceOrderStatus.canPlan`/`canAssign`/`canStart`/
  `canComplete`, `ShiftStatus.canStart`/`canClose`, `DefectStatus.isPending`...) están copiados de
  las máquinas de estado del servicio solo para no ofrecer lo que va a fallar; no se reimplementa
  ninguna otra regla. Si el estado cambió entre medias, llega su 409 `TRN-001` y se notifica con el
  diálogo abierto. Cada transición es su llamada, con su cuerpo, y no se funden. `maintenance-supervise`
  va siempre **junto con** `maintenance-write` (`hasAllRoles`): cancelar una orden, completarla con
  `force` y resolver, cerrar o descartar un defecto; `force` ni se ve sin él. Tras guardar, la ficha
  pinta lo que devuelve el servicio o relee. Una ficha (orden, turno, inspección, defecto) que no se
  puede leer por algo que no es un 404 se queda con el motivo, «Volver a la lista» y «Reintentar»
  (`MaintenanceUi.showLoadFailure`); un 404 dice que no existe y vuelve a la lista.
- **Una modificación de mantenimiento es un `PATCH` merge-patch con la versión leída.** Cada
  `*Form.toPatch(original)` compara con lo leído (`Changes`) y arma un `MergePatch`
  (`application/merge-patch+json`, RFC 7396): lo que cambió viaja con su valor, lo que se vació
  viaja a `null` y lo demás no viaja. La `version` leída va siempre: si otra persona guardó antes,
  el servicio responde 409 `CON-001` sin escribir nada, el diálogo sigue abierto con lo escrito y la
  notificación pide recargar. Qué se puede vaciar lo decide el servicio (400 `VAL-001` si no); aquí
  lo obligatorio lleva `asRequired` y nunca sale vacío. Un formulario nunca lee como vacía una
  referencia que no sabe nombrar, porque la mandaría a vaciar: `MaintenanceNames.trackRef`,
  `packageRef`, `stationRef` y `projectRef` devuelven `#id` sin nombre, nunca `null`. `MergePatch`
  rechaza antes de llamar un campo a vaciar que su record no tiene. Los equipos son la excepción: su
  `PUT` es completo (base y vehículo a `null` los borran) y por eso `TeamRequest` no lleva
  `NON_NULL`. Un record de petición no lleva métodos `isX()`/`getX()`: Jackson los serializa como
  propiedades (`isEmpty()` salió como `"empty":false`); por eso se llaman `changesNothing()`. Como
  en mto-frontend, los tipos de una tarea (al modificarla y al completarla) y los seccionadores de
  un turno se comparan como conjunto, los tipos que el catálogo no trae se conservan, y quitarlos
  todos viaja como `null` (el servicio los deja vaciar), no como lista vacía.
- **Un activo sincronizado es de `mto-configuration`, pero su desactivación también es de
  mantenimiento.** Perfiles, seccionadores y aisladores llegan por datos maestros
  (`sourceService`); de ellos solo se ofrecen la descripción y el intervalo del preventivo, porque
  cualquier otro campo es 409 `AST-001`. `enabled` lo deciden dos voces que el servicio guarda por
  separado: `enabledAtSource` (lo que dice `mto-configuration`, `null` en un tramo propio) y
  `disabledLocally` (lo que decidió mantenimiento, que ningún evento deshace); el estado dice quién
  lo desactivó (`MaintenanceFormats.assetState`). Cualquier activo se desactiva aquí (`DELETE`, con
  `maintenance-delete` y confirmación; la de un sincronizado avisa de que sobrevive a los datos
  maestros), también uno que el origen ya tenía desactivado, para que siga así cuando lo reactive.
  Se reactiva (un `PUT` con `enabled=true`, con `maintenance-write`) solo lo que se desactivó aquí
  y el origen tiene activo: si no, el servicio responde 409 `AST-001` y no se ofrece.
- **Los nombres de otros servicios se piden a su servicio.** `mto-maintenance` solo guarda ids de
  vías, estaciones y paquetes (`mto-configuration`) y de materiales, almacenes y proyectos
  (`mto-stock`); `MaintenanceNames` los nombra con el token de la persona (vías, estaciones y
  paquetes una vez por pantalla, almacenes y proyectos por id y recordados mientras vive). Sin
  `config-read` o sin `stock-read` no llama a ese servicio (respondería 403): pinta `#id` y deja
  vacíos sus desplegables. Los tres perfiles de mantenimiento del realm llevan `config-read` y
  `stock-read` para esto (lo declara `mto-maintenance`), así que sus personas ven también
  Infraestructura, Catálogos, Trabajos y Almacén, en lectura.
- **Una línea de material se quita, no se cancela.** `DELETE /orders/{id}/materials/{lineId}`
  (`maintenance-delete`) libera antes su reserva en `mto-stock`; no se ofrece en una línea
  consumida ni en una orden terminada (`MAT-001`), la confirmación avisa de la liberación, y con el
  almacén caído (503 `STK-503`) la línea se queda como estaba. «Sincronizar» reintenta una línea
  `FAILED` (el almacén no respondió) o `REJECTED` (dijo que no; el motivo, en el tooltip del estado),
  o sin pedir fuera de borrador, también con la orden terminada, porque el servicio la liquida al
  reintentar; con la orden abierta, además, comprueba una `RESERVED` («Comprobar la reserva en el
  almacén»): si Almacén liberó su reserva, el servicio pide otra. Lo previsto de una línea reservada no se ofrece: el
  servicio no lo cambia. Un `MAT-001` al completar la orden sugiere sincronizar o, con
  `maintenance-supervise`, completar con `force`.
- **Una línea con una petición al almacén sin respuesta no cambia lo que viaja en ella.**
  `stockRequestInDoubt` (`RESERVATION` u `OUTPUT`, enumerado tolerante) es la reserva o la salida
  que la línea mandó a `mto-stock` y se quedó sin respuesta; el servicio la repite antes de hacer
  nada más con la línea, la reintenta solo cada 5 minutos y, mientras tanto, rechaza con 409
  `MAT-001` cambiar lo previsto o lo consumido de la línea, quitarla con una salida en duda (el
  material quizá ya salió) y cambiar el proyecto de almacén de la orden. Aquí no se ofrece nada de
  eso: el estado lo dice («Fallida · Reserva sin respuesta», con el motivo y el reintento en el
  tooltip), el diálogo de la línea deja solo «admite consumir de más», la fila no ofrece «Quitar»
  con una salida (ni con una petición desconocida), y el editor de la orden, fuera de borrador,
  lee las líneas y deja el proyecto de solo lectura si alguna está en duda (si no puede leerlas,
  decide el servicio). Una reserva en duda sí se quita: el servicio la confirma para liberarla, así
  que la confirmación avisa de que pasa por el almacén aunque la línea no tenga reserva todavía.
- **Los enumerados que se leen de un servicio toleran lo desconocido.** Son los de
  mantenimiento, los de almacén (`MovementType`, `ReservationStatus`), el del historial
  (`RevisionOperation`), los de los trabajos (`JobStatus`, `JobType`) y los de notificaciones
  (`ActivityCategory`, `ActivitySeverity`, `ActorKind`, `AccessOutcome`). Cada uno lleva `UNKNOWN`
  («Desconocido»), un `@JsonCreator(mode = DELEGATING) of(String)` que delega en
  `ClientEnums.parse` y `selectable()` sin `UNKNOWN` para los desplegables: un valor nuevo en el
  servicio se lee como desconocido en vez de romper la página entera, que además fallaría sin
  aviso, porque ese fallo de lectura no es un `BackofficeApiException`. Lo desconocido no abre
  nada: una reserva `UNKNOWN` no es activa; una línea de material `UNKNOWN` no ofrece ni modificar,
  ni sincronizar, ni quitar (`StockSyncStatus.isChangeable`); un trabajo en un estado `UNKNOWN` se da por terminado,
  para no consultarlo sin fin; y uno de un tipo `UNKNOWN` no tiene familia, así que ni se consulta
  por separado ni se descarga. No se toca el mapper global. Quedan fuera, a propósito, los que solo
  viajan en peticiones (`AdjustmentDirection`, `RequiredAction`, `ReportFormat`) y el de los
  maestros de `mto-configuration` (`SectionInsulatorInstallationType`): el maestro se devuelve
  entero, y un `UNKNOWN` volvería al servicio como un valor que no existe; tolerarlo exigiría
  guardar la cadena original.
- **Un `LocalDate` en un `@HttpExchange` lleva `@DateTimeFormat(iso = DATE)`** (y un `YearMonth`,
  `pattern = "yyyy-MM"`): sin él sale con el formato corto de la máquina y el servicio responde 400.
  Los `Instant` viajan en ISO sin nada, con `:` codificado.
- **Un informe se ve en pantalla y su fichero se descarga a través de esta aplicación.**
  `ReportClient` pide cada informe por su ruta en JSON (lo que se pinta, con los nombres) o con
  `format=xlsx|pdf` como fichero, que `Downloads` sirve con `DownloadHandler` (primera regla). Los
  enlaces aparecen tras consultar y descargan esa consulta, fijada en un record, aunque luego
  cambien los filtros: exportan lo que se ve. Las cifras son del servicio: el avance llega como
  fracción (`0.4500`) y solo se pinta como porcentaje; aquí no se suma nada.
- **«Sacar a la persona» son tres llamadas en ese orden, y no se funden en una.** Desactivar
  solo bloquea el siguiente login, cerrar las sesiones no toca las offline y un token offline
  sobrevive a las dos cosas hasta que se revoca: es lo que el README de `mto-users` deja
  explícitamente en manos del cliente. `TakeOut.run` hace `PATCH /enabled {false}`,
  `DELETE /sessions` y `DELETE /offline-sessions`, para en el primer `BackofficeApiException` y
  devuelve lo hecho y el paso que falló; el botón pide `users-write` **y** `users-sessions-write`
  (`hasAllRoles`). No es una regla nueva de negocio: es la orquestación documentada allí.
- **La campana y la bandeja son de la persona, y a quién va cada aviso lo decide el servicio con el
  token.** `mto-notification` resuelve al leer, por usuario, perfil y rol de cliente, qué
  notificaciones son mías; aquí no se filtra por nadie ni se cuenta nada: el contador es el de
  `GET /inbox/unread-count`, acotado (`capped` → «100+»), pedido al entrar y cada 30 s desde
  `SharedPolling` (`InboxBell.REFRESH_PERIOD`) con `CurrentPrincipal.callAs` + `UI.access()` (con
  la pestaña oculta no se pide: `PageVisibility`), y
  un fallo al pedirlo deja el número como estaba sin notificar nada (cada 30 s y por cada
  pantalla abierta, un aviso sería ruido; la bandeja lo dirá al abrirse). La campana existe solo
  con `notification-inbox`, que llevan todos los perfiles del dominio. La bandeja abre con las no
  leídas (`unread=true`), porque el estado de lectura es de cada persona y el servicio no ordena
  por él; abrir una notificación (la fila o su flecha) la marca como leída **antes** de seguir su
  enlace, y si el servicio dice 404 `NTF-404` (ya no es mía) se notifica y no se abre nada;
  «Marcar todas como leídas» es `POST /inbox/read-all`, que va hasta la más reciente visible, no
  hasta ahora. El enlace es una ruta de esta aplicación que ponen las reglas del servicio
  (`/mantenimiento/ordenes/{id}`, `/actividad?category=SYSTEM`, `/actividad/accesos?username=`):
  `NotificationLinks` la navega con sus parámetros. Qué es un destino lo dice
  `NotificationLinks.target`, con la regla de mto-frontend: una ruta empieza por una sola barra, un
  `http(s)` se abre en otra pestaña con `noopener`, y cualquier otra cosa no lleva flecha.
  `ActivityView` y `AccessView` ponen a cero sus filtros al entrar (la pantalla puede estar ya
  abierta con otros) y aplican los que llegan en la URL.
- **Los accesos tienen su permiso aparte, y el registro nunca los enseña.** `actividad/accesos`
  pide `notification-access-read`, que no viene con `notification-activity-read` ni al revés (un
  permiso nunca implica otro, como en el servicio), porque un acceso lleva usuario e IP. El
  registro no ofrece `ACCESS` como categoría (`ActivityCategory.selectableForActivity`): el
  servicio lo rechaza con 400, y una notificación de un acceso no ofrece su línea del registro
  (sería un 404 `ACT-404`). Una IP se busca entera (`AccessView.isIpLiteral`, la regla de
  mto-frontend): a medio escribir no se pide nada. El registro pide la línea a su id para enseñar
  su `payload`, que la lista no trae; si ya no existe (`ACT-404`), su diálogo lo dice dentro, con su
  referencia y sin aviso (`EventDetailDialog.openActivityEvent`), como en mto-frontend. Los tipos, los orígenes y los sujetos se escriben enteros y se
  comparan en el servicio: el catálogo de tipos es suyo y aquí no se copia. Lo fundido (el evento
  de administración de Keycloak que ya cuenta el de `mto-users` del mismo cambio) solo viaja como
  `includeSuperseded=true` cuando se pide. Una línea se enseña entera en `EventDetailDialog`, con
  su `payload` tal cual lo dejó la lista blanca del servicio: aquí no se interpreta nada.
- **Una lista paginada en el servidor pide una vez cada página** (`LazyPages`). Vaadin pide por
  separado el recuento y la página (`setItems(fetch, count)`), y los servicios devuelven las dos
  cosas en la misma respuesta: el recuento pide la primera página con el tamaño y el orden del Grid
  (su consulta no trae el orden: se lee de `getBackEndSorting()`) y la guarda hasta el final de esa
  ida y vuelta (`beforeClientResponse`), para la petición de página que la sigue. Un fallo se
  notifica una vez y deja la lista vacía hasta que se vuelva a pedir, y nunca se pinta una página
  vieja. La usan todas las listas perezosas, y `RevisionsDialog` lee su 404 como una página vacía.
- **Una fila se abre con doble clic o con su botón; un clic no abre nada** (`RowActions`), como en
  mto-frontend: el clic sirve para seleccionar o copiar, y el botón, con el nombre de la fila en su
  tooltip («Abrir MO-000012»), es lo que llega con el teclado. Las pestañas Defectos e Inspecciones
  de una orden solo llevan el botón, y los miembros de un perfil o de un rol, el usuario como enlace.
  Lo que es elegir y no abrir (los catálogos de perfiles y de roles, las plantillas, los bajo mínimo)
  sigue con su clic.
- **Los números se comprueban antes de llamar como en mto-frontend, con la columna del servicio**
  (`Numbers`). Una cantidad de almacén o de mantenimiento es `numeric(19,6)`: 13 enteros y 6
  decimales, mayor que cero en un movimiento, una reserva, una línea de conjunto o el material de una
  tarea, y cero o más en el stock mínimo y en las líneas de material, también al modificarlas. Un KP
  de mantenimiento y una medida de checklist llevan signo y son `numeric(12,3)`; el KP final de un
  defecto puede ser el inicial, nunca menor. En infraestructura, el vano, las alturas, el viento y
  los KP de agujas y aisladores no son negativos; lo que va en mm del perfil y el descentramiento son
  enteros, la longitud de un paquete son solo cifras y el KP del perfil viaja recortado. Lo demás (un
  rango, una regla de negocio) lo decide el servicio. El `Binder` aplica antes que nada la
  comprobación propia del campo (su mínimo y lo que no sabe leer), así que un `IntegerField` con
  mínimo lleva su mensaje (`Numbers.atLeast`); en un diálogo sin `Binder`, `Numbers.check` hace lo
  mismo, porque lo que el campo no sabe leer lo da por vacío y viajaría como «vaciar»: borraría la
  medida guardada.
- **El menú no es una guarda.** `MainLayout` esconde lo que la persona no puede abrir; quien manda
  es `@RolesAllowed` en la vista y el 403 del servicio. Dentro de una vista pasa lo mismo: los
  botones de `LovCrudView` siguen los permisos del servicio (`config-write`+`lov-manage` para crear
  y modificar, `config-delete`+`lov-manage` para borrar, `config-import`+`lov-manage` para los
  lotes) con `AuthenticationContext.hasAllRoles`, y un 403 igualmente se traduce a notificación.
- **«Abrir en mto-frontend» lleva a la misma pantalla allí** (`MainLayout`): la ruta en la que se
  está, con su query (`afterNavigation`), sobre `app.frontend.url`, en otra pestaña con
  `noopener noreferrer`. La query va sin el `continue` con el que Spring Security vuelve del login
  a la URL pedida (`MainLayout.SAVED_REQUEST_PARAMETER`): es suyo, no de la pantalla. Sin
  dirección, o sin `http(s)://`, la barra no lo ofrece (`FrontendProperties.linkTo`). Las dos
  aplicaciones entran por el mismo SSO de Keycloak, y la SPA tiene el enlace de vuelta.
- **Una vista por familia de endpoints, no por recurso.** Los 17 catálogos comparten controlador
  base y DTO en `mto-configuration`; aquí son una `LovCrudView` con el recurso en la ruta. Un
  catálogo nuevo allí es una constante más en `LovResource`, nada más.
- **Una entrada de catálogo se modifica con el `versionNumber` que se leyó.** Es el bloqueo
  optimista de `mto-configuration`: `LovForm.toDto(existing)` y el lote de activar o desactivar
  parten de la fila leída, así que la versión viaja sin que nadie la toque, y el alta no la lleva.
  Tras guardar, la vista relee el catálogo, y la siguiente modificación lleva la versión nueva. Si
  otra persona guardó antes, el servicio responde 409 `CON-001` sin escribir nada (en un lote, una
  sola entrada vieja rechaza el lote entero); el diálogo sigue abierto con lo escrito y la
  notificación pide recargar. La pantalla no compara versiones: eso lo decide el servicio. El
  `PUT` sustituye la entrada, así que viaja entera: lo que `LovDto` no modela (`drawingNumber`, el
  tipo de los tres catálogos que lo tienen) cae en `extras` y vuelve tal cual. En esos tres
  (`LovResource.Parent`: cimentaciones, cimentaciones de anclaje y pórticos) el tipo se elige en el
  editor y en el alta múltiple y viaja por su id. El catálogo se ordena en orden natural y se
  filtra sin mayúsculas ni tildes (`TextMatching`), como en mto-frontend.
- **La validación de negocio vive en el servicio.** El formulario solo exige lo evidente (código y
  descripción obligatorios, longitud de columna) y vuelca `errors[{field, code, message}]` campo a
  campo con `ServerValidation`. Un `code` repetido llega como 409 `BUS-002` y un cuerpo sin `code`
  como 400 desde que `RestExceptionHandler` los mapea (antes eran 500).
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
  cambien lo que enseña el Grid. `profiles.disconnector` se enseña de solo lectura y vuelve como
  se leyó: `mto-configuration` lo ignora al escribir un perfil (#31), y el vínculo se cambia desde
  Seccionadores, con el `profileId` del seccionador. Vaciar una referencia opcional a catálogo de un
  perfil viaja como `{}` (`ProfileEditor.CLEARED`), porque `null` es «no la toques»; la que nadie
  tocó vuelve como se leyó.
- **Las listas de maestros se paginan en el servidor.** `MasterView` pide cada página a
  `POST /{recurso}/filter` con `page`, `size`, `sort=campo,asc` y `searchText`; sin orden elegido
  `sort` no viaja y ordena el servicio (por eso es opcional en `MasterClient.filter`); el recuento es
  `totalElements`. Nada de `findAll` en memoria como en los catálogos: los perfiles son miles.
  Las filas de paquetes, estaciones y vías llegan sin hijos y los filtros booleanos solo filtran
  si vienen; las dos cosas se arreglaron en `mto-configuration` para esta fase, no aquí.
- **El esquema de una vía es una proyección del servicio, cacheada allí.** `GET /tracks/{id}/schematic`
  de `mto-configuration` devuelve en una llamada lo justo para dibujar (los perfiles en el orden
  físico con sus ménsulas y su seccionador, los aisladores, las estaciones), y `SchematicDrawing`
  solo reparte los postes a distancia uniforme, coloca cada aislador entre sus dos vecinos por KP y
  **escapa todo texto** antes de meterlo en el SVG (`Svg` vuelca la cadena en `innerHTML` tal cual).
  Nada se ordena, suma ni interpreta aquí; si el dibujo necesita otro dato, se añade a la proyección
  en el servicio. La columna de acciones de `MasterView` existe siempre, también para quien solo
  lee, porque el esquema es lectura (`addRowActions`); modificar y borrar siguen tras sus permisos.
- **Un trabajo se lanza y se sigue; no se espera.** Lanzar responde 202 con el trabajo, o 429 con
  el trabajo ya rechazado y un `Retry-After` (`TooManyRequestsApiException` trae ese cuerpo, y
  `JobsView` lo apunta como rechazado en vez de tratarlo como un fallo). El progreso lo trae
  `@Push`: `SharedPolling` consulta desde un hilo propio, con el principal fijado por
  `CurrentPrincipal.callAs`, y `JobsView` lo lleva a la pantalla con `UI.access()`; sin pantalla
  abierta, sin nada en curso o con la pestaña oculta (`PageVisibility`), no se consulta nada. Un
  fallo al leer la lista no se notifica en cada pasada: se enseña fijo encima de ella, con su
  referencia, mientras el último intento falle. La lista es la del servicio (`GET /jobs`):
  `JobLog` solo guarda la etiqueta con la que esta sesión lanzó cada trabajo y su último estado,
  para pintarla encima y avisar cuando uno termina; un trabajo de la sesión que no esté en la
  página se consulta por su familia. Las filas no traen los errores por elemento: `JobErrorsDialog`
  los pide al detalle. El `downloadUrl` del servicio no se usa: es su ruta interna, y la descarga
  se pide por familia e id a través del gateway.
- **El fichero de un trabajo se descarga a través de esta aplicación.** `DownloadHandler` pide el
  fichero al servicio con el token de la persona y lo sirve en la misma respuesta; un `Anchor` al
  gateway no serviría porque el navegador no tiene token (primera regla). Una exportación solo se
  descarga `COMPLETED`; una importación también `COMPLETED_WITH_ERRORS`, porque su fichero es el
  informe de esos errores (`JobDto.isDownloadable`), y solo entonces se dice que el informe los trae
  todos (`JobDto.hasErrorReport`). El navegador solo vería una descarga fallida, así que
  `Downloads.link` notifica el fallo en la pantalla; un 410 pide relanzar el trabajo.
- **Una colección de hijos que el editor gestiona va entera o no va.** `ChildrenEditor` trabaja
  sobre una lista propia y `edited()` solo la devuelve si alguien la tocó; `prepare(dto)` del
  editor la pone entonces en el DTO, ya después de `forgetChildren()`, y si no, se queda el
  `null` que deja al servicio sin decir nada. Media lista no existe: para el servicio la
  colección que llega es el estado final y el hijo que falta se borra (`README_API.md` §4). El 1:1
  `cantilevers.steadyArm` se manda entero para crearlo o mantenerlo y `null` para quitarlo. El
  seccionador del perfil ya no se escribe desde el perfil: `ProfileEditor` lo enseña de solo
  lectura, sin `Binder`, y viaja el objeto leído.
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
catálogos: el `versionNumber` leído en el `PUT` y en el lote, y los dos 409 de configuration
(`CON-001` y `BUS-002`) distinguidos por su código; los maestros: resolución del genérico,
parámetros de página y orden del `/filter` (sin orden no viaja `sort`, como en el recuento), `extras` e hijos a
`null` en un `PUT`, referencias a catálogo como `{id, code}`, las ménsulas tipadas con su brazo y
el seccionador 1:1 en un `PUT`, el esquema de una vía con sus records anidados; los trabajos: la importación como parte multipart con `dryRun` en
la query, el 429 con el trabajo rechazado y el `Retry-After`, la lista paginada con sus filtros,
el estado por familia y el fichero con sus cabeceras, qué es descargable, un tipo o un estado
desconocidos leídos como `UNKNOWN`; los usuarios: la
búsqueda `first/max` con su total, atributos repetidos, alta 201 sin la contraseña en el `toString`,
`PUT` parcial y `PATCH` de activo, el `DELETE` con cuerpo de los roles, perfiles, sesiones,
credenciales, contraseña y correo, miembros sin total, catálogos, y el `problem+json` de
`mto-users` por alias; el almacén: `search`/`active`/`Pageable` y la página anidada de los
catálogos, alta y modificación con `active`, el proyecto sincronizado, existencias y libro de un
material, los cuatro movimientos en sus rutas y la transferencia con dos apuntes, la reserva
cancelada con un `DELETE` con cuerpo y liberada o consumida con `POST` sin cuerpo, el conjunto con
su BOM y su disponibilidad, el historial tipado, el JSON de error de `mto-stock` por alias, y un
tipo de apunte, un estado de reserva y una operación del historial desconocidos leídos como
`UNKNOWN`; el
mantenimiento: la lista de órdenes con sus filtros (enumerado por nombre, fecha ISO, `sort`
repetido) y un valor desconocido leído como `UNKNOWN`, la petición en duda de una línea (reserva,
salida o desconocida, y cuál se puede quitar), su JSON de error por alias, el merge-patch
(lo cambiado, lo vaciado a `null` y la versión; un campo a vaciar que no existe, rechazado antes de
llamar; el 409 `CON-001`), activos (búsqueda, alta, `PATCH` con lo cambiado y lo vaciado,
desactivar), catálogos y equipos enteros,
órdenes y sus transiciones con sus cuerpos, tareas (alta, generar, modificar, cancelar), turnos con
sus conjuntos enteros, sus tareas y perfiles, ejecutar una tarea con su checklist, defectos y
materiales, inspecciones y lo que generan, defectos y sus transiciones, líneas de material
(quitar con 204 y el 503 `STK-503`; la rechazada con su motivo, y el 422 `STK-422` o el 409
`STK-001` al sincronizarla), los informes en JSON y como fichero con su nombre, y el
historial de cada recurso con el 404 de un activo sin revisiones; las notificaciones: la bandeja
con sus filtros y una gravedad desconocida leída como `UNKNOWN`, el contador acotado, las marcas
como `POST` sin cuerpo y el 404 `NTF-404` por alias, el registro con todos sus filtros
(`includeSuperseded` solo cuando es verdadero), una categoría y un actor desconocidos, el detalle
con su `payload`, los accesos por usuario, IP, tipo y resultado, y el 400 `REQ-400` de un `sort`
desconocido),
`SecurityLayerTest` (mapeo de roles de los cinco clientes con el sinónimo cualificado, un cliente
no listado no aporta nada, un rol de realm `users-read`, `stock-read`, `maintenance-read` o
`notification-access-read` nunca abre el módulo, `SecurityRoles`, `UserRoles`, `StockRoles`,
`MaintenanceRoles` y `NotificationRoles` coinciden con el realm y son disjuntos, registro OIDC sin
descubrimiento, roles desde el access token, `CurrentPrincipal`), `ViewLayerTest` (Karibu-Testing 2.7.3 sobre el contexto de Spring: el catálogo
de la ruta y su filtro local, menú por roles, controles de escritura ocultos sin permiso, alta por
diálogo, errores del servicio campo a campo, la modificación con la versión leída y la siguiente
con la recargada, la versión vieja con su aviso de recargar y el diálogo abierto, los dos 409 de
configuration dichos distinto, borrado con confirmación, lote sobre la selección con la versión
de cada fila, parser del alta múltiple, notificación de error, diagnóstico de audiencias; los
maestros: lista paginada, ordenada y filtrada contra el cliente simulado, nombres de referencias en las columnas,
edición sobre una copia que vuelve con `extras` e hijos a `null`, errores del servicio sobre un
desplegable, borrado confirmado, alta de un perfil con sus referencias, KP no válido, las
ménsulas a `null` sin tocar y enteras al tocarlas, el seccionador del perfil de solo lectura y de
vuelta como se leyó, las agujas en su diálogo y enteras al guardar,
el perfil legible en la lista de seccionadores, los mensajes de sistema, el esquema de una vía desde
su fila en una llamada con los postes en el orden recibido, el texto escapado, el fallo notificado
sin ventana, la vía sin perfiles y el reparto del dibujo (aisladores entre sus vecinos por KP, brazos
al lado del poste); los trabajos: subir y
lanzar una importación, el progreso llegando por `pollOnce()` + `UI.access()` hasta el enlace de
descarga y el botón de errores (que pide el detalle), el 429 apuntado como rechazado con su aviso,
un trabajo propio fuera de la página seguido por su familia, la lista paginada y filtrada en el
servicio, los lanzadores según permisos, un tipo o un estado desconocidos sin descarga, sin
detalle pedido y sin consultas, y los filtros sin «Desconocido»; los usuarios: el grupo «Usuarios» del menú y su ausencia
sin `users-read`, un rol de realm que no abre la vista, la lista paginada con `first`/`max` y
filtrada en el servicio, la exclusión entre búsqueda y atributo, los controles según permisos,
alta con contraseña temporal y acciones, errores del servicio campo a campo, modificación con
solo lo cambiado, activar/desactivar sin confirmación, borrado confirmado, el parser de
atributos; la ficha: cabecera y pestañas cargadas al abrirse, usuario desconocido de vuelta a la
lista, cada botón y cada panel tras su permiso, perfiles y roles asignados y quitados pintando la
respuesta, contraseña temporal por defecto y la política del realm sobre el campo, el correo de
acciones con su 502 detallado y sin email, modificar y desactivar repintando la cabecera, borrar
de vuelta a la lista; sesiones normales y offline listadas y cerradas una a una o todas con
confirmación, la sesión ajena avisada y recargada, credenciales quitadas con su aviso, y «sacar
a la persona» con sus tres llamadas en orden y parando en el primer fallo; los catálogos: las
rutas estáticas ganan a `:userId`, el catálogo de perfiles con lo que concede y sus miembros
paseados sin total, el de roles por cliente con quién los tiene, y la fila que abre la ficha; el
almacén: los mensajes de sus errores, el grupo «Almacén» con sus catálogos y su ausencia sin
`stock-read`, un rol de realm que no abre la vista, la lista paginada, buscada, ordenada y filtrada
en el servicio, lectura sin controles, alta y modificación con `active`, errores del servicio campo
a campo, el proyecto sincronizado sin botón de modificar, el editor de materiales; las
existencias: cifras y libro de un material en un almacén, la lista bajo mínimo que sigue al almacén
y cuya fila elige el material, el libro filtrado en el servicio, la entrada con su proveedor, la
salida sin stock con su mensaje, la transferencia que exige otro almacén, el ajuste solo con
`stock-adjust`, un tipo de apunte desconocido pintado pero no ofrecido como filtro; las reservas:
la lista filtrada y ordenada en el servicio con las acciones solo en
las filas activas y cancelar solo con `stock-delete`, lectura sin acciones, el alta con su proyecto
obligatorio, la modificación sin tocar el material, liberar, cancelar y consumir confirmados y el
422 `RES-001` notificado, la salida desde una reserva con material, almacén y cantidad fijos y su
`reservationId`, y una reserva en un estado desconocido con solo el historial; los conjuntos: la lista con sus líneas y la disponibilidad ofrecida a quien solo
lee, la disponibilidad por almacén con el componente que limita, el alta con sus líneas (la lista
vacía rechazada antes de llamar, la línea sin material ni cantidad, el material repetido
sustituido) y la modificación con la lista entera y `active`; el historial: paginado y la más
reciente primero con cómo quedó la fila, el 404 como «sin historial» sin notificación, y el de
una reserva desde cualquier fila; el mantenimiento: el grupo con las órdenes como nodo y lo que el
perfil lee de los otros módulos, un rol de realm que no abre las vistas, los mensajes de sus
códigos, nombres de vías y paquetes (y `#id` sin `config-read`, sin llamar a configuración), la
descarga de un fichero; activos filtrados en el servidor, el alta de un tramo con su rango, el
activo sincronizado que solo cambia descripción e intervalo (y vacía el intervalo) y se desactiva aquí con su aviso, el estado
que dice quién desactivó un activo y la reactivación solo de lo desactivado aquí, desactivar y
reactivar un tramo,
equipos enteros y catálogos de lectura; órdenes filtradas, el alta con su activo buscado, vaciar la
fecha y el equipo con el `CON-001` de una versión vieja y el diálogo abierto, el proyecto de almacén
que no se vacía sin `stock-read`, la ficha
con lo que su estado admite, planificar y el `TRN-001` con el diálogo abierto, `force` solo con
supervise, las tareas (añadir, generar, modificar, cancelar), el historial de estados; turnos
filtrados, el alta con su vía y sus seccionadores, iniciar y cerrar, asignar tareas con las
rechazadas resumidas, ejecutar una tarea con su checklist, sus defectos y su material, completar
desde la orden eligiendo un turno en curso de su vía; inspecciones con su defecto (`force` si es
leve) y su orden correctiva, o los enlaces a ellos, y sus puntos contestados; defectos vinculados,
resueltos y descartados con sus motivos, los de una orden; las líneas de material con su almacén y
lo que admite cada una (la reservada se comprueba; la rechazada, con su motivo, se reintenta y dice
por qué el almacén vuelve a decir que no), el alta desde el almacén, quitar una reservada con su aviso y el almacén
caído notificado, el `MAT-001` al completar y el proyecto de almacén de la orden, y una línea con
una petición al almacén sin respuesta (su estado y su tooltip, las cantidades de solo lectura, sin
«Quitar» con una salida y, con una reserva, avisando de que pasa por el almacén, y el proyecto de la
orden de solo lectura mientras dure); los informes (el
avance con sus nombres y porcentaje, el mensual con sus 24 meses, las descargas de punta a punta
con el `_download` de Karibu, y el parte del turno en su pestaña); el historial de cada ficha y el
de un activo sin revisiones; las notificaciones: la campana con su número, solo con
`notification-inbox` (un rol de realm no la da), sin número con nada sin leer, refrescada con
`pollOnce()` + `UI.access()` hasta «100+» y con un fallo que deja el número como estaba, el menú
con la bandeja, el registro como nodo y los accesos cada uno tras su permiso, la bandeja que abre
con las no leídas y filtra y ordena en el servicio, abrir una notificación marcándola y siguiendo
su enlace con sus filtros hasta el registro, la leída que no se vuelve a marcar y la que no tiene
enlace, «marcar todas», el `NTF-404` notificado sin abrir nada, la línea del registro tras una
notificación solo con `notification-activity-read`; el registro filtrado y ordenado en el servicio
sin ofrecer los accesos, una categoría nueva como «Desconocido», la ráfaga con su recuento y la
fundida, el detalle con su `payload`; los accesos desde el enlace de una regla con el usuario en
la URL, filtrados en el servicio, un resultado nuevo como «Desconocido» y su detalle) y
`MtoBackofficeApplicationTests` (contexto completo sin Keycloak ni gateway, con los clientes de
cada servicio y los cinco clientes cuyos roles son permisos; redirección al login; sonda de
salud; ausencia de artefactos comerciales; las dos hojas de Lumo en el shell). Lo que trajo la
convivencia con mto-frontend son casos de esas mismas clases: el enlace a la SPA con su query, el
tipo padre y los `extras` de un catálogo, la referencia vaciada, el aviso fijo de la lista de
trabajos, la pestaña oculta, las descargas fallidas, los cambios de usuarios, los desempates, lo
retirado en los filtros del almacén, las fichas de mantenimiento ante un fallo y los enlaces de
las notificaciones. Y las cinco diferencias que cerró la fase 9 de la SPA: una petición por página y
un fallo avisado una vez, el catálogo de roles una vez por pantalla, las filas que se abren con doble
clic o con su botón y no con un clic, el `ACT-404` dentro de su diálogo y los números de cada diálogo
(lo que no cabe en la columna, el mínimo con su mensaje, la medida que el campo no sabe leer). Todo
corre en la JVM sin Docker. En un navegador, contra la plataforma entera, lo prueba el job `e2e` del
CI: `mto-platform/scripts/e2e.sh` construye la imagen de este commit, usa la publicada de cada
hermano y corre el e2e de Playwright de mto-frontend, que recorre las dos aplicaciones.
