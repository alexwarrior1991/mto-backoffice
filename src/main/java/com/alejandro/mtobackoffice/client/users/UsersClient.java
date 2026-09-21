package com.alejandro.mtobackoffice.client.users;

import com.alejandro.mtobackoffice.client.dto.users.ClientDto;
import com.alejandro.mtobackoffice.client.dto.users.ClientRoleDto;
import com.alejandro.mtobackoffice.client.dto.users.CreateUserRequest;
import com.alejandro.mtobackoffice.client.dto.users.ExecuteActionsEmailRequest;
import com.alejandro.mtobackoffice.client.dto.users.RealmProfileDto;
import com.alejandro.mtobackoffice.client.dto.users.RealmProfileSummaryDto;
import com.alejandro.mtobackoffice.client.dto.users.ResetPasswordRequest;
import com.alejandro.mtobackoffice.client.dto.users.RoleNamesRequest;
import com.alejandro.mtobackoffice.client.dto.users.UpdateUserRequest;
import com.alejandro.mtobackoffice.client.dto.users.UserCredentialDto;
import com.alejandro.mtobackoffice.client.dto.users.UserDto;
import com.alejandro.mtobackoffice.client.dto.users.UserEnabledRequest;
import com.alejandro.mtobackoffice.client.dto.users.UserRolesDto;
import com.alejandro.mtobackoffice.client.dto.users.UserSessionDto;
import com.alejandro.mtobackoffice.client.dto.users.UsersPage;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PatchExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

import java.util.List;

/**
 * Los usuarios, roles de cliente y perfiles del realm, administrados por mto-users a traves del
 * gateway ({@code /api/users/**}, que el gateway reescribe a {@code /api/v1/users/**}). Keycloak es
 * la unica fuente: nada se persiste aqui ni alli.
 *
 * <p>Lo que hay que saber de la API (README de mto-users):</p>
 * <ul>
 *   <li>La paginacion es la de Keycloak, {@code first}/{@code max} con {@code max} de 200 como
 *       mucho, y solo la busqueda devuelve pagina ({@link UsersPage}, con total). Las listas de
 *       miembros de un perfil o de un rol son arrays planos <b>sin total</b>: Keycloak no cuenta.</li>
 *   <li>{@code search} no se combina con {@code attribute} (400 {@code SEARCH-400}): Keycloak
 *       aplicaria la busqueda y tiraria el atributo. Tampoco se repite una clave de atributo.</li>
 *   <li>Los miembros son asignaciones directas: quien tiene un rol por un perfil aparece en el
 *       perfil, no en el rol. Los clientes protegidos ({@code realm-management}...) nunca se
 *       listan ni se asignan.</li>
 *   <li>Quitar roles es un {@code DELETE} <b>con cuerpo</b> ({@code {"roles":[...]}}); anadirlos,
 *       un {@code PUT} aditivo. Los dos devuelven las asignaciones actualizadas.</li>
 *   <li>Desactivar a alguien no cierra sus sesiones ni revoca sus tokens offline: sacar a una
 *       persona son tres llamadas, en este orden: {@link #setEnabled}, {@link #revokeAllSessions},
 *       {@link #revokeAllOfflineSessions}.</li>
 *   <li>Errores en {@code application/problem+json} con {@code errorCode} y
 *       {@code validationErrors[{field, message}]}; un 503 {@code KC-503} trae {@code Retry-After}.</li>
 * </ul>
 *
 * <p>Permisos del servicio (roles de cliente de {@code mto-users-api}): leer, {@code users-read};
 * alta, modificacion, activar/desactivar y correo de acciones, {@code users-write}; borrar,
 * {@code users-delete}; roles, {@code users-roles-write}; perfiles, {@code users-profiles-write};
 * contrasena temporal, {@code users-password-reset}; sesiones, {@code users-sessions-write};
 * credenciales, {@code users-credentials-write}. Ninguno implica otro.</p>
 */
@HttpExchange("/api/users")
public interface UsersClient {

    // --- Usuarios -------------------------------------------------------------------------------

    /** La busqueda, paginada con {@code first}/{@code max}; un filtro ausente no filtra. */
    @GetExchange
    UsersPage<UserDto> search(@RequestParam(value = "search", required = false) String search,
                              @RequestParam(value = "username", required = false) String username,
                              @RequestParam(value = "email", required = false) String email,
                              @RequestParam(value = "enabled", required = false) Boolean enabled,
                              @RequestParam(value = "emailVerified", required = false) Boolean emailVerified,
                              @RequestParam(value = "attribute", required = false) List<String> attributes,
                              @RequestParam("first") int first,
                              @RequestParam("max") int max);

    /** 201 con el usuario creado (y su id). */
    @PostExchange
    UserDto create(@RequestBody CreateUserRequest request);

    @GetExchange("/{userId}")
    UserDto get(@PathVariable("userId") String userId);

    /** Parcial: lo que va a {@code null} se deja como esta; el nombre de usuario no cambia. */
    @PutExchange("/{userId}")
    UserDto update(@PathVariable("userId") String userId, @RequestBody UpdateUserRequest request);

    @PatchExchange("/{userId}/enabled")
    UserDto setEnabled(@PathVariable("userId") String userId, @RequestBody UserEnabledRequest request);

    /** 204. Irreversible: se lleva roles, perfiles, sesiones y credenciales. */
    @DeleteExchange("/{userId}")
    void delete(@PathVariable("userId") String userId);

    /** 204. La politica de contrasenas del realm llega como 400 {@code KC-400}. */
    @PostExchange("/{userId}/reset-password")
    void resetPassword(@PathVariable("userId") String userId, @RequestBody ResetPasswordRequest request);

    /** 202. Sin SMTP en el realm, 502 {@code KC-502}. */
    @PostExchange("/{userId}/execute-actions-email")
    void executeActionsEmail(@PathVariable("userId") String userId, @RequestBody ExecuteActionsEmailRequest request);

    @GetExchange("/{userId}/sessions")
    List<UserSessionDto> sessions(@PathVariable("userId") String userId);

    /** 204, idempotente. No toca las sesiones offline. */
    @DeleteExchange("/{userId}/sessions")
    void revokeAllSessions(@PathVariable("userId") String userId);

    /** 204; 404 {@code SES-404} si la sesion no es de ese usuario. */
    @DeleteExchange("/{userId}/sessions/{sessionId}")
    void revokeSession(@PathVariable("userId") String userId, @PathVariable("sessionId") String sessionId);

    @GetExchange("/{userId}/offline-sessions")
    List<UserSessionDto> offlineSessions(@PathVariable("userId") String userId);

    @DeleteExchange("/{userId}/offline-sessions")
    void revokeAllOfflineSessions(@PathVariable("userId") String userId);

    @DeleteExchange("/{userId}/offline-sessions/{sessionId}")
    void revokeOfflineSession(@PathVariable("userId") String userId, @PathVariable("sessionId") String sessionId);

    /** Sin secretos ni forma de almacenamiento: tipo, etiqueta y fecha. */
    @GetExchange("/{userId}/credentials")
    List<UserCredentialDto> credentials(@PathVariable("userId") String userId);

    @DeleteExchange("/{userId}/credentials/{credentialId}")
    void deleteCredential(@PathVariable("userId") String userId, @PathVariable("credentialId") String credentialId);

    // --- Roles de cliente -----------------------------------------------------------------------

    /** Los clientes del realm menos los protegidos, por {@code clientId}. */
    @GetExchange("/roles/clients")
    List<ClientDto> clients();

    @GetExchange("/roles/clients/{clientId}")
    List<ClientRoleDto> clientRoles(@PathVariable("clientId") String clientId);

    /** Quien tiene el rol asignado directamente; lista plana, sin total. */
    @GetExchange("/roles/clients/{clientId}/{roleName}/users")
    List<UserDto> clientRoleMembers(@PathVariable("clientId") String clientId, @PathVariable("roleName") String roleName,
                                    @RequestParam("first") int first, @RequestParam("max") int max);

    @GetExchange("/{userId}/roles")
    UserRolesDto userRoles(@PathVariable("userId") String userId);

    /** Aditivo; devuelve las asignaciones actualizadas. */
    @PutExchange("/{userId}/roles/clients/{clientId}")
    UserRolesDto addClientRoles(@PathVariable("userId") String userId, @PathVariable("clientId") String clientId,
                                @RequestBody RoleNamesRequest roles);

    /** Un {@code DELETE} con cuerpo: los nombres a quitar viajan en el JSON. */
    @DeleteExchange("/{userId}/roles/clients/{clientId}")
    UserRolesDto removeClientRoles(@PathVariable("userId") String userId, @PathVariable("clientId") String clientId,
                                   @RequestBody RoleNamesRequest roles);

    // --- Perfiles (roles compuestos de realm con prefijo mto-) ------------------------------------

    @GetExchange("/profiles")
    List<RealmProfileSummaryDto> profiles();

    /** Lo que concede: roles por cliente y roles de realm. */
    @GetExchange("/profiles/{profileName}")
    RealmProfileDto profile(@PathVariable("profileName") String profileName);

    /** Quien tiene el perfil; lista plana, sin total. */
    @GetExchange("/profiles/{profileName}/users")
    List<UserDto> profileMembers(@PathVariable("profileName") String profileName,
                                 @RequestParam("first") int first, @RequestParam("max") int max);

    @GetExchange("/{userId}/profiles")
    List<RealmProfileSummaryDto> userProfiles(@PathVariable("userId") String userId);

    /** Sin cuerpo, idempotente; devuelve los perfiles del usuario. */
    @PutExchange("/{userId}/profiles/{profileName}")
    List<RealmProfileSummaryDto> assignProfile(@PathVariable("userId") String userId, @PathVariable("profileName") String profileName);

    @DeleteExchange("/{userId}/profiles/{profileName}")
    List<RealmProfileSummaryDto> removeProfile(@PathVariable("userId") String userId, @PathVariable("profileName") String profileName);
}
