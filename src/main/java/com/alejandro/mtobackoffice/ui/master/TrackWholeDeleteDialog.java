package com.alejandro.mtobackoffice.ui.master;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;

/**
 * La confirmacion de borrar una via entera ({@code DELETE /tracks/{id}?cascade=true}), como en
 * mto-frontend: no se deshace y son miles de filas, asi que dice exactamente que se borra y su boton
 * no se activa hasta escribir el nombre de la via. Al pulsarlo se deshabilita (la llamada puede
 * tardar varios segundos) y el dialogo se cierra con la respuesta, sea cual sea: lo que pase despues
 * lo dice {@link TracksView}.
 */
public class TrackWholeDeleteDialog extends Dialog {

    public static final String NAME_ID = "delete-whole-name";
    public static final String CONFIRM_ID = "delete-whole-confirm";
    public static final String WHAT = "Se borra la via con todo lo suyo: sus perfiles con sus mensulas y sus brazos, sus seccionadores, "
            + "sus aisladores con sus agujas y sus accesos a via. No se puede deshacer.";
    public static final String HOW = "Si algo de otra via la usa, el servicio no borra nada y dice que lo impide. Con miles de perfiles "
            + "puede tardar varios segundos: si la respuesta no llega, recarga la lista antes de repetirlo.";

    TrackWholeDeleteDialog(String trackName, Runnable delete) {
        setHeaderTitle("Borrar la via entera " + trackName);
        setCloseOnOutsideClick(false);

        TextField name = new TextField("Escribe el nombre de la via para confirmar");
        name.setId(NAME_ID);
        name.setHelperText(trackName);
        name.setWidthFull();
        name.setValueChangeMode(ValueChangeMode.EAGER);
        add(new Paragraph(WHAT), new Paragraph(HOW), name);

        Button confirm = new Button("Borrar la via entera", click -> {
            close();
            delete.run();
        });
        confirm.setId(CONFIRM_ID);
        confirm.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        confirm.setDisableOnClick(true);
        confirm.setEnabled(false);
        name.addValueChangeListener(change -> confirm.setEnabled(trackName.strip().equals(change.getValue() == null ? "" : change.getValue().strip())));
        getFooter().add(new Button("Cancelar", click -> close()), confirm);
    }
}
