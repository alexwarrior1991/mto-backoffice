package com.alejandro.mtobackoffice.client.notification;

import com.alejandro.mtobackoffice.client.configuration.MasterFilters;
import com.alejandro.mtobackoffice.client.dto.PageResponse;
import com.alejandro.mtobackoffice.client.dto.notification.AccessEventDto;
import com.alejandro.mtobackoffice.client.dto.notification.AccessFilter;
import com.alejandro.mtobackoffice.client.dto.notification.AccessOutcome;
import com.alejandro.mtobackoffice.client.dto.notification.ActivityCategory;
import com.alejandro.mtobackoffice.client.dto.notification.ActivityEventDto;
import com.alejandro.mtobackoffice.client.dto.notification.ActivityFilter;
import com.alejandro.mtobackoffice.client.dto.notification.ActivitySeverity;
import com.alejandro.mtobackoffice.client.dto.notification.InboxFilter;
import com.alejandro.mtobackoffice.client.dto.notification.InboxItemDto;
import com.alejandro.mtobackoffice.client.dto.notification.ReadAllDto;
import com.alejandro.mtobackoffice.client.dto.notification.UnreadCountDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * La API de mto-notification a traves del gateway ({@code /api/notifications/**}, que el gateway
 * reescribe a {@code /api/v1/notifications/**}): mi bandeja, el registro de actividad y los accesos.
 *
 * <p>Lo que hay que saber de la API (README y {@code docs/04-rest-api.md} de mto-notification):</p>
 * <ul>
 *   <li>El permiso lo decide el recurso, no el verbo, y ninguno implica otro: la bandeja entera
 *       ({@code GET} y las marcas) pide {@code notification-inbox}; el registro,
 *       {@code notification-activity-read}; los accesos, {@code notification-access-read}, que no
 *       viene con el registro porque llevan usuario e IP.</li>
 *   <li>A quien va cada notificacion se resuelve <b>al leer, con el token</b> (usuario, perfiles y
 *       roles de cliente): la bandeja es siempre la de la persona que llama. Marcar una que no es
 *       mia es 404 {@code NTF-404}, y «marcar todas» va hasta la mas reciente visible, no hasta
 *       ahora.</li>
 *   <li>El contador de la campana esta <b>acotado</b>: con {@code capped} el numero es «ese o mas».</li>
 *   <li>Los accesos nunca salen por el registro: {@code category=ACCESS} es un 400. Lo fundido (el
 *       evento de administracion de Keycloak que ya cuenta el de mto-users del mismo cambio) se
 *       esconde salvo con {@code includeSuperseded=true}.</li>
 *   <li>Paginacion {@code page}/{@code size}/{@code sort=campo,dir} con la pagina anidada que
 *       {@link PageResponse} ya lee; un {@code sort} desconocido es 400 {@code REQ-400}. La
 *       bandeja ordena por {@code createdAt}, {@code severity}, {@code category} y {@code title}; el
 *       registro y los accesos, por {@code occurredAt}, {@code recordedAt}, {@code severity},
 *       {@code type} y {@code seq}. El estado de lectura es de cada persona y no se ordena por el.</li>
 *   <li>Errores con el JSON de mto-maintenance ({@code errorCode}, {@code message},
 *       {@code validationErrors[{field, message}]}), que {@code ApiProblem} lee por alias.</li>
 * </ul>
 */
@HttpExchange("/api/notifications")
public interface NotificationClient {

    /** El desempate de la bandeja, como en mto-frontend: la fecha, porque el id no se puede ordenar. */
    String INBOX_TIE_BREAK = "createdAt,desc";
    /** El del registro y los accesos: la secuencia, que no se repite. */
    String LOG_TIE_BREAK = "seq,desc";

    // --- Mi bandeja -------------------------------------------------------------------------------

    @GetExchange("/inbox")
    PageResponse<InboxItemDto> inbox(@RequestParam(value = "unread", required = false) Boolean unread,
                                     @RequestParam(value = "category", required = false) ActivityCategory category,
                                     @RequestParam(value = "severity", required = false) ActivitySeverity severity,
                                     @RequestParam(value = "from", required = false) Instant from,
                                     @RequestParam(value = "to", required = false) Instant to,
                                     @RequestParam("page") int page, @RequestParam("size") int size,
                                     @RequestParam("sort") List<String> sort);

    /** La bandeja desempata por la fecha de creacion: el servicio no la deja ordenar por el id. */
    default PageResponse<InboxItemDto> inbox(InboxFilter filter, int page, int size, List<String> sort) {
        return inbox(filter.unread(), filter.category(), filter.severity(), filter.from(), filter.to(), page, size,
                MasterFilters.withTieBreak(sort, INBOX_TIE_BREAK));
    }

    /** Para la campana; acotado. */
    @GetExchange("/inbox/unread-count")
    UnreadCountDto unreadCount();

    /** Devuelve la notificacion ya leida; 404 {@code NTF-404} si no va dirigida a mi. */
    @PostExchange("/inbox/{id}/read")
    InboxItemDto markRead(@PathVariable("id") UUID id);

    @PostExchange("/inbox/read-all")
    ReadAllDto markAllRead();

    // --- El registro ------------------------------------------------------------------------------

    @GetExchange("/activity")
    PageResponse<ActivityEventDto> activity(@RequestParam(value = "category", required = false) ActivityCategory category,
                                            @RequestParam(value = "type", required = false) String type,
                                            @RequestParam(value = "actorUsername", required = false) String actorUsername,
                                            @RequestParam(value = "subjectType", required = false) String subjectType,
                                            @RequestParam(value = "subjectId", required = false) String subjectId,
                                            @RequestParam(value = "severity", required = false) ActivitySeverity severity,
                                            @RequestParam(value = "sourceService", required = false) String sourceService,
                                            @RequestParam(value = "from", required = false) Instant from,
                                            @RequestParam(value = "to", required = false) Instant to,
                                            @RequestParam(value = "includeSuperseded", required = false) Boolean includeSuperseded,
                                            @RequestParam("page") int page, @RequestParam("size") int size,
                                            @RequestParam("sort") List<String> sort);

    /** El registro desempata por su secuencia, que no se repite. */
    default PageResponse<ActivityEventDto> activity(ActivityFilter filter, int page, int size, List<String> sort) {
        return activity(filter.category(), filter.type(), filter.actorUsername(), filter.subjectType(), filter.subjectId(),
                filter.severity(), filter.sourceService(), filter.from(), filter.to(), filter.includeSuperseded() ? Boolean.TRUE : null,
                page, size, MasterFilters.withTieBreak(sort, LOG_TIE_BREAK));
    }

    /** Una linea con su {@code payload}; 404 {@code ACT-404} si no existe o es un acceso. */
    @GetExchange("/activity/{id}")
    ActivityEventDto activityEvent(@PathVariable("id") UUID id);

    // --- Los accesos ------------------------------------------------------------------------------

    @GetExchange("/access")
    PageResponse<AccessEventDto> access(@RequestParam(value = "username", required = false) String username,
                                        @RequestParam(value = "ipAddress", required = false) String ipAddress,
                                        @RequestParam(value = "type", required = false) String type,
                                        @RequestParam(value = "outcome", required = false) AccessOutcome outcome,
                                        @RequestParam(value = "from", required = false) Instant from,
                                        @RequestParam(value = "to", required = false) Instant to,
                                        @RequestParam("page") int page, @RequestParam("size") int size,
                                        @RequestParam("sort") List<String> sort);

    /** Los accesos tambien desempatan por su secuencia. */
    default PageResponse<AccessEventDto> access(AccessFilter filter, int page, int size, List<String> sort) {
        return access(filter.username(), filter.ipAddress(), filter.type(), filter.outcome(), filter.from(), filter.to(), page, size,
                MasterFilters.withTieBreak(sort, LOG_TIE_BREAK));
    }
}
