package com.alejandro.mtobackoffice.ui.master;

import com.alejandro.mtobackoffice.client.dto.master.SectionInsulatorSwitchDto;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.RegexpValidator;

import java.util.function.Consumer;

/**
 * Una aguja del aislador de seccion (README_API.md §4 quater): {@code W} y hasta cuatro cifras,
 * su KP en metros, el denominador de la tangente ({@code 9} para {@code 1:9}) y la via. Que el
 * codigo no se repita dentro del aislador lo comprueba el servicio.
 */
public class SwitchDialog extends Dialog {

    static final String CODE_PATTERN = "W\\d{1,4}";

    private final Binder<SectionInsulatorSwitchDto> binder = new Binder<>(SectionInsulatorSwitchDto.class);

    public SwitchDialog(SectionInsulatorSwitchDto child, ReferenceCatalog catalog, Consumer<SectionInsulatorSwitchDto> onAccepted) {
        setHeaderTitle(child.isNew() ? "Nueva aguja" : "Modificar aguja");
        setCloseOnOutsideClick(false);
        setWidth("min(40rem, 96vw)");

        TextField code = new TextField("Codigo");
        code.setMaxLength(5);
        code.setRequiredIndicatorVisible(true);
        code.setHelperText("Como en el plano: W31");
        BigDecimalField kp = new BigDecimalField("KP (m)");
        IntegerField turnout = new IntegerField("Denominador de la tangente (1:n)");
        turnout.setMin(1);
        turnout.setHelperText("9 para un desvio 1:9");
        ComboBox<RefItem> track = Pickers.reference("Via", catalog.tracks());
        Checkbox enabled = new Checkbox("Activa");

        binder.forField(code).asRequired("El codigo es obligatorio")
                .withValidator(new RegexpValidator("W y hasta cuatro cifras, como W31", CODE_PATTERN)).bind("code");
        binder.forField(kp).bind("kp");
        binder.forField(turnout).bind("turnoutDenominator");
        binder.forField(track).withConverter(Pickers.refToId(catalog::trackRef)).bind("trackId");
        binder.forField(enabled).bind("enabled");

        FormLayout form = new FormLayout(code, kp, turnout, track, enabled);
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1), new FormLayout.ResponsiveStep("30em", 2));
        add(form);

        Button accept = new Button("Aceptar", click -> {
            if (!binder.writeBeanIfValid(child)) {
                return;
            }
            close();
            onAccepted.accept(child);
        });
        accept.setId("switch-accept");
        accept.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(new Button("Cancelar", click -> close()), accept);

        if (child.getEnabled() == null) {
            child.setEnabled(Boolean.TRUE);
        }
        binder.readBean(child);
    }
}
