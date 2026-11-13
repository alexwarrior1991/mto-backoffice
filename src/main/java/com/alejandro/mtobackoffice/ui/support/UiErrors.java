package com.alejandro.mtobackoffice.ui.support;

import com.alejandro.mtobackoffice.client.error.ApiFieldError;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.error.ConflictApiException;
import com.alejandro.mtobackoffice.client.error.ForbiddenApiException;
import com.alejandro.mtobackoffice.client.error.NotFoundApiException;
import com.alejandro.mtobackoffice.client.error.ServiceUnavailableApiException;
import com.alejandro.mtobackoffice.client.error.SessionExpiredApiException;
import com.alejandro.mtobackoffice.client.error.TooManyRequestsApiException;
import com.alejandro.mtobackoffice.client.error.UnreachableApiException;
import com.alejandro.mtobackoffice.client.error.ValidationApiException;
import com.alejandro.mtobackoffice.configuration.security.SecurityConfiguration;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Traduce un fallo de la API a algo que una persona pueda entender y, si hay algo que hacer, lo
 * ofrece: volver a entrar cuando la sesion ha caducado, reintentar cuando el servicio no esta.
 */
public final class UiErrors {

    private static final int DURATION_MS = 8000;
    /** Los 409 de un codigo repetido: mantenimiento (activos, equipos) y los cinco catalogos de almacen. */
    private static final Set<String> DUPLICATED_CODES = Set.of("AST-409", "TEA-409", "MAT-409", "WH-409", "SUP-409", "PRJ-409", "ASM-409");
    /**
     * Los 409 de un valor repetido en mto-resources: ademas del codigo, un usuario o un numero de
     * empleado (personas), una matricula o un numero ferroviario (vehiculos). El detalle dice cual.
     */
    private static final Set<String> RESOURCES_DUPLICATED = Set.of("PER-409", "UNI-409", "VEH-409", "VTY-409", "PRK-409");
    /**
     * Los 404 de mto-resources de lo que cuelga de una ficha o de un catalogo, cada uno dicho por lo que es;
     * un punto de acceso a la via, ademas, puede no haber llegado todavia por evento desde mto-configuration.
     */
    private static final Map<String, String> RESOURCES_MISSING = Map.of(
            "QUA-404", "Esa cualificacion ya no existe.",
            "VDO-404", "Ese documento del vehiculo ya no existe.",
            "INT-404", "Ese instrumento no existe.",
            "QTY-404", "Ese tipo de cualificacion no existe.",
            "TAP-404", "Ese punto de acceso ya no existe o aun no ha llegado a Recursos.",
            "ABS-404", "Esa ausencia ya no existe.",
            "REP-404", "Esa reparacion ya no existe.");

    private UiErrors() {
    }

    public static Notification show(BackofficeApiException exception) {
        return show(exception, message(exception));
    }

    /**
     * Como {@link #show(BackofficeApiException)}, con un texto propio en vez del mensaje del fallo
     * (que lo diga la pantalla, con su contexto), y la misma referencia y el mismo «Volver a entrar».
     */
    public static Notification show(BackofficeApiException exception, String text) {
        Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.setPosition(Notification.Position.BOTTOM_START);
        notification.setDuration(exception instanceof SessionExpiredApiException ? 0 : DURATION_MS);

        Div content = new Div(new Span(text));
        String reference = exception.getReference();
        if (reference != null && !reference.isBlank()) {
            Span ref = new Span("Referencia: " + reference);
            ref.getStyle().set("font-size", "var(--lumo-font-size-xs)");
            content.add(new Div(ref));
        }
        HorizontalLayout layout = new HorizontalLayout(content);
        layout.setAlignItems(HorizontalLayout.Alignment.CENTER);
        if (exception instanceof SessionExpiredApiException) {
            layout.add(new Button("Volver a entrar", click -> {
                notification.close();
                UI.getCurrent().getPage().setLocation(SecurityConfiguration.LOGIN_URL);
            }));
        }
        notification.add(layout);
        notification.open();
        return notification;
    }

    /** Texto para la persona. Se prueba aparte de la notificacion. */
    public static String message(BackofficeApiException exception) {
        return switch (exception) {
            case SessionExpiredApiException ignored -> "La sesion ha caducado. Hay que volver a entrar.";
            // El gateway no ha contestado (conexion rechazada, timeout): no hay estado ni detalle que
            // decir. Es el NetworkError de mto-frontend, con su mismo mensaje.
            case UnreachableApiException ignored ->
                    "No se ha podido contactar con el servidor. Comprueba la conexion y vuelve a intentarlo.";
            // mto-maintenance: el permiso esta, pero lo que se escribe es de un EP fuera del ambito de la
            // persona. No es «no tienes permiso»: el detalle del servicio dice que EP es y cuales son los suyos.
            case ForbiddenApiException scope when "SCP-403".equals(scope.getProblem().code()) ->
                    "Queda fuera de tu ambito." + detail(exception);
            // mto-users: el permiso esta, lo que no vale es el objetivo. Una cuenta de servicio solo se lee
            // (y su prefijo esta reservado en el alta), y nadie se cambia sus roles o perfiles, se desactiva
            // ni se borra. Ninguno se arregla recargando ni pidiendo otro permiso.
            case ForbiddenApiException account when "USR-SVC-403".equals(account.getProblem().code()) ->
                    "Una cuenta de servicio no se modifica desde aqui: se administra con el realm." + detail(exception);
            case ForbiddenApiException self when "USR-SELF-403".equals(self.getProblem().code()) ->
                    "Nadie cambia sus propios permisos ni se saca a si mismo: tiene que hacerlo otra persona." + detail(exception);
            case ForbiddenApiException ignored -> "No tienes permiso para esta operacion.";
            // mto-notification: lo que ya no es de la persona o ya no existe. Se dice asi, como en
            // mto-frontend, sin el detalle del servicio (que nombra ids y usuarios en ingles).
            case NotFoundApiException inbox when "NTF-404".equals(inbox.getProblem().code()) ->
                    "Esa notificacion ya no existe o no va dirigida a ti.";
            case NotFoundApiException line when "ACT-404".equals(line.getProblem().code()) ->
                    "Esa linea del registro ya no existe.";
            // La administracion de mto-notification: una entrega que ya se purgo, o una fuente de RabbitMQ
            // que el servicio no tiene configurada.
            case NotFoundApiException delivery when "DLV-404".equals(delivery.getProblem().code()) ->
                    "Esa entrega ya no existe.";
            case NotFoundApiException source when "SRC-404".equals(source.getProblem().code()) ->
                    "Esa fuente no existe en mto-notification.";
            // mto-users: cerrar una sesion o quitar una credencial que ya no esta, o que es de otra persona
            // (el servicio lo comprueba antes). Sin su detalle, que nombra ids.
            case NotFoundApiException session when "SES-404".equals(session.getProblem().code()) ->
                    "Esa sesion ya no existe o no es de este usuario.";
            case NotFoundApiException credential when "CRED-404".equals(credential.getProblem().code()) ->
                    "Esa credencial ya no existe o no es de este usuario.";
            // mto-maintenance: una persona o un vehiculo de mto-resources que todavia no ha llegado por
            // evento a mantenimiento (se acaba de dar de alta alli). Llega en unos segundos: reintentar vale.
            case NotFoundApiException crew when "CRW-404".equals(crew.getProblem().code()) ->
                    "Esa persona aun no ha llegado a mantenimiento desde mto-resources: intentalo en un momento.";
            case NotFoundApiException fleet when "FLT-404".equals(fleet.getProblem().code()) ->
                    "Ese vehiculo aun no ha llegado a mantenimiento desde mto-resources: intentalo en un momento.";
            case NotFoundApiException instrument when "MSI-404".equals(instrument.getProblem().code()) ->
                    "Ese instrumento aun no ha llegado a mantenimiento desde mto-resources: intentalo en un momento.";
            // mto-maintenance: una version de una plantilla de inspeccion que el servicio no conoce. Nada se
            // borra alli, asi que es un id de otro entorno o una lista leida de otro despliegue: releerla.
            case NotFoundApiException template when "TPL-404".equals(template.getProblem().code()) ->
                    "Esa version de la plantilla de inspeccion no existe: recarga la lista.";
            // mto-resources: lo que ya no existe (otra persona lo quito entre medias) de las cualificaciones,
            // los documentos y los instrumentos. Se dice que es, sin el detalle del servicio, que nombra ids.
            case NotFoundApiException missing when missing.getProblem().code() != null
                    && RESOURCES_MISSING.containsKey(missing.getProblem().code()) -> RESOURCES_MISSING.get(missing.getProblem().code());
            case NotFoundApiException ignored -> "No se ha encontrado lo que se pedia."
                    + detail(exception);
            // mto-maintenance: la inspeccion no casa con su checklist (un OK con items defectuosos, un
            // defecto que no hay que registrar...). Es un 422 sin campos, pero dice algo mas concreto.
            case ValidationApiException inspection when "INS-001".equals(inspection.getProblem().code()) ->
                    "La inspeccion o su checklist no admiten esta operacion." + detail(exception);
            // mto-maintenance: mto-stock ha dicho que no al sincronizar una linea de material (un material
            // o un almacen retirado...). La linea queda rechazada, con el motivo que viene en el detalle.
            case ValidationApiException stock when "STK-422".equals(stock.getProblem().code()) ->
                    "El almacen ha rechazado la operacion." + detail(exception);
            // mto-notification: un aviso manual que nombra una clase de audiencia o un canal que el servicio
            // no conoce. Su detalle dice cual y cuales valen.
            case ValidationApiException broadcast when "NTF-422".equals(broadcast.getProblem().code()) ->
                    "El aviso nombra una audiencia o un canal que el servicio no conoce." + detail(exception);
            // mto-users: Keycloak no admite lo pedido (la politica de contrasenas del realm, un atributo que
            // el perfil de usuario exige...). Su detalle, que es el texto de Keycloak, es lo que lo explica.
            case ValidationApiException keycloak when "KC-400".equals(keycloak.getProblem().code()) && !keycloak.getProblem().hasFieldErrors() ->
                    "Keycloak ha rechazado la peticion." + detail(exception);
            // Un 422 sin errores por campo es una regla de negocio (mto-stock: reserva no activa, conjunto
            // sin lista de materiales, almacen inactivo...): la peticion esta bien, la operacion no cabe.
            case ValidationApiException business when business.getStatus().value() == 422 && !business.getProblem().hasFieldErrors() ->
                    "La operacion no es posible." + detail(exception);
            case ValidationApiException validation -> validation.getProblem().hasFieldErrors()
                    ? "Datos no validos: " + validation.getProblem().errors().stream()
                            .map(UiErrors::field).collect(Collectors.joining("; "))
                    : "La peticion no es valida." + detail(exception);
            case ConflictApiException stock when "STK-001".equals(stock.getProblem().code()) -> "No hay stock disponible suficiente."
                    + detail(exception);
            // La administracion de mto-notification: ninguno es de concurrencia, y recargar no los arregla.
            // Una entrega solo se reintenta fallida u omitida; una cola de mensajes muertos solo se mira o
            // se reprocesa con RabbitMQ encendido en el despliegue y con la cola declarada en el broker.
            case ConflictApiException delivery when "DLV-409".equals(delivery.getProblem().code()) ->
                    "La entrega ya no admite el reintento: solo se reintenta una fallida o una omitida.";
            case ConflictApiException source when "SRC-409".equals(source.getProblem().code()) ->
                    "RabbitMQ esta apagado en este despliegue, o esa cola de mensajes muertos no existe en el broker." + detail(exception);
            // mto-configuration: el versionNumber que se mando ya no es el guardado. Lo que hay que
            // hacer es exactamente eso, y el detalle del servicio ("intentelo de nuevo") no aporta nada.
            case ConflictApiException concurrent when "CON-001".equals(concurrent.getProblem().code()) ->
                    "Conflicto con otro cambio: recarga y vuelve a intentarlo.";
            // mto-configuration: un valor unico repetido (el codigo de un catalogo) o una entrada en
            // uso. Recargar no lo arregla, asi que no se pide recargar.
            case ConflictApiException duplicated when "BUS-002".equals(duplicated.getProblem().code()) ->
                    "Ya existe otro registro con ese valor (un codigo que no se puede repetir), o la entrada esta en uso.";
            // mto-maintenance: los 409 que no son de concurrencia (el servicio no tiene bloqueo optimista)
            // sino de estado. Recargar no los arregla; el detalle del servicio dice que falta.
            case ConflictApiException transition when "TRN-001".equals(transition.getProblem().code()) ->
                    "El estado actual no permite esta operacion." + detail(exception);
            case ConflictApiException shift when "SHF-001".equals(shift.getProblem().code()) ->
                    "El turno no admite ese trabajo." + detail(exception);
            case ConflictApiException material when "MAT-001".equals(material.getProblem().code()) ->
                    "La linea de material no admite esta operacion." + detail(exception);
            case ConflictApiException team when "TEA-001".equals(team.getProblem().code()) ->
                    "El equipo es de mto-resources: se cambia en Recursos > Organigrama.";
            case ConflictApiException asset when "AST-001".equals(asset.getProblem().code()) ->
                    "El activo esta desactivado, o ese dato lo manda mto-configuration." + detail(exception);
            // Un codigo repetido en mto-maintenance (activos y equipos) y en los catalogos de mto-stock:
            // recargar no lo arregla, asi que no se pide.
            case ConflictApiException duplicated when DUPLICATED_CODES.contains(duplicated.getProblem().code()) ->
                    "Ya existe otro con ese codigo.";
            case ConflictApiException duplicated when RESOURCES_DUPLICATED.contains(duplicated.getProblem().code()) ->
                    "Ya existe otro con ese valor (codigo, usuario, numero de empleado, matricula o numero ferroviario)."
                            + detail(exception);
            // mto-resources: las reglas del organigrama (de 2 a 6 linemen, el nivel del padre, la categoria
            // del jefe, una persona en otro equipo) y las de la flota (donde puede estar un vehiculo segun
            // su movilidad, el remolque, un vehiculo retirado). Recargar no las arregla; el detalle dice cual.
            case ConflictApiException unit when "UNI-001".equals(unit.getProblem().code()) ->
                    "El organigrama no admite ese cambio." + detail(exception);
            case ConflictApiException fleet when "VEH-001".equals(fleet.getProblem().code()) ->
                    "La flota no admite ese cambio." + detail(exception);
            // mto-resources: una persona tiene una cualificacion de cada tipo y un vehiculo un documento de cada
            // tipo; renovar es cambiar la caducidad. Tampoco se apunta un tipo retirado. Recargar no lo arregla.
            case ConflictApiException qualification when "QUA-001".equals(qualification.getProblem().code()) ->
                    "La persona ya tiene esa cualificacion, o su tipo esta retirado: para renovarla, cambia su caducidad." + detail(exception);
            case ConflictApiException document when "VDO-409".equals(document.getProblem().code()) ->
                    "El vehiculo ya tiene un documento de ese tipo: para renovarlo, cambia su caducidad.";
            // mto-resources: dos ausencias de una persona no se solapan, y una reparacion solo admite lo que
            // su estado deja (abrir una planificada, cerrar una abierta...). Recargar no lo arregla.
            case ConflictApiException absence when "ABS-001".equals(absence.getProblem().code()) ->
                    "La persona ya esta ausente en esas fechas: cambia esa ausencia en vez de anadir otra." + detail(exception);
            case ConflictApiException repair when "REP-001".equals(repair.getProblem().code()) ->
                    "La reparacion no admite ese cambio en su estado." + detail(exception);
            case ConflictApiException duplicated when "QTY-409".equals(duplicated.getProblem().code()) ->
                    "Ya existe otro tipo de cualificacion con ese codigo.";
            case ConflictApiException duplicated when "INT-409".equals(duplicated.getProblem().code()) ->
                    "Ya existe otro instrumento con ese codigo.";
            // mto-users: un nombre de usuario o un email repetido. Recargar tampoco lo arregla.
            case ConflictApiException duplicated when "USR-409".equals(duplicated.getProblem().code()) ->
                    "Ya existe un usuario con ese nombre de usuario o ese email." + detail(exception);
            case ConflictApiException ignored -> "Conflicto con otro cambio: recarga y vuelve a intentarlo."
                    + detail(exception);
            // mto-maintenance no ha podido hablar con mto-stock al sincronizar o al quitar una linea: el
            // servicio de mantenimiento si responde, y la linea se queda como estaba.
            case ServiceUnavailableApiException stock when "STK-503".equals(stock.getProblem().code()) ->
                    "El almacen no responde: la linea de material se queda como estaba. Intentalo mas tarde." + detail(exception);
            // mto-resources no ha podido leer Keycloak para el informe de puestos y perfiles: es transitorio.
            // mto-notification no ha podido hablar con RabbitMQ: las colas de mensajes muertos siguen como estaban.
            case ServiceUnavailableApiException broker when "SRC-503".equals(broker.getProblem().code()) ->
                    "RabbitMQ no responde ahora: la cola se queda como estaba. Intentalo mas tarde.";
            case ServiceUnavailableApiException keycloak when "KCK-503".equals(keycloak.getProblem().code()) ->
                    "Keycloak no responde ahora: el informe no se puede calcular. Intentalo mas tarde.";
            // Un 502 no es transitorio (mto-users sin SMTP, por ejemplo): su detalle es lo unico que lo explica.
            case ServiceUnavailableApiException unavailable when unavailable.getStatus().value() == 502 ->
                    "El servicio no ha podido completar la operacion." + detail(exception);
            case ServiceUnavailableApiException unavailable -> "El servicio no esta disponible ahora mismo."
                    + unavailable.getRetryAfter().map(d -> " Intentalo en " + d.toSeconds() + " s.").orElse(" Intentalo mas tarde.");
            // Las operaciones de fondo de mto-maintenance y mto-resources: el tope de todo el despliegue
            // esta lleno. No es un fallo de la peticion; vale reintentarla pasado el Retry-After.
            case TooManyRequestsApiException operations when "OPS-429".equals(operations.getProblem().code()) ->
                    "Hay demasiadas operaciones en marcha."
                            + operations.getRetryAfter().map(d -> " Intentalo en " + d.toSeconds() + " s.").orElse(" Intentalo mas tarde.");
            // Una operacion de fondo de mto-maintenance cuya instancia dejo de latir (se paro a medias): no
            // va a terminar, hay que volver a lanzarla. Llega como el error de la operacion, con estado 500.
            case BackofficeApiException abandoned when "OPS-410".equals(abandoned.getProblem().code()) ->
                    "La operacion se interrumpio en el servicio (se paro la instancia que la llevaba): vuelve a lanzarla.";
            // El unico 410 del dominio es el fichero de un trabajo de mto-configuration que ya no esta
            // (purgado, o generado en otra replica): reintentar no lo trae, hay que relanzar el trabajo.
            case BackofficeApiException gone when gone.getStatus().value() == 410 ->
                    "El fichero ya no esta en el servicio: vuelve a lanzar el trabajo.";
            default -> "Error inesperado (" + exception.getStatus().value() + ")." + detail(exception);
        };
    }

    private static String field(ApiFieldError error) {
        String where = error.field() == null || error.field().isBlank() ? "" : error.field() + " ";
        return where + (error.message() == null ? error.code() : error.message());
    }

    private static String detail(BackofficeApiException exception) {
        String detail = exception.getProblem().detail();
        return detail == null || detail.isBlank() ? "" : " " + detail;
    }
}
