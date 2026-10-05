package com.alejandro.mtobackoffice.ui.lov;

import com.alejandro.mtobackoffice.client.configuration.LovResource;
import com.alejandro.mtobackoffice.client.dto.LovDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.error.ValidationApiException;
import com.alejandro.mtobackoffice.ui.support.ServerValidation;
import com.alejandro.mtobackoffice.ui.support.TextMatching;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * Alta o modificacion de una entrada de catalogo. Lo unico que valida aqui es lo que el servicio
 * exige de forma evidente (codigo y descripcion obligatorios, longitud de columna, y el tipo en los
 * tres catalogos que lo tienen); el resto lo dice el servicio y se ensena campo a campo.
 *
 * <p>Una modificacion manda la entrada leida entera con lo cambiado encima ({@link LovForm#toDto}):
 * lo que la pantalla no ensena vuelve tal cual. El tipo viaja por su id; la lista ofrece el catalogo
 * del tipo por codigo, y el que tiene la entrada sale aunque ya no este en el.</p>
 */
public class LovEditorDialog extends Dialog {

    static final int CODE_MAX_LENGTH = 40;
    static final int DESCRIPTION_MAX_LENGTH = 200;

    private final Binder<LovForm> binder = new Binder<>(LovForm.class);
    private final TextField code = new TextField("Codigo");
    private final TextField description = new TextField("Descripcion");
    private final Checkbox enabled = new Checkbox("Activo");
    private final ComboBox<LovDto> parent = new ComboBox<>();
    private final Optional<LovResource.Parent> parentType;
    private final LovForm form;
    private final LovDto existing;
    private final UnaryOperator<LovDto> saver;
    private final Consumer<LovDto> onSaved;

    /**
     * @param resource el catalogo
     * @param existing entrada a modificar, o {@code null} para un alta
     * @param parents  las entradas del catalogo del tipo, si el catalogo lo tiene (si no, vacia)
     * @param saver    la llamada al servicio (crear o modificar); puede lanzar {@link BackofficeApiException}
     * @param onSaved  que hacer con lo que devolvio el servicio
     */
    public LovEditorDialog(LovResource resource, LovDto existing, List<LovDto> parents, UnaryOperator<LovDto> saver,
                           Consumer<LovDto> onSaved) {
        this.existing = existing;
        this.saver = saver;
        this.onSaved = onSaved;
        this.parentType = resource.parent();
        this.form = LovForm.of(existing, parentType);

        setHeaderTitle((existing == null ? "Nueva entrada de " : "Modificar entrada de ") + resource.title());
        setCloseOnOutsideClick(false);

        code.setMaxLength(CODE_MAX_LENGTH);
        code.setRequiredIndicatorVisible(true);
        description.setMaxLength(DESCRIPTION_MAX_LENGTH);
        description.setRequiredIndicatorVisible(true);
        description.setWidthFull();

        binder.forField(code)
                .asRequired("El codigo es obligatorio")
                .withValidator(value -> value.trim().length() <= CODE_MAX_LENGTH, "Como mucho " + CODE_MAX_LENGTH + " caracteres")
                .bind("code");
        binder.forField(description)
                .asRequired("La descripcion es obligatoria")
                .withValidator(value -> value.trim().length() <= DESCRIPTION_MAX_LENGTH, "Como mucho " + DESCRIPTION_MAX_LENGTH + " caracteres")
                .bind("description");
        binder.forField(enabled).bind("enabled");

        FormLayout layout = new FormLayout(code, description);
        parentType.ifPresent(type -> {
            parent.setLabel(type.label());
            parent.setRequiredIndicatorVisible(true);
            // Por su id: la que trae la entrada no lleva lo mismo que la de la lista.
            parent.setItems(parentOptions(parents, form.getParent())).setIdentifierProvider(LovDto::id);
            parent.setItemLabelGenerator(LovEditorDialog::describe);
            parent.setWidthFull();
            binder.forField(parent).asRequired("El tipo es obligatorio").bind("parent");
            layout.add(parent);
        });
        layout.add(enabled);
        binder.readBean(form);

        layout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        add(layout);

        Button save = new Button("Guardar", click -> save());
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancel = new Button("Cancelar", click -> close());
        getFooter().add(cancel, save);
    }

    private void save() {
        if (!binder.writeBeanIfValid(form)) {
            return;
        }
        try {
            LovDto saved = saver.apply(form.toDto(existing, parentType));
            close();
            Notification.show("Guardado", 3000, Notification.Position.BOTTOM_START)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            onSaved.accept(saved);
        } catch (ValidationApiException validation) {
            List<String> unattributed = ServerValidation.apply(binder, validation);
            if (!unattributed.isEmpty()) {
                Notification.show(String.join(". ", unattributed), 8000, Notification.Position.BOTTOM_START)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }

    TextField codeField() {
        return code;
    }

    ComboBox<LovDto> parentField() {
        return parent;
    }

    /** Las entradas del catalogo del tipo, por codigo; la que tiene la entrada sale aunque ya no este. */
    static List<LovDto> parentOptions(List<LovDto> parents, LovDto current) {
        List<LovDto> options = new ArrayList<>(parents == null ? List.of() : parents);
        options.sort(Comparator.comparing(LovDto::code, TextMatching.NATURAL_ORDER));
        if (current != null && options.stream().noneMatch(option -> option.id().equals(current.id()))) {
            options.addFirst(current);
        }
        return options;
    }

    /** Una entrada en un desplegable: su codigo y su descripcion. */
    static String describe(LovDto entry) {
        return entry.description() == null || entry.description().isBlank()
                ? String.valueOf(entry.code())
                : entry.code() + " · " + entry.description();
    }
}
