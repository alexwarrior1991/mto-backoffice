package com.alejandro.mtobackoffice.ui.support;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

/**
 * Anterior / siguiente para una lista que el servicio pagina con {@code first}/{@code max} y
 * <b>sin total</b> (los miembros de un perfil o de un rol en mto-users). Sin recuento, una pagina
 * llena es la unica senal de que puede haber mas: {@link #shown(int)} habilita «siguiente» solo
 * entonces, y la ultima pagina puede salir vacia si el total era multiplo del tamano.
 */
public class OffsetPager extends HorizontalLayout {

    private final int pageSize;
    private final Button previous;
    private final Button next;
    private final Span info = new Span();

    private int first;

    /**
     * @param idPrefix  los ids son {@code <prefix>-previous}, {@code <prefix>-next} y {@code <prefix>-page}
     * @param onChange  que hacer cuando cambia la pagina (volver a pedirla)
     */
    public OffsetPager(String idPrefix, int pageSize, Runnable onChange) {
        this.pageSize = pageSize;
        previous = new Button("Anteriores", VaadinIcon.ANGLE_LEFT.create(), click -> {
            first = Math.max(0, first - pageSize);
            onChange.run();
        });
        previous.setId(idPrefix + "-previous");
        previous.addThemeVariants(ButtonVariant.LUMO_SMALL);
        next = new Button("Siguientes", VaadinIcon.ANGLE_RIGHT.create(), click -> {
            first += pageSize;
            onChange.run();
        });
        next.setIconAfterText(true);
        next.setId(idPrefix + "-next");
        next.addThemeVariants(ButtonVariant.LUMO_SMALL);
        info.setId(idPrefix + "-page");
        setAlignItems(FlexComponent.Alignment.BASELINE);
        add(previous, info, next);
        reset();
    }

    public int first() {
        return first;
    }

    public int pageSize() {
        return pageSize;
    }

    /** Vuelve a la primera pagina sin pedir nada: quien cambia de lista pide despues. */
    public void reset() {
        first = 0;
        shown(0);
    }

    /** Cuantas filas trajo la pagina actual: llena, puede haber mas; corta, es la ultima. */
    public void shown(int count) {
        previous.setEnabled(first > 0);
        next.setEnabled(count >= pageSize);
        info.setText("Pagina " + (first / pageSize + 1));
    }
}
