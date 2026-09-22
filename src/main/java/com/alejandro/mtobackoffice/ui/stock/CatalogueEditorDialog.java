package com.alejandro.mtobackoffice.ui.stock;

import com.alejandro.mtobackoffice.client.dto.stock.CatalogueRequest;
import com.alejandro.mtobackoffice.client.dto.stock.CatalogueUpdateRequest;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.error.ValidationApiException;
import com.alejandro.mtobackoffice.client.stock.StockCatalogueClient;
import com.alejandro.mtobackoffice.ui.support.ServerValidation;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import java.util.List;
import java.util.UUID;

/**
 * Alta o modificacion de un almacen, un proveedor o un proyecto: codigo y nombre, y en la
 * modificacion el estado, porque retirar uno es modificarlo con {@code active=false}. Aqui solo se
 * exige lo evidente (codigo y nombre, y la longitud de columna); un codigo repetido lo dice el
 * servicio (409) y cae como notificacion.
 */
public class CatalogueEditorDialog extends Dialog {

    public static final String SAVE_ID = "catalogue-save";
    static final int CODE_LENGTH = 64;
    static final int NAME_LENGTH = 255;

    /** Lo que el editor necesita de la fila leida; la vista lo saca de su DTO. */
    public record Snapshot(UUID id, String code, String name, boolean active) {
    }

    private final Binder<CatalogueForm> binder = new Binder<>(CatalogueForm.class);
    private final CatalogueForm form;

    /**
     * @param entity   el nombre de la cosa, para el titulo («Almacen», «Proveedor», «Proyecto»)
     * @param existing la fila a modificar, o {@code null} para un alta
     * @param saved    que hacer despues de guardar
     */
    public CatalogueEditorDialog(String entity, Snapshot existing, StockCatalogueClient<?, CatalogueRequest, CatalogueUpdateRequest> client,
                                 Runnable saved) {
        boolean creating = existing == null;
        this.form = CatalogueForm.of(existing);
        setHeaderTitle(creating ? "Alta de " + entity.toLowerCase() : "Modificar " + entity.toLowerCase() + " " + existing.code());
        setCloseOnOutsideClick(false);

        TextField code = new TextField("Codigo");
        code.setMaxLength(CODE_LENGTH);
        code.setRequiredIndicatorVisible(true);
        TextField name = new TextField("Nombre");
        name.setMaxLength(NAME_LENGTH);
        name.setRequiredIndicatorVisible(true);
        Checkbox active = new Checkbox("Activo");
        active.setHelperText("Desmarcarlo lo retira: deja de poder usarse en movimientos y reservas nuevos");

        binder.forField(code).asRequired("El codigo es obligatorio").bind("code");
        binder.forField(name).asRequired("El nombre es obligatorio").bind("name");
        if (!creating) {
            binder.forField(active).bind("active");
        }
        binder.readBean(form);

        FormLayout layout = new FormLayout(code, name);
        if (!creating) {
            layout.add(active);
        }
        layout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        add(layout);

        Button save = new Button("Guardar", click -> save(existing, client, saved));
        save.setId(SAVE_ID);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(new Button("Cancelar", click -> close()), save);
    }

    private void save(Snapshot existing, StockCatalogueClient<?, CatalogueRequest, CatalogueUpdateRequest> client, Runnable saved) {
        if (!binder.writeBeanIfValid(form)) {
            return;
        }
        try {
            if (existing == null) {
                client.create(form.toCreateRequest());
            } else {
                client.update(existing.id(), form.toUpdateRequest());
            }
            close();
            Notification.show("Guardado " + form.getCode().trim(), 3000, Notification.Position.BOTTOM_START)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            saved.run();
        } catch (ValidationApiException validation) {
            List<String> unattributed = ServerValidation.apply(binder, validation);
            if (!unattributed.isEmpty()) {
                Notification.show(String.join(". ", unattributed), 8000, Notification.Position.BOTTOM_START)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }
}
