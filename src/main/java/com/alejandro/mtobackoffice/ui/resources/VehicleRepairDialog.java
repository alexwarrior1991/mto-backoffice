package com.alejandro.mtobackoffice.ui.resources;

import com.alejandro.mtobackoffice.client.dto.MergePatch;
import com.alejandro.mtobackoffice.client.dto.resources.RepairKind;
import com.alejandro.mtobackoffice.client.dto.resources.RepairStatus;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleDto;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleRepairDto;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleRepairUpdateRequest;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.error.ValidationApiException;
import com.alejandro.mtobackoffice.ui.support.Numbers;
import com.alejandro.mtobackoffice.ui.support.Required;
import com.alejandro.mtobackoffice.ui.support.Screens;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import java.time.LocalDate;
import java.util.List;

/**
 * Alta y modificacion de una reparacion de un vehiculo. El alta elige si nace planificada (con su inicio
 * previsto, obligatorio) o abierta ahora, y si inmoviliza el vehiculo (marcado por defecto). Lo evidente
 * se comprueba antes de llamar: la clase y la descripcion, el fin previsto no anterior al inicio, y los
 * contadores y el coste con su columna. La modificacion manda lo que cambio y la version leida: las fechas
 * previstas solo cambian planificada o abierta, y si inmoviliza, solo planificada (lo demas seria el 409
 * {@code REP-001}); que el vehiculo admita el cambio lo dice el servicio ({@code VEH-001}), con el dialogo
 * abierto.
 */
public class VehicleRepairDialog extends Dialog {

    public static final String SAVE_ID = "repair-save";
    static final int DESCRIPTION_LENGTH = 500;
    static final int WORKSHOP_LENGTH = 150;
    static final String PLANNED_FROM_REQUIRED = "Una planificada dice desde cuando";
    static final String END_BEFORE_START = "El fin previsto no puede ser anterior al inicio";

    private final Binder<VehicleRepairForm> binder = new Binder<>(VehicleRepairForm.class);
    private final VehicleRepairForm form;

    /**
     * @param existing la reparacion a modificar, o {@code null} para dar una de alta
     * @param saved    que hacer despues de guardar: releer la ficha del vehiculo, cuyo estado puede cambiar
     */
    public VehicleRepairDialog(VehicleDto vehicle, VehicleRepairDto existing, ResourcesClients clients, Runnable saved) {
        boolean creating = existing == null;
        this.form = VehicleRepairForm.of(existing);
        setHeaderTitle(creating ? "Nueva reparacion de " + vehicle.label() : "Modificar " + existing.code() + " de " + vehicle.code());
        setCloseOnOutsideClick(false);
        setWidth("min(90vw, 720px)");

        RadioButtonGroup<RepairStatus> status = new RadioButtonGroup<>("Estado");
        status.setId("repair-status");
        status.setItems(List.of(RepairStatus.PLANNED, RepairStatus.OPEN));
        status.setItemLabelGenerator(value -> value == RepairStatus.OPEN ? "Abierta ahora" : value.label());
        status.setHelperText("Abierta ahora y si inmoviliza, el vehiculo pasa a En reparacion");
        status.setVisible(creating);
        Select<RepairKind> kind = new Select<>();
        kind.setLabel("Clase");
        kind.setId("repair-kind");
        kind.setItems(RepairKind.selectable());
        kind.setItemLabelGenerator(value -> value == null ? "" : value.label());
        Checkbox immobilises = new Checkbox("Inmoviliza el vehiculo");
        immobilises.setId("repair-immobilises");
        immobilises.setReadOnly(!creating && existing.state() != RepairStatus.PLANNED);
        TextArea description = new TextArea("Descripcion");
        description.setId("repair-description");
        description.setMaxLength(DESCRIPTION_LENGTH);
        TextField workshop = new TextField("Taller");
        workshop.setId("repair-workshop");
        workshop.setMaxLength(WORKSHOP_LENGTH);
        DatePicker plannedFrom = new DatePicker("Previsto desde");
        plannedFrom.setId("repair-planned-from");
        plannedFrom.setClearButtonVisible(true);
        DatePicker plannedTo = new DatePicker("Previsto hasta");
        plannedTo.setId("repair-planned-to");
        plannedTo.setClearButtonVisible(true);
        if (!creating && !existing.state().datesChange()) {
            plannedFrom.setReadOnly(true);
            plannedTo.setReadOnly(true);
            plannedFrom.setHelperText("Ya no cambia: la reparacion esta " + existing.state().label().toLowerCase());
        }
        IntegerField odometer = Numbers.atLeast(new IntegerField("Km"), 0, "No puede ser negativo");
        odometer.setId("repair-odometer");
        BigDecimalField hours = new BigDecimalField("Horas de motor");
        hours.setId("repair-engine-hours");
        BigDecimalField cost = new BigDecimalField("Coste");
        cost.setId("repair-cost");
        TextArea notes = new TextArea("Notas");
        notes.setId("repair-notes");

        if (creating) {
            binder.forField(status).bind("status");
        }
        binder.forField(kind).asRequired("La clase es obligatoria").bind("kind");
        binder.forField(immobilises).bind("immobilises");
        binder.forField(description).asRequired(Required.text("La descripcion es obligatoria")).bind("description");
        binder.forField(workshop).bind("workshop");
        RepairStatus fixed = creating ? null : existing.state();
        Binder.Binding<VehicleRepairForm, LocalDate> from = binder.forField(plannedFrom)
                .withValidator(date -> date != null || (fixed == null ? status.getValue() : fixed) != RepairStatus.PLANNED, PLANNED_FROM_REQUIRED)
                .bind("plannedFrom");
        Binder.Binding<VehicleRepairForm, LocalDate> to = binder.forField(plannedTo)
                .withValidator(date -> date == null || plannedFrom.getValue() == null || !date.isBefore(plannedFrom.getValue()), END_BEFORE_START)
                .bind("plannedTo");
        status.addValueChangeListener(change -> from.validate());
        plannedFrom.addValueChangeListener(change -> {
            if (plannedTo.getValue() != null) {
                to.validate();
            }
        });
        binder.forField(odometer).bind("odometerKm");
        binder.forField(hours).withValidator(Numbers.all(Numbers.nonNegative(), Numbers.digits(9, 1))).bind("engineHours");
        binder.forField(cost).withValidator(Numbers.all(Numbers.nonNegative(), Numbers.digits(10, 2))).bind("cost");
        binder.forField(notes).bind("notes");

        FormLayout layout = new FormLayout(status, kind, immobilises, description, workshop, plannedFrom, plannedTo, odometer, hours, cost, notes);
        layout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1), new FormLayout.ResponsiveStep("480px", 2));
        layout.setColspan(status, 2);
        layout.setColspan(description, 2);
        layout.setColspan(notes, 2);
        binder.readBean(form);
        add(layout);

        Button save = new Button("Guardar", click -> save(vehicle, existing, clients, saved));
        save.setId(SAVE_ID);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(new Button("Cancelar", click -> close()), save);
    }

    private void save(VehicleDto vehicle, VehicleRepairDto existing, ResourcesClients clients, Runnable saved) {
        if (!binder.writeBeanIfValid(form)) {
            return;
        }
        try {
            VehicleRepairDto result;
            if (existing == null) {
                result = clients.vehicles().addRepair(vehicle.id(), form.toRequest());
            } else {
                MergePatch<VehicleRepairUpdateRequest> patch = form.toPatch(existing);
                if (patch.changesNothing()) {
                    close();
                    return;
                }
                result = clients.vehicles().updateRepair(vehicle.id(), existing.id(), patch);
            }
            close();
            Screens.success("Guardada la reparacion " + result.code());
            saved.run();
        } catch (ValidationApiException validation) {
            Screens.showValidation(binder, validation);
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }
}
