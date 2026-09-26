package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.dto.maintenance.AssetSummaryDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.DefectDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.DefectSeverity;
import com.alejandro.mtobackoffice.client.dto.maintenance.DefectUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.MergePatch;
import com.alejandro.mtobackoffice.client.dto.maintenance.OrderDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.error.ValidationApiException;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import java.util.function.Consumer;

/**
 * Alta y modificacion de un defecto. El alta elige el activo (o lo trae de la orden desde cuya
 * ficha se abre, y queda vinculado a ella); gravedad y descripcion son obligatorias. La
 * modificacion, mientras no este cerrado ni descartado, manda solo lo cambiado.
 */
public class DefectEditorDialog extends Dialog {

    public static final String SAVE_ID = "defect-save";

    private final Binder<DefectForm> binder = new Binder<>(DefectForm.class);
    private final DefectForm form;

    /**
     * @param existing el defecto a modificar, o {@code null} para un alta
     * @param order    la orden desde cuya ficha se da de alta, o {@code null}
     */
    public DefectEditorDialog(DefectDto existing, OrderDto order, MaintenanceClients clients, Consumer<DefectDto> saved) {
        boolean creating = existing == null;
        this.form = DefectForm.of(existing);
        if (creating && order != null) {
            form.setAssetId(order.asset());
        }
        setHeaderTitle(creating ? "Nuevo defecto" : "Modificar " + existing.code());
        setCloseOnOutsideClick(false);
        setWidth("min(90vw, 720px)");

        ComboBox<DefectSeverity> severity = new ComboBox<>("Gravedad", DefectSeverity.selectable());
        severity.setId("defect-severity");
        severity.setItemLabelGenerator(DefectSeverity::label);
        TextArea description = new TextArea("Descripcion");
        description.setId("defect-description");
        TextArea technicalNotes = new TextArea("Notas tecnicas");
        technicalNotes.setId("defect-technical-notes");
        TextField correctionType = new TextField("Tipo de correccion");
        correctionType.setId("defect-correction-type");
        correctionType.setMaxLength(120);
        TextField partsReplaced = new TextField("Piezas cambiadas");
        partsReplaced.setId("defect-parts-replaced");
        DatePicker repairPlannedDate = new DatePicker("Reparacion prevista");
        repairPlannedDate.setId("defect-repair-date");

        FormLayout layout = new FormLayout();
        layout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1), new FormLayout.ResponsiveStep("480px", 2));
        if (creating) {
            if (order == null) {
                ComboBox<AssetSummaryDto> asset = MaintenancePickers.asset("Activo", clients.assets(), null);
                asset.setId("defect-asset");
                binder.forField(asset).asRequired("El activo es obligatorio").bind("assetId");
                layout.add(asset);
                layout.setColspan(asset, 2);
            } else {
                add(new Paragraph("Defecto de la orden " + order.code() + " sobre " + order.asset().label() + "; queda vinculado a ella."));
            }
            DateTimePicker detectedAt = new DateTimePicker("Detectado");
            detectedAt.setId("defect-detected-at");
            detectedAt.setHelperText("Vacio: ahora");
            BigDecimalField startKp = new BigDecimalField("KP inicial");
            startKp.setId("defect-start-kp");
            BigDecimalField endKp = new BigDecimalField("KP final");
            endKp.setId("defect-end-kp");
            binder.forField(detectedAt).bind("detectedAt");
            binder.forField(startKp).bind("startKp");
            binder.forField(endKp).bind("endKp");
            layout.add(detectedAt, startKp, endKp);
        }
        binder.forField(severity).asRequired("La gravedad es obligatoria").bind("severity");
        binder.forField(description).asRequired("La descripcion es obligatoria").bind("description");
        binder.forField(technicalNotes).bind("technicalNotes");
        binder.forField(correctionType).bind("correctionType");
        binder.forField(partsReplaced).bind("partsReplaced");
        binder.forField(repairPlannedDate).bind("repairPlannedDate");
        binder.readBean(form);
        layout.add(severity, repairPlannedDate, description, technicalNotes, correctionType, partsReplaced);
        layout.setColspan(description, 2);
        layout.setColspan(technicalNotes, 2);
        add(layout);

        Button save = new Button("Guardar", click -> save(existing, order, clients, saved));
        save.setId(SAVE_ID);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(new Button("Cancelar", click -> close()), save);
    }

    private void save(DefectDto existing, OrderDto order, MaintenanceClients clients, Consumer<DefectDto> saved) {
        if (!binder.writeBeanIfValid(form)) {
            return;
        }
        try {
            DefectDto result;
            if (existing == null) {
                result = clients.defects().create(form.toRequest(order == null ? null : order.id()));
            } else {
                MergePatch<DefectUpdateRequest> patch = form.toPatch(existing);
                if (patch.changesNothing()) {
                    close();
                    return;
                }
                result = clients.defects().update(existing.id(), patch);
            }
            close();
            MaintenanceUi.success("Guardado " + result.code());
            saved.accept(result);
        } catch (ValidationApiException validation) {
            MaintenanceUi.showValidation(binder, validation);
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }
}
