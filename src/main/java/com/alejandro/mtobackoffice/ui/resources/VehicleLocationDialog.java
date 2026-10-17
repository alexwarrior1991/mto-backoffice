package com.alejandro.mtobackoffice.ui.resources;

import com.alejandro.mtobackoffice.client.dto.resources.AccessPointDto;
import com.alejandro.mtobackoffice.client.dto.resources.ParkingSiteDto;
import com.alejandro.mtobackoffice.client.dto.resources.ParkingSiteFilter;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleAttachment;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleDto;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleFilter;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleLocationDto;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleLocationKind;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleLocationRequest;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleMobility;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleSummaryDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.error.NotFoundApiException;
import com.alejandro.mtobackoffice.client.error.ValidationApiException;
import com.alejandro.mtobackoffice.ui.master.RefItem;
import com.alejandro.mtobackoffice.ui.support.Formats;
import com.alejandro.mtobackoffice.ui.support.Numbers;
import com.alejandro.mtobackoffice.ui.support.Screens;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.HasValidation;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Registrar donde esta un vehiculo, a mano ({@code resources-location-write}), como en mto-frontend.
 * Solo se ofrecen los tipos que admite su movilidad (uno de solo via nunca va por carretera, uno de
 * solo carretera nunca esta en la via) y los campos de cada tipo; en uno de solo via, solo los sitios
 * con acceso por via, y en uno de solo carretera, los que tienen acceso por carretera. Lo evidente se
 * comprueba antes de llamar; lo demas lo dice el servicio (409 {@code VEH-001}) con el dialogo abierto.
 *
 * <p>Donde se ofrece la via, en cuanto se elige una se ofrece tambien el punto de acceso por el que
 * encarrilo, del maestro de mto-configuration tal como lo tiene mto-resources: los activos de esa via
 * ({@code GET /track-access-points?trackId=&enabled=true}), pedidos cada vez que cambia la via, que lo
 * vacia; si no se pueden leer, lo dice su ayuda, sin aviso. Es opcional y viaja como
 * {@code accessPointId}. Si le vale al vehiculo (biviales, acceso preparado, pesos) lo dice el servicio
 * con su 409 {@code VEH-001}; uno que ya no existe o aun no ha llegado a Recursos es su 404
 * {@code TAP-404}, que vuelve a pedir la lista; los dos con el dialogo abierto. El texto libre se queda,
 * «Acceso (texto)», para una bivial en via o por carretera.</p>
 */
public class VehicleLocationDialog extends Dialog {

    public static final String SAVE_ID = "location-save";
    /** El 404 de mto-resources de un punto de acceso que no conoce. */
    static final String UNKNOWN_ACCESS_POINT = "TAP-404";
    private static final List<String> BY_CODE = List.of("code,asc");
    /** Los sitios caben en una peticion: es un catalogo de decenas. */
    static final int ALL_SITES = 200;

    private final VehicleDto vehicle;
    private final ResourcesNames names;
    private final ResourcesClients clients;
    private final Select<VehicleLocationKind> kind = new Select<>();
    private final ComboBox<ParkingSiteDto> site = new ComboBox<>("Sitio");
    private final ComboBox<RefItem> executionPackage = new ComboBox<>("EP");
    private final ComboBox<RefItem> track = new ComboBox<>("Via");
    private final BigDecimalField kp = new BigDecimalField("KP");
    private final ComboBox<AccessPointDto> accessPoint = new ComboBox<>("Punto de acceso");
    private final TextField accessPointText = new TextField("Acceso (texto)");
    private final BigDecimalField latitude = new BigDecimalField("Latitud");
    private final BigDecimalField longitude = new BigDecimalField("Longitud");
    private final ComboBox<VehicleSummaryDto> carrier = new ComboBox<>("Con");
    private final Select<VehicleAttachment> attachment = new Select<>();
    private final TextArea description = new TextArea("Descripcion");
    private final DateTimePicker observedAt = new DateTimePicker("Cuando");
    /** Cada campo por el nombre del suyo en la peticion, para los errores del servicio. */
    private final Map<String, HasValidation> fields = new LinkedHashMap<>();

    public VehicleLocationDialog(VehicleDto vehicle, ResourcesClients clients, ResourcesNames names, Consumer<VehicleLocationDto> saved) {
        this.vehicle = vehicle;
        this.names = names;
        this.clients = clients;
        setHeaderTitle("Registrar donde esta " + vehicle.label());
        setCloseOnOutsideClick(false);
        setWidth("min(95vw, 720px)");
        VehicleMobility mobility = vehicle.mobility();

        kind.setLabel("Donde esta");
        kind.setId("location-kind");
        kind.setItems(VehicleLocationKind.recordable(mobility));
        kind.setItemLabelGenerator(value -> value == null ? "" : value.label());
        kind.setValue(VehicleLocationKind.PARKED);
        site.setId("location-site");
        site.setItems(sites(clients, mobility));
        site.setItemLabelGenerator(ParkingSiteDto::label);
        site.setClearButtonVisible(true);
        site.setHelperText(mobility == VehicleMobility.RAIL_ONLY ? "Solo los que tienen acceso por via"
                : mobility == VehicleMobility.ROAD_ONLY ? "Solo los que tienen acceso por carretera" : null);
        executionPackage.setId("location-package");
        executionPackage.setItems(names.packages());
        executionPackage.setItemLabelGenerator(RefItem::label);
        executionPackage.setClearButtonVisible(true);
        executionPackage.addValueChangeListener(change -> {
            RefItem chosen = track.getValue();
            List<RefItem> tracks = names.tracks(change.getValue() == null ? null : change.getValue().id());
            track.setItems(tracks);
            track.setValue(chosen != null && tracks.contains(chosen) ? chosen : null);
        });
        track.setId("location-track");
        track.setItems(names.tracks(null));
        track.setItemLabelGenerator(RefItem::label);
        track.setClearButtonVisible(true);
        track.addValueChangeListener(change -> trackChanged(change.getValue()));
        kp.setId("location-kp");
        accessPoint.setId("location-access-point");
        accessPoint.setItemLabelGenerator(AccessPointDto::label);
        accessPoint.setClearButtonVisible(true);
        trackChanged(null);
        accessPointText.setId("location-access-point-text");
        accessPointText.setMaxLength(150);
        accessPointText.setHelperText("Donde encarrilo o desencarrilo, con texto libre: un camino, una rampa provisional");
        latitude.setId("location-latitude");
        longitude.setId("location-longitude");
        carrier.setId("location-carrier");
        carrier.setItemLabelGenerator(VehicleSummaryDto::label);
        carrier.setPlaceholder("Escribe su codigo");
        carrier.setItems(query -> {
            try {
                return clients.vehicles().search(new VehicleFilter(query.getFilter().orElse(""), null, null, null, null, null, true), query.getPage(),
                        query.getPageSize(), BY_CODE).content().stream().filter(other -> !other.id().equals(vehicle.id())).map(VehicleDto::summary);
            } catch (BackofficeApiException failure) {
                UiErrors.show(failure);
                return Stream.empty();
            }
        });
        attachment.setLabel("Como");
        attachment.setId("location-attachment");
        attachment.setItems(VehicleAttachment.selectable());
        attachment.setItemLabelGenerator(value -> value == null ? "" : value.label());
        description.setId("location-description");
        description.setMaxLength(500);
        observedAt.setId("location-observed-at");
        observedAt.setHelperText("Vacio: ahora");
        observedAt.setMax(LocalDateTime.now().plusMinutes(1));

        fields.putAll(Map.of("kind", kind, "parkingSiteId", site, "executionPackageId", executionPackage, "trackId", track, "kp", kp,
                "accessPointId", accessPoint, "accessPoint", accessPointText, "latitude", latitude, "longitude", longitude, "carrierVehicleId", carrier));
        fields.put("attachment", attachment);
        fields.put("description", description);
        fields.put("observedAt", observedAt);

        FormLayout layout = new FormLayout(kind, observedAt, site, executionPackage, track, kp, accessPoint, accessPointText, latitude, longitude,
                carrier, attachment, description);
        layout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1), new FormLayout.ResponsiveStep("480px", 2));
        layout.setColspan(description, 2);
        add(layout);
        kind.addValueChangeListener(change -> kindChanged());
        kindChanged();

        Button save = new Button("Registrar", click -> save(clients, saved));
        save.setId(SAVE_ID);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(new Button("Cancelar", click -> close()), save);
    }

    /** Los sitios activos a los que puede llegar: con acceso por via uno de solo via, por carretera uno de solo carretera. */
    private static List<ParkingSiteDto> sites(ResourcesClients clients, VehicleMobility mobility) {
        try {
            return clients.parkingSites().search(new ParkingSiteFilter(null, null, null, null, mobility == VehicleMobility.RAIL_ONLY ? true : null,
                    mobility == VehicleMobility.ROAD_ONLY ? true : null, true), 0, ALL_SITES, BY_CODE).content();
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
            return List.of();
        }
    }

    /**
     * Los puntos de acceso activos de la via elegida, pedidos a mto-resources cada vez que cambia; sin via
     * no se ofrece. Cambiar de via vacia el elegido: era de la otra. Un fallo al pedirlos no se avisa: lo
     * dice la ayuda del campo, y la ubicacion se puede registrar sin el.
     */
    private void trackChanged(RefItem chosen) {
        accessPoint.clear();
        accessPoint.setInvalid(false);
        accessPointVisibility();
        if (chosen == null || chosen.id() == null) {
            accessPoint.setItems(List.of());
            return;
        }
        try {
            List<AccessPointDto> offered = clients.accessPoints().enabledOnTrack(chosen.id());
            accessPoint.setItems(offered);
            accessPoint.setHelperText(offered.isEmpty() ? "La via no tiene puntos de acceso activos"
                    : "Por donde encarrilo o desencarrilo; vacio si no fue por ninguno");
        } catch (BackofficeApiException failure) {
            accessPoint.setItems(List.of());
            accessPoint.setHelperText("No se han podido leer los puntos de acceso de la via: " + UiErrors.message(failure));
        }
    }

    /** El punto de acceso, donde se ofrece la via y en cuanto hay una elegida. */
    private void accessPointVisibility() {
        accessPoint.setVisible(track.isVisible() && track.getValue() != null);
    }

    /** Los campos de cada tipo; lo que no se ve no viaja. */
    private void kindChanged() {
        VehicleLocationKind current = kind.getValue();
        boolean parked = current == VehicleLocationKind.PARKED;
        boolean onTrack = current == VehicleLocationKind.ON_TRACK;
        boolean onRoad = current == VehicleLocationKind.ON_ROAD;
        boolean roadRail = vehicle.mobility() == VehicleMobility.ROAD_RAIL;
        boolean readsConfiguration = names.readsConfiguration();
        site.setVisible(parked || current == VehicleLocationKind.IN_WORKSHOP);
        executionPackage.setVisible(readsConfiguration && (parked || onTrack || onRoad));
        boolean trackOffered = readsConfiguration && (onTrack || (parked && vehicle.mobility() != VehicleMobility.ROAD_ONLY));
        track.setVisible(trackOffered);
        track.setRequiredIndicatorVisible(onTrack);
        kp.setVisible(trackOffered);
        // El punto de acceso del maestro, donde se ofrece la via y con una elegida; el texto libre, a una bivial
        // en via o por carretera.
        accessPointVisibility();
        accessPointText.setVisible(roadRail && (onTrack || onRoad));
        latitude.setVisible(parked || onRoad);
        longitude.setVisible(parked || onRoad);
        carrier.setVisible(current == VehicleLocationKind.ATTACHED);
        attachment.setVisible(current == VehicleLocationKind.ATTACHED);
        Set.<HasValidation>copyOf(fields.values()).forEach(field -> field.setInvalid(false));
    }

    /** Lo evidente: lo que pide cada tipo y las coordenadas juntas. Marca el campo y dice si paso. */
    private boolean valid() {
        // Lo que dijo el servicio del punto de acceso (TAP-404) vale hasta el siguiente intento.
        accessPoint.setInvalid(false);
        boolean ok = Numbers.check(kp, Numbers.kp());
        ok &= Numbers.check(latitude, Numbers.coordinate(90));
        ok &= Numbers.check(longitude, Numbers.coordinate(180));
        if ((value(latitude) == null) != (value(longitude) == null)) {
            ok = invalid(latitude, "Las coordenadas van juntas: latitud y longitud");
        }
        VehicleLocationKind current = kind.getValue();
        if (current == VehicleLocationKind.ON_TRACK && value(track) == null) {
            ok = invalid(track, "En via dice su via");
        }
        if (current == VehicleLocationKind.PARKED) {
            boolean somewhere = value(site) != null || value(track) != null || !description.getValue().isBlank() || value(latitude) != null;
            if (!somewhere) {
                ok = invalid(site, "Aparcado dice donde: un sitio, una via y un kp, una descripcion o unas coordenadas");
            } else if (vehicle.mobility() == VehicleMobility.RAIL_ONLY && value(site) == null && value(track) == null) {
                ok = invalid(site, "Uno de solo via se aparca sobre una via: un sitio con acceso por via, o una via y un kp");
            }
        }
        if (current == VehicleLocationKind.ATTACHED) {
            if (carrier.getValue() == null) {
                ok = invalid(carrier, "Dice con que vehiculo va");
            }
            if (attachment.getValue() == null) {
                ok = invalid(attachment, "Dice como: remolcado o transportado");
            }
        }
        LocalDateTime when = observedAt.getValue();
        if (when != null && Formats.toInstant(when).isAfter(Instant.now())) {
            ok = invalid(observedAt, "Una ubicacion se registra cuando ya ha pasado");
        }
        return ok;
    }

    private static boolean invalid(HasValidation field, String message) {
        field.setErrorMessage(message);
        field.setInvalid(true);
        return false;
    }

    private static <T> T value(HasValue<?, T> field) {
        return ((Component) field).isVisible() ? field.getValue() : null;
    }

    /** La peticion con lo que se ve; lo vacio no viaja. */
    VehicleLocationRequest request() {
        ParkingSiteDto chosenSite = value(site);
        RefItem chosenPackage = value(executionPackage);
        RefItem chosenTrack = value(track);
        VehicleSummaryDto chosenCarrier = value(carrier);
        AccessPointDto chosenAccessPoint = value(accessPoint);
        String access = value(accessPointText);
        String text = description.getValue();
        LocalDateTime when = observedAt.getValue();
        return new VehicleLocationRequest(kind.getValue(), chosenSite == null ? null : chosenSite.id(),
                chosenPackage == null ? null : chosenPackage.id(), chosenTrack == null ? null : chosenTrack.id(), value(kp), value(latitude),
                value(longitude), chosenAccessPoint == null ? null : chosenAccessPoint.id(), access == null || access.isBlank() ? null : access.trim(),
                text == null || text.isBlank() ? null : text.trim(),
                chosenCarrier == null ? null : chosenCarrier.id(), value(attachment), when == null ? null : Formats.toInstant(when));
    }

    private void save(ResourcesClients clients, Consumer<VehicleLocationDto> saved) {
        if (!valid()) {
            return;
        }
        try {
            VehicleLocationDto recorded = clients.vehicles().recordLocation(vehicle.id(), request());
            close();
            Screens.success("Registrado donde esta " + vehicle.code());
            saved.accept(recorded);
        } catch (ValidationApiException validation) {
            Screens.showValidation(fields, validation);
        } catch (NotFoundApiException missing) {
            // El punto de acceso ya no existe o aun no ha llegado a Recursos: se vuelve a pedir la lista de la
            // via, se dice en su campo y se elige otro.
            if (UNKNOWN_ACCESS_POINT.equals(missing.getProblem().code())) {
                trackChanged(value(track));
                invalid(accessPoint, UiErrors.message(missing));
            }
            UiErrors.show(missing);
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }
}
