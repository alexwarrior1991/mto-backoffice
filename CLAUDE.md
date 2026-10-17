# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Proyecto

`mto-backoffice`: backoffice web del dominio `MTO` (infraestructura ferroviaria de catenaria).
Aplicación **Spring Boot 4.1 / Java 25 + Vaadin Flow 25** que consume las APIs del dominio a través
de `mto-gateway` con el token de la persona, servidor a servidor. Es un **cliente**: sin base de
datos, sin broker, sin lógica de negocio. Lo que una pantalla necesita y la API no da bien se
arregla en el servicio (`mto-configuration`, `mto-users`, `mto-stock`, `mto-maintenance`, `mto-notification` o
`mto-resources`), no aquí. `README.md` es la referencia funcional y
operativa; `keycloak/README.md`, la del cliente OIDC en el realm.

⚠️ **Convive con `mto-frontend`**, la SPA en React del dominio: las dos aplicaciones se usan
indistintamente, con las mismas pantallas, las mismas rutas y las mismas reglas, y entran por el
mismo SSO. Se mantienen a la par: un cambio de comportamiento en una (una regla, un fallo arreglado,
una llamada distinta, lo que se ofrece, cómo se dice un error) se lleva a la otra en el mismo
cambio; los textos no tienen que coincidir (aquí siguen sin tildes). «Abrir en mto-frontend», en la
barra, lleva a la misma pantalla allí, y la SPA tiene el enlace de vuelta.

⚠️ `mto-configuration`, `mto-stock`, `mto-maintenance`, `mto-users`, `mto-notification`, `mto-resources` y `mto-gateway` son **repos
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
Hilos virtuales activos (`SPRING_THREADS_VIRTUAL_ENABLED`): Tomcat, las tareas de Vaadin (el executor de
tareas de Spring) y cada pasada de `SharedPolling`; con `false`, todo vuelve a hilos de plataforma.
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
  `SecurityRoles`, `UserRoles`, `StockRoles`, `MaintenanceRoles`, `NotificationRoles` y
  `ResourcesRoles` (los permisos de `mto-configuration-api`, `mto-users-api`, `mto-stock-api`,
  `mto-maintenance-api`, `mto-notification-api` y `mto-resources-api`), `SecurityAuthorityPrefixes`,
  `JwtClaimNames`, `KeycloakProperties`.
- `configuration/client` — `GatewayClientConfiguration` (un `RestClient` hacia el gateway con dos
  interceptores, `BearerTokenInterceptor` y `CorrelationIdInterceptor`, y `ApiErrorDecoder` como
  manejador de estado; `HttpServiceProxyFactory` para las interfaces `@HttpExchange`),
  `UserTokenProvider` (`AuthorizedClientServiceOAuth2AuthorizedClientManager` con `refreshToken()`),
  `GatewayProperties`, `CorrelationProperties`.
- `client` — las interfaces `@HttpExchange` por servicio: `client/configuration/LovClient` (los
  ocho endpoints de `AbstractLovController` parametrizados por recurso; `LovResource`, los 17
  catálogos con su ruta y su título), `MasterClient<D>` (lo que comparten los maestros de
  `CRUDController`: `POST /filter` paginado, `GET/POST/PUT/DELETE`) con siete subinterfaces vacías (salvo `TrackClient`, que añade `schematic`: el esquema de la vía en una llamada)
  que solo ponen la ruta y el tipo (`StationClient`, `TrackClient`, `TrackAccessPointClient`...; Spring
  resuelve el genérico contra la subinterfaz), `BusinessEntityClient` (solo lectura), `MasterFilters` (cuerpo y orden
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
  inspección—, `ShiftClient` —turnos, sus tareas y perfiles, asignar una tarea y sacarla—, `InspectionClient`,
  `DefectClient` y `ReportClient` —cada informe en JSON y como fichero `ResponseEntity<byte[]>`—,
  todos con `revisions` salvo los catálogos y los informes, y `OperationClient`, las operaciones de
  fondo (`GET /operations/{id}`) que deja un `Prefer: respond-async`; DTO como records en
  `client/dto/maintenance`: filtros (`OrderFilter`, `AssetFilter`...), peticiones `*Request`, las
  modificaciones como `MergePatch<*UpdateRequest>` (en `client/dto`, porque `mto-resources` modifica
  igual: lo cambiado, lo vaciado y la versión leída) y
  enumerados tolerantes —ver las reglas—); `client/notification/NotificationClient` (la API de
  `mto-notification` bajo `/api/notifications`: mi bandeja paginada con sus filtros, el contador
  acotado, marcar una o todas, el registro con sus filtros y el detalle de una línea, y los
  accesos; DTO como records en `client/dto/notification`, con `payload` como `Map` y los
  enumerados tolerantes `ActivityCategory`, `ActivitySeverity`, `ActorKind` y `AccessOutcome`);
  `client/resources` (la API de `mto-resources` bajo `/api/resources`: `PersonClient` —con sus
  cualificaciones, `/people/{id}/qualifications`, la lista entera, alta, `PATCH` y `DELETE`—,
  `OrganizationClient` —el árbol, las unidades, sus hijas y los linemen de un equipo, con `PUT` de la
  lista entera, `POST` para añadir uno y `leave` para que salga—, `VehicleTypeClient`,
  `ParkingSiteClient` y `VehicleClient` —con sus ubicaciones y el tablero, `positions`, ordenado por vía
  y kp con el vehículo para desempatar, y sus documentos, `/vehicles/{id}/documents`—,
  `QualificationTypeClient` —el catálogo de tipos de cualificación—, `InstrumentClient` —los
  instrumentos con su calibración, filtrables por `calibrationExpiresBefore`, y su historial— y
  `AccessPointClient` —los puntos de acceso a la vía que `mto-resources` ha recibido de
  `mto-configuration`, una lista entera sin paginar, que `AccessPointListDto` lee también como página
  (`enabledOnTrack`: los activos de una vía)—; DTO como
  records en `client/dto/resources`, con los resúmenes —`PersonSummaryDto`, `UnitSummaryDto`,
  `VehicleSummaryDto`, `QualificationTypeSummaryDto`, `InstrumentSummaryDto`...— iguales por id para que
  un desplegable reconozca lo leído, y las reglas copiadas del servicio en sus enumerados: `OrganizationUnitKind`
  con sus niveles, a qué puede colgar y qué categorías lo dirigen, `VehicleCategory` con su movilidad,
  `VehicleLocationKind.recordable` con lo que admite cada movilidad; `QualificationKind`,
  `VehicleDocumentKind`, `InstrumentKind` y `AccessPointKind`, tolerantes; `AccessPointDto`, igual por
  id, y en una ubicación `trackAccessPoint` (`AccessPointSummaryDto {id, name, kind}`) junto al texto
  libre de siempre, `accessPoint`).
  Los DTO
  (`client/dto`): `LovDto` es un record con solo las claves que usa la UI (con `versionNumber`,
  que vuelve como se leyó) y `@JsonInclude(NON_NULL)`; los maestros (`client/dto/master`) son
  **clases mutables** que heredan de `MasterDto` (ver la regla de abajo), con `LovRef` para las referencias a catálogo y los hijos
  tipados que los editores gestionan (`CantileverDto` con su `SteadyArmDto` 1:1,
  `SectionInsulatorSwitchDto`; `DisconnectorDto` trae además `profileCode`/`profileKp`, solo de
  salida, `kp`, el suyo, solo sin poste, `trackId`, el suyo sin poste y, con poste, la vía que
  maniobra cuando no es la del poste (un haz de vías, en talleres y cocheras), `connectedTrackId`,
  la otra vía de uno que pone dos en paralelo, con poste o sin él, y `stationId`, opcional: uno en
  plena vía no es de ninguna; `TrackAccessPointDto`, el punto de acceso a la vía del `V28`, con la vía
  obligatoria, la estación opcional, sus decimales —KP, toneladas, coordenadas— como números y sin
  hijos, y su clase y su lado como la cadena leída, con las etiquetas de `TrackAccessPointKind` y
  `TrackAccessPointSide`); `PageResponse<T>` con la forma `{content, page}`; los trabajos (`client/dto/jobs`:
  `JobDto`, la unión de las tres respuestas del servicio, `JobStatus`, `JobType`, `UploadedFile`);
  `ClientEnums`, lo que comparten los enumerados tolerantes (`parse` y `selectable`). Los
  errores en `client/error` (`ApiProblem`, `ApiErrorDecoder` y la jerarquía
  `BackofficeApiException`, con `TooManyRequestsApiException` llevando el cuerpo del 429).
- `ui` — `MainLayout` (AppLayout, con «Abrir en mto-frontend» en la barra; el menú lo dan las vistas anotadas con `@Menu`, filtradas por
  `AccessAnnotationChecker`; las rutas con prefijo conocido (`infraestructura/*`, `usuarios/*`,
  `almacen/*`, `mantenimiento/*`, `recursos/*`) se agrupan por prefijo (`MainLayout.GROUPS`), una entrada cuya ruta es el propio prefijo es el
  nodo del grupo, y los catálogos se listan a mano porque su vista lleva el recurso en la ruta),
  `ui/views/HomeView`,
  `ui/lov` (`LovCrudView` en `catalogos/:resource`, `LovEditorDialog` con `Binder` sobre el modelo
  mutable `LovForm`, `LovBulkCreateDialog` con su parser de líneas), `ui/master` (`MasterView<D>`,
  la lista paginada en el servidor con `LazyPages` (una petición por página) sobre `POST /filter`;
  `MasterEditorDialog<D>`, el `Binder` sobre el DTO leído; una vista y un editor por maestro, también
  `TrackAccessPointsView` en `infraestructura/accesos` con `TrackAccessPointEditor`;
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
  `MaintenancePickers`, `MaintenanceCatalogs` y `MaintenanceFormats`, lo compartido), `ui/notification` (`NotificationRoutes`: `notificaciones`, `actividad` y
  `actividad/accesos`; `InboxBell`, la campana de la barra, que `MainLayout` pone solo con
  `notification-inbox` y que pide el contador al entrar y cada 30 s desde `SharedPolling` con
  `CurrentPrincipal.callAs` + `UI.access()`; `NotificationsView`, la bandeja paginada en el
  servidor, que abre con las no leídas, marca al abrir y sigue el enlace con `NotificationLinks`
  (una ruta de esta aplicación con sus parámetros, o una pestaña nueva si es absoluto);
  `ActivityView`, el registro con los filtros del servicio y los de la URL (`BeforeEnterObserver`);
  `AccessView`, los accesos con su permiso aparte; `EventDetailDialog`, una línea entera con su
  `payload` clave a clave), `ui/resources` (`ResourcesRoutes`, las rutas bajo `recursos`;
  `ResourcesClients`; `ResourcesNames`, los nombres de `mto-configuration` como en mantenimiento;
  `ResourcesPickers`, las personas, los vehículos y los instrumentos buscados en el servidor, que usan
  también los turnos de mantenimiento; `PeopleView` con `PersonEditorDialog`, y `PersonDetailView` en
  `recursos/personal/:personId`, la ficha de una persona con su pestaña `PersonQualificationsPanel`
  (`PersonQualificationDialog` sobre `PersonQualificationForm`); `QualificationTypesView` en
  `recursos/cualificaciones`, el catálogo de tipos, con `QualificationTypeEditorDialog`; `InstrumentsView`
  en `recursos/instrumentos` con `InstrumentEditorDialog`; `Expiry`, cómo se dice una caducidad;
  `OrganizationView`, el árbol en un
  `TreeGrid`, y `UnitDetailView` con `UnitEditorDialog`, `TeamMembersPanel`, `TeamMembersDialog`,
  `MemberDialogs` y `ChildUnitsPanel`; `VehiclesView`, `VehicleDetailView` con `VehicleLocationsPanel`,
  `VehicleLocationDialog` (con el punto de acceso de la vía elegida), `VehicleDocumentsPanel`
  (`VehicleDocumentDialog`) y `VehicleEditorDialog`;
  `FleetBoardView`, el tablero; `VehicleTypesView` y
  `ParkingSitesView` con sus editores; `ResourcesFormats` y `ResourcesHistory`; en `ui/maintenance`,
  `TaskGeneration`, la generación con `Prefer` y su seguimiento, y `ParkingRows`, dónde queda cada
  vehículo al cerrar un turno), `ui/support` (`UiErrors`: excepción →
  `Notification`; `Screens`: los botones de fila, las confirmaciones, los avisos y la ficha que no se
  puede leer, que comparten mantenimiento y recursos; `Changes`: lo que cambió en un editor frente a
  lo leído, para un `MergePatch`, con los conjuntos comparados sin orden; `ServerValidation`: `errors[]` del servicio → campos del `Binder`;
  `OffsetPager`: anteriores/siguientes para una lista `first`/`max` sin total, donde una página
  llena es la única señal de que hay más; `RevisionsDialog<D>`, el historial de cualquier fila
  (paginado, la más reciente primero; el 404 es «sin historial»); `LazyPanel`, la pestaña que
  pide sus datos al abrirse; `Downloads`, el fichero de un servicio servido a través de esta
  aplicación con `DownloadHandler`, que notifica en la pantalla un fallo; `Formats`, cantidades y
  fechas; `SharedPolling`, el reloj compartido que vuelve a preguntar al servicio mientras hay una
  pantalla abierta: los trabajos en curso y el contador de la campana (un hilo lleva el reloj y, con
  los hilos virtuales, cada pasada corre en el suyo: una llamada lenta solo retrasa su pantalla, y la
  pasada de una pantalla nunca se pisa consigo misma); `PageVisibility`, si la
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
  `mto-maintenance-api`, `mto-notification-api` y `mto-resources-api` (`app.keycloak.roles-client-ids`).
  Si un rol de realm se emitiera con `ROLE_`, quien administre el realm podría crear un rol llamado
  como un permiso y concederlo a cualquiera (`SecurityLayerTest`). El mapeo emite `ROLE_X` para
  los seis clientes, así que sus nombres de rol no pueden solaparse (`config-*` y `lov-manage`,
  `users-*`, `stock-*`, `maintenance-*`, `notification-*` y `resources-*`; `SecurityLayerTest` lo
  comprueba), y además
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
  almacén ha rechazado la operación», con el motivo de stock). Los de las caducidades de
  `mto-resources` tampoco piden recargar: `QUA-001` (la persona ya tiene ese tipo, o el tipo está
  retirado: renovar es cambiar la caducidad), `VDO-409` (el vehículo ya tiene un documento de ese tipo) y
  `QTY-409`/`INT-409` (código repetido); `QUA-404`, `VDO-404`, `INT-404` y `QTY-404` dicen qué ya no existe,
  y el 404 `MSI-404` de mantenimiento, que el instrumento aún no ha llegado allí desde `mto-resources`. El
  404 `TAP-404` es un punto de acceso a la vía que ya no existe o aún no ha llegado a `mto-resources`.
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
  pinta lo que devuelve el servicio o relee. Asignar (y el editor de la orden) sin persona deja que el
  servicio ponga al jefe de un equipo de Recursos: aquí solo se dice en el campo
  (`OrderEditorDialog.LEADER_HINT`). Una ficha (orden, turno, inspección, defecto) que no se
  puede leer por algo que no es un 404 se queda con el motivo, «Volver a la lista» y «Reintentar»
  (`Screens.showLoadFailure`); un 404 dice que no existe y vuelve a la lista.
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
  (`RevisionOperation`), los de los trabajos (`JobStatus`, `JobType`), los de notificaciones
  (`ActivityCategory`, `ActivitySeverity`, `ActorKind`, `AccessOutcome`) y los de recursos (también
  `QualificationKind`, `VehicleDocumentKind`, `InstrumentKind` y `AccessPointKind`, la clase de un punto de
  acceso tal como la cuenta `mto-resources`). Cada uno lleva `UNKNOWN`
  («Desconocido»), un `@JsonCreator(mode = DELEGATING) of(String)` que delega en
  `ClientEnums.parse` y `selectable()` sin `UNKNOWN` para los desplegables: un valor nuevo en el
  servicio se lee como desconocido en vez de romper la página entera, que además fallaría sin
  aviso, porque ese fallo de lectura no es un `BackofficeApiException`. Lo desconocido no abre
  nada: una reserva `UNKNOWN` no es activa; una línea de material `UNKNOWN` no ofrece ni modificar,
  ni sincronizar, ni quitar (`StockSyncStatus.isChangeable`); un trabajo en un estado `UNKNOWN` se da por terminado,
  para no consultarlo sin fin; uno de un tipo `UNKNOWN` no tiene familia, así que ni se consulta
  por separado ni se descarga; y un documento de un vehículo de un tipo `UNKNOWN` se ve, pero ni se
  modifica ni se quita. No se toca el mapper global. Quedan fuera, a propósito, los que solo
  viajan en peticiones (`AdjustmentDirection`, `RequiredAction`, `ReportFormat`) y el de los
  maestros de `mto-configuration` (`SectionInsulatorInstallationType`): el maestro se devuelve
  entero, y un `UNKNOWN` volvería al servicio como un valor que no existe; tolerarlo exigiría
  guardar la cadena original. Es lo que hace el punto de acceso a la vía: su clase y su lado son la
  cadena leída (`TrackAccessPointDto.kind`/`side`), que vuelve tal cual y se enseña cruda si no se
  conoce (`TrackAccessPointKind.labelOf`, `TrackAccessPointSide.labelOf`); el editor la ofrece además de
  las conocidas.
- **Un `LocalDate` en un `@HttpExchange` lleva `@DateTimeFormat(iso = DATE)`** (y un `YearMonth`,
  `pattern = "yyyy-MM"`): sin él sale con el formato corto de la máquina y el servicio responde 400.
  Los `Instant` viajan en ISO sin nada, con `:` codificado.
- **Un informe se ve en pantalla y su fichero se descarga a través de esta aplicación.**
  `ReportClient` pide cada informe por su ruta en JSON (lo que se pinta, con los nombres) o con
  `format=xlsx|pdf` como fichero, que `Downloads` sirve con `DownloadHandler` (primera regla). Los
  enlaces aparecen tras consultar y descargan esa consulta, fijada en un record, aunque luego
  cambien los filtros: exportan lo que se ve. Las cifras son del servicio: el avance llega como
  fracción (`0.4500`) y solo se pinta como porcentaje; aquí no se suma nada. El parte de un turno
  trae debajo de las tareas las inspecciones registradas en él (`ShiftReportPanel`, con su botón para
  abrir cada una) y su recuento del servicio; `inspectionsRecorded` es un `Integer` para que un
  servicio anterior se lea igual.
- **Una tarea abierta se quita del turno y vuelve a su orden**, como en mto-frontend
  (`ShiftTasksPanel`, «Quitar del turno»: `POST /shifts/{id}/tasks/{taskId}/unassign`, con
  `maintenance-write` como asignar). Se ofrece con el turno planificado o en curso
  (`ShiftStatus.isOpen`), sin confirmación porque se deshace asignándola otra vez; una terminada o
  cancelada se queda, porque es lo que se hizo en el turno. Es lo que pide el 409 `SHF-001` de quitarle
  una vía o pasarlo a parcial con una tarea que dejaría de caber. La pestaña «Perfiles» del turno
  filtra siempre por un estado (un `Select` que no se vacía, empezando en «Completada»): sin él, el
  servicio da los completados, y un «todas» vacío habría enseñado solo esos.
- **Un activo que une dos vías se trabaja desde un turno de cualquiera**, como en mto-frontend.
  `AssetSummaryDto` trae `connectedTrackId`, y `tracks(respaldo)` da sus vías: completar desde la
  orden busca los turnos en curso de las dos en una sola llamada (`ShiftFilter.trackIds`, que viaja
  como `trackId` repetido), y el alta de una inspección ofrece los trabajados (en curso o cerrados,
  `ShiftStatus.recordsInspections`) de las dos; otro activo vacía el turno elegido. Que el turno le
  vale lo dice el servicio (409 `SHF-001`).
- **Una orden preventiva es de un solo EP y genera en varias de sus vías**, como en mto-frontend
  (`GenerateTasksDialog`). Cada fila es un tipo de activo (perfiles, aisladores, seccionadores) en un
  sitio: el tramo de la orden, otro tramo, una vía con KP opcionales, una estación, todo el EP o
  activos elegidos, con sus propios tipos si se quiere. Los desplegables solo ofrecen lo del EP de la
  orden (`MaintenanceNames.tracksIn`/`stationsIn`, que salen de `ReferenceCatalog`, y los activos con
  `MaintenancePickers.asset/assets` filtrados por EP), porque lo de otro EP va en otra orden; sin EP
  solo cabe el tramo de la orden. Si todo es el tramo de la orden sin tipos propios viaja la petición
  de siempre (`GenerateTasksRequest.ofSection`, `{}` o `assetTypes`); si no, una `GenerationSelection`
  por fila. «Previsualizar» manda `dryRun` y pinta lo que diría el servicio de cada activo
  (`GenerationOutcome`, tolerante); cualquier cambio la borra. Qué se salta (ya en la orden, en otra
  orden abierta, de otro EP, sin tipo de tarea) y los tipos por defecto (RG-03 y RP-02 en un aislador,
  y RG-04 y RP-03 si tiene agujas; RP-07 en un seccionador, y RP-06 con motor, `DisconnectorDrive`, y
  RG-06 en una autoválvula) los decide el servicio. Los recuentos nuevos de
  `GenerateTasksResultDto` son `Integer` y listas, para que un servicio anterior se lea igual. Se ofrece
  en una orden en borrador o planificada sobre un tramo o con EP: una sobre un aislador o un
  seccionador también genera.
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
  enteros, la longitud de un paquete son solo cifras y el KP del perfil, y el propio de un seccionador
  sin poste, viajan recortados. Un punto de acceso a la vía lleva su KP sin signo con 9 enteros y 3
  decimales (`Numbers.infrastructureKp`), sus toneladas sin signo con 5 y 3 (`tonnes`) y sus coordenadas
  en su rango con 6 decimales (`coordinate(90)`, `coordinate(180)`, las mismas que una ubicación de
  `mto-resources`), y las dos juntas o ninguna: se marca la que falta («Con la latitud va tambien la
  longitud», y al revés), como el 400 del servicio. Lo demás (un
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
  físico con sus ménsulas y su seccionador, con la vía que maniobra si no es la del poste y la otra
  vía si pone dos en paralelo, los aisladores, las estaciones), y `SchematicDrawing`
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
- **Personal, organigrama y flota son de `mto-resources`, y sus reglas las decide el servicio.** Lo que
  se copia aquí es solo para no ofrecer lo que va a fallar: un equipo nace con su jefe y de 2 a 6
  linemen (comprobado antes de llamar), «Añadir lineman» se apaga con 6 y «Sale del equipo» con 2, y
  cambiar varios a la vez es la lista entera (`PUT /members`, con su fecha); el padre de una unidad es de
  un nivel estrictamente superior y de la misma disciplina, y su jefe, de las categorías que admite el
  nivel (en un equipo se sustituye, nunca se vacía); un modelo enseña solo las secciones de su movilidad
  y sus capacidades con sus cifras (quitar una vacía sus cifras; una desconocida vuelve tal cual); un
  vehículo pide lo que exige su modelo; y «Registrar ubicación» ofrece solo lo que admite su movilidad,
  con los sitios que alcanza. Lo demás es su 409 `UNI-001` o `VEH-001`, con el diálogo abierto. Todo se
  modifica con merge-patch y la versión leída, como en mantenimiento, y nada se borra: `DELETE`
  desactiva o retira (`resources-delete`) y reactivar es un `PATCH active=true`. El tablero es una tabla
  por vía y kp (`effective ?? position`: un remolcado está donde su portador) que se vuelve a pedir con
  «Actualizar», sin sondeo. Las rutas `recursos/*` son las de mto-frontend
  (`theResourcesRoutesAreTheOnesOfTheSpa`).
- **Las cualificaciones, los documentos y las calibraciones caducan, y si han caducado lo dice el
  servicio.** El estado (`Expiry`) es «Caducada» (o «Caducado», un documento) cuando el servicio lo dice
  con `expired` o `calibrationExpired`, «Caduca en N dias» mientras falten 30 o menos, y «Vigente» más
  allá; sin fecha, «No caduca» o «Sin calibracion». Los días que faltan son solo presentación: la fecha
  y la zona son del servicio, y lo que no entra en un turno lo decide mantenimiento a la fecha del turno.
  La lista de Personal abre la **ficha de una persona** (`recursos/personal/:personId`, que es también el
  enlace de los avisos de caducidad) con doble clic o con «Abrir»: su cabecera con los botones de la
  lista y la pestaña «Cualificaciones». Una persona tiene una de cada tipo, así que el alta ofrece solo
  los tipos activos que no tiene (lo demás es el 409 `QUA-001`), dice la vigencia habitual del tipo sin
  calcular nada, y renovar es cambiar la caducidad (merge-patch con la versión; vaciarla es que no
  caduca); quitarla es un `DELETE` con confirmación. Escribir pide `resources-write` y quitar
  `resources-delete`. La ficha de un vehículo lleva la pestaña «Documentos», uno de cada tipo y con la
  caducidad obligatoria (`VDO-409`), con `resources-fleet-write` y `resources-delete`. El catálogo de
  tipos (`recursos/cualificaciones`) pide el permiso de conducir solo en uno de `DRIVING_LICENCE`, y al
  pasar a otra clase lo vacía; se retira con un `DELETE`. Los instrumentos (`recursos/instrumentos`) se
  piden paginados y filtrados en el servicio, también por «calibración caduca antes de»
  (`calibrationExpiresBefore`, una fecha ISO), con su historial; escribirlos pide
  `resources-fleet-write`. El editor de modelos elige la «Habilitacion del conductor» entre los tipos
  activos (y el leído, aunque esté retirado): vacía en el alta no viaja y la pone el servicio según la
  movilidad y el permiso; vaciarla al modificar viaja a `null` (no exige ninguna). Un código repetido
  (`QTY-409`, `INT-409`) y los 409 de una de cada tipo no piden recargar.
- **Un equipo de mantenimiento que llega de `mto-resources` se lee.** Su origen, su jefe, su supervisor
  y sus linemen salen en la lista, y su fila lleva a su unidad del organigrama y a su vehículo (con
  `resources-read`) en vez de a «Modificar», que el servicio rechazaría con 409 `TEA-001`; si llega, se
  dice que se cambia en Recursos > Organigrama.
- **La dotación, los vehículos y los instrumentos de un turno son de `mto-resources`.** Con
  `resources-read` el editor elige el jefe (cualquiera menos un operario), el supervisor (de supervisor
  para arriba), los linemen (operarios o jefes de equipo), los vehículos disponibles y los instrumentos
  activos (cada uno con hasta cuándo está calibrado: `ResourcesPickers.instrumentLabel`); en el alta, lo
  que se deja vacío no viaja y la dotación y los vehículos los copia el servicio del equipo (los
  instrumentos, nadie); al modificar, linemen, vehículos e instrumentos son conjuntos y quitarlos todos
  viaja a `null`. Sin `resources-read` se leen y no viajan. La ficha y el parte (`ShiftReportPanel`, con
  los `instruments` del turno del parte) enseñan los instrumentos con su calibración. Las demás reglas
  (de 2 a 6 linemen para iniciar, las plazas, el remolque, una persona o un vehículo en otro turno del
  día, y lo caducado a la fecha del turno: una cualificación de la dotación, un documento de un
  vehículo, la calibración de un instrumento, o la habilitación de cada conductor al iniciar) son el 409
  `SHF-001` del servicio, y una persona, un vehículo o un instrumento que aún no ha llegado por evento,
  su 404 `CRW-404`/`FLT-404`/`MSI-404`. Cerrar el turno dice dónde queda cada vehículo (`ParkingRows`: un sitio que
  alcanza por su movilidad, una vía y su kp, o un texto); solo viaja lo rellenado, lo evidente se dice
  en su fila antes de llamar, y lo que `mto-resources` lee de ahí es lo que lo mueve.
- **Un punto de acceso a la vía es un maestro de `mto-configuration`, y lo aplica `mto-resources`.** Es
  un maestro más de Infraestructura (`infraestructura/accesos`, «Accesos a via», `TrackAccessPointsView`):
  la lista paginada en el servidor con el nombre de su vía y de su estación, filtrada por texto, vía
  (`trackId`), tipo (`kind`) y estado (`enabled`) en el cuerpo del `/filter`, y ordenable por nombre, vía,
  estación, KP, tipo, lado, biviales, activo y «Modificado»; el editor sobre una copia de la fila,
  devuelta entera con su `versionNumber` y sus `extras` (un alta empieza admitiendo biviales y activa; la
  estación se puede vaciar); el borrado (lógico) con confirmación, con los permisos de los maestros. Un
  nombre repetido en la vía es el 409 `BUS-002`, con el diálogo abierto. Republicar ofrece también
  «Puntos de acceso a via» (`track-access-point`), antes de «Todo» y acotado por vía como los perfiles
  (`JobsView.REPUBLISH_BY_TRACK`): pasar de uno a otro conserva la vía elegida. En «Registrar ubicacion»
  de un vehículo, donde se ofrece la vía (en vía, o aparcado si no es de solo carretera, y con
  `config-read`) y en cuanto se elige una, se ofrece el punto de acceso por el que encarriló, para
  cualquier movilidad: los activos de esa vía tal como los tiene `mto-resources`
  (`AccessPointClient.enabledOnTrack`, `GET /track-access-points?trackId=&enabled=true`), pedidos cada vez
  que cambia la vía, que lo vacía; un fallo al pedirlos no se avisa: lo dice la ayuda del campo. Cada uno
  dice su nombre, su tipo y su kp, y «sin biviales» si no sirve para encarrilarlos; no se filtra nada más
  aquí: si le vale al vehículo (biviales, acceso preparado, pesos) lo dice el servicio con su 409
  `VEH-001`, con el diálogo abierto. Es opcional y viaja como `accessPointId`. Uno que ya no existe o aún
  no ha llegado a Recursos es su 404 `TAP-404`: se vuelve a pedir la lista, se dice en el campo y el
  diálogo sigue abierto. El texto libre se sigue ofreciendo, «Acceso (texto)» (`accessPoint` en la
  petición), a una bivial en vía o por carretera. Una ubicación trae el punto de acceso del maestro en
  `trackAccessPoint {id, name, kind}` y el texto, como siempre, en `accessPoint`: la ficha («Acceso: …»)
  y la columna «Acceso» del histórico enseñan `VehicleLocationDto.accessPointLabel()`, el nombre del
  maestro (`#id` si llega sin él) y, sin él, el texto. El tablero no lo enseña.
- **Generar tareas de verdad va con `Prefer: respond-async, wait=5`** (`TaskGeneration`). Si el servicio
  termina en su espera contesta lo de siempre; si no, 202 con la operación, que el diálogo sigue cada
  2 s desde `SharedPolling` con la pestaña a la vista (`GET /operations/{id}`, nunca el `Location`, que
  es la ruta interna del servicio): al terminar bien dice lo mismo que la llamada síncrona y relee las
  tareas; si falla, su error como el de la llamada síncrona (`TaskGeneration.failureOf`; `OPS-410` es una
  abandonada), con el diálogo abierto; cerrarlo deja de seguirla y avisa de que continúa allí. El tope
  de operaciones del despliegue es un 429 `OPS-429` con cuándo reintentar. La previsualización sigue
  siendo síncrona. Un `OperationStatus` desconocido se da por terminado, para no preguntar sin fin.
- **Nada de componentes de pago.** `vaadin-spring-boot-starter` trae solo `vaadin-core-internal`.

### Tests

Una clase por capa; se añaden métodos, no clases: `ClientLayerTest` (interfaces `@HttpExchange` y
`RestClient` reales contra `MockRestServiceServer`: prefijo del gateway, Bearer y correlación, forma
de página, `problem+json` de configuration, 401/403 y 503 del gateway, cuerpo no JSON; los
catálogos: el `versionNumber` leído en el `PUT` y en el lote, y los dos 409 de configuration
(`CON-001` y `BUS-002`) distinguidos por su código; los maestros: resolución del genérico,
parámetros de página y orden del `/filter` (sin orden no viaja `sort`, como en el recuento), `extras` e hijos a
`null` en un `PUT`, referencias a catálogo como `{id, code}`, las ménsulas tipadas con su brazo y
el seccionador 1:1 en un `PUT`, el seccionador con su estado normal, su accionamiento y sin poste,
con su KP y su vía, y el que pone dos vías en paralelo con su vía conectada, que se quita a `null`,
el esquema de una vía con sus records anidados; los trabajos: la importación como parte multipart con `dryRun` en
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
órdenes y sus transiciones con sus cuerpos, tareas (alta, generar, modificar, cancelar), un aislador
que une dos vías (su vía conectada, sus turnos con `trackId` repetido, generar con los aisladores, el
parte con las inspecciones del turno y el alta de una inspección con su turno), generar en varias vías
del EP (una selección por sitio con sus tipos y `dryRun`, la previsualización con lo desconocido),
turnos con
sus conjuntos enteros, sus tareas y perfiles, asignar y sacar una tarea, ejecutar una tarea con su checklist, defectos y
materiales, inspecciones y lo que generan, defectos y sus transiciones, líneas de material
(quitar con 204 y el 503 `STK-503`; la rechazada con su motivo, y el 422 `STK-422` o el 409
`STK-001` al sincronizarla), los informes en JSON y como fichero con su nombre, y el
historial de cada recurso con el 404 de un activo sin revisiones; las notificaciones: la bandeja
con sus filtros y una gravedad desconocida leída como `UNKNOWN`, el contador acotado, las marcas
como `POST` sin cuerpo y el 404 `NTF-404` por alias, el registro con todos sus filtros
(`includeSuperseded` solo cuando es verdadero), una categoría y un actor desconocidos, el detalle
con su `payload`, los accesos por usuario, IP, tipo y resultado, y el 400 `REQ-400` de un `sort`
desconocido),
`SecurityLayerTest` (mapeo de roles de los seis clientes con el sinónimo cualificado, un cliente
no listado no aporta nada, un rol de realm `users-read`, `stock-read`, `maintenance-read`,
`notification-access-read` o `resources-read` nunca abre el módulo, `SecurityRoles`, `UserRoles`,
`StockRoles`, `MaintenanceRoles`, `NotificationRoles` y `ResourcesRoles` coinciden con el realm y son
disjuntos, registro OIDC sin
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
el perfil legible en la lista de seccionadores, el seccionador guardado sin poste y con su estado
normal y su accionamiento, con su KP (comprobado antes de llamar) y su vía, el KP que elegir un
poste vacía y la vía que se queda, la vía que maniobra uno en un poste en la lista y en el editor,
y vaciada de vuelta a la del poste, la vía conectada en la lista y en el editor, también con poste,
con el 400 del servicio en su campo, el seccionador sin estación (de ida y vuelta, y con el 400 del servicio sobre la estación
cuando no queda en ningún sitio), los mensajes de sistema, el esquema de una vía desde
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
supervise, las tareas (añadir, generar, también con los aisladores, modificar, cancelar), generar por
filas en varias vías del EP con su previsualización, una orden sobre un aislador que genera desde su EP con
lo que falta dicho antes de llamar y el 400 con el diálogo abierto, y una orden sin EP que solo genera su
tramo, el historial de estados; turnos
filtrados, el alta con su vía y sus seccionadores, iniciar y cerrar, asignar tareas con las
rechazadas resumidas, quitar una abierta del turno (y no lo terminado ni con el turno cerrado), los
perfiles filtrados siempre por un estado, ejecutar una tarea con su checklist, sus defectos y su material, completar
desde la orden eligiendo un turno en curso de su vía (o de las dos de un aislador que las une); inspecciones con su turno, su defecto (`force` si es
leve) y su orden correctiva, o los enlaces a ellos, y sus puntos contestados; defectos vinculados,
resueltos y descartados con sus motivos, los de una orden; las líneas de material con su almacén y
lo que admite cada una (la reservada se comprueba; la rechazada, con su motivo, se reintenta y dice
por qué el almacén vuelve a decir que no), el alta desde el almacén, quitar una reservada con su aviso y el almacén
caído notificado, el `MAT-001` al completar y el proyecto de almacén de la orden, y una línea con
una petición al almacén sin respuesta (su estado y su tooltip, las cantidades de solo lectura, sin
«Quitar» con una salida y, con una reserva, avisando de que pasa por el almacén, y el proyecto de la
orden de solo lectura mientras dure); los informes (el
avance con sus nombres y porcentaje, el mensual con sus 24 meses, las descargas de punta a punta
con el `_download` de Karibu, y el parte del turno en su pestaña, con sus inspecciones); el historial de cada ficha y el
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
cada servicio y los seis clientes cuyos roles son permisos; redirección al login; sonda de
salud; ausencia de artefactos comerciales; las dos hojas de Lumo en el shell). Lo que trajo la
convivencia con mto-frontend son casos de esas mismas clases: el enlace a la SPA con su query, el
tipo padre y los `extras` de un catálogo, la referencia vaciada, el aviso fijo de la lista de
trabajos, la pestaña oculta, las descargas fallidas, los cambios de usuarios, los desempates, lo
retirado en los filtros del almacén, las fichas de mantenimiento ante un fallo y los enlaces de
las notificaciones. Y las cinco diferencias que cerró la fase 9 de la SPA: una petición por página y
un fallo avisado una vez, el catálogo de roles una vez por pantalla, las filas que se abren con doble
clic o con su botón y no con un clic, el `ACT-404` dentro de su diálogo y los números de cada diálogo
(lo que no cabe en la columna, el mínimo con su mensaje, la medida que el campo no sabe leer). Los
recursos (fase 10) son casos de esas mismas clases: en `ClientLayerTest`, las personas, el organigrama
con sus linemen, los modelos, los sitios, los vehículos con sus ubicaciones y el tablero, el turno con
su dotación, sus vehículos y sus aparcamientos, y el 202 de una generación con su operación; en
`ViewLayerTest`, el menú de recursos y sus permisos, el personal, el árbol y la ficha de una unidad con
la regla de 2 a 6, los modelos por secciones, los vehículos y dónde están, el tablero, el vehículo por
defecto de un equipo, el equipo sincronizado de solo lectura, la dotación del turno y su cierre con
aparcamientos, la generación seguida hasta su resultado, su fallo y su 429, y las rutas de recursos
iguales a las de la SPA. Las cualificaciones y caducidades de `mto-resources` también: en
`ClientLayerTest`, el catálogo de tipos (una clase nueva como `UNKNOWN`), las cualificaciones de una
persona con su `QUA-001`, los documentos de un vehículo con su `VDO-409`, los instrumentos con
«calibración caduca antes de» y su historial, la habilitación del conductor de un modelo (vaciada a
`null`) y los instrumentos de un turno y de su parte, con el `MSI-404`; en `ViewLayerTest`, el estado de
una caducidad, los mensajes de sus códigos, el menú y sus permisos, la ficha de una persona abierta desde
su fila con sus cualificaciones (el alta con los tipos que no tiene, el error del servicio en su campo, el
`QUA-001`, renovar, quitar con confirmación, solo lectura sin `resources-write`), el catálogo de tipos, los
instrumentos, la pestaña de documentos (un tipo desconocido no abre nada), el editor de modelos y los
instrumentos de un turno (un conjunto que se vacía a `null`, el `MSI-404`, la ficha y el parte, de solo
lectura sin `resources-read`). Los puntos de acceso a la vía (fase 4, bloque D) también: en
`ClientLayerTest`, el maestro filtrado por vía, tipo y estado con sus decimales como números, devuelto
entero con su versión y lo desconocido, el 409 `BUS-002` de un nombre repetido y el borrado; el
republicado con `entity=track-access-point` y la vía; una clase y un lado nuevos devueltos como se
leyeron; la lista de `mto-resources` de una vía (una clase nueva como `UNKNOWN`, «sin biviales», y una
página leída por su `content`), la ubicación con `accessPointId`, el 404 `TAP-404` y lo que enseña una
ubicación (`trackAccessPoint` sobre el texto de `accessPoint`, `#id` sin nombre); en `ViewLayerTest`, el
menú, la lista con los nombres de su vía y su estación, un tipo nuevo crudo y sus filtros en el
servicio, lectura sin controles, el editor con lo que se comprueba antes de llamar (KP, toneladas,
coordenadas y la que falta marcada), la copia devuelta entera con su versión, el `BUS-002` con el diálogo
abierto, el borrado confirmado, el republicado acotado por vía que conserva la vía de los perfiles, el
mensaje del `TAP-404`, y el diálogo de ubicación con los puntos de acceso de la vía (solo con una elegida,
pedidos al elegirla, vaciados al cambiarla, el fallo en su ayuda sin aviso, el `TAP-404` que relee la lista
y se dice en su campo, el `VEH-001` con el diálogo abierto) y la ficha y el histórico con su nombre. Todo corre en la JVM sin Docker. En un navegador, contra la plataforma entera, lo prueba el job `e2e` del
CI: `mto-platform/scripts/e2e.sh` construye la imagen de este commit, usa la publicada de cada
hermano y corre el e2e de Playwright de mto-frontend, que recorre las dos aplicaciones.
