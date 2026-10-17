package com.alejandro.mtobackoffice.ui.master;

import com.alejandro.mtobackoffice.client.configuration.MasterResource;
import com.alejandro.mtobackoffice.client.dto.master.TrackAccessPointDto;
import com.alejandro.mtobackoffice.client.dto.master.TrackAccessPointKind;
import com.alejandro.mtobackoffice.client.dto.master.TrackAccessPointSide;
import com.alejandro.mtobackoffice.ui.support.Numbers;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Alta o modificacion de un punto de acceso a la via (README_API.md §4 quinquies de
 * mto-configuration). La via es obligatoria y la estacion no; el KP va en metros, como el del
 * aislador. Antes de llamar se comprueba lo que dice la columna (el KP y las toneladas no negativos,
 * con sus cifras; la latitud entre -90 y 90 y la longitud entre -180 y 180, con 6 decimales) y que las
 * coordenadas van juntas: si falta una, se marca la que falta, como hace el servicio con su 400. Un
 * nombre repetido en la via es el 409 {@code BUS-002} del servicio, con el dialogo abierto. Un alta
 * empieza admitiendo biviales y activa; la estacion se puede vaciar.
 */
public class TrackAccessPointEditor extends MasterEditorDialog<TrackAccessPointDto> {

    static final int NAME_MAX_LENGTH = 200;
    static final String LATITUDE_MISSING = "Con la longitud va tambien la latitud";
    static final String LONGITUDE_MISSING = "Con la latitud va tambien la longitud";

    public TrackAccessPointEditor(TrackAccessPointDto dto, ReferenceCatalog catalog,
                                  Function<TrackAccessPointDto, TrackAccessPointDto> saver, Consumer<TrackAccessPointDto> onSaved) {
        super(MasterResource.TRACK_ACCESS_POINTS, TrackAccessPointDto.class, dto, saver, onSaved);

        TextField name = text("Nombre", NAME_MAX_LENGTH, true);
        name.setId("access-point-name");
        ComboBox<RefItem> track = Pickers.reference("Via", catalog.tracks());
        track.setId("access-point-track");
        track.setRequiredIndicatorVisible(true);
        ComboBox<RefItem> station = Pickers.reference("Estacion", catalog.stations());
        station.setId("access-point-station");
        station.setHelperText("Vacia si esta en plena via");
        BigDecimalField kp = new BigDecimalField("KP (m)");
        kp.setId("access-point-kp");
        // La clase y el lado son la cadena leida: uno que el servicio estrene se ofrece crudo y vuelve tal cual.
        ComboBox<String> kind = new ComboBox<>("Tipo");
        kind.setId("access-point-kind");
        kind.setItems(withRead(Arrays.stream(TrackAccessPointKind.values()).map(Enum::name).toList(), dto.getKind()));
        kind.setItemLabelGenerator(TrackAccessPointKind::labelOf);
        kind.setRequiredIndicatorVisible(true);
        ComboBox<String> side = new ComboBox<>("Lado");
        side.setId("access-point-side");
        side.setItems(withRead(Arrays.stream(TrackAccessPointSide.values()).map(Enum::name).toList(), dto.getSide()));
        side.setItemLabelGenerator(TrackAccessPointSide::labelOf);
        side.setRequiredIndicatorVisible(true);
        side.setHelperText("En el sentido creciente del KP");
        Checkbox roadRailAccess = new Checkbox("Admite biviales");
        roadRailAccess.setId("access-point-road-rail");
        BigDecimalField maxAxleLoad = new BigDecimalField("Carga maxima por eje (t)");
        maxAxleLoad.setId("access-point-max-axle-load");
        BigDecimalField maxGrossWeight = new BigDecimalField("Peso bruto maximo (t)");
        maxGrossWeight.setId("access-point-max-gross-weight");
        BigDecimalField latitude = new BigDecimalField("Latitud");
        latitude.setId("access-point-latitude");
        latitude.setHelperText("Grados decimales (WGS 84)");
        BigDecimalField longitude = new BigDecimalField("Longitud");
        longitude.setId("access-point-longitude");
        longitude.setHelperText("Grados decimales (WGS 84)");
        Checkbox enabled = new Checkbox("Activo");
        enabled.setId("access-point-enabled");

        binder.forField(name).asRequired("El nombre es obligatorio").bind("name");
        binder.forField(track).asRequired("La via es obligatoria").withConverter(Pickers.refToId(catalog::trackRef)).bind("trackId");
        binder.forField(station).withConverter(Pickers.refToId(catalog::stationRef)).bind("stationId");
        binder.forField(kp).withValidator(Numbers.infrastructureKp()).bind("kp");
        binder.forField(kind).asRequired("Hay que decir de que tipo es").bind("kind");
        binder.forField(side).asRequired("Hay que decir por que lado se entra").bind("side");
        binder.forField(roadRailAccess).bind("roadRailAccess");
        binder.forField(maxAxleLoad).withValidator(Numbers.tonnes()).bind("maxAxleLoadT");
        binder.forField(maxGrossWeight).withValidator(Numbers.tonnes()).bind("maxGrossWeightT");
        // Las dos o ninguna: la que falta es la que se marca, como el 400 del servicio.
        binder.forField(latitude).withValidator(Numbers.coordinate(90))
                .withValidator(value -> value != null || longitude.getValue() == null, LATITUDE_MISSING).bind("latitude");
        binder.forField(longitude).withValidator(Numbers.coordinate(180))
                .withValidator(value -> value != null || latitude.getValue() == null, LONGITUDE_MISSING).bind("longitude");
        binder.forField(enabled).bind("enabled");

        form.add(name, track, station, kp, kind, side, roadRailAccess, maxAxleLoad, maxGrossWeight, latitude, longitude, enabled);
        if (dto.getRoadRailAccess() == null) {
            dto.setRoadRailAccess(Boolean.TRUE);
        }
        if (dto.getEnabled() == null) {
            dto.setEnabled(Boolean.TRUE);
        }
        ready();
    }

    /** Lo que se ofrece, y ademas lo leido si no es ninguno de ellos. */
    private static List<String> withRead(List<String> offered, String read) {
        if (read == null || offered.contains(read)) {
            return offered;
        }
        List<String> items = new ArrayList<>(offered);
        items.add(read);
        return items;
    }
}
