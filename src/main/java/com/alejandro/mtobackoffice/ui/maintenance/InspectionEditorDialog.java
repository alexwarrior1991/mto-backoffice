package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.dto.maintenance.AssetSummaryDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.InspectionDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.InspectionKind;
import com.alejandro.mtobackoffice.client.dto.maintenance.InspectionResult;
import com.alejandro.mtobackoffice.client.dto.maintenance.InspectionUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.MergePatch;
import com.alejandro.mtobackoffice.client.dto.maintenance.OrderDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.ShiftDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.error.ValidationApiException;
import com.alejandro.mtobackoffice.ui.support.Numbers;
import com.alejandro.mtobackoffice.ui.support.Screens;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import java.util.List;
import java.util.function.Consumer;

/**
 * Alta y modificacion de una inspeccion. El alta elige el activo, o lo trae de la orden de
 * inspeccion desde la que se abre (y queda como su origen); la plantilla activa del tipo de activo
 * la copia el servicio. Que el resultado case con los puntos lo comprueba el servicio (422
 * {@code INS-001}). El alta puede llevar el turno en que se hizo, entre los recientes de las vias del
 * activo que se han trabajado: sale en su parte. La modificacion manda solo lo cambiado y no cambia el
 * turno.
 */
public class InspectionEditorDialog extends Dialog {

    public static final String SAVE_ID = "inspection-save";
    public static final String SHIFT_ID = "inspection-shift";

    private final Binder<InspectionForm> binder = new Binder<>(InspectionForm.class);
    private final InspectionForm form;

    /**
     * @param existing    la inspeccion a modificar, o {@code null} para un alta
     * @param originOrder la orden de inspeccion desde la que se da de alta, o {@code null}
     */
    public InspectionEditorDialog(InspectionDto existing, OrderDto originOrder, MaintenanceClients clients, Consumer<InspectionDto> saved) {
        boolean creating = existing == null;
        this.form = InspectionForm.of(existing);
        if (creating && originOrder != null) {
            form.setAssetId(originOrder.asset());
        }
        setHeaderTitle(creating ? "Nueva inspeccion" : "Modificar " + existing.code());
        setCloseOnOutsideClick(false);
        setWidth("min(90vw, 720px)");

        DatePicker date = new DatePicker("Fecha");
        date.setId("inspection-date");
        TextField inspector = new TextField("Inspector");
        inspector.setId("inspection-inspector");
        inspector.setMaxLength(100);
        ComboBox<InspectionKind> kind = new ComboBox<>("Tipo", InspectionKind.selectable());
        kind.setId("inspection-kind");
        kind.setItemLabelGenerator(InspectionKind::label);
        ComboBox<InspectionResult> result = new ComboBox<>("Resultado", InspectionResult.selectable());
        result.setId("inspection-result");
        result.setItemLabelGenerator(InspectionResult::label);
        BigDecimalField kp = new BigDecimalField("KP");
        kp.setId("inspection-kp");
        TextArea description = new TextArea("Descripcion");
        description.setId("inspection-description");
        TextArea detectedDefects = new TextArea("Defectos observados");
        detectedDefects.setId("inspection-detected-defects");
        TextArea recommendedActions = new TextArea("Acciones recomendadas");
        recommendedActions.setId("inspection-recommended-actions");

        FormLayout layout = new FormLayout();
        layout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1), new FormLayout.ResponsiveStep("480px", 2));
        ComboBox<ShiftDto> shift = new ComboBox<>("Turno");
        shift.setId(SHIFT_ID);
        shift.setClearButtonVisible(true);
        shift.setItemLabelGenerator(candidate -> candidate.code() + " · " + MaintenanceFormats.date(candidate.shiftDate())
                + (candidate.team() == null ? "" : " · " + candidate.team().label())
                + (candidate.status() == null ? "" : " · " + candidate.status().label()));
        shift.setHelperText("Opcional: el turno en curso o cerrado de su via en que se hizo; sale en su parte");
        if (creating && originOrder == null) {
            ComboBox<AssetSummaryDto> asset = MaintenancePickers.asset("Activo", clients.assets(), null);
            asset.setId("inspection-asset");
            binder.forField(asset).asRequired("El activo es obligatorio").bind("assetId");
            // Otro activo, otras vias: el turno elegido podria no recorrerlas.
            asset.addValueChangeListener(change -> offerShifts(shift, change.getValue(), clients));
            layout.add(asset);
            layout.setColspan(asset, 2);
        } else if (creating) {
            add(new Paragraph("Inspeccion de la orden " + originOrder.code() + " sobre " + originOrder.asset().label() + "."));
        }
        if (creating) {
            offerShifts(shift, form.getAssetId(), clients);
            binder.forField(shift).bind("shiftId");
            layout.add(shift);
            layout.setColspan(shift, 2);
        }
        binder.forField(date).asRequired("La fecha es obligatoria").bind("inspectionDate");
        binder.forField(inspector).bind("inspector");
        // Siempre tiene uno (visual por defecto): vaciarlo no vaciaba nada y parecia guardado.
        binder.forField(kind).asRequired("El tipo es obligatorio").bind("inspectionKind");
        binder.forField(result).asRequired("El resultado es obligatorio").bind("result");
        binder.forField(kp).withValidator(Numbers.kp()).bind("kp");
        binder.forField(description).bind("description");
        binder.forField(detectedDefects).bind("detectedDefects");
        binder.forField(recommendedActions).bind("recommendedActions");
        binder.readBean(form);
        layout.add(date, result, kind, inspector, kp, description, detectedDefects, recommendedActions);
        layout.setColspan(description, 2);
        layout.setColspan(detectedDefects, 2);
        layout.setColspan(recommendedActions, 2);
        add(layout);

        Button save = new Button("Guardar", click -> save(existing, originOrder, clients, saved));
        save.setId(SAVE_ID);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(new Button("Cancelar", click -> close()), save);
    }

    /**
     * Los turnos en que se pudo hacer: los recientes de las vias del activo (la suya y la conectada)
     * que se han trabajado, en curso o cerrados. Que le vale lo decide el servicio (409 {@code SHF-001},
     * con el dialogo abierto). Sin activo no hay turnos que ofrecer.
     */
    private static void offerShifts(ComboBox<ShiftDto> shift, AssetSummaryDto asset, MaintenanceClients clients) {
        shift.clear();
        List<ShiftDto> worked = DefectTransitionDialogs.recentShiftsOn(clients, AssetSummaryDto.tracksOf(asset, null)).stream()
                .filter(candidate -> candidate.status() != null && candidate.status().recordsInspections())
                .toList();
        shift.setItems(worked);
        shift.setEnabled(asset != null);
    }

    private void save(InspectionDto existing, OrderDto originOrder, MaintenanceClients clients, Consumer<InspectionDto> saved) {
        if (!binder.writeBeanIfValid(form)) {
            return;
        }
        try {
            InspectionDto result;
            if (existing == null) {
                result = clients.inspections().create(form.toRequest(originOrder == null ? null : originOrder.id()));
            } else {
                MergePatch<InspectionUpdateRequest> patch = form.toPatch(existing);
                if (patch.changesNothing()) {
                    close();
                    return;
                }
                result = clients.inspections().update(existing.id(), patch);
            }
            close();
            Screens.success("Guardada " + result.code());
            saved.accept(result);
        } catch (ValidationApiException validation) {
            Screens.showValidation(binder, validation);
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }
}
