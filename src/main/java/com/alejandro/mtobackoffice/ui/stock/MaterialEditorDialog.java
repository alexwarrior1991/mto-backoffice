package com.alejandro.mtobackoffice.ui.stock;

import com.alejandro.mtobackoffice.client.dto.stock.MaterialDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.error.ValidationApiException;
import com.alejandro.mtobackoffice.client.stock.MaterialClient;
import com.alejandro.mtobackoffice.ui.support.ServerValidation;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import java.math.BigDecimal;
import java.util.List;

/**
 * Alta o modificacion de un material: codigo, nombre, unidad (texto libre en el servicio) y el
 * stock minimo con el que se calcula «bajo minimo»; en la modificacion tambien el estado. Un
 * material retirado no admite movimientos nuevos (el servicio responde 400 {@code VAL-001}).
 */
public class MaterialEditorDialog extends Dialog {

    public static final String SAVE_ID = "material-save";
    static final int UNIT_LENGTH = 32;

    private final Binder<MaterialForm> binder = new Binder<>(MaterialForm.class);
    private final MaterialForm form;

    public MaterialEditorDialog(MaterialDto existing, MaterialClient client, Runnable saved) {
        boolean creating = existing == null;
        this.form = MaterialForm.of(existing);
        setHeaderTitle(creating ? "Alta de material" : "Modificar material " + existing.code());
        setCloseOnOutsideClick(false);
        setWidth("min(40rem, 96vw)");

        TextField code = new TextField("Codigo");
        code.setMaxLength(CatalogueEditorDialog.CODE_LENGTH);
        code.setRequiredIndicatorVisible(true);
        TextField name = new TextField("Nombre");
        name.setMaxLength(CatalogueEditorDialog.NAME_LENGTH);
        name.setRequiredIndicatorVisible(true);
        TextField unit = new TextField("Unidad de medida");
        unit.setMaxLength(UNIT_LENGTH);
        unit.setRequiredIndicatorVisible(true);
        unit.setHelperText("m, kg, ud...");
        BigDecimalField minimum = new BigDecimalField("Stock minimo");
        minimum.setRequiredIndicatorVisible(true);
        minimum.setHelperText("Por debajo del disponible, el material sale como bajo minimo");
        Checkbox active = new Checkbox("Activo");
        active.setHelperText("Desmarcarlo lo retira: sin movimientos ni reservas nuevos");

        binder.forField(code).asRequired("El codigo es obligatorio").bind("code");
        binder.forField(name).asRequired("El nombre es obligatorio").bind("name");
        binder.forField(unit).asRequired("La unidad es obligatoria").bind("unitOfMeasure");
        binder.forField(minimum).asRequired("El stock minimo es obligatorio")
                .withValidator(value -> value.signum() >= 0, "No puede ser negativo")
                .bind("minimumStockLevel");
        if (!creating) {
            binder.forField(active).bind("active");
        }
        binder.readBean(form);

        FormLayout layout = new FormLayout(code, name, unit, minimum);
        if (!creating) {
            layout.add(active);
        }
        layout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1), new FormLayout.ResponsiveStep("30em", 2));
        layout.setColspan(name, 2);
        add(layout);

        Button save = new Button("Guardar", click -> save(existing, client, saved));
        save.setId(SAVE_ID);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(new Button("Cancelar", click -> close()), save);
    }

    private void save(MaterialDto existing, MaterialClient client, Runnable saved) {
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

    static String quantity(BigDecimal value) {
        return value == null ? "" : value.stripTrailingZeros().toPlainString();
    }
}
