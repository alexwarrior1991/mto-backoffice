package com.alejandro.mtobackoffice.ui.lov;

import com.alejandro.mtobackoffice.client.dto.LovDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextArea;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * Alta en lote ({@code POST /{recurso}/bulk}): una entrada por linea, {@code CODIGO;Descripcion}.
 * Tambien vale el tabulador o «{@code  - }» como separador, que es lo que sale al pegar desde una
 * hoja de calculo o desde un documento. Todo lo que se crea nace activo.
 */
public class LovBulkCreateDialog extends Dialog {

    private final TextArea lines = new TextArea("Entradas");
    private final UnaryOperator<List<LovDto>> creator;
    private final Consumer<List<LovDto>> onCreated;

    public LovBulkCreateDialog(String catalogueTitle, UnaryOperator<List<LovDto>> creator, Consumer<List<LovDto>> onCreated) {
        this.creator = creator;
        this.onCreated = onCreated;
        setHeaderTitle("Alta multiple en " + catalogueTitle);
        setCloseOnOutsideClick(false);
        setWidth("40em");

        lines.setWidthFull();
        lines.setMinHeight("14em");
        lines.setPlaceholder("PT1;Poste tipo 1\nPT2;Poste tipo 2");
        add(new Paragraph("Una entrada por linea: codigo, separador (; tabulador o \" - \") y descripcion."), lines);

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
            entries.add(LovDto.forCreate(parts[0].strip(), parts[1].strip(), true));
        }
        if (entries.isEmpty()) {
            throw new IllegalArgumentException("No hay ninguna entrada");
        }
        return entries;
    }
}
