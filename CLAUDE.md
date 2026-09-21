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
- `client` — las interfaces `@HttpExchange` por servicio (`client/configuration/LovClient`, los
  ocho endpoints de `AbstractLovController` parametrizados por recurso; `LovResource`, los 17
  catálogos con su ruta y su título), los DTO (`client/dto`: solo las claves que usa la UI,
  `@JsonInclude(NON_NULL)` para no enviar lo que no se rellena, `PageResponse<T>` con la forma
  `{content, page}`) y los errores (`client/error`: `ApiProblem`, `ApiErrorDecoder` y la jerarquía
  `BackofficeApiException`).
- `ui` — `MainLayout` (AppLayout; el menú lo dan las vistas anotadas con `@Menu`, filtradas por
  `AccessAnnotationChecker`, más el grupo «Catalogos» construido a mano porque la vista de catálogos
  lleva el recurso en la ruta), `ui/views/HomeView`, `ui/lov` (`LovCrudView` en
  `catalogos/:resource`, `LovEditorDialog` con `Binder` sobre el modelo mutable `LovForm`,
  `LovBulkCreateDialog` con su parser de líneas), `ui/support` (`UiErrors`: excepción →
  `Notification`; `ServerValidation`: `errors[]` del servicio → campos del `Binder`).

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
- **Nada de componentes de pago.** `vaadin-spring-boot-starter` trae solo `vaadin-core-internal`.

### Tests

Una clase por capa; se añaden métodos, no clases: `ClientLayerTest` (interfaces `@HttpExchange` y
`RestClient` reales contra `MockRestServiceServer`: prefijo del gateway, Bearer y correlación, forma
de página, `problem+json` de configuration, 401/403 y 503 del gateway, cuerpo no JSON),
`SecurityLayerTest` (mapeo de roles, registro OIDC sin descubrimiento, roles desde el access token,
`CurrentPrincipal`), `ViewLayerTest` (Karibu-Testing 2.7.3 sobre el contexto de Spring: el catálogo
de la ruta y su filtro local, menú por roles, controles de escritura ocultos sin permiso, alta por
diálogo, errores del servicio campo a campo, borrado con confirmación, lote sobre la selección,
parser del alta múltiple, notificación de error, diagnóstico de audiencias) y
`MtoBackofficeApplicationTests` (contexto completo sin Keycloak ni gateway; redirección al login;
sonda de salud; ausencia de artefactos comerciales). Todo corre en la JVM sin Docker.
