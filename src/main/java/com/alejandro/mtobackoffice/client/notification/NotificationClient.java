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
import com.alejandro.mtobackoffice.client.dto.notification.AdminNotificationDto;
import com.alejandro.mtobackoffice.client.dto.notification.BroadcastRequest;
import com.alejandro.mtobackoffice.client.dto.notification.DeadLetterPeekDto;
import com.alejandro.mtobackoffice.client.dto.notification.DeadLetterQueuesDto;
import com.alejandro.mtobackoffice.client.dto.notification.DeadLetterReprocessDto;
import com.alejandro.mtobackoffice.client.dto.notification.DeliveryDto;
import com.alejandro.mtobackoffice.client.dto.notification.DeliveryFilter;
import com.alejandro.mtobackoffice.client.dto.notification.DeliveryStatus;
import com.alejandro.mtobackoffice.client.dto.notification.InboxFilter;
import com.alejandro.mtobackoffice.client.dto.notification.InboxItemDto;
import com.alejandro.mtobackoffice.client.dto.notification.ReadAllDto;
import com.alejandro.mtobackoffice.client.dto.notification.RulesDto;
import com.alejandro.mtobackoffice.client.dto.notification.SourceStatusDto;
import com.alejandro.mtobackoffice.client.dto.notification.TestEmailRequest;
import com.alejandro.mtobackoffice.client.dto.notification.UnreadCountDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * La API de mto-notification a traves del gateway ({@code /api/notifications/**}, que el gateway
 * reescribe a {@code /api/v1/notifications/**}): mi bandeja, el registro de actividad, los accesos y
 * la administracion del servicio ({@code /admin/**}, toda con {@code notification-admin}).
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

    // --- La administracion (notification-admin) -----------------------------------------------------

    /** El desempate de las entregas: la fecha de creacion, porque el servicio no deja ordenar por el id. */
    String DELIVERIES_TIE_BREAK = "createdAt,desc";

    /** Las reglas cargadas, de donde salieron y sus variables. */
    @GetExchange("/admin/rules")
    RulesDto adminRules();

    /** Marcas del lector de Keycloak, inbox por fuente y estado, rafagas abiertas y entregas por estado. */
    @GetExchange("/admin/sources")
    SourceStatusDto adminSources();

    /** {@code sort} admite {@code createdAt}, {@code status}, {@code nextAttemptAt}, {@code channel} y {@code sentAt}. */
    @GetExchange("/admin/deliveries")
    PageResponse<DeliveryDto> adminDeliveries(@RequestParam(value = "status", required = false) DeliveryStatus status,
                                              @RequestParam(value = "channel", required = false) String channel,
                                              @RequestParam(value = "notificationId", required = false) UUID notificationId,
                                              @RequestParam("page") int page, @RequestParam("size") int size,
                                              @RequestParam("sort") List<String> sort);

    default PageResponse<DeliveryDto> adminDeliveries(DeliveryFilter filter, int page, int size, List<String> sort) {
        return adminDeliveries(filter.status(), filter.channel(), filter.notificationId(), page, size,
                MasterFilters.withTieBreak(sort, DELIVERIES_TIE_BREAK));
    }

    /** Una fallida u omitida vuelve a la cola; 404 {@code DLV-404}, y 409 {@code DLV-409} en otro estado. */
    @PostExchange("/admin/deliveries/{id}/retry")
    DeliveryDto retryDelivery(@PathVariable("id") UUID id);

    /** Cuantos mensajes guarda la DLQ de cada fuente; 503 {@code SRC-503} si el broker no responde. */
    @GetExchange("/admin/dead-letters")
    DeadLetterQueuesDto deadLetterQueues();

    /**
     * Los primeros {@code max} mensajes (entre 1 y 100) de la DLQ de una fuente, que siguen en ella. 404
     * {@code SRC-404} con una fuente que no existe, 409 {@code SRC-409} con el broker apagado o sin esa
     * cola, 503 {@code SRC-503} si el broker no responde.
     */
    @GetExchange("/admin/dead-letters/{source}")
    DeadLetterPeekDto peekDeadLetters(@PathVariable("source") String source, @RequestParam("max") int max);

    /** Pasa hasta {@code max} mensajes (entre 1 y 100) por el consumidor de la fuente; los mismos errores que mirar. */
    @PostExchange("/admin/dead-letters/{source}/reprocess")
    DeadLetterReprocessDto reprocessDeadLetters(@PathVariable("source") String source, @RequestParam("max") int max);

    /** 202: la notificacion de prueba para quien llama, con una entrega directa a la direccion. */
    @PostExchange("/admin/test-email")
    AdminNotificationDto sendTestEmail(@RequestBody TestEmailRequest request);

    /** 201: un aviso manual; 422 {@code NTF-422} con una clase de audiencia o un canal desconocidos. */
    @PostExchange("/admin/broadcasts")
    AdminNotificationDto broadcast(@RequestBody BroadcastRequest request);
}
