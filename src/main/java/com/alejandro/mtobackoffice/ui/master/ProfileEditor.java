package com.alejandro.mtobackoffice.ui.master;

import com.alejandro.mtobackoffice.client.configuration.DisconnectorClient;
import com.alejandro.mtobackoffice.client.configuration.LovResource;
import com.alejandro.mtobackoffice.client.configuration.MasterResource;
import com.alejandro.mtobackoffice.client.dto.master.CantileverDto;
import com.alejandro.mtobackoffice.client.dto.master.DisconnectorDto;
import com.alejandro.mtobackoffice.client.dto.master.LovRef;
import com.alejandro.mtobackoffice.client.dto.master.ProfileDto;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.validator.RegexpValidator;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Alta o modificacion de un perfil: identificador, KP, via, medidas y las referencias a catalogo
 * (una por campo salvo seccionamientos, anclajes y aparatos de seccionamiento, que son varios),
 * sus mensulas (hasta tres, cada una con su brazo de atirantado) y el seccionador que cuelga
 * de el.
 *
 * <p>Las mensulas siguen la regla de las colecciones de hijos (README_API.md §4): si nadie las
 * toca van a {@code null} y el servicio las deja como estan; si alguien las toca, va la lista
 * entera. El seccionador es una relacion 1:1: se manda el objeto para vincularlo o mantenerlo y
 * {@code null} para desvincularlo, que no lo borra.</p>
 */
public class ProfileEditor extends MasterEditorDialog<ProfileDto> {

    static final int PROFILE_ID_MAX_LENGTH = 50;
    static final String KP_PATTERN = "\\d+(\\.\\d+)?";
    /** El tope del servicio ({@code PROFILE_MAX_CANTILEVERS}); el boton de anadir se apaga al llegar. */
    static final int MAX_CANTILEVERS = 3;

    private final DisconnectorClient disconnectors;
    private final ChildrenEditor<CantileverDto> cantilevers;
    private final ComboBox<RefItem> disconnector;
    private final Long disconnectorAsRead;
    private boolean disconnectorTouched;

    public ProfileEditor(ProfileDto dto, ReferenceCatalog catalog, LovCatalog lovs, DisconnectorClient disconnectors,
                         Function<ProfileDto, ProfileDto> saver, Consumer<ProfileDto> onSaved) {
        super(MasterResource.PROFILES, ProfileDto.class, dto, saver, onSaved);
        this.disconnectors = disconnectors;

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

        // El seccionador 1:1 no pasa por el Binder: lo que viaja es el objeto entero, no un id.
        disconnector = Pickers.lazyDisconnector("Seccionador", disconnectors);
        disconnector.setId("profile-disconnector");
        disconnector.setHelperText("El que cuelga de este perfil; vacio lo desvincula, no lo borra");
        disconnectorAsRead = dto.getDisconnector() == null ? null : dto.getDisconnector().getId();
        if (dto.getDisconnector() != null) {
            disconnector.setValue(Pickers.disconnectorRef(dto.getDisconnector()));
        }
        disconnector.addValueChangeListener(change -> disconnectorTouched = true);

        cantilevers = new ChildrenEditor<>("Mensulas", "cantilevers", dto.getCantilevers(), MAX_CANTILEVERS, CantileverDto::new,
                (child, accepted) -> new CantileverDialog(child, lovs, accepted).open(), grid -> {
                    grid.addColumn(child -> child.getCantileverType() == null ? "" : child.getCantileverType().label()).setHeader("Tipo").setAutoWidth(true).setFlexGrow(1);
                    grid.addColumn(CantileverDto::getCwHeight).setHeader("Altura hilo (mm)").setAutoWidth(true);
                    grid.addColumn(CantileverDto::getStagger).setHeader("Descentramiento (mm)").setAutoWidth(true);
                    grid.addColumn(CantileverDto::getCatenaryHeight).setHeader("Altura sustentador (mm)").setAutoWidth(true);
                    grid.addColumn(ProfileEditor::armOf).setHeader("Brazo de atirantado").setAutoWidth(true);
                });

        form.add(profileId, kp, track, orderInTrack, profileStatus, poleType, foundation, anchorageFoundation,
                portal, returnSupport, supportType, assemblyConfiguration, sectionings, anchorages, sectioningFeedings,
                span, heightCantileverSupport, poleGaugeLocation, railPoleDistance, disconnector, cantilevers);
        wide(sectionings);
        wide(anchorages);
        wide(sectioningFeedings);
        wide(disconnector);
        wide(cantilevers);
        ready();
    }

    private static String armOf(CantileverDto child) {
        if (child.getSteadyArm() == null) {
            return "Sin brazo";
        }
        String type = child.getSteadyArm().getSteadyArmType() == null ? "" : child.getSteadyArm().getSteadyArmType().label();
        String length = child.getSteadyArm().getLength() == null ? "" : " " + child.getSteadyArm().getLength() + " mm";
        return (type + length).trim();
    }

    @Override
    protected void prepare(ProfileDto dto) {
        cantilevers.edited().ifPresent(dto::setCantilevers);
        if (!disconnectorTouched) {
            return;
        }
        RefItem chosen = disconnector.getValue();
        if (chosen == null) {
            dto.setDisconnector(null);
        } else if (!Objects.equals(chosen.id(), disconnectorAsRead)) {
            DisconnectorDto linked = disconnectors.findById(chosen.id());
            linked.setProfileId(dto.getId());
            dto.setDisconnector(linked);
        }
    }

    /** Para los tests: las mensulas tal como quedan en el editor. */
    List<CantileverDto> cantilevers() {
        return cantilevers.items();
    }
}
