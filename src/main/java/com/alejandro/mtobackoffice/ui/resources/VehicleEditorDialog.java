package com.alejandro.mtobackoffice.ui.resources;

import com.alejandro.mtobackoffice.client.dto.MergePatch;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleDto;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleOperationalStatus;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleOwner;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleTypeFilter;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleTypeSummaryDto;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleUpdateRequest;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.error.ValidationApiException;
import com.alejandro.mtobackoffice.ui.support.Numbers;
import com.alejandro.mtobackoffice.ui.support.Required;
import com.alejandro.mtobackoffice.ui.support.Screens;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Alta y modificacion de un vehiculo. Exige lo que pide su modelo, como en mto-frontend: matricula si
 * va por carretera (y ninguna en uno de solo via), numero ferroviario si su modelo puede ir por via
 * abierta (y ninguno en uno de solo carretera), la empresa si no es propio y el motivo si no esta
 * operativo. Que la matricula o el numero no los tenga otro, o que el modelo no este retirado, lo dice
 * el servicio (409 {@code VEH-409}, {@code VEH-001}) con el dialogo abierto. El dispositivo GPS es
 * opcional (hasta 64 letras sin tildes, cifras, '.', ':', '-' y '_', comprobado antes de llamar), vaciarlo
 * viaja a {@code null}, y que no lo tenga otro vehiculo lo dice el servicio.
 */
public class VehicleEditorDialog extends Dialog {

    public static final String SAVE_ID = "vehicle-save";
    static final int TELEMATICS_LENGTH = 64;
    static final Pattern TELEMATICS_PATTERN = Pattern.compile("[A-Za-z0-9_.:-]+");
    static final String TELEMATICS_FORMAT = "Solo letras sin tildes, cifras, '.', ':', '-' y '_'";
    private static final List<String> BY_CODE = List.of("code,asc");

    private final Binder<VehicleForm> binder = new Binder<>(VehicleForm.class);
    private final VehicleForm form;
    private final ComboBox<VehicleTypeSummaryDto> type = new ComboBox<>("Modelo");
    private final TextField plate = text("vehicle-plate", "Matricula", 20);
    private final TextField railNumber = text("vehicle-rail-number", "Numero de vehiculo ferroviario", 30);
    private final Select<VehicleOwner> owner = new Select<>();
    private final TextField company = text("vehicle-owner-company", "Empresa", 150);
    private final Select<VehicleOperationalStatus> status = new Select<>();
    private final TextField reason = text("vehicle-status-reason", "Motivo", 500);

    public VehicleEditorDialog(VehicleDto existing, ResourcesClients clients, Consumer<VehicleDto> saved) {
        boolean creating = existing == null;
        this.form = VehicleForm.of(existing);
        setHeaderTitle(creating ? "Nuevo vehiculo" : "Modificar " + existing.label());
        setCloseOnOutsideClick(false);
        setWidth("min(95vw, 760px)");

        TextField code = text("vehicle-code", "Codigo (numero de flota)", 32);
        code.setReadOnly(!creating);
        TextField name = text("vehicle-name", "Nombre", 150);
        type.setId("vehicle-type");
        type.setItemLabelGenerator(VehicleTypeSummaryDto::label);
        type.setPlaceholder("Escribe su codigo o su nombre");
        type.setItems(query -> {
            try {
                return clients.vehicleTypes().search(new VehicleTypeFilter(query.getFilter().orElse(""), null, null, null, true), query.getPage(),
                        query.getPageSize(), BY_CODE).content().stream().map(VehicleTypeSummaryDto::of);
            } catch (BackofficeApiException failure) {
                UiErrors.show(failure);
                return Stream.empty();
            }
        });
        TextField serial = text("vehicle-serial", "Numero de serie", 60);
        IntegerField year = new IntegerField("Ano de fabricacion");
        year.setId("vehicle-year");
        year.setMin(1900);
        year.setMax(2200);
        year.setI18n(new IntegerField.IntegerFieldI18n().setMinErrorMessage("Entre 1900 y 2200").setMaxErrorMessage("Entre 1900 y 2200")
                .setBadInputErrorMessage(Numbers.UNREADABLE));
        owner.setLabel("Propiedad");
        owner.setId("vehicle-owner");
        owner.setItems(VehicleOwner.selectable());
        owner.setItemLabelGenerator(value -> value == null ? "" : value.label());
        status.setLabel("Estado operativo");
        status.setId("vehicle-status");
        status.setItems(VehicleOperationalStatus.selectable());
        status.setItemLabelGenerator(value -> value == null ? "" : value.label());
        IntegerField odometer = Numbers.atLeast(new IntegerField("Km"), 0, "No puede ser negativo");
        odometer.setId("vehicle-odometer");
        BigDecimalField hours = new BigDecimalField("Horas de motor");
        hours.setId("vehicle-engine-hours");
        TextField telematics = text("vehicle-telematics-device", "Dispositivo GPS", TELEMATICS_LENGTH);
        telematics.setHelperText("Por el que llegan sus posiciones; vacio, sin GPS");
        TextArea notes = new TextArea("Notas");
        notes.setId("vehicle-notes");

        binder.forField(code).asRequired(Required.text("El codigo es obligatorio")).bind("code");
        binder.forField(name).bind("name");
        binder.forField(type).asRequired("El modelo es obligatorio").bind("typeId");
        binder.forField(plate).withValidator(value -> type.getValue() == null || !VehicleForm.hasPlate(type.getValue()) || !value.isBlank(),
                "Va por carretera: lleva su matricula").bind("registrationPlate");
        binder.forField(railNumber)
                .withValidator(value -> type.getValue() == null || !Boolean.TRUE.equals(type.getValue().transportModeAuthorised()) || !value.isBlank(),
                        "Su modelo va por via abierta: lleva su numero de vehiculo ferroviario")
                .bind("railVehicleNumber");
        binder.forField(serial).bind("serialNumber");
        binder.forField(year).bind("manufactureYear");
        binder.forField(owner).asRequired("La propiedad es obligatoria").bind("owner");
        binder.forField(company).withValidator(value -> owner.getValue() == VehicleOwner.OWN || !value.isBlank(),
                "Uno que no es propio dice de quien es").bind("ownerCompany");
        binder.forField(status).asRequired("El estado es obligatorio").bind("operationalStatus");
        binder.forField(reason).withValidator(value -> status.getValue() == VehicleOperationalStatus.OPERATIONAL || !value.isBlank(),
                "Uno que no esta operativo dice por que").bind("statusReason");
        binder.forField(odometer).bind("odometerKm");
        binder.forField(hours).withValidator(Numbers.all(Numbers.nonNegative(), Numbers.digits(9, 1))).bind("engineHours");
        binder.forField(telematics).withValidator(value -> value.isBlank() || TELEMATICS_PATTERN.matcher(value.trim()).matches(),
                TELEMATICS_FORMAT).bind("telematicsDeviceId");
        binder.forField(notes).bind("notes");

        FormLayout layout = new FormLayout(code, name, type, plate, railNumber, serial, year, owner, company, status, reason, odometer, hours,
                telematics, notes);
        layout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1), new FormLayout.ResponsiveStep("480px", 2));
        add(layout);
        binder.readBean(form);
        type.addValueChangeListener(change -> typeChanged());
        owner.addValueChangeListener(change -> company.setVisible(change.getValue() != VehicleOwner.OWN));
        status.addValueChangeListener(change -> reason.setVisible(change.getValue() != VehicleOperationalStatus.OPERATIONAL));
        typeChanged();
        company.setVisible(form.getOwner() != VehicleOwner.OWN);
        reason.setVisible(form.getOperationalStatus() != VehicleOperationalStatus.OPERATIONAL);

        Button save = new Button("Guardar", click -> save(existing, clients, saved));
        save.setId(SAVE_ID);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(new Button("Cancelar", click -> close()), save);
    }

    /** El modelo decide que se ofrece: sin matricula en uno de solo via, sin numero ferroviario en uno de solo carretera. */
    private void typeChanged() {
        VehicleTypeSummaryDto chosen = type.getValue();
        plate.setVisible(VehicleForm.hasPlate(chosen));
        plate.setRequiredIndicatorVisible(chosen != null && VehicleForm.hasPlate(chosen));
        railNumber.setVisible(VehicleForm.hasRailNumber(chosen));
        railNumber.setRequiredIndicatorVisible(chosen != null && Boolean.TRUE.equals(chosen.transportModeAuthorised()));
    }

    private static TextField text(String id, String label, int maxLength) {
        TextField field = new TextField(label);
        field.setId(id);
        field.setMaxLength(maxLength);
        return field;
    }

    private void save(VehicleDto existing, ResourcesClients clients, Consumer<VehicleDto> saved) {
        if (!binder.writeBeanIfValid(form)) {
            return;
        }
        try {
            VehicleDto result;
            if (existing == null) {
                result = clients.vehicles().create(form.toRequest());
            } else {
                MergePatch<VehicleUpdateRequest> patch = form.toPatch(existing);
                if (patch.changesNothing()) {
                    close();
                    return;
                }
                result = clients.vehicles().update(existing.id(), patch);
            }
            close();
            Screens.success("Guardado " + result.label());
            saved.accept(result);
        } catch (ValidationApiException validation) {
            Screens.showValidation(binder, validation);
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }
}
