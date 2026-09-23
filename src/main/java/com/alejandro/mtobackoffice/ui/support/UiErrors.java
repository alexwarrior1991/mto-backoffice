package com.alejandro.mtobackoffice.ui.support;

import com.alejandro.mtobackoffice.client.error.ApiFieldError;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.error.ConflictApiException;
import com.alejandro.mtobackoffice.client.error.ForbiddenApiException;
import com.alejandro.mtobackoffice.client.error.NotFoundApiException;
import com.alejandro.mtobackoffice.client.error.ServiceUnavailableApiException;
import com.alejandro.mtobackoffice.client.error.SessionExpiredApiException;
import com.alejandro.mtobackoffice.client.error.ValidationApiException;
import com.alejandro.mtobackoffice.configuration.security.SecurityConfiguration;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import java.util.stream.Collectors;

/**
 * Traduce un fallo de la API a algo que una persona pueda entender y, si hay algo que hacer, lo
 * ofrece: volver a entrar cuando la sesion ha caducado, reintentar cuando el servicio no esta.
 */
public final class UiErrors {

    private static final int DURATION_MS = 8000;

    private UiErrors() {
    }

    public static Notification show(BackofficeApiException exception) {
        Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.setPosition(Notification.Position.BOTTOM_START);
        notification.setDuration(exception instanceof SessionExpiredApiException ? 0 : DURATION_MS);

        Div text = new Div(new Span(message(exception)));
        String reference = exception.getReference();
        if (reference != null && !reference.isBlank()) {
            Span ref = new Span("Referencia: " + reference);
            ref.getStyle().set("font-size", "var(--lumo-font-size-xs)");
            text.add(new Div(ref));
        }
        HorizontalLayout content = new HorizontalLayout(text);
        content.setAlignItems(HorizontalLayout.Alignment.CENTER);
        if (exception instanceof SessionExpiredApiException) {
            content.add(new Button("Volver a entrar", click -> {
                notification.close();
                UI.getCurrent().getPage().setLocation(SecurityConfiguration.LOGIN_URL);
            }));
        }
        notification.add(content);
        notification.open();
        return notification;
    }

    /** Texto para la persona. Se prueba aparte de la notificacion. */
    public static String message(BackofficeApiException exception) {
        return switch (exception) {
            case SessionExpiredApiException ignored -> "La sesion ha caducado. Hay que volver a entrar.";
            case ForbiddenApiException ignored -> "No tienes permiso para esta operacion.";
            case NotFoundApiException ignored -> "No se ha encontrado lo que se pedia."
                    + detail(exception);
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
            // mto-configuration: el versionNumber que se mando ya no es el guardado. Lo que hay que
            // hacer es exactamente eso, y el detalle del servicio ("intentelo de nuevo") no aporta nada.
            case ConflictApiException concurrent when "CON-001".equals(concurrent.getProblem().code()) ->
                    "Conflicto con otro cambio: recarga y vuelve a intentarlo.";
            // mto-configuration: un valor unico repetido (el codigo de un catalogo) o una entrada en
            // uso. Recargar no lo arregla, asi que no se pide recargar.
            case ConflictApiException duplicated when "BUS-002".equals(duplicated.getProblem().code()) ->
                    "Ya existe otro registro con ese valor (un codigo que no se puede repetir), o la entrada esta en uso.";
            case ConflictApiException ignored -> "Conflicto con otro cambio: recarga y vuelve a intentarlo."
                    + detail(exception);
            // Un 502 no es transitorio (mto-users sin SMTP, por ejemplo): su detalle es lo unico que lo explica.
            case ServiceUnavailableApiException unavailable when unavailable.getStatus().value() == 502 ->
                    "El servicio no ha podido completar la operacion." + detail(exception);
            case ServiceUnavailableApiException unavailable -> "El servicio no esta disponible ahora mismo."
                    + unavailable.getRetryAfter().map(d -> " Intentalo en " + d.toSeconds() + " s.").orElse(" Intentalo mas tarde.");
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
