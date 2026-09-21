package com.alejandro.mtobackoffice.ui.master;

import com.alejandro.mtobackoffice.client.configuration.MasterResource;
import com.alejandro.mtobackoffice.client.dto.master.TrackDto;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.textfield.TextField;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Alta o modificacion de una via. Las estaciones que atraviesa se mandan como la lista completa
 * ({@code stationIds}): la vacia las desliga todas, que es lo que quiere decir no elegir ninguna.
 * Los perfiles no se tocan aqui.
 */
public class TrackEditor extends MasterEditorDialog<TrackDto> {

    static final int NAME_MAX_LENGTH = 200;

    public TrackEditor(TrackDto dto, ReferenceCatalog catalog, Function<TrackDto, TrackDto> saver, Consumer<TrackDto> onSaved) {
        super(MasterResource.TRACKS, TrackDto.class, dto, saver, onSaved);

        TextField name = text("Nombre", NAME_MAX_LENGTH, true);
        ComboBox<RefItem> executionPackage = Pickers.reference("Paquete de ejecucion", catalog.packages());
        executionPackage.setRequiredIndicatorVisible(true);
        MultiSelectComboBox<RefItem> stations = Pickers.references("Estaciones que atraviesa", catalog.stations());
        Checkbox enabled = new Checkbox("Activa");

        binder.forField(name).asRequired("El nombre es obligatorio").bind("name");
        binder.forField(executionPackage).asRequired("El paquete de ejecucion es obligatorio")
                .withConverter(Pickers.refToId(catalog::packageRef)).bind("executionPackageId");
        binder.forField(stations).withConverter(Pickers.refsToIds(catalog::stationRef)).bind("stationIds");
        binder.forField(enabled).bind("enabled");

        form.add(name, executionPackage, stations, enabled);
        wide(stations);
        if (dto.getEnabled() == null) {
            dto.setEnabled(Boolean.TRUE);
        }
        ready();
    }
}
