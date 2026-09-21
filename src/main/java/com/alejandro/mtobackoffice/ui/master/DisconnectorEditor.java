package com.alejandro.mtobackoffice.ui.master;

import com.alejandro.mtobackoffice.client.configuration.LovResource;
import com.alejandro.mtobackoffice.client.configuration.MasterResource;
import com.alejandro.mtobackoffice.client.configuration.ProfileClient;
import com.alejandro.mtobackoffice.client.dto.master.DisconnectorDto;
import com.alejandro.mtobackoffice.client.dto.master.LovRef;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.TextField;

import java.util.function.Consumer;
import java.util.function.Function;

/** Alta o modificacion de un seccionador. El perfil se busca en el servidor: son miles. */
public class DisconnectorEditor extends MasterEditorDialog<DisconnectorDto> {

    static final int NAME_MAX_LENGTH = 200;

    public DisconnectorEditor(DisconnectorDto dto, ReferenceCatalog catalog, LovCatalog lovs, ProfileClient profiles,
                              Function<DisconnectorDto, DisconnectorDto> saver, Consumer<DisconnectorDto> onSaved) {
        super(MasterResource.DISCONNECTORS, DisconnectorDto.class, dto, saver, onSaved);

        TextField name = text("Nombre", NAME_MAX_LENGTH, true);
        ComboBox<RefItem> station = Pickers.reference("Estacion", catalog.stations());
        station.setRequiredIndicatorVisible(true);
        ComboBox<RefItem> profile = Pickers.lazyProfile("Perfil", profiles);
        profile.setRequiredIndicatorVisible(true);
        ComboBox<LovRef> function = Pickers.lov("Funcion", lovs.of(LovResource.DISCONNECTOR_FUNCTIONS));
        function.setRequiredIndicatorVisible(true);
        Checkbox onLoad = new Checkbox("En carga");

        binder.forField(name).asRequired("El nombre es obligatorio").bind("name");
        binder.forField(station).asRequired("La estacion es obligatoria").withConverter(Pickers.refToId(catalog::stationRef)).bind("stationId");
        binder.forField(profile).asRequired("El perfil es obligatorio")
                .withConverter(Pickers.refToId(Pickers.profileResolver(profiles), true)).bind("profileId");
        binder.forField(function).asRequired("La funcion es obligatoria").bind("disconnectorFunction");
        binder.forField(onLoad).bind("onLoad");

        form.add(name, station, profile, function, onLoad);
        if (dto.getOnLoad() == null) {
            dto.setOnLoad(Boolean.FALSE);
        }
        ready();
    }
}
