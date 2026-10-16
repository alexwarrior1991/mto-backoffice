package com.alejandro.mtobackoffice.ui.resources;

import com.alejandro.mtobackoffice.client.dto.MergePatch;
import com.alejandro.mtobackoffice.client.dto.resources.ParkingSiteDto;
import com.alejandro.mtobackoffice.client.dto.resources.ParkingSiteKind;
import com.alejandro.mtobackoffice.client.dto.resources.ParkingSiteUpdateRequest;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.error.ValidationApiException;
import com.alejandro.mtobackoffice.ui.master.RefItem;
import com.alejandro.mtobackoffice.ui.support.Numbers;
import com.alejandro.mtobackoffice.ui.support.Required;
import com.alejandro.mtobackoffice.ui.support.Screens;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Validator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Alta y modificacion de un sitio de aparcamiento. Lo evidente se comprueba antes de llamar: se llega
 * por via, por carretera o por las dos; con acceso por via dice su via; las coordenadas van juntas y
 * dentro de su rango, y el kp con su columna. EP, estacion y via son de mto-configuration: sin
 * {@code config-read} se ven como {@code #id} y no viajan.
 */
public class ParkingSiteEditorDialog extends Dialog {

    public static final String SAVE_ID = "parking-site-save";
    static final int CODE_LENGTH = 32;
    static final int NAME_LENGTH = 150;
    static final int ADDRESS_LENGTH = 255;

    private final Binder<ParkingSiteForm> binder = new Binder<>(ParkingSiteForm.class);
    private final ParkingSiteForm form;
    private final ResourcesNames names;
    private final ComboBox<RefItem> track = new ComboBox<>("Via");
    private final ComboBox<RefItem> station = new ComboBox<>("Estacion");
    private final Checkbox railAccess = new Checkbox("Acceso por via");
    private final Checkbox roadAccess = new Checkbox("Acceso por carretera");

    public ParkingSiteEditorDialog(ParkingSiteDto existing, ResourcesClients clients, ResourcesNames names, Runnable saved) {
        this.names = names;
        boolean creating = existing == null;
        this.form = ParkingSiteForm.of(existing, names);
        setHeaderTitle(creating ? "Nuevo sitio de aparcamiento" : "Modificar " + existing.label());
        setCloseOnOutsideClick(false);
        setWidth("min(95vw, 760px)");

        TextField code = new TextField("Codigo");
        code.setId("parking-site-code");
        code.setMaxLength(CODE_LENGTH);
        code.setReadOnly(!creating);
        TextField name = new TextField("Nombre");
        name.setId("parking-site-name");
        name.setMaxLength(NAME_LENGTH);
        Select<ParkingSiteKind> kind = new Select<>();
        kind.setLabel("Tipo");
        kind.setId("parking-site-kind");
        kind.setItems(ParkingSiteKind.selectable());
        kind.setItemLabelGenerator(value -> value == null ? "" : value.label());
        railAccess.setId("parking-site-rail-access");
        roadAccess.setId("parking-site-road-access");
        BigDecimalField kp = new BigDecimalField("KP");
        kp.setId("parking-site-kp");
        TextField address = new TextField("Direccion");
        address.setId("parking-site-address");
        address.setMaxLength(ADDRESS_LENGTH);
        BigDecimalField latitude = new BigDecimalField("Latitud");
        latitude.setId("parking-site-latitude");
        BigDecimalField longitude = new BigDecimalField("Longitud");
        longitude.setId("parking-site-longitude");
        TextArea notes = new TextArea("Notas");
        notes.setId("parking-site-notes");

        binder.forField(code).asRequired(Required.text("El codigo es obligatorio")).bind("code");
        binder.forField(name).asRequired(Required.text("El nombre es obligatorio")).bind("name");
        binder.forField(kind).asRequired("El tipo es obligatorio").bind("kind");
        // Sin config-read la via no se elige aqui: el acceso por via vale si el sitio ya tenia via.
        binder.forField(railAccess)
                .withValidator(value -> value || roadAccess.getValue(), "Un sitio se alcanza por via, por carretera o por las dos")
                .withValidator(value -> names.readsConfiguration() || !value || form.getTrackId() != null, "Con acceso por via dice su via")
                .bind("railAccess");
        binder.forField(roadAccess).bind("roadAccess");
        binder.forField(kp).withValidator(Numbers.kp()).bind("kp");
        binder.forField(address).bind("address");
        binder.forField(latitude)
                .withValidator(Numbers.all(range(90), Numbers.digits(3, 6)))
                .withValidator(value -> (value == null) == (longitude.getValue() == null), "Las coordenadas van juntas: latitud y longitud")
                .bind("latitude");
        binder.forField(longitude).withValidator(Numbers.all(range(180), Numbers.digits(3, 6))).bind("longitude");
        binder.forField(notes).bind("notes");

        FormLayout layout = new FormLayout(code, name, kind);
        layout.add(configurationFields(existing));
        layout.add(kp, railAccess, roadAccess, address, latitude, longitude, notes);
        layout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1), new FormLayout.ResponsiveStep("480px", 2));
        add(layout);
        binder.readBean(form);

        Button save = new Button("Guardar", click -> save(existing, clients, saved));
        save.setId(SAVE_ID);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(new Button("Cancelar", click -> close()), save);
    }

    /** EP, estacion y via de mto-configuration; sin {@code config-read}, lo leido como {@code #id} y de solo lectura. */
    private Component[] configurationFields(ParkingSiteDto existing) {
        if (!names.readsConfiguration()) {
            TextField where = new TextField("EP, estacion y via");
            where.setId("parking-site-where-readonly");
            where.setReadOnly(true);
            where.setValue(existing == null ? "" : ResourcesHistory.join(names.packageName(existing.executionPackageId()),
                    names.stationName(existing.stationId()), names.trackName(existing.trackId())));
            where.setHelperText("Sin permiso para leer infraestructura: se quedan como estan");
            return new Component[]{where};
        }
        ComboBox<RefItem> executionPackage = new ComboBox<>("EP");
        executionPackage.setId("parking-site-package");
        executionPackage.setItems(withCurrent(names.packages(), form.getExecutionPackageId()));
        executionPackage.setItemLabelGenerator(RefItem::label);
        executionPackage.setClearButtonVisible(true);
        station.setId("parking-site-station");
        station.setItemLabelGenerator(RefItem::label);
        station.setClearButtonVisible(true);
        track.setId("parking-site-track");
        track.setItemLabelGenerator(RefItem::label);
        track.setClearButtonVisible(true);
        refreshPlaces(form.getExecutionPackageId() == null ? null : form.getExecutionPackageId().id());
        binder.forField(executionPackage).bind("executionPackageId");
        binder.forField(station).bind("stationId");
        binder.forField(track).withValidator(value -> !railAccess.getValue() || value != null, "Con acceso por via dice su via").bind("trackId");
        executionPackage.addValueChangeListener(change -> refreshPlaces(change.getValue() == null ? null : change.getValue().id()));
        return new Component[]{executionPackage, station, track};
    }

    /** Las estaciones y vias del EP elegido (o todas); lo ya elegido se queda si sigue en la lista. */
    private void refreshPlaces(Long packageId) {
        RefItem chosenTrack = track.getValue() != null ? track.getValue() : form.getTrackId();
        RefItem chosenStation = station.getValue() != null ? station.getValue() : form.getStationId();
        List<RefItem> tracks = withCurrent(names.tracks(packageId), chosenTrack);
        List<RefItem> stations = withCurrent(names.stations(packageId), chosenStation);
        track.setItems(tracks);
        station.setItems(stations);
        track.setValue(chosenTrack != null && tracks.contains(chosenTrack) ? chosenTrack : null);
        station.setValue(chosenStation != null && stations.contains(chosenStation) ? chosenStation : null);
    }

    /** La lista con lo ya elegido aunque no este en ella (otro EP, o algo que ya no existe), para no leerlo como vacio. */
    private static List<RefItem> withCurrent(List<RefItem> items, RefItem current) {
        if (current == null || items.contains(current)) {
            return items;
        }
        List<RefItem> all = new ArrayList<>(items);
        all.addFirst(current);
        return all;
    }

    private static Validator<BigDecimal> range(int limit) {
        return Validator.from(value -> value == null || value.abs().compareTo(BigDecimal.valueOf(limit)) <= 0,
                "Entre -" + limit + " y " + limit);
    }

    private void save(ParkingSiteDto existing, ResourcesClients clients, Runnable saved) {
        if (!binder.writeBeanIfValid(form)) {
            return;
        }
        try {
            ParkingSiteDto result;
            if (existing == null) {
                result = clients.parkingSites().create(form.toRequest(names.readsConfiguration()));
            } else {
                MergePatch<ParkingSiteUpdateRequest> patch = form.toPatch(existing, names.readsConfiguration());
                if (patch.changesNothing()) {
                    close();
                    return;
                }
                result = clients.parkingSites().update(existing.id(), patch);
            }
            close();
            Screens.success("Guardado " + result.label());
            saved.run();
        } catch (ValidationApiException validation) {
            Screens.showValidation(binder, validation);
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }
}
