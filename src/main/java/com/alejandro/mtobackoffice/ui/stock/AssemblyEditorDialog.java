package com.alejandro.mtobackoffice.ui.stock;

import com.alejandro.mtobackoffice.client.dto.stock.AssemblyDto;
import com.alejandro.mtobackoffice.client.dto.stock.AssemblyRequest;
import com.alejandro.mtobackoffice.client.dto.stock.AssemblyUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.stock.MaterialDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.error.ValidationApiException;
import com.alejandro.mtobackoffice.client.stock.AssemblyClient;
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

/**
 * Alta o modificacion de un conjunto: codigo, nombre, en la modificacion el estado, y su lista de
 * materiales, que va entera y no puede ir vacia (un conjunto es lo que lo compone; no tiene stock
 * propio). Codigo y nombre van por el {@code Binder} sobre {@link CatalogueForm}, con los nombres
 * del servicio para que un {@code validationErrors[].field} caiga en su campo; las lineas las
 * lleva {@link BomEditor}.
 */
public class AssemblyEditorDialog extends Dialog {

    public static final String SAVE_ID = "assembly-save";

    private final Binder<CatalogueForm> binder = new Binder<>(CatalogueForm.class);
    private final CatalogueForm form;
    private final BomEditor bom;

    public AssemblyEditorDialog(AssemblyDto existing, AssemblyClient client, StockCatalogueClient<MaterialDto, ?, ?> materials, Runnable saved) {
        boolean creating = existing == null;
        this.form = CatalogueForm.of(creating ? null
                : new CatalogueEditorDialog.Snapshot(existing.id(), existing.code(), existing.name(), existing.isEnabled()));
        setHeaderTitle(creating ? "Alta de conjunto" : "Modificar conjunto " + existing.code());
        setCloseOnOutsideClick(false);
        setWidth("min(56rem, 96vw)");

        TextField code = new TextField("Codigo");
        code.setMaxLength(CatalogueEditorDialog.CODE_LENGTH);
        code.setRequiredIndicatorVisible(true);
        TextField name = new TextField("Nombre");
        name.setMaxLength(CatalogueEditorDialog.NAME_LENGTH);
        name.setRequiredIndicatorVisible(true);
        Checkbox active = new Checkbox("Activo");
        active.setHelperText("Desmarcarlo lo retira");

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
        layout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1), new FormLayout.ResponsiveStep("36em", 2));
        bom = new BomEditor(creating ? List.of() : existing.components(), materials);
        add(layout, bom);

        Button save = new Button("Guardar", click -> save(existing, client, saved));
        save.setId(SAVE_ID);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(new Button("Cancelar", click -> close()), save);
    }

    private void save(AssemblyDto existing, AssemblyClient client, Runnable saved) {
        boolean valid = binder.writeBeanIfValid(form);
        if (bom.isEmpty()) {
            bom.showError("La lista de materiales no puede ir vacia: un conjunto es lo que lo compone");
            valid = false;
        }
        if (!valid) {
            return;
        }
        try {
            if (existing == null) {
                client.create(new AssemblyRequest(form.getCode().trim(), form.getName().trim(), bom.lines()));
            } else {
                client.update(existing.id(), new AssemblyUpdateRequest(form.getCode().trim(), form.getName().trim(), form.isActive(), bom.lines()));
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
