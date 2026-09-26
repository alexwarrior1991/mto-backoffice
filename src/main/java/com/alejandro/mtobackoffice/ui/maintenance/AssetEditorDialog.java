package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.dto.maintenance.AssetDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.AssetUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.TrackKind;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.error.ValidationApiException;
import com.alejandro.mtobackoffice.ui.master.Pickers;
import com.alejandro.mtobackoffice.ui.master.RefItem;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import java.math.BigDecimal;

/**
 * Alta de un tramo de via y modificacion de un activo. Un tramo propio se modifica entero salvo el
 * codigo; uno que llega de mto-configuration solo en descripcion e intervalo preventivo, porque su
 * identidad y su localizacion son de alli (el servicio responderia 409 {@code AST-001}). Solo se
 * exige lo evidente: los obligatorios y un KP final mayor que el inicial. La modificacion manda
 * solo lo que cambio, y como {@code null} es «no tocar», lo que tenia valor no se deja vaciar.
 */
public class AssetEditorDialog extends Dialog {

    public static final String SAVE_ID = "asset-save";
    static final int CODE_LENGTH = 64;
    static final int NAME_LENGTH = 255;

    private final Binder<AssetForm> binder = new Binder<>(AssetForm.class);
    private final AssetForm form;

    /**
     * @param existing el activo a modificar, o {@code null} para dar de alta un tramo de via
     * @param saved    que hacer despues de guardar
     */
    public AssetEditorDialog(AssetDto existing, MaintenanceClients clients, MaintenanceNames names, Runnable saved) {
        boolean creating = existing == null;
        this.form = AssetForm.of(existing, names);
        setHeaderTitle(creating ? "Nuevo tramo de via" : "Modificar " + existing.label());
        setCloseOnOutsideClick(false);
        setWidth("min(90vw, 720px)");

        TextArea description = new TextArea("Descripcion");
        description.setId("asset-description");
        IntegerField interval = new IntegerField("Intervalo preventivo (dias)");
        interval.setId("asset-interval");
        interval.setMin(1);
        interval.setHelperText("Cada cuanto toca un preventivo; vacio, sin plan");

        FormLayout layout = new FormLayout();
        layout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1), new FormLayout.ResponsiveStep("480px", 2));
        if (!creating && existing.isSynchronized()) {
            String type = existing.type() == null ? "activo" : existing.type().label().toLowerCase();
            add(new Paragraph("Llega de mto-configuration (" + type + " " + existing.label()
                    + "): aqui solo se cambian la descripcion y el intervalo preventivo. El resto se cambia alli."));
            binder.forField(description).bind("description");
            binder.forField(interval)
                    .withValidator(days -> days == null || days > 0, "Tiene que ser mayor que cero")
                    .withValidator(days -> existing.preventiveIntervalDays() == null || days != null, MaintenanceUi.CANNOT_CLEAR)
                    .bind("preventiveIntervalDays");
            layout.add(description, interval);
            layout.setColspan(description, 2);
        } else {
            bindTrackSection(existing, names, layout, description, interval);
        }
        binder.readBean(form);
        add(layout);

        Button save = new Button("Guardar", click -> save(existing, clients, saved));
        save.setId(SAVE_ID);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(new Button("Cancelar", click -> close()), save);
    }

    private void bindTrackSection(AssetDto existing, MaintenanceNames names, FormLayout layout, TextArea description, IntegerField interval) {
        boolean creating = existing == null;
        TextField code = new TextField("Codigo");
        code.setId("asset-code");
        code.setMaxLength(CODE_LENGTH);
        code.setRequiredIndicatorVisible(true);
        TextField name = new TextField("Nombre");
        name.setId("asset-name");
        name.setMaxLength(NAME_LENGTH);
        name.setRequiredIndicatorVisible(true);
        ComboBox<RefItem> executionPackage = Pickers.reference("Paquete de ejecucion", names.packages());
        executionPackage.setId("asset-package");
        ComboBox<RefItem> track = Pickers.reference("Via", names.tracks());
        track.setId("asset-track");
        track.setRequiredIndicatorVisible(true);
        ComboBox<RefItem> station = Pickers.reference("Estacion", names.stations());
        station.setId("asset-station");
        BigDecimalField startKp = new BigDecimalField("KP inicial");
        startKp.setId("asset-start-kp");
        startKp.setRequiredIndicatorVisible(true);
        BigDecimalField endKp = new BigDecimalField("KP final");
        endKp.setId("asset-end-kp");
        endKp.setRequiredIndicatorVisible(true);
        ComboBox<TrackKind> trackKind = new ComboBox<>("Tipo de via", TrackKind.selectable());
        trackKind.setId("asset-track-kind");
        trackKind.setItemLabelGenerator(TrackKind::label);
        trackKind.setHelperText("Una via desviada solo admite turnos con posesion total");

        if (creating) {
            binder.forField(code).asRequired("El codigo es obligatorio").bind("code");
            layout.add(code);
        }
        binder.forField(name).asRequired("El nombre es obligatorio").bind("name");
        binder.forField(description).bind("description");
        binder.forField(executionPackage)
                .withValidator(ref -> creating || existing.executionPackageId() == null || ref != null, MaintenanceUi.CANNOT_CLEAR)
                .bind("executionPackageId");
        binder.forField(track).asRequired("La via es obligatoria").bind("trackId");
        binder.forField(station)
                .withValidator(ref -> creating || existing.stationId() == null || ref != null, MaintenanceUi.CANNOT_CLEAR)
                .bind("stationId");
        binder.forField(startKp).asRequired("El KP inicial es obligatorio").bind("startKp");
        Binder.Binding<AssetForm, BigDecimal> end = binder.forField(endKp).asRequired("El KP final es obligatorio")
                .withValidator(kp -> startKp.getValue() == null || kp.compareTo(startKp.getValue()) > 0,
                        "El KP final tiene que ser mayor que el inicial")
                .bind("endKp");
        startKp.addValueChangeListener(change -> {
            if (endKp.getValue() != null) {
                end.validate();
            }
        });
        binder.forField(trackKind).asRequired("El tipo de via es obligatorio").bind("trackKind");
        binder.forField(interval)
                .withValidator(days -> days == null || days > 0, "Tiene que ser mayor que cero")
                .withValidator(days -> creating || existing.preventiveIntervalDays() == null || days != null, MaintenanceUi.CANNOT_CLEAR)
                .bind("preventiveIntervalDays");
        layout.add(name, track, trackKind, startKp, endKp, executionPackage, station, interval, description);
        layout.setColspan(description, 2);
    }

    private void save(AssetDto existing, MaintenanceClients clients, Runnable saved) {
        if (!binder.writeBeanIfValid(form)) {
            return;
        }
        try {
            AssetDto result;
            if (existing == null) {
                result = clients.assets().create(form.toRequest());
            } else {
                AssetUpdateRequest request = form.toUpdateRequest(existing);
                if (request.changesNothing()) {
                    close();
                    return;
                }
                result = clients.assets().update(existing.id(), request);
            }
            close();
            MaintenanceUi.success("Guardado " + result.label());
            saved.run();
        } catch (ValidationApiException validation) {
            MaintenanceUi.showValidation(binder, validation);
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }
}
