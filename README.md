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

## Estado: fase 7

- **Fase 0**: circuito completo con lo mínimo. Cliente `mto-backoffice` en el realm, login OIDC,
  marco con menú filtrado por roles y la pantalla de inicio con el diagnóstico del token.
- **Fase 1**: los 17 catálogos (listas de valores) con **una sola vista**, `LovCrudView`, que recibe
  el recurso en la ruta (`/catalogos/pole-types`, `/catalogos/profile-statuses`...). Los 17
  controladores de `mto-configuration` heredan los mismos ocho endpoints y el mismo DTO, así que
  una pantalla cubre 136 endpoints: listar (con filtro local: el servicio devuelve la lista entera),
  alta y modificación en un diálogo, borrado con confirmación (lógico en el servicio), alta múltiple
  pegando `CODIGO;Descripción` por línea (`POST /bulk`) y activar o desactivar la selección
  (`PUT /bulk`). Los errores del servicio se enseñan en su campo: `errors[{field, code, message}]`
  se vuelca sobre el formulario y lo que no se puede atribuir va a una notificación. Cada
  modificación, también cada entrada del lote, lleva el `versionNumber` de la fila que se leyó, y
  el catálogo se relee al guardar: si otra persona guardó la entrada entre medias, el servicio
  responde 409 `CON-001` sin escribir nada y la notificación pide recargar, en vez de pisar su
  cambio.

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
  de un paquete, perfiles de una vía) van a `null`, que para el servicio es «de esta colección no
  digo nada». Para que eso fuera posible el backend
  cambió con la fase: las listas de paquetes, estaciones y vías van sin hijos, los filtros
  booleanos solo filtran si vienen y las empresas se pueden leer (`GET /business-entities`).

- **Fase 3**: los trabajos en segundo plano de `mto-configuration` (`README_ASYNC_JOBS.md`) en
  `trabajos`: exportar los perfiles de una vía a CSV, importar el maestro de perfiles
  (`profile-master.xlsx`) y el catálogo de LOV (`lov-master.xlsx`) con simulación (`dryRun`), y
  republicar los datos maestros. Lanzar es una llamada que responde 202 con el trabajo, o **429 con
  el trabajo ya rechazado** y un `Retry-After` cuando no hay cupo: se apunta igual, como rechazado,
  y se dice cuándo reintentar. Seguirlos es lo que hace `@Push`: mientras la pantalla está abierta
  y hay algo en curso, un hilo compartido vuelve a pedir la página cada dos segundos y lleva el
  progreso al navegador con `UI.access()`. El fichero de un trabajo (el CSV, o el informe JSON de
  una importación, disponible también cuando terminó con errores) se descarga **a través de esta
  aplicación**, con el token de la persona: el navegador nunca habla con el gateway. La lista es
  la del servicio (`GET /jobs`, añadido en `mto-configuration` para esto): todas las familias, del
  más reciente al más antiguo, paginada y filtrable por tipo y estado, así que se ven también los
  trabajos lanzados desde otra sesión o antes de un reinicio; lo que solo sabe esta sesión (con
  qué etiqueta lanzó cada trabajo) se pinta encima, y los errores por elemento se piden al detalle
  al abrirlos.

- **Fase 4**: lo que los editores no tocaban. Las **ménsulas** de un perfil (hasta tres, cada una
  con su brazo de atirantado 1:1) y las **agujas** de un aislador de sección se editan dentro del
  editor del padre, en su propia tabla con alta, modificación y baja; al guardar, si nadie las
  tocó van a `null` y si alguien las tocó va la lista entera, que para el servicio es el estado
  final (`README_API.md` §4: la que no mandas se borra). El **seccionador** de un perfil (1:1) se
  vincula o desvincula desde el editor del perfil: se manda el objeto entero para vincularlo y
  `null` para desvincularlo, que no lo borra. En la lista de seccionadores el perfil se muestra
  por su identificador y su KP, que el servicio manda ahora con cada fila (`profileCode`,
  `profileKp`) para no ir perfil por perfil. Y los mensajes de sistema de Vaadin quedan fijados en
  castellano y sin diálogo de sesión caducada: tras un reinicio la pantalla recarga sola y vuelve
  por el SSO (ver «Límites», más abajo).

- **Fase 5**: el módulo **Usuarios** sobre `mto-users`, que administra usuarios, roles
  de cliente y perfiles del realm por la Admin API de Keycloak. U0 deja la base: los roles de
  `mto-users-api` se leen del access token junto a los de `mto-configuration-api`
  (`KEYCLOAK_ROLES_CLIENT_IDS`), `ApiErrorDecoder` entiende el `problem+json` de `mto-users`,
  `UsersClient` cubre la API entera (`/api/users/**`) y el menú agrupa por prefijo de ruta. U1
  trae la **lista de usuarios** (`usuarios`), paginada en el servidor al estilo de Keycloak (el
  grid pide cada tramo con `first`/`max`, nunca más de 200 por petición, y el recuento es el
  `total` de la búsqueda), con búsqueda por texto, filtro por atributo `clave:valor` y por estado;
  la búsqueda y el atributo se **excluyen** (el servicio los rechaza juntos, `SEARCH-400`): escribir
  en uno deshabilita el otro. El alta pide usuario, datos, contraseña temporal (mínimo ocho) y las
  acciones requeridas al entrar; la modificación enseña el usuario en solo lectura y manda **solo
  lo que cambió** (`null` es «no tocar» para el servicio; vaciar un campo viaja como cadena vacía);
  los atributos se editan como texto, una línea `clave=valor` por valor. Activar y desactivar van
  sin confirmación (son reversibles y no cierran sesiones: eso es de la ficha); borrar confirma. U2
  trae la **ficha** (`usuarios/{id}`): cabecera con estado, email, acciones pendientes y
  atributos, botonera con modificar, activar/desactivar, contraseña temporal, correo de acciones
  y borrar (cada botón con su permiso), y una pestaña por cosa que Keycloak guarda aparte, que
  pide sus datos la primera vez que se abre: **Perfiles** (asignar uno de los que faltan y quitar;
  se pinta la lista que devuelve el servicio) y **Roles de cliente** (elegir cliente, luego los
  roles que aún no tiene; quitar es el `DELETE` con cuerpo). La contraseña temporal lo es por
  defecto (la fija otra persona) y la política del realm cae sobre el campo; el correo de acciones
  necesita SMTP en el realm y, sin él, la notificación enseña el detalle del 502. U3 añade a la
  ficha las pestañas **Sesiones** (las normales y las offline, cada una con su lista y su «cerrar
  todas» confirmado; una sola se cierra sin preguntar, y una que ya no exista o no sea de ese
  usuario, `SES-404`, se avisa y se recarga) y **Credenciales** (tipo, etiqueta y fecha, sin
  secretos; quitar confirma y, si es la contraseña, avisa de que la persona no podrá entrar
  hasta que se le fije una temporal), y el botón **«Sacar a la persona»**: las tres llamadas que
  el README de `mto-users` deja al cliente, en su orden (desactivar, cerrar las sesiones, revocar
  las offline), parando en el primer fallo y diciendo qué paso falló y qué quedó hecho. Pide
  `users-write` y `users-sessions-write` a la vez. U4 cierra el módulo con dos catálogos de solo
  lectura bajo el mismo grupo del menú: **Perfiles de usuario** (`usuarios/perfiles`: la lista con
  filtro local y, para el elegido, lo que concede —roles por cliente y de realm— y sus miembros)
  y **Roles de cliente** (`usuarios/roles`: los roles del cliente elegido, sin los clientes
  protegidos que `mto-users` no lista, y quién tiene cada uno). Los miembros llegan del servicio
  como lista plana y sin total, así que se pasean con «anteriores/siguientes» y una página llena
  es la única señal de que hay más; son asignaciones directas (quien tiene un rol por un perfil
  aparece en el perfil, no en el rol), y una fila abre la ficha.

- **Fase 6**: el módulo **Almacén** sobre `mto-stock`, el inventario de la nave de
  catenaria: catálogos (almacenes, proveedores, proyectos, materiales), existencias y movimientos
  (entradas, salidas, transferencias, ajustes), reservas, conjuntos con su lista de materiales y su
  disponibilidad, e historial de revisiones. S0 deja la base: los roles de `mto-stock-api` se leen
  del access token junto a los de los otros dos clientes, `ApiErrorDecoder` entiende el JSON de
  error de `mto-stock` (que no es `problem+json`), siete clientes `@HttpExchange` cubren la API
  entera (`/api/stock/**`) y el menú tiene el grupo «Almacén». Antes de las pantallas,
  `mto-stock` ganó lo que la pantalla necesita y la API no daba (su PR previo): `search` por código o
  nombre y `active` en las cinco listas de catálogo, y los proyectos sincronizados desde
  `mto-configuration` expuestos con su origen y rechazados en el `PUT`. S1 trae los **catálogos**
  bajo `almacen/*` (materiales, almacenes, proveedores, proyectos): la lista paginada en el servidor
  con la búsqueda por código o nombre, el estado y el orden de la columna (`search`, `active`,
  `page`, `size`, `sort=campo,asc`; el recuento es `totalElements`), el alta con código y nombre (y
  unidad y stock mínimo en materiales) y la modificación con el estado, que es como se retira uno
  (no hay borrado en `mto-stock`). Un proyecto sincronizado desde `mto-configuration` enseña su
  origen y no se ofrece modificarlo: el servicio lo rechazaría con `PRJ-001`. S2 trae las
  **existencias** (`almacen`, la entrada «Almacén» del menú): se elige un material (buscado en el
  servidor) y, si se quiere, un almacén, y se ven las cifras que calcula `mto-stock` (físico,
  reservado, disponible, mínimo y si está bajo mínimo), el libro de ese material y los materiales
  bajo mínimo; y los **movimientos** (`almacen/movimientos`): el libro entero paginado en el
  servidor con sus filtros (tipo, almacén, material, proyecto, fechas inclusivas, quién lo
  registró) y las cuatro operaciones en un diálogo: entrada (proveedor opcional), salida
  (proyecto opcional), transferencia (dos almacenes distintos; el servicio devuelve dos apuntes) y
  ajuste (positivo o negativo; solo con `stock-adjust`). Nada se calcula aquí: si no hay
  disponible, el servicio responde 409 `STK-001` y la notificación lo dice. S3 trae las
  **reservas** (`almacen/reservas`): la lista paginada en el servidor con sus filtros (estado,
  almacén, material, proyecto; por defecto las activas), el alta (material, almacén, proyecto y
  cantidad; nace activa y reduce el disponible) y la modificación (almacén, proyecto y cantidad;
  el material no cambia) y, solo en las filas activas, liberar (vuelve al disponible sin
  movimiento), consumir (baja el físico), cancelar (con `stock-delete`; el servicio devuelve la
  reserva cancelada) y «salida con esta reserva», que abre la salida de movimientos con el
  material, el almacén y la cantidad fijos porque el servicio exige que coincidan exactamente con
  lo reservado. Una reserva que ya no está activa no cambia: el servicio responde 422 `RES-001` y
  la notificación lo dice. S4 trae los **conjuntos** (`almacen/conjuntos`): productos virtuales
  definidos por su lista de materiales y sin stock propio; la lista es la de cualquier catálogo,
  el editor lleva la lista de materiales entera (material buscado en el servidor y cantidad por
  conjunto; repetir un material sustituye su cantidad, y no puede ir vacía) y cada fila ofrece,
  también a quien solo lee, la **disponibilidad** por almacén: cuántos se podrían montar ahora y
  qué componente lo limita, tal como lo calcula el servicio
  (`GET /assemblies/{id}/availability?warehouseId`). S5 cierra la fase con el **historial**: cada
  fila de un catálogo (materiales, almacenes, proveedores, proyectos, conjuntos) y de las reservas
  tiene su botón de historial, para quien puede leer: las revisiones que guarda el servicio
  (`GET /{recurso}/{id}/revisions`, paginadas, la más reciente primero) con quién, cuándo, la
  operación, el origen (`HTTP`, `MESSAGING`, `SYSTEM` o `BASELINE`, la foto inicial de lo que ya
  existía), la referencia de correlación y una línea con cómo quedó la fila. Sin revisiones el
  servicio responde 404 y la pantalla dice «sin historial todavía», no un error.

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

Las pantallas de usuarios (fase 5) siguen los permisos de `mto-users-api`, y ninguno implica otro:

| Acción sobre un usuario | Roles de cliente de `mto-users-api` |
|---|---|
| Ver la lista, la ficha, perfiles, roles, sesiones, credenciales y catálogos | `users-read` |
| Nuevo, modificar, activar/desactivar, enviar acciones por correo | `users-write` |
| Borrar | `users-delete` |
| Asignar y quitar roles de cliente | `users-roles-write` |
| Asignar y quitar perfiles | `users-profiles-write` |
| Contraseña temporal | `users-password-reset` |
| Cerrar sesiones (normales y offline) | `users-sessions-write` |
| Quitar una credencial | `users-credentials-write` |
| «Sacar a la persona» (desactivar, cerrar sesiones, revocar offline) | `users-write` **y** `users-sessions-write` |

Las pantallas de almacén (fase 6) siguen los permisos de `mto-stock-api`, que el servicio aplica por
método HTTP (`GET` lee, `POST`/`PUT` escriben, `DELETE` cancela) y ninguno implica otro:

| Acción en el almacén | Roles de cliente de `mto-stock-api` |
|---|---|
| Ver catálogos, existencias, movimientos, reservas, conjuntos e historial | `stock-read` |
| Nuevo y modificar en los catálogos y conjuntos (retirar es modificar con `active=false`) | `stock-write` |
| Entradas, salidas y transferencias; crear, modificar, liberar y consumir reservas | `stock-write` |
| Ajustes de inventario | `stock-write` **y** `stock-adjust` |
| Cancelar una reserva | `stock-delete` |

Esconder un botón es cortesía: la guarda real es `@RolesAllowed` en la vista y el 403 del servicio.
Con los usuarios de desarrollo, `config.responsable` (`mto-admin`) lo ve todo; `config.editor`
(`mto-editor`) ve los catálogos pero no puede tocarlos: le falta `lov-manage`, a propósito.
- **Fase 7**: el **esquema de una vía**, desde el botón «Esquema» de cada fila de *Infraestructura ›
  Vías* (también para quien solo lee): una ventana con la vía como una línea recta y, sobre ella, un
  poste por perfil a distancia uniforme, en el orden físico de la vía, con su código encima y su KP
  debajo, el tipo de poste y el estado, los seccionamientos, sus ménsulas como brazos (con el tipo,
  hacia el lado que dice `railPoleDistance`) y su seccionador; los aisladores de sección van sobre
  la línea, colocados entre los dos perfiles vecinos por KP, con sus agujas; las estaciones de la
  vía, en la cabecera; el detalle de cada elemento, al pasar por encima. Es esquemático a propósito:
  no es el layout CAD. Lo que se dibuja es la proyección que `mto-configuration` devuelve en **una
  llamada** (`GET /tracks/{id}/schematic`, añadido allí para esto y **cacheado en Redis**, con solo
  lo que el dibujo necesita): la pantalla no ordena ni calcula nada, reparte los postes y escapa el
  texto antes de convertirlo en SVG.

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
| `config.responsable` | `mto-admin` | todo lo de configuración; los `config.*` no llevan `users-*`, así que no ven «Usuarios» |
| `config.editor` | `mto-editor` | lectura; escritura de infraestructura pero no de catálogos (sin `lov-manage`) |
| `config.lector` | `mto-viewer` | solo lectura |
| `almacen.lector` | `mto-warehouse-viewer` | Almacén en solo lectura; nada de configuración ni de usuarios: el menú no ofrece esas pantallas y la URL directa se deniega |
| `almacen.operario` | `mto-warehouse-operator` | Almacén: catálogos, movimientos y reservas, sin ajustes ni cancelaciones |
| `almacen.responsable` | `mto-warehouse-admin` | el módulo Almacén entero (fase 6) |
| `usuarios.responsable` | `mto-users-admin` | el módulo Usuarios entero (fase 5); nada de configuración |
| `usuarios.gestor` | `mto-users-manager` | Usuarios, todo menos borrar |
| `usuarios.lector` | `mto-users-viewer` | Usuarios en solo lectura |

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
| `KEYCLOAK_ROLES_CLIENT_IDS` | Clientes cuyos roles del access token son los permisos (lista por comas) | `mto-configuration-api,mto-users-api,mto-stock-api` |
| `MTO_GATEWAY_URL` | El gateway | `http://localhost:8090` |
| `MTO_GATEWAY_CONNECT_TIMEOUT` / `MTO_GATEWAY_READ_TIMEOUT` | Timeouts del cliente HTTP | `2s` / `15s` |

`.env.example` las recoge todas. Perfiles: `dev` (8085, abre el navegador, secreto local), `test`
(nadie escucha en Keycloak ni en el gateway) y `prod` (realm, secreto y gateway obligatorios).

## Seguridad

- Cliente **confidencial** `mto-backoffice` (Authorization Code con secreto) declarado en
  `keycloak/mto-backoffice-partial-import.json`, con los mismos cinco audience mapper que
  `mto-frontend`, que queda intacto y reservado a una futura SPA. Detalle en `keycloak/README.md`.
- Los permisos son roles de **cliente** de tres clientes: `mto-configuration-api` (`config-read`,
  `config-write`, `config-delete`, `config-import`, `lov-manage`, `config-audit`) para las
  pantallas de configuración, `mto-users-api` (`users-read`, `users-write`, `users-delete`,
  `users-roles-write`, `users-password-reset`, `users-profiles-write`, `users-sessions-write`,
  `users-credentials-write`) para el módulo de usuarios y `mto-stock-api` (`stock-read`,
  `stock-write`, `stock-delete`, `stock-adjust`) para el de almacén. Llegan como `ROLE_CONFIG_READ`,
  `ROLE_USERS_READ`, `ROLE_STOCK_READ`... y se comprueban con `@RolesAllowed` en cada vista; cada
  uno sale además cualificado por su cliente (`ROLE_CLIENT_MTO_USERS_API_USERS_READ`). Que un rol
  de un cliente no se confunda con uno de otro depende de que sus nombres no se solapen, cosa que
  `SecurityLayerTest` comprueba. Los roles de realm (`mto-admin`, `mto-users-manager`...) llegan
  solo como `ROLE_REALM_*`: un perfil nunca se confunde con un permiso.
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

### Límites

- **Todo vive en memoria: la sesión y los tokens.** El cliente autorizado (access y refresh token)
  está en un `InMemoryOAuth2AuthorizedClientService` y la sesión de Vaadin en la JVM. Un reinicio
  los pierde. Lo que pasa entonces es lo menos malo posible: Vaadin detecta que la sesión ya no
  está y **recarga la pantalla** (el aviso de «sesión caducada» está apagado a propósito en
  `BackofficeSystemMessages`), la cadena de seguridad manda al login de Keycloak y, con la sesión
  SSO de Keycloak aún viva, la persona vuelve a la misma pantalla sin escribir nada. Lo que era de
  la sesión (las etiquetas de los trabajos lanzados, los catálogos cargados) se vuelve a cargar; el
  historial de trabajos no se pierde porque es del servicio. No se persisten las sesiones: la
  serialización de una sesión de Vaadin es frágil y el almacén de tokens se perdería igual.
- **Un login por persona.** El almacén guarda un cliente autorizado por nombre de principal: entrar
  desde un segundo navegador sustituye los tokens, y las dos pestañas siguen funcionando con los
  nuevos. Salir en una cierra la sesión de Keycloak, y la otra deja de poder renovar el token: en
  su siguiente llamada se le dice que vuelva a entrar.
- **Una instancia.** Sin sesiones compartidas ni almacén de tokens externo, dos réplicas detrás de
  un balanceador necesitarían afinidad de sesión. No es el caso de uso.

## Cómo se habla con la API

- Un `RestClient` hacia el gateway (`/api/configuration/**` → `/api/v1/configuration/**`,
  `/api/users/**` → `/api/v1/users/**`, `/api/stock/**` → `/api/v1/inventory/**`) con el Bearer de la persona y un `X-Correlation-Id` nuevo
  por llamada, que el gateway acepta y propaga: el mismo id sale en el log del gateway, en el del
  servicio y en la notificación de error que ve la persona.
- Interfaces declarativas `@HttpExchange` escritas a mano, pantalla a pantalla (`client/`). Nada de
  `openapi-generator`: los `/v3/api-docs` no pasan por el gateway y no se van a hacer pantallas
  para los 300 endpoints.
- Los DTO son un **subconjunto**: solo las claves que usa la UI. La paginación de
  `mto-configuration` es la forma DTO `{content, page:{size,number,totalElements,totalPages}}`,
  fijada allí con `spring.data.web.pageable.serialization-mode: via_dto`. La de `mto-users` es la
  de Keycloak: `first`/`max` (`max` ≤ 200) y una página `{content, first, max, total}` solo en la
  búsqueda; las listas de miembros de un perfil o de un rol van planas, sin total. La de `mto-stock`
  es el `Pageable` de Spring por parámetros (`page`, `size`, `sort=campo,asc`, solo atributos de la
  entidad) con la misma página anidada que configuración.
- Errores: `ApiErrorDecoder` entiende el `application/problem+json` de `mto-configuration` (`code`,
  `traceId`, `retryable`, `errors[{field,code,message}]`; sus dos 409 son `CON-001`, un
  `versionNumber` que ya no es el guardado y que se arregla recargando, y `BUS-002`, un valor único
  repetido o una entrada en uso, que no), el de `mto-users` (`errorCode`,
  `correlationId`, `validationErrors[{field,message}]`, `Retry-After` en su 503), el JSON de error
  de `mto-stock` (no es `problem+json`: `error`, `message`, `errorCode`, `correlationId`,
  `validationErrors[{field,message}]`; 409 `STK-001` es falta de stock y un 422 sin campos es una
  regla de negocio), el 401/403 del gateway (solo `correlationId`) y el 503 de su circuit breaker
  (`Retry-After`, `service`), y los convierte en `NotFoundApiException`, `ValidationApiException`, `ForbiddenApiException`,
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
