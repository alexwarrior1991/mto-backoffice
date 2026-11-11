package com.alejandro.mtobackoffice.ui.master;

import com.alejandro.mtobackoffice.client.configuration.MasterResource;
import com.alejandro.mtobackoffice.client.dto.master.MasterDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.error.ValidationApiException;
import com.alejandro.mtobackoffice.ui.support.ServerValidation;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Alta o modificacion de un maestro sobre el DTO que llego del servicio. El {@code Binder} lee y
 * escribe ese mismo objeto por nombre de propiedad, que es lo que permite volcar los
 * {@code errors[{field}]} del servicio sobre cada campo ({@link ServerValidation}); antes de
 * guardar, las colecciones de hijos que el editor no toca van a {@code null}
 * ({@link MasterDto#forgetChildren()}) para que el servicio las deje como estan. mto-configuration
 * dice el campo como ruta desde la raiz del cuerpo, y {@link ServerValidation#applyWithPaths} lleva
 * cada error a su sitio sin perder ninguno.
 *
 * <p>Aqui solo se valida lo evidente (obligatorios, longitudes, formato); la regla de negocio la
 * dice el servicio y se ensena en su campo.</p>
 */
public abstract class MasterEditorDialog<D extends MasterDto> extends Dialog {

    protected final Binder<D> binder;
    protected final D dto;
    protected final FormLayout form = new FormLayout();
    private final Function<D, D> saver;
    private final Consumer<D> onSaved;

    protected MasterEditorDialog(MasterResource resource, Class<D> type, D dto, Function<D, D> saver, Consumer<D> onSaved) {
        this.binder = new Binder<>(type);
        this.dto = dto;
        this.saver = saver;
        this.onSaved = onSaved;

        setHeaderTitle((dto.isNew() ? "Alta de " : "Modificar ") + resource.singular().toLowerCase(Locale.ROOT));
        setCloseOnOutsideClick(false);
        setWidth("min(56rem, 96vw)");
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1), new FormLayout.ResponsiveStep("40em", 2));
        add(form);

        Button save = new Button("Guardar", click -> save());
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancel = new Button("Cancelar", click -> close());
        getFooter().add(cancel, save);
    }

    /** Al final del constructor de la subclase, con los campos ya creados y enlazados. */
    protected final void ready() {
        binder.readBean(dto);
    }

    protected TextField text(String label, int maxLength, boolean required) {
        TextField field = new TextField(label);
        field.setMaxLength(maxLength);
        field.setRequiredIndicatorVisible(required);
        return field;
    }

    protected void wide(Component component) {
        form.setColspan(component, 2);
    }

    /**
     * Despues de {@link MasterDto#forgetChildren()} y antes de mandar: aqui el editor que si
     * gestiona una coleccion de hijos la pone entera en el DTO (README_API.md §4), y resuelve lo
     * que necesite del servicio. Una excepcion de la API se ensena como cualquier otra.
     */
    protected void prepare(D dto) {
    }

    /**
     * Las colecciones de hijos que el editor lleva en una tabla, por su nombre en el cuerpo, con quien
     * ensena lo que el servicio rechaza de ellas ({@code cantilevers[1].cantileverType}): debajo de la
     * tabla, con su fila y su campo ({@link ChildrenEditor#showErrors}).
     */
    protected Map<String, Consumer<List<ServerValidation.ChildError>>> childErrors() {
        return Map.of();
    }

    private void save() {
        if (!binder.writeBeanIfValid(dto)) {
            return;
        }
        dto.forgetChildren();
        // Lo que se dijo de los hijos en el intento anterior ya no vale.
        childErrors().values().forEach(show -> show.accept(List.of()));
        try {
            prepare(dto);
            D saved = saver.apply(dto);
            close();
            Notification.show("Guardado", 3000, Notification.Position.BOTTOM_START)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            onSaved.accept(saved);
        } catch (ValidationApiException validation) {
            // Cada error en su sitio y ninguno perdido: su campo, el campo de su lista, la tabla de sus
            // hijos o, si no tiene ninguno, la notificacion con su ruta delante.
            List<String> unattributed = ServerValidation.applyWithPaths(binder, validation, childErrors());
            if (!unattributed.isEmpty()) {
                Notification.show(String.join(". ", unattributed), 8000, Notification.Position.BOTTOM_START)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }
}
