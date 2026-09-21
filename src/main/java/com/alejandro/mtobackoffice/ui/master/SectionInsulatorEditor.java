package com.alejandro.mtobackoffice.ui.master;

import com.alejandro.mtobackoffice.client.configuration.MasterResource;
import com.alejandro.mtobackoffice.client.dto.master.SectionInsulatorDto;
import com.alejandro.mtobackoffice.client.dto.master.SectionInsulatorInstallationType;
import com.alejandro.mtobackoffice.client.dto.master.SectionInsulatorSwitchDto;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Alta o modificacion de un aislador de seccion y de sus agujas. La via conectada solo tiene
 * sentido en una conexion de vias; en un aislador en medio de una via se deshabilita y se vacia.
 * Las agujas son una coleccion de hijos (README_API.md §4 quater): si nadie las toca van a
 * {@code null} y se quedan como estan; si alguien las toca, va la lista entera y la que no va se
 * borra.
 */
public class SectionInsulatorEditor extends MasterEditorDialog<SectionInsulatorDto> {

    static final int NAME_MAX_LENGTH = 200;

    private final ChildrenEditor<SectionInsulatorSwitchDto> switches;

    public SectionInsulatorEditor(SectionInsulatorDto dto, ReferenceCatalog catalog,
                                  Function<SectionInsulatorDto, SectionInsulatorDto> saver, Consumer<SectionInsulatorDto> onSaved) {
        super(MasterResource.SECTION_INSULATORS, SectionInsulatorDto.class, dto, saver, onSaved);

        TextField name = text("Nombre", NAME_MAX_LENGTH, true);
        ComboBox<RefItem> station = Pickers.reference("Estacion", catalog.stations());
        station.setRequiredIndicatorVisible(true);
        BigDecimalField kp = new BigDecimalField("KP (m)");
        ComboBox<SectionInsulatorInstallationType> installationType = new ComboBox<>("Instalacion");
        installationType.setItems(SectionInsulatorInstallationType.values());
        installationType.setItemLabelGenerator(SectionInsulatorInstallationType::label);
        installationType.setRequiredIndicatorVisible(true);
        ComboBox<RefItem> track = Pickers.reference("Via", catalog.tracks());
        track.setRequiredIndicatorVisible(true);
        ComboBox<RefItem> connectedTrack = Pickers.reference("Via conectada", catalog.tracks());
        Checkbox enabled = new Checkbox("Activo");

        installationType.addValueChangeListener(change -> {
            boolean connection = change.getValue() == SectionInsulatorInstallationType.TRACK_CONNECTION;
            connectedTrack.setEnabled(connection);
            connectedTrack.setRequiredIndicatorVisible(connection);
            if (!connection) {
                connectedTrack.clear();
            }
        });

        binder.forField(name).asRequired("El nombre es obligatorio").bind("name");
        binder.forField(station).asRequired("La estacion es obligatoria").withConverter(Pickers.refToId(catalog::stationRef)).bind("stationId");
        binder.forField(kp).bind("kp");
        binder.forField(installationType).asRequired("Hay que decir como esta instalado").bind("installationType");
        binder.forField(track).asRequired("La via es obligatoria").withConverter(Pickers.refToId(catalog::trackRef)).bind("trackId");
        binder.forField(connectedTrack).withConverter(Pickers.refToId(catalog::trackRef)).bind("connectedTrackId");
        binder.forField(enabled).bind("enabled");

        switches = new ChildrenEditor<>("Agujas", "switches", dto.getSwitches(), Integer.MAX_VALUE, SectionInsulatorSwitchDto::new,
                (child, accepted) -> new SwitchDialog(child, catalog, accepted).open(), grid -> {
                    grid.addColumn(SectionInsulatorSwitchDto::getCode).setHeader("Codigo").setAutoWidth(true);
                    grid.addColumn(SectionInsulatorSwitchDto::getKp).setHeader("KP (m)").setAutoWidth(true);
                    grid.addColumn(SectionInsulatorSwitchDto::turnoutLabel).setHeader("Tangente").setAutoWidth(true);
                    grid.addColumn(child -> child.getTrackId() == null ? "" : catalog.trackName(child.getTrackId())).setHeader("Via").setAutoWidth(true).setFlexGrow(1);
                    grid.addColumn(child -> Boolean.FALSE.equals(child.getEnabled()) ? "No" : "Si").setHeader("Activa").setAutoWidth(true);
                });

        form.add(name, station, kp, installationType, track, connectedTrack, enabled, switches);
        wide(switches);
        if (dto.getEnabled() == null) {
            dto.setEnabled(Boolean.TRUE);
        }
        ready();
    }

    @Override
    protected void prepare(SectionInsulatorDto dto) {
        switches.edited().ifPresent(dto::setSwitches);
    }
}
