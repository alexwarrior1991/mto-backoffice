package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.dto.maintenance.ShiftVehicleDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.VehicleParkingRequest;
import com.alejandro.mtobackoffice.client.dto.resources.ParkingSiteDto;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleMobility;
import com.alejandro.mtobackoffice.ui.master.Pickers;
import com.alejandro.mtobackoffice.ui.master.RefItem;
import com.alejandro.mtobackoffice.ui.support.Numbers;
import com.vaadin.flow.component.HasValidation;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Donde se deja cada vehiculo de un turno al cerrarlo: una fila por vehiculo con su sitio de
 * mto-resources (los que alcanza por su movilidad, con {@code resources-read}), su via y su kp (con
 * {@code config-read}, y no en uno de solo carretera) o un texto. Solo viaja lo rellenado; sin nada, vale
 * el estacionamiento del turno. Lo evidente se dice en su fila antes de llamar (un kp va con su via, uno
 * de solo via no se deja con solo un texto); lo demas lo dice el servicio con 409 {@code SHF-001} y el
 * dialogo abierto, y sus errores por campo ({@code parkings[0].kp}) caen tambien en su fila.
 */
final class ParkingRows extends VerticalLayout {

    private final List<Row> rows = new ArrayList<>();

    ParkingRows(List<ShiftVehicleDto> vehicles, MaintenanceNames names) {
        setPadding(false);
        setSpacing(false);
        add(new H4("Donde queda cada vehiculo"));
        add(new Paragraph("Solo viaja lo rellenado; un vehiculo sin nada se queda en el estacionamiento del turno."));
        for (int index = 0; index < vehicles.size(); index++) {
            Row row = new Row(index, vehicles.get(index), names);
            rows.add(row);
            add(row.layout);
        }
    }

    /** Comprueba cada fila; las que no estan bien quedan marcadas. */
    boolean validate() {
        boolean valid = true;
        for (Row row : rows) {
            valid &= row.validate();
        }
        return valid;
    }

    /** Lo rellenado, una peticion por vehiculo; {@code null} si no hay nada (vale el estacionamiento del turno). */
    List<VehicleParkingRequest> requests() {
        List<VehicleParkingRequest> requests = rows.stream().map(Row::request).filter(Objects::nonNull).toList();
        return requests.isEmpty() ? null : requests;
    }

    /** Los campos de cada fila con el nombre que les da el servicio, por su posicion en lo que se mando. */
    Map<String, HasValidation> fields() {
        Map<String, HasValidation> fields = new HashMap<>();
        int sent = 0;
        for (Row row : rows) {
            if (row.request() == null) {
                continue;
            }
            String prefix = "parkings[" + sent++ + "].";
            fields.put(prefix + "description", row.description);
            fields.put(prefix + "vehicleId", row.description);
            if (row.site != null) {
                fields.put(prefix + "parkingSiteId", row.site);
            }
            if (row.track != null) {
                fields.put(prefix + "trackId", row.track);
                fields.put(prefix + "kp", row.kp);
            }
        }
        return fields;
    }

    private static final class Row {

        private final ShiftVehicleDto vehicle;
        private final FormLayout layout = new FormLayout();
        private final ComboBox<ParkingSiteDto> site;
        private final ComboBox<RefItem> track;
        private final BigDecimalField kp;
        private final TextField description = new TextField("Descripcion");

        private Row(int index, ShiftVehicleDto vehicle, MaintenanceNames names) {
            this.vehicle = vehicle;
            VehicleMobility mobility = vehicle.mobility();
            Div name = new Div(vehicle.label());
            name.setId("parking-vehicle-" + index);
            layout.add(name);
            if (names.readsResources()) {
                site = new ComboBox<>("Sitio", names.parkingSites().stream().filter(candidate -> reaches(candidate, mobility)).toList());
                site.setItemLabelGenerator(ParkingSiteDto::label);
                site.setClearButtonVisible(true);
                site.setId("parking-site-" + index);
                layout.add(site);
            } else {
                site = null;
            }
            if (names.readsConfiguration() && mobility != VehicleMobility.ROAD_ONLY) {
                track = Pickers.reference("Via", names.tracks());
                track.setId("parking-track-" + index);
                kp = new BigDecimalField("KP");
                kp.setId("parking-kp-" + index);
                layout.add(track, kp);
            } else {
                track = null;
                kp = null;
            }
            description.setId("parking-description-" + index);
            description.setMaxLength(500);
            layout.add(description);
            layout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1), new FormLayout.ResponsiveStep("520px", 5));
        }

        /** Uno de solo via, en un sitio con acceso por via; uno de solo carretera, en uno con acceso por carretera. */
        private static boolean reaches(ParkingSiteDto site, VehicleMobility mobility) {
            if (mobility == VehicleMobility.RAIL_ONLY) {
                return Boolean.TRUE.equals(site.railAccess());
            }
            if (mobility == VehicleMobility.ROAD_ONLY) {
                return Boolean.TRUE.equals(site.roadAccess());
            }
            return true;
        }

        private boolean validate() {
            description.setInvalid(false);
            if (track != null) {
                track.setInvalid(false);
                if (!Numbers.check(kp, Numbers.kp())) {
                    return false;
                }
                if (kp.getValue() != null && track.getValue() == null) {
                    track.setErrorMessage("Un KP va con su via");
                    track.setInvalid(true);
                    return false;
                }
            }
            VehicleParkingRequest request = request();
            if (request != null && vehicle.mobility() == VehicleMobility.RAIL_ONLY && request.parkingSiteId() == null && request.trackId() == null) {
                description.setErrorMessage("Uno de solo via se deja sobre una via: un sitio o una via");
                description.setInvalid(true);
                return false;
            }
            return true;
        }

        private VehicleParkingRequest request() {
            ParkingSiteDto chosenSite = site == null ? null : site.getValue();
            RefItem chosenTrack = track == null ? null : track.getValue();
            BigDecimal chosenKp = kp == null ? null : kp.getValue();
            String text = description.getValue() == null || description.getValue().isBlank() ? null : description.getValue().trim();
            if (chosenSite == null && chosenTrack == null && chosenKp == null && text == null) {
                return null;
            }
            return new VehicleParkingRequest(vehicle.vehicleId(), chosenSite == null ? null : chosenSite.id(),
                    chosenTrack == null ? null : chosenTrack.id(), chosenKp, text);
        }
    }
}
