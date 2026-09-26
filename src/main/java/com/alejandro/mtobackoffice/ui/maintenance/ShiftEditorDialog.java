package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.dto.maintenance.AssetSummaryDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.CatenaryAssetType;
import com.alejandro.mtobackoffice.client.dto.maintenance.MergePatch;
import com.alejandro.mtobackoffice.client.dto.maintenance.PossessionType;
import com.alejandro.mtobackoffice.client.dto.maintenance.ShiftDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.ShiftUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.TeamSummaryDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.error.ValidationApiException;
import com.alejandro.mtobackoffice.ui.master.Pickers;
import com.alejandro.mtobackoffice.ui.master.RefItem;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.function.Consumer;

/**
 * Alta y modificacion de un turno planificado o en curso: fecha, posesion y vias (al menos una) son
 * obligatorias; los seccionadores que se abren se buscan en el servidor. La modificacion manda solo
 * lo que cambio, con vias y seccionadores enteros si cambiaron; lo que tenia valor y el
 * {@code PUT} parcial no sabe vaciar no se deja vaciar. Que una via desviada pida posesion total lo
 * dice el servicio al trabajar sus tareas.
 */
public class ShiftEditorDialog extends Dialog {

    public static final String SAVE_ID = "shift-save";
    static final int TEXT_LENGTH = 120;

    private final Binder<ShiftForm> binder = new Binder<>(ShiftForm.class);
    private final ShiftForm form;

    public ShiftEditorDialog(ShiftDto existing, MaintenanceClients clients, MaintenanceNames names, MaintenanceCatalogs catalogs,
                             Consumer<ShiftDto> saved) {
        boolean creating = existing == null;
        this.form = ShiftForm.of(existing, names);
        setHeaderTitle(creating ? "Nuevo turno" : "Modificar " + existing.code());
        setCloseOnOutsideClick(false);
        setWidth("min(95vw, 820px)");

        DatePicker shiftDate = new DatePicker("Fecha");
        shiftDate.setId("shift-date");
        ComboBox<PossessionType> possession = new ComboBox<>("Posesion", PossessionType.selectable());
        possession.setId("shift-possession");
        possession.setItemLabelGenerator(PossessionType::label);
        possession.setHelperText("Parcial entre semana; los grupos 3 y 5 y las vias desviadas piden total");
        ComboBox<TeamSummaryDto> team = MaintenancePickers.team("Equipo", catalogs.activeTeams());
        team.setId("shift-team");
        MultiSelectComboBox<RefItem> tracks = Pickers.references("Vias", names.tracks());
        tracks.setId("shift-tracks");
        tracks.setRequiredIndicatorVisible(true);
        ComboBox<RefItem> executionPackage = Pickers.reference("Paquete de ejecucion", names.packages());
        executionPackage.setId("shift-package");
        BigDecimalField startKp = new BigDecimalField("KP inicial");
        startKp.setId("shift-start-kp");
        BigDecimalField endKp = new BigDecimalField("KP final");
        endKp.setId("shift-end-kp");
        DateTimePicker plannedStart = new DateTimePicker("Inicio previsto");
        plannedStart.setId("shift-planned-start");
        DateTimePicker plannedEnd = new DateTimePicker("Fin previsto");
        plannedEnd.setId("shift-planned-end");
        MultiSelectComboBox<AssetSummaryDto> disconnectors = MaintenancePickers.assets("Seccionadores que se abren", clients.assets(),
                CatenaryAssetType.DISCONNECTOR);
        disconnectors.setId("shift-disconnectors");
        TextField baseName = new TextField("Base");
        baseName.setId("shift-base");
        baseName.setMaxLength(TEXT_LENGTH);
        baseName.setHelperText("Vacia en el alta: la del equipo");
        TextField vehicle = new TextField("Vehiculo");
        vehicle.setId("shift-vehicle");
        vehicle.setMaxLength(TEXT_LENGTH);
        TextField earthingPoints = new TextField("Puntos de puesta a tierra");
        earthingPoints.setId("shift-earthing");
        earthingPoints.setMaxLength(500);
        TextField parkingPlace = new TextField("Estacionamiento");
        parkingPlace.setId("shift-parking");
        parkingPlace.setMaxLength(255);
        TextArea personnel = new TextArea("Personal");
        personnel.setId("shift-personnel");
        TextField measurementEquipment = new TextField("Equipos de medida");
        measurementEquipment.setId("shift-measurement");
        measurementEquipment.setMaxLength(500);
        TextArea observations = new TextArea("Observaciones");
        observations.setId("shift-observations");

        binder.forField(shiftDate).asRequired("La fecha es obligatoria").bind("shiftDate");
        binder.forField(possession).asRequired("La posesion es obligatoria").bind("possessionType");
        binder.forField(team).bind("teamId");
        binder.forField(tracks).withValidator(selected -> !selected.isEmpty(), "Al menos una via").bind("trackIds");
        binder.forField(executionPackage).bind("executionPackageId");
        binder.forField(startKp).bind("startKp");
        Binder.Binding<ShiftForm, BigDecimal> end = binder.forField(endKp)
                .withValidator(kp -> kp == null || startKp.getValue() == null || kp.compareTo(startKp.getValue()) > 0,
                        "El KP final tiene que ser mayor que el inicial")
                .bind("endKp");
        startKp.addValueChangeListener(change -> end.validate());
        binder.forField(plannedStart).bind("plannedStart");
        Binder.Binding<ShiftForm, LocalDateTime> finish = binder.forField(plannedEnd)
                .withValidator(time -> time == null || plannedStart.getValue() == null || time.isAfter(plannedStart.getValue()),
                        "El fin previsto tiene que ser posterior al inicio")
                .bind("plannedEnd");
        plannedStart.addValueChangeListener(change -> finish.validate());
        binder.forField(disconnectors).bind("blockingDisconnectorIds");
        binder.forField(baseName).bind("baseName");
        binder.forField(vehicle).bind("vehicle");
        binder.forField(earthingPoints).bind("earthingPoints");
        binder.forField(parkingPlace).bind("parkingPlace");
        binder.forField(personnel).bind("personnel");
        binder.forField(measurementEquipment).bind("measurementEquipment");
        binder.forField(observations).bind("observations");
        binder.readBean(form);

        FormLayout layout = new FormLayout(shiftDate, possession, tracks, team, executionPackage, startKp, endKp, plannedStart, plannedEnd,
                disconnectors, baseName, vehicle, earthingPoints, parkingPlace, measurementEquipment, personnel, observations);
        layout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1), new FormLayout.ResponsiveStep("520px", 2));
        layout.setColspan(tracks, 2);
        layout.setColspan(disconnectors, 2);
        layout.setColspan(observations, 2);
        add(layout);

        Button save = new Button("Guardar", click -> save(existing, clients, saved));
        save.setId(SAVE_ID);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(new Button("Cancelar", click -> close()), save);
    }

    /** Un valor que habia no se deja vaciar; {@code wasEmpty} dice si se puede. */
    private void save(ShiftDto existing, MaintenanceClients clients, Consumer<ShiftDto> saved) {
        if (!binder.writeBeanIfValid(form)) {
            return;
        }
        try {
            ShiftDto result;
            if (existing == null) {
                result = clients.shifts().create(form.toRequest());
            } else {
                MergePatch<ShiftUpdateRequest> patch = form.toPatch(existing);
                if (patch.changesNothing()) {
                    close();
                    return;
                }
                result = clients.shifts().update(existing.id(), patch);
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
