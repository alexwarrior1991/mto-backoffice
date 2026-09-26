# Lo que `mto-backoffice` aporta al realm

Dos ficheros, que aplica `mto-platform/keycloak/apply-partials.sh` en su sitio del orden de ensamblado:

| Fichero | Qué contiene | Dónde vale |
|---|---|---|
| `mto-backoffice-partial-import.json` | El cliente `mto-backoffice` | Cualquier entorno |
| `mto-backoffice-dev.json` | Su secreto local | Solo local |

## Qué hay dentro

Un solo cliente, y sin roles: los permisos que comprueba el backoffice son los roles de cliente de
cuatro clientes, cada uno declarado por su servicio (`app.keycloak.roles-client-ids`):

| Cliente | Roles | Lo declara |
|---|---|---|
| `mto-configuration-api` | `config-read`, `config-write`, `config-delete`, `config-import`, `lov-manage`, `config-audit` | `mto-configuration` |
| `mto-users-api` | `users-read`, `users-write`, `users-delete`, `users-roles-write`, `users-password-reset`, `users-profiles-write`, `users-sessions-write`, `users-credentials-write` | `mto-users` |
| `mto-stock-api` | `stock-read`, `stock-write`, `stock-delete`, `stock-adjust` | `mto-stock` |
| `mto-maintenance-api` | `maintenance-read`, `maintenance-write`, `maintenance-delete`, `maintenance-supervise` | `mto-maintenance` |

Los perfiles de realm que reúnen esos roles también son de cada servicio. Los tres de
mantenimiento (`mto-maintenance-viewer`, `-technician` y `-manager`) llevan además `config-read` y
`stock-read`, porque sus pantallas nombran vías, paquetes, materiales, almacenes y proyectos que
`mto-maintenance` solo guarda como id; sin ellos, el backoffice pintaría `#id`.

| Cliente | Tipo | Para qué |
|---|---|---|
| `mto-backoffice` | Confidencial, Authorization Code con secreto (`publicClient: false`, `standardFlowEnabled: true`) | Con él entra la persona. El token vive en la sesión de servidor del backoffice, nunca en el navegador |

- `redirectUris`: `http://localhost:8085/login/oauth2/code/keycloak` (el callback de Spring para el
  registro `keycloak`). El puerto es el 8085 porque los demás están ocupados.
- `post.logout.redirect.uris`: `http://localhost:8085/*`, para que «Salir» pueda volver a la
  aplicación después de cerrar la sesión de Keycloak (`end_session_endpoint`).
- Cinco **audience mapper** (`oidc-audience-mapper`, `access.token.claim=true`), copiados de
  `mto-frontend`: `mto-configuration-api`, `mto-stock-api`, `mto-maintenance-api`, `mto-users-api`
  y `mto-gateway-api`. Sin el de un servicio, ese servicio responde 401 a todo: su
  `JwtAudienceValidator` exige su `clientId` en `aud`. `mto-platform/scripts/check_realm_consistency.py`
  comprueba que todo cliente de login lleve los cinco.
- Sin PKCE: es un cliente confidencial con secreto. Se puede añadir (`pkce.code.challenge.method`
  en el cliente y `withPkce()` en Spring), pero entonces hay que hacerlo en los dos sitios.

`mto-frontend` (público, PKCE, `localhost:4200`) queda intacto: está reservado a una futura SPA.

## La trampa de los roles

Keycloak pone `resource_access` (los roles de cliente) **solo en el access token**; los mappers de
audiencia son `id.token.claim=false` y el mapper de roles del scope `roles` también. `oauth2Login`
construye las autoridades desde el ID token, así que sin `BackofficeOidcUserService` —que verifica
el access token y saca de él los roles— ninguna vista vería un permiso. Y los roles de realm
(`mto-admin`, `mto-editor`...) se emiten solo como `ROLE_REALM_*`, nunca como `ROLE_*`: un perfil
de realm creado con el nombre de un permiso no concede nada.

## Cómo cargarlo

En local lo hace `mto-platform/keycloak/apply-partials.sh` (con `--no-dev-users` no aplica el
secreto). La parcial no lleva `ifResourceExists`: el script la importa con `OVERWRITE`.

El `-dev.json` no se importa tal cual. Solo trae `clientId` y `secret`, y una importación con
`OVERWRITE` sustituiría el cliente entero; el script pone el secreto sobre el cliente ya importado
(un `GET` del cliente y un `PUT` con el secreto), así que el cliente conserva el redirect URI, el
post-logout y los cinco mappers. Hasta mto-platform#14 no era así, y en local el login del
backoffice no podía funcionar. Ahora el CI de `mto-platform` lo comprueba sobre un Keycloak de
verdad (`scripts/check_applied_realm.py`): ya no hace falta mirarlo en la consola.

En un entorno desplegado: importar la parcial, poner el redirect URI real en lugar de `localhost:8085`
y copiar el secreto de Clients → `mto-backoffice` → *Credentials* a `KEYCLOAK_CLIENT_SECRET`.
