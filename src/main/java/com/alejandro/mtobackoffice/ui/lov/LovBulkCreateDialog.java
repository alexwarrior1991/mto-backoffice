package com.alejandro.mtobackoffice.ui.lov;

import com.alejandro.mtobackoffice.client.configuration.LovResource;
import com.alejandro.mtobackoffice.client.dto.LovDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextArea;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * Alta en lote ({@code POST /{recurso}/bulk}): una entrada por linea, {@code CODIGO;Descripcion}.
 * Tambien vale el tabulador o «{@code  - }» como separador, que es lo que sale al pegar desde una
 * hoja de calculo o desde un documento. Todo lo que se crea nace activo.
 *
 * <p>En los tres catalogos con tipo, el tipo se elige una vez y vale para todas las lineas. Una linea
 * con un codigo o una descripcion mas largos que la columna se dice antes de llamar, con su numero: el
 * servicio rechazaria el lote entero sin decir cual.</p>
 */
public class LovBulkCreateDialog extends Dialog {

    private final TextArea lines = new TextArea("Entradas");
    private final ComboBox<LovDto> parent = new ComboBox<>();
    private final Optional<LovResource.Parent> parentType;
    private final UnaryOperator<List<LovDto>> creator;
    private final Consumer<List<LovDto>> onCreated;

    /**
     * @param parents las entradas del catalogo del tipo, si el catalogo lo tiene (si no, vacia)
     */
    public LovBulkCreateDialog(LovResource resource, List<LovDto> parents, UnaryOperator<List<LovDto>> creator,
                               Consumer<List<LovDto>> onCreated) {
        this.creator = creator;
        this.onCreated = onCreated;
        this.parentType = resource.parent();
        setHeaderTitle("Alta multiple en " + resource.title());
        setCloseOnOutsideClick(false);
        setWidth("40em");

        lines.setWidthFull();
        lines.setMinHeight("14em");
        lines.setPlaceholder("PT1;Poste tipo 1\nPT2;Poste tipo 2");
        add(new Paragraph("Una entrada por linea: codigo, separador (; tabulador o \" - \") y descripcion."), lines);
        parentType.ifPresent(type -> {
            parent.setLabel(type.label());
            parent.setHelperText("El mismo para todas");
            parent.setRequiredIndicatorVisible(true);
            parent.setItems(LovEditorDialog.parentOptions(parents, null));
            parent.setItemLabelGenerator(LovEditorDialog::describe);
            parent.setWidthFull();
            add(parent);
        });

        Button create = new Button("Crear", click -> create());
        create.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(new Button("Cancelar", click -> close()), create);
    }

    private void create() {
        List<LovDto> entries;
        try {
            entries = parse(lines.getValue());
        } catch (IllegalArgumentException invalid) {
            lines.setErrorMessage(invalid.getMessage());
            lines.setInvalid(true);
            return;
        }
        lines.setInvalid(false);
        if (parentType.isPresent()) {
            if (parent.getValue() == null) {
                parent.setErrorMessage("El tipo es obligatorio");
                parent.setInvalid(true);
                return;
            }
            parent.setInvalid(false);
            String field = parentType.get().field();
            Long parentId = parent.getValue().id();
            entries = entries.stream().map(entry -> entry.withParent(field, parentId)).toList();
        }
        try {
            List<LovDto> created = creator.apply(entries);
            close();
            Notification.show(created.size() + " entradas creadas", 3000, Notification.Position.BOTTOM_START)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            onCreated.accept(created);
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }

    /**
     * Convierte el texto pegado en entradas. Es tratamiento de la entrada de la persona, no una
     * regla de negocio: lo que el servicio acepte o rechace lo decide el servicio.
     *
     * @throws IllegalArgumentException con el numero de la primera linea que no se entiende
     */
    public static List<LovDto> parse(String text) {
        List<LovDto> entries = new ArrayList<>();
        String[] rows = text == null ? new String[0] : text.split("\\R");
        for (int i = 0; i < rows.length; i++) {
            String row = rows[i].strip();
            if (row.isEmpty()) {
                continue;
            }
            String[] parts = row.split(";|\\t| - ", 2);
            if (parts.length < 2 || parts[0].isBlank() || parts[1].isBlank()) {
                throw new IllegalArgumentException("La linea " + (i + 1) + " no tiene codigo y descripcion separados por ';'");
            }
            String code = parts[0].strip();
            String description = parts[1].strip();
            if (code.length() > LovEditorDialog.CODE_MAX_LENGTH) {
                throw new IllegalArgumentException("La linea " + (i + 1) + " tiene un codigo de mas de "
                        + LovEditorDialog.CODE_MAX_LENGTH + " caracteres");
            }
            if (description.length() > LovEditorDialog.DESCRIPTION_MAX_LENGTH) {
                throw new IllegalArgumentException("La linea " + (i + 1) + " tiene una descripcion de mas de "
                        + LovEditorDialog.DESCRIPTION_MAX_LENGTH + " caracteres");
            }
            entries.add(LovDto.forCreate(code, description, true));
        }
        if (entries.isEmpty()) {
            throw new IllegalArgumentException("No hay ninguna entrada");
        }
        return entries;
    }

    ComboBox<LovDto> parentField() {
        return parent;
    }
}
