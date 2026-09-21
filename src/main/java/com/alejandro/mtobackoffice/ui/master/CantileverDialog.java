package com.alejandro.mtobackoffice.ui.master;

import com.alejandro.mtobackoffice.client.configuration.LovResource;
import com.alejandro.mtobackoffice.client.dto.master.CantileverDto;
import com.alejandro.mtobackoffice.client.dto.master.LovRef;
import com.alejandro.mtobackoffice.client.dto.master.SteadyArmDto;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.converter.Converter;

import java.util.function.Consumer;

/**
 * Una mensula del perfil: sus medidas, su tipo de catalogo y, si lo lleva, su brazo de atirantado
 * (1:1). Escribe sobre el objeto solo al aceptar; cancelar no cambia nada.
 */
public class CantileverDialog extends Dialog {

    private final Binder<CantileverDto> binder = new Binder<>(CantileverDto.class);
    private final Binder<SteadyArmDto> armBinder = new Binder<>(SteadyArmDto.class);

    public CantileverDialog(CantileverDto child, LovCatalog lovs, Consumer<CantileverDto> onAccepted) {
        setHeaderTitle(child.isNew() ? "Nueva mensula" : "Modificar mensula");
        setCloseOnOutsideClick(false);
        setWidth("min(48rem, 96vw)");

        ComboBox<LovRef> cantileverType = Pickers.lov("Tipo de mensula", lovs.of(LovResource.CANTILEVER_TYPES));
        cantileverType.setRequiredIndicatorVisible(true);
        BigDecimalField cwHeight = new BigDecimalField("Altura del hilo de contacto (mm)");
        BigDecimalField stagger = new BigDecimalField("Descentramiento (mm)");
        BigDecimalField catenaryHeight = new BigDecimalField("Altura del sustentador (mm)");
        BigDecimalField cwElevation = new BigDecimalField("Elevacion del hilo (mm)");
        BigDecimalField windDeflection = new BigDecimalField("Desplazamiento por viento (mm)");
        BigDecimalField armAngle = new BigDecimalField("Angulo del brazo (grados)");
        Checkbox withArm = new Checkbox("Lleva brazo de atirantado");
        withArm.setId("cantilever-with-arm");
        IntegerField armLength = new IntegerField("Longitud del brazo (mm)");
        armLength.setMin(0);
        ComboBox<LovRef> armType = Pickers.lov("Tipo de brazo", lovs.of(LovResource.STEADY_ARM_TYPES));

        binder.forField(cantileverType).asRequired("El tipo de mensula es obligatorio").bind("cantileverType");
        binder.forField(cwHeight).bind("cwHeight");
        binder.forField(stagger).bind("stagger");
        binder.forField(catenaryHeight).bind("catenaryHeight");
        binder.forField(cwElevation).bind("cwElevation");
        binder.forField(windDeflection).bind("windDeflection");
        binder.forField(armAngle).bind("armAngle");
        armBinder.forField(armLength).withConverter(Converter.<Integer, Long>from(
                value -> Result.ok(value == null ? null : value.longValue()),
                value -> value == null ? null : value.intValue())).bind("length");
        armBinder.forField(armType).asRequired("El tipo de brazo es obligatorio").bind("steadyArmType");

        withArm.addValueChangeListener(change -> {
            boolean arm = Boolean.TRUE.equals(change.getValue());
            armLength.setEnabled(arm);
            armType.setEnabled(arm);
        });

        FormLayout form = new FormLayout(cantileverType, cwHeight, stagger, catenaryHeight, cwElevation, windDeflection, armAngle,
                withArm, armLength, armType);
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1), new FormLayout.ResponsiveStep("36em", 2));
        form.setColspan(withArm, 2);
        add(form);

        Button accept = new Button("Aceptar", click -> {
            if (!binder.writeBeanIfValid(child)) {
                return;
            }
            if (Boolean.TRUE.equals(withArm.getValue())) {
                SteadyArmDto arm = child.getSteadyArm() == null ? new SteadyArmDto() : child.getSteadyArm();
                if (!armBinder.writeBeanIfValid(arm)) {
                    return;
                }
                child.setSteadyArm(arm);
            } else {
                child.setSteadyArm(null);
            }
            close();
            onAccepted.accept(child);
        });
        accept.setId("cantilever-accept");
        accept.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(new Button("Cancelar", click -> close()), accept);

        binder.readBean(child);
        armBinder.readBean(child.getSteadyArm() == null ? new SteadyArmDto() : child.getSteadyArm());
        withArm.setValue(child.getSteadyArm() != null);
        armLength.setEnabled(child.getSteadyArm() != null);
        armType.setEnabled(child.getSteadyArm() != null);
    }
}
