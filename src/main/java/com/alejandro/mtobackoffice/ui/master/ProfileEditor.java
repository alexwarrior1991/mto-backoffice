package com.alejandro.mtobackoffice.ui.master;

import com.alejandro.mtobackoffice.client.configuration.LovResource;
import com.alejandro.mtobackoffice.client.configuration.MasterResource;
import com.alejandro.mtobackoffice.client.dto.master.LovRef;
import com.alejandro.mtobackoffice.client.dto.master.ProfileDto;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.validator.RegexpValidator;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Alta o modificacion de un perfil: identificador, KP, via, medidas y las referencias a catalogo
 * (una por campo salvo seccionamientos, anclajes y aparatos de seccionamiento, que son varios).
 * Las mensulas y el seccionador 1:1 no se tocan aqui.
 */
public class ProfileEditor extends MasterEditorDialog<ProfileDto> {

    static final int PROFILE_ID_MAX_LENGTH = 50;
    static final String KP_PATTERN = "\\d+(\\.\\d+)?";

    public ProfileEditor(ProfileDto dto, ReferenceCatalog catalog, LovCatalog lovs,
                         Function<ProfileDto, ProfileDto> saver, Consumer<ProfileDto> onSaved) {
        super(MasterResource.PROFILES, ProfileDto.class, dto, saver, onSaved);

        TextField profileId = text("Identificador", PROFILE_ID_MAX_LENGTH, true);
        TextField kp = text("KP", 13, true);
        kp.setHelperText("Metros, con punto decimal: 10.500");
        ComboBox<RefItem> track = Pickers.reference("Via", catalog.tracks());
        track.setRequiredIndicatorVisible(true);
        IntegerField orderInTrack = new IntegerField("Orden en la via");
        orderInTrack.setReadOnly(true);
        orderInTrack.setHelperText("Lo fija la importacion del maestro");
        ComboBox<LovRef> profileStatus = Pickers.lov("Estado", lovs.of(LovResource.PROFILE_STATUSES));
        profileStatus.setRequiredIndicatorVisible(true);
        ComboBox<LovRef> poleType = Pickers.lov("Tipo de poste", lovs.of(LovResource.POLE_TYPES));
        ComboBox<LovRef> foundation = Pickers.lov("Cimentacion", lovs.of(LovResource.FOUNDATIONS));
        ComboBox<LovRef> anchorageFoundation = Pickers.lov("Cimentacion de anclaje", lovs.of(LovResource.ANCHORAGE_FOUNDATIONS));
        ComboBox<LovRef> portal = Pickers.lov("Portico", lovs.of(LovResource.PORTALS));
        ComboBox<LovRef> returnSupport = Pickers.lov("Soporte de retorno", lovs.of(LovResource.RETURN_SUPPORTS));
        ComboBox<LovRef> supportType = Pickers.lov("Tipo de soporte", lovs.of(LovResource.SUPPORT_TYPES));
        ComboBox<LovRef> assemblyConfiguration = Pickers.lov("Configuracion de montaje", lovs.of(LovResource.ASSEMBLY_CONFIGURATIONS));
        MultiSelectComboBox<LovRef> sectionings = Pickers.lovs("Seccionamientos", lovs.of(LovResource.SECTIONINGS));
        MultiSelectComboBox<LovRef> anchorages = Pickers.lovs("Anclajes", lovs.of(LovResource.ANCHORAGES));
        MultiSelectComboBox<LovRef> sectioningFeedings = Pickers.lovs("Aparatos de seccionamiento y alimentacion", lovs.of(LovResource.DISCONNECTOR_FUNCTIONS));
        BigDecimalField span = new BigDecimalField("Vano hasta el siguiente (m)");
        BigDecimalField heightCantileverSupport = new BigDecimalField("Altura del soporte de mensula (mm)");
        BigDecimalField poleGaugeLocation = new BigDecimalField("Separacion del poste al galibo (mm)");
        BigDecimalField railPoleDistance = new BigDecimalField("Distancia carril-poste (mm, con signo)");

        binder.forField(profileId).asRequired("El identificador es obligatorio").bind("profileId");
        binder.forField(kp).asRequired("El KP es obligatorio")
                .withValidator(new RegexpValidator("Numero con punto decimal, como 10.500", KP_PATTERN)).bind("kp");
        binder.forField(track).asRequired("La via es obligatoria").withConverter(Pickers.refToId(catalog::trackRef)).bind("trackId");
        binder.forField(orderInTrack).bind("orderInTrack");
        binder.forField(profileStatus).asRequired("El estado es obligatorio").bind("profileStatus");
        binder.forField(poleType).bind("poleType");
        binder.forField(foundation).bind("foundation");
        binder.forField(anchorageFoundation).bind("anchorageFoundation");
        binder.forField(portal).bind("portal");
        binder.forField(returnSupport).bind("returnSupport");
        binder.forField(supportType).bind("supportType");
        binder.forField(assemblyConfiguration).bind("assemblyConfiguration");
        binder.forField(sectionings).withConverter(Pickers.lovSetToList()).bind("sectionings");
        binder.forField(anchorages).withConverter(Pickers.lovSetToList()).bind("anchorages");
        binder.forField(sectioningFeedings).withConverter(Pickers.lovSetToList()).bind("sectioningFeedings");
        binder.forField(span).bind("span");
        binder.forField(heightCantileverSupport).bind("heightCantileverSupport");
        binder.forField(poleGaugeLocation).bind("poleGaugeLocation");
        binder.forField(railPoleDistance).bind("railPoleDistance");

        form.add(profileId, kp, track, orderInTrack, profileStatus, poleType, foundation, anchorageFoundation,
                portal, returnSupport, supportType, assemblyConfiguration, sectionings, anchorages, sectioningFeedings,
                span, heightCantileverSupport, poleGaugeLocation, railPoleDistance);
        wide(sectionings);
        wide(anchorages);
        wide(sectioningFeedings);
        ready();
    }
}
