package com.alejandro.mtobackoffice.ui.resources;

import com.alejandro.mtobackoffice.client.dto.MergePatch;
import com.alejandro.mtobackoffice.client.dto.resources.DrivingLicence;
import com.alejandro.mtobackoffice.client.dto.resources.QualificationTypeDto;
import com.alejandro.mtobackoffice.client.dto.resources.QualificationTypeFilter;
import com.alejandro.mtobackoffice.client.dto.resources.QualificationTypeSummaryDto;
import com.alejandro.mtobackoffice.client.dto.resources.RailGearType;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleCapability;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleCategory;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleMobility;
import com.alejandro.mtobackoffice.client.dto.resources.VehiclePower;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleTypeDto;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleTypeUpdateRequest;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.error.ValidationApiException;
import com.alejandro.mtobackoffice.ui.support.Numbers;
import com.alejandro.mtobackoffice.ui.support.Required;
import com.alejandro.mtobackoffice.ui.support.Screens;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValidation;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Alta y modificacion de un modelo de vehiculo, por secciones segun su movilidad, como en mto-frontend:
 * las cifras de via no se ofrecen en uno de solo carretera ni las de carretera en uno de solo via; el
 * equipo ferroviario (9A, 9B, 9C), los minutos de encarrilado y el acceso preparado, solo en una
 * bivial; la categoria, solo las de su movilidad. Cada capacidad ensena sus cifras y quitarla las vacia.
 * El codigo y la movilidad no cambian despues del alta. Los numeros se comprueban con la columna del
 * servicio; que el modelo sea coherente lo dice el servicio (400 con el motivo). «Habilitacion del
 * conductor» ofrece los tipos de cualificacion activos (y el leido, aunque ya no lo este): vacia en el
 * alta la pone el servicio segun la movilidad y el permiso, y vaciarla al modificar es no exigir ninguna.
 */
public class VehicleTypeEditorDialog extends Dialog {

    public static final String SAVE_ID = "vehicle-type-save";
    static final int CODE_LENGTH = 32;
    static final int NAME_LENGTH = 150;
    static final int MAKE_LENGTH = 100;

    private final Binder<VehicleTypeForm> binder = new Binder<>(VehicleTypeForm.class);
    private final VehicleTypeForm form;
    private final VehicleTypeDto existing;
    /** Cada campo por el nombre del suyo en la peticion, para los errores del servicio. */
    private final Map<String, HasValidation> fields = new HashMap<>();
    private final Map<VehicleTypeForm.Section, VerticalLayout> sections = new EnumMap<>(VehicleTypeForm.Section.class);
    private final Map<String, Component> figureFields = new HashMap<>();

    private final Select<VehicleMobility> mobility = new Select<>();
    private final ComboBox<VehicleCategory> category = new ComboBox<>("Categoria");
    private final MultiSelectComboBox<String> capabilities = new MultiSelectComboBox<>("Capacidades");
    private final ComboBox<QualificationTypeSummaryDto> driver = new ComboBox<>("Habilitacion del conductor");

    /**
     * @param existing el modelo a modificar, o {@code null} para dar uno de alta
     * @param saved    que hacer despues de guardar
     */
    public VehicleTypeEditorDialog(VehicleTypeDto existing, ResourcesClients clients, Runnable saved) {
        this.existing = existing;
        boolean creating = existing == null;
        this.form = VehicleTypeForm.of(existing);
        setHeaderTitle(creating ? "Nuevo modelo de vehiculo" : "Modificar " + existing.label());
        setCloseOnOutsideClick(false);
        setWidth("min(95vw, 900px)");

        TextField code = text("vehicle-type-code", "Codigo", CODE_LENGTH, "code");
        code.setReadOnly(!creating);
        code.setHelperText("Letras, cifras, ., - y _");
        TextField name = text("vehicle-type-name", "Nombre", NAME_LENGTH, "name");
        TextField manufacturer = text("vehicle-type-manufacturer", "Fabricante", MAKE_LENGTH, "manufacturer");
        TextField model = text("vehicle-type-model", "Modelo", MAKE_LENGTH, "model");
        mobility.setLabel("Movilidad");
        mobility.setId("vehicle-type-mobility");
        mobility.setItems(creating ? VehicleMobility.selectable() : List.of(existing.mobility()));
        mobility.setItemLabelGenerator(VehicleMobility::label);
        mobility.setReadOnly(!creating);
        mobility.setHelperText(creating ? "No cambia despues: decide que cifras lleva" : "No cambia");
        fields.put("mobility", mobility);
        category.setId("vehicle-type-category");
        category.setItemLabelGenerator(VehicleCategory::label);
        fields.put("category", category);
        Checkbox selfPropelled = new Checkbox("Autopropulsado");
        selfPropelled.setId("vehicle-type-self-propelled");
        Checkbox portable = new Checkbox("Portatil (se pone a mano)");
        portable.setId("vehicle-type-portable");
        Select<VehiclePower> power = new Select<>();
        power.setLabel("Propulsion");
        power.setId("vehicle-type-power");
        power.setItems(VehiclePower.selectable());
        power.setItemLabelGenerator(value -> value == null ? "" : value.label());
        power.setEmptySelectionAllowed(true);
        fields.put("power", power);
        Checkbox transportMode = new Checkbox("Autorizado a circular por via abierta");
        transportMode.setId("vehicle-type-transport-mode");
        Select<RailGearType> railGear = new Select<>();
        railGear.setLabel("Equipo ferroviario");
        railGear.setId("vehicle-type-rail-gear");
        railGear.setItems(RailGearType.selectable());
        railGear.setItemLabelGenerator(value -> value == null ? "" : value.label());
        fields.put("railGearType", railGear);
        Checkbox preparedAccess = new Checkbox("Necesita un acceso preparado (no le vale un paso a nivel)");
        preparedAccess.setId("vehicle-type-prepared-access");
        Select<DrivingLicence> licence = new Select<>();
        licence.setLabel("Permiso de conducir");
        licence.setId("vehicle-type-licence");
        licence.setItems(DrivingLicence.selectable());
        licence.setItemLabelGenerator(value -> value == null ? "" : value.label());
        licence.setEmptySelectionAllowed(true);
        fields.put("requiredLicence", licence);
        capabilities.setId("vehicle-type-capabilities");
        List<String> codes = new ArrayList<>(VehicleCapability.codes());
        form.getCapabilities().stream().filter(item -> !codes.contains(item)).forEach(codes::add);
        capabilities.setItems(codes);
        capabilities.setItemLabelGenerator(VehicleCapability::labelOf);
        fields.put("capabilities", capabilities);
        TextArea notes = new TextArea("Notas");
        notes.setId("vehicle-type-notes");
        fields.put("notes", notes);
        driver.setId("vehicle-type-driver-qualification");
        driver.setItems(driverQualifications(clients, form.getRequiredDriverQualificationTypeId()));
        driver.setItemLabelGenerator(QualificationTypeSummaryDto::label);
        driver.setClearButtonVisible(true);
        driver.setHelperText(creating ? "Vacia: la que corresponde a su movilidad y a su permiso" : "Vacia: no exige ninguna");
        fields.put("requiredDriverQualificationTypeId", driver);

        binder.forField(code).asRequired(Required.text("El codigo es obligatorio")).bind("code");
        binder.forField(name).asRequired(Required.text("El nombre es obligatorio")).bind("name");
        binder.forField(manufacturer).bind("manufacturer");
        binder.forField(model).bind("model");
        binder.forField(mobility).asRequired("La movilidad es obligatoria").bind("mobility");
        binder.forField(category).asRequired("La categoria es obligatoria").bind("category");
        binder.forField(selfPropelled).bind("selfPropelled");
        binder.forField(portable).bind("portable");
        binder.forField(power).bind("power");
        binder.forField(transportMode).bind("transportModeAuthorised");
        binder.forField(railGear)
                .withValidator(value -> value != null || mobility.getValue() != VehicleMobility.ROAD_RAIL,
                        "Una bivial dice su equipo ferroviario")
                .bind("railGearType");
        binder.forField(preparedAccess).bind("needsPreparedAccess");
        binder.forField(licence).bind("requiredLicence");
        binder.forField(driver).bind("requiredDriverQualificationTypeId");
        binder.forField(capabilities).bind("capabilities");
        binder.forField(notes).bind("notes");

        FormLayout general = grid(code, name, manufacturer, model, mobility, category, power, driver, selfPropelled, portable);
        section(VehicleTypeForm.Section.GENERAL, "Dimensiones y pesos");
        section(VehicleTypeForm.Section.TRACK, "En via").add(transportMode);
        section(VehicleTypeForm.Section.ROAD_RAIL, "Bivial").add(railGear, preparedAccess);
        section(VehicleTypeForm.Section.ROAD, "Por carretera").add(licence);
        section(VehicleTypeForm.Section.CAPABILITY, "Capacidades y sus cifras").add(capabilities);
        for (VehicleTypeForm.Figure figure : VehicleTypeForm.FIGURES) {
            sections.get(figure.section()).add(figureField(figure));
        }
        VerticalLayout content = new VerticalLayout(general);
        content.setPadding(false);
        sections.values().forEach(content::add);
        content.add(notes);
        add(content);

        // Un ComboBox no admite un valor sin items: las categorias de su movilidad (y la leida) antes de leer el formulario.
        List<VehicleCategory> offered = new ArrayList<>(VehicleCategory.of(form.getMobility()));
        if (form.getCategory() != null && !offered.contains(form.getCategory())) {
            offered.addFirst(form.getCategory());
        }
        category.setItems(offered);
        binder.readBean(form);
        mobility.addValueChangeListener(change -> mobilityChanged());
        capabilities.addValueChangeListener(change -> capabilitiesChanged());
        mobilityChanged();

        Button save = new Button("Guardar", click -> save(clients, saved));
        save.setId(SAVE_ID);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(new Button("Cancelar", click -> close()), save);
    }

    /**
     * Los tipos de cualificacion activos, y el que tiene el modelo aunque ya no lo este, para no leerlo
     * como vacio; si no se pueden leer, se notifica y queda solo el leido.
     */
    private static List<QualificationTypeSummaryDto> driverQualifications(ResourcesClients clients, QualificationTypeSummaryDto current) {
        List<QualificationTypeSummaryDto> offered = new ArrayList<>();
        try {
            clients.qualificationTypes().search(QualificationTypeFilter.ACTIVE, 0, PersonQualificationDialog.ALL_TYPES, List.of("code,asc"))
                    .content().stream().map(QualificationTypeDto::summary).forEach(offered::add);
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
        if (current != null && !offered.contains(current)) {
            offered.addFirst(current);
        }
        return offered;
    }

    private TextField text(String id, String label, int maxLength, String field) {
        TextField text = new TextField(label);
        text.setId(id);
        text.setMaxLength(maxLength);
        fields.put(field, text);
        return text;
    }

    private static FormLayout grid(Component... components) {
        FormLayout layout = new FormLayout(components);
        layout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1), new FormLayout.ResponsiveStep("480px", 2),
                new FormLayout.ResponsiveStep("720px", 3));
        return layout;
    }

    /** Una seccion con su titulo y su rejilla de campos. */
    private FormLayout section(VehicleTypeForm.Section section, String title) {
        FormLayout layout = grid();
        VerticalLayout holder = new VerticalLayout(new H4(title), layout);
        holder.setPadding(false);
        holder.setSpacing(false);
        holder.setId("vehicle-type-section-" + section.name().toLowerCase().replace('_', '-'));
        sections.put(section, holder);
        return layout;
    }

    /** El campo de una cifra: decimal con su columna o entero con su minimo, nunca negativo. */
    private Component figureField(VehicleTypeForm.Figure figure) {
        String id = "vehicle-type-" + figure.field();
        Component component;
        if (figure.decimal()) {
            BigDecimalField field = new BigDecimalField(figure.label());
            field.setId(id);
            binder.forField(field).withValidator(Numbers.all(Numbers.nonNegative(), Numbers.digits(figure.integers(), figure.decimals())))
                    .bind(bean -> (BigDecimal) bean.getFigure(figure.field()), (bean, value) -> bean.setFigure(figure.field(), value));
            fields.put(figure.field(), field);
            component = field;
        } else {
            IntegerField field = Numbers.atLeast(new IntegerField(figure.label()), figure.min(),
                    figure.min() > 0 ? "Como minimo " + figure.min() : "No puede ser negativo");
            field.setId(id);
            binder.forField(field).bind(bean -> (Integer) bean.getFigure(figure.field()), (bean, value) -> bean.setFigure(figure.field(), value));
            fields.put(figure.field(), field);
            component = field;
        }
        figureFields.put(figure.field(), component);
        return component;
    }

    /** La movilidad decide las secciones y las categorias; la categoria de otra movilidad se quita. */
    private void mobilityChanged() {
        VehicleMobility current = mobility.getValue();
        sections.forEach((section, holder) -> holder.setVisible(VehicleTypeForm.offered(section, current)));
        VehicleCategory chosen = category.getValue();
        List<VehicleCategory> offered = VehicleCategory.of(current);
        List<VehicleCategory> items = new ArrayList<>(offered);
        if (chosen != null && !offered.contains(chosen) && existing != null && chosen == existing.category()) {
            items.addFirst(chosen);
        }
        category.setItems(items);
        category.setValue(chosen != null && items.contains(chosen) ? chosen : null);
        capabilitiesChanged();
    }

    /** Cada capacidad ensena sus cifras; sin ella no se ven ni viajan. */
    private void capabilitiesChanged() {
        for (VehicleTypeForm.Figure figure : VehicleTypeForm.FIGURES) {
            if (figure.capability() != null) {
                figureFields.get(figure.field()).setVisible(capabilities.getValue().contains(figure.capability().name()));
            }
        }
    }

    private void save(ResourcesClients clients, Runnable saved) {
        if (!binder.writeBeanIfValid(form)) {
            return;
        }
        try {
            VehicleTypeDto result;
            if (existing == null) {
                result = clients.vehicleTypes().create(form.toRequest());
            } else {
                MergePatch<VehicleTypeUpdateRequest> patch = form.toPatch(existing);
                if (patch.changesNothing()) {
                    close();
                    return;
                }
                result = clients.vehicleTypes().update(existing.id(), patch);
            }
            close();
            Screens.success("Guardado " + result.label());
            saved.run();
        } catch (ValidationApiException validation) {
            Screens.showValidation(fields, validation);
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }
}
