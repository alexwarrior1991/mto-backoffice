package com.alejandro.mtobackoffice.ui.support;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.function.SerializableFunction;

/**
 * Como se abre una fila de una lista, igual que en mto-frontend: con doble clic o con su boton. Un
 * clic simple no abre nada (deja seleccionar o copiar texto), y el boton, con su nombre en el
 * tooltip, es lo que llega con el teclado.
 */
public final class RowActions {

    /** La columna del boton de abrir. */
    public static final String OPEN_COLUMN = "open";

    private RowActions() {
    }

    /** Un boton de fila: un icono pequeno, su nombre en el tooltip y un id para los tests. */
    public static Button button(String id, VaadinIcon icon, String tooltip, ComponentEventListener<ClickEvent<Button>> listener) {
        Button button = new Button(icon.create(), listener);
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_SMALL);
        button.setTooltipText(tooltip);
        button.setId(id);
        return button;
    }

    /**
     * La columna con el boton que abre la fila ({@code open-<id>}) y el doble clic que hace lo mismo.
     *
     * @param id      el id de la fila, para el del boton
     * @param tooltip lo que dice el boton («Abrir MO-000012»)
     */
    public static <T> void openWithDoubleClickOrButton(Grid<T> grid, SerializableFunction<T, Object> id,
                                                       SerializableFunction<T, String> tooltip, SerializableConsumer<T> open) {
        openButton(grid, id, tooltip, open);
        grid.addItemDoubleClickListener(event -> open.accept(event.getItem()));
    }

    /** Solo la columna con el boton de abrir, para las listas en las que la SPA tampoco abre con doble clic. */
    public static <T> void openButton(Grid<T> grid, SerializableFunction<T, Object> id,
                                      SerializableFunction<T, String> tooltip, SerializableConsumer<T> open) {
        grid.addColumn(new ComponentRenderer<>(row -> button("open-" + id.apply(row), VaadinIcon.ARROW_RIGHT, tooltip.apply(row),
                        click -> open.accept(row))))
                .setHeader("").setKey(OPEN_COLUMN).setAutoWidth(true).setFlexGrow(0);
    }
}
