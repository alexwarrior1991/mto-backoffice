package com.alejandro.mtobackoffice.ui.master;

import com.alejandro.mtobackoffice.client.configuration.LovResource;
import com.alejandro.mtobackoffice.client.configuration.MasterResource;
import com.alejandro.mtobackoffice.client.configuration.ProfileClient;
import com.alejandro.mtobackoffice.client.dto.master.DisconnectorDriveType;
import com.alejandro.mtobackoffice.client.dto.master.DisconnectorDto;
import com.alejandro.mtobackoffice.client.dto.master.LovRef;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.TextField;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Alta o modificacion de un seccionador. El perfil se busca en el servidor (son miles) y es
 * opcional: hay seccionadores que no estan en un poste. El estado normal y el accionamiento
 * tambien lo son, y vaciarlos es «sin dato».
 */
public class DisconnectorEditor extends MasterEditorDialog<DisconnectorDto> {

    static final int NAME_MAX_LENGTH = 200;

    public DisconnectorEditor(DisconnectorDto dto, ReferenceCatalog catalog, LovCatalog lovs, ProfileClient profiles,
                              Function<DisconnectorDto, DisconnectorDto> saver, Consumer<DisconnectorDto> onSaved) {
        super(MasterResource.DISCONNECTORS, DisconnectorDto.class, dto, saver, onSaved);

        TextField name = text("Nombre", NAME_MAX_LENGTH, true);
        ComboBox<RefItem> station = Pickers.reference("Estacion", catalog.stations());
        station.setRequiredIndicatorVisible(true);
        ComboBox<RefItem> profile = Pickers.lazyProfile("Perfil", profiles);
        profile.setHelperText("Vacio si el seccionador no esta en un poste");
        ComboBox<LovRef> function = Pickers.lov("Funcion", lovs.of(LovResource.DISCONNECTOR_FUNCTIONS));
        function.setRequiredIndicatorVisible(true);
        Checkbox onLoad = new Checkbox("En carga");
        ComboBox<Boolean> normallyOpen = new ComboBox<>("Estado normal");
        normallyOpen.setItems(Boolean.TRUE, Boolean.FALSE);
        normallyOpen.setItemLabelGenerator(DisconnectorEditor::normalState);
        normallyOpen.setClearButtonVisible(true);
        ComboBox<DisconnectorDriveType> driveType = new ComboBox<>("Accionamiento");
        driveType.setItems(DisconnectorDriveType.values());
        driveType.setItemLabelGenerator(DisconnectorDriveType::label);
        driveType.setClearButtonVisible(true);

        binder.forField(name).asRequired("El nombre es obligatorio").bind("name");
        binder.forField(station).asRequired("La estacion es obligatoria").withConverter(Pickers.refToId(catalog::stationRef)).bind("stationId");
        binder.forField(profile).withConverter(Pickers.refToId(Pickers.profileResolver(profiles), true)).bind("profileId");
        binder.forField(function).asRequired("La funcion es obligatoria").bind("disconnectorFunction");
        binder.forField(onLoad).bind("onLoad");
        binder.forField(normallyOpen).bind("normallyOpen");
        binder.forField(driveType).bind("driveType");

        form.add(name, station, profile, function, onLoad, normallyOpen, driveType);
        if (dto.getOnLoad() == null) {
            dto.setOnLoad(Boolean.FALSE);
        }
        ready();
    }

    static String normalState(Boolean open) {
        return Boolean.TRUE.equals(open) ? "Normalmente abierto" : "Normalmente cerrado";
    }
}
