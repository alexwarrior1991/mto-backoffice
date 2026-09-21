package com.alejandro.mtobackoffice.ui.master;

import com.alejandro.mtobackoffice.client.dto.master.MasterDto;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Una coleccion de hijos dentro del editor de su padre (las mensulas de un perfil, las agujas de
 * un aislador): una tabla y el alta, la modificacion y la baja sobre una lista de trabajo.
 *
 * <p>Al guardar el padre, si alguien toco la lista, va <b>entera</b> en el {@code PUT}: para el
 * servicio la coleccion que se manda es el estado final, y el hijo que no se manda se borra
 * ({@code README_API.md} §4). Mientras nadie la toque el padre manda {@code null}, que es lo que
 * la deja como esta. Por eso {@link #edited()} distingue "no he dicho nada" de "esta es la
 * lista".</p>
 */
public class ChildrenEditor<C extends MasterDto> extends VerticalLayout {

    /** Abre el dialogo de un hijo y avisa con el hijo ya escrito cuando la persona acepta. */
    public interface Opener<C> {
        void open(C child, Consumer<C> onAccepted);
    }

    private final Grid<C> grid = new Grid<>();
    private final List<C> items = new ArrayList<>();
    private final Button add;
    private final Button edit;
    private final Button remove;
    private final int max;
    private final Supplier<C> factory;
    private final Opener<C> opener;
    private boolean touched;

    public ChildrenEditor(String title, String idPrefix, List<C> initial, int max, Supplier<C> factory,
                          Opener<C> opener, Consumer<Grid<C>> columns) {
        this.max = max;
        this.factory = factory;
        this.opener = opener;
        if (initial != null) {
            items.addAll(initial);
        }
        setPadding(false);
        setSpacing(false);

        add = new Button("Anadir", VaadinIcon.PLUS.create(), click -> create());
        add.setId(idPrefix + "-add");
        add.addThemeVariants(ButtonVariant.LUMO_SMALL);
        edit = new Button("Modificar", VaadinIcon.EDIT.create(), click -> grid.getSelectedItems().stream().findFirst().ifPresent(this::modify));
        edit.setId(idPrefix + "-edit");
        edit.addThemeVariants(ButtonVariant.LUMO_SMALL);
        remove = new Button("Quitar", VaadinIcon.TRASH.create(), click -> grid.getSelectedItems().stream().findFirst().ifPresent(this::remove));
        remove.setId(idPrefix + "-remove");
        remove.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);

        H4 heading = new H4(title);
        heading.addClassNames(LumoUtility.Margin.NONE);
        Span hint = new Span("Se guardan con el " + (title.toLowerCase(java.util.Locale.ROOT).endsWith("s") ? "padre" : "padre"));
        hint.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);
        HorizontalLayout toolbar = new HorizontalLayout(heading, hint, add, edit, remove);
        toolbar.setAlignItems(FlexComponent.Alignment.BASELINE);
        toolbar.expand(hint);
        toolbar.setWidthFull();

        columns.accept(grid);
        grid.setId(idPrefix + "-grid");
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.setAllRowsVisible(true);
        grid.addSelectionListener(selection -> updateButtons());
        grid.addItemDoubleClickListener(event -> modify(event.getItem()));
        add(toolbar, grid);
        show();
    }

    /** La lista tal como queda, solo si alguien la toco; vacia si no hay nada que decir. */
    public Optional<List<C>> edited() {
        return touched ? Optional.of(List.copyOf(items)) : Optional.empty();
    }

    public List<C> items() {
        return List.copyOf(items);
    }

    private void create() {
        if (items.size() >= max) {
            return;
        }
        C child = factory.get();
        opener.open(child, accepted -> {
            items.add(accepted);
            touched = true;
            show();
        });
    }

    private void modify(C child) {
        opener.open(child, accepted -> {
            touched = true;
            show();
        });
    }

    private void remove(C child) {
        items.remove(child);
        touched = true;
        show();
    }

    private void show() {
        grid.setItems(items);
        updateButtons();
    }

    private void updateButtons() {
        boolean selected = !grid.getSelectedItems().isEmpty();
        add.setEnabled(items.size() < max);
        edit.setEnabled(selected);
        remove.setEnabled(selected);
    }
}
