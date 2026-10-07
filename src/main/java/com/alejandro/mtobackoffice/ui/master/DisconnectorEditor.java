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
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.converter.Converter;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Alta o modificacion de un seccionador. El perfil se busca en el servidor (son miles) y es
 * opcional: hay seccionadores que no estan en un poste. Uno sin poste lleva su propio KP y su via
 * (V26 de mto-configuration), que con poste son los del perfil: se vacian y no se ofrecen. Uno que
 * pone dos vias en paralelo lleva ademas la otra (V27), con poste o sin el; que no sea la suya lo
 * dice el servicio, en su campo. El estado normal y el accionamiento tambien son opcionales, y
 * vaciarlos es «sin dato».
 */
public class DisconnectorEditor extends MasterEditorDialog<DisconnectorDto> {

    static final int NAME_MAX_LENGTH = 200;
    /** El KP propio es texto, como el del perfil: 9 enteros, el punto y 3 decimales. */
    static final int KP_MAX_LENGTH = 13;

    public DisconnectorEditor(DisconnectorDto dto, ReferenceCatalog catalog, LovCatalog lovs, ProfileClient profiles,
                              Function<DisconnectorDto, DisconnectorDto> saver, Consumer<DisconnectorDto> onSaved) {
        super(MasterResource.DISCONNECTORS, DisconnectorDto.class, dto, saver, onSaved);

        TextField name = text("Nombre", NAME_MAX_LENGTH, true);
        ComboBox<RefItem> station = Pickers.reference("Estacion", catalog.stations());
        station.setRequiredIndicatorVisible(true);
        ComboBox<RefItem> profile = Pickers.lazyProfile("Perfil", profiles);
        profile.setHelperText("Vacio si el seccionador no esta en un poste");
        TextField kp = text("KP propio (m)", KP_MAX_LENGTH, false);
        kp.setHelperText("Solo sin poste: con poste, el del perfil");
        ComboBox<RefItem> track = Pickers.reference("Via propia", catalog.tracks());
        track.setHelperText("Solo sin poste: con poste, la del perfil");
        ComboBox<RefItem> connectedTrack = Pickers.reference("Via conectada", catalog.tracks());
        connectedTrack.setHelperText("La otra via, si pone dos en paralelo");
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
        // Recortado antes de comprobarlo y de enviarlo, como el del perfil; vacio viaja como null.
        binder.forField(kp)
                .withConverter(Converter.<String, String>from(
                        text -> Result.ok(text == null || text.isBlank() ? null : text.trim()),
                        value -> value == null ? "" : value))
                .withValidator(value -> value == null || value.matches(ProfileEditor.KP_PATTERN),
                        "Numero con punto decimal, como 98375.500")
                .bind("kp");
        binder.forField(track).withConverter(Pickers.refToId(catalog::trackRef)).bind("trackId");
        binder.forField(connectedTrack).withConverter(Pickers.refToId(catalog::trackRef)).bind("connectedTrackId");
        profile.addValueChangeListener(change -> ownLocation(change.getValue() == null, kp, track));
        binder.forField(function).asRequired("La funcion es obligatoria").bind("disconnectorFunction");
        binder.forField(onLoad).bind("onLoad");
        binder.forField(normallyOpen).bind("normallyOpen");
        binder.forField(driveType).bind("driveType");

        form.add(name, station, profile, kp, track, connectedTrack, function, onLoad, normallyOpen, driveType);
        if (dto.getOnLoad() == null) {
            dto.setOnLoad(Boolean.FALSE);
        }
        ready();
        ownLocation(profile.getValue() == null, kp, track);
    }

    /** El KP y la via propios, solo sin poste: al elegir un poste se vacian, porque son los suyos. */
    private static void ownLocation(boolean withoutPole, TextField kp, ComboBox<RefItem> track) {
        kp.setEnabled(withoutPole);
        track.setEnabled(withoutPole);
        if (!withoutPole) {
            kp.clear();
            track.clear();
        }
    }

    static String normalState(Boolean open) {
        return Boolean.TRUE.equals(open) ? "Normalmente abierto" : "Normalmente cerrado";
    }
}
