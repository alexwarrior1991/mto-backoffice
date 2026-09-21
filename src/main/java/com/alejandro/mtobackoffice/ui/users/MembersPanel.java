package com.alejandro.mtobackoffice.ui.users;

import com.alejandro.mtobackoffice.client.dto.users.UserDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.ui.support.OffsetPager;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.List;
import java.util.function.IntFunction;

/**
 * Quien tiene un perfil o un rol: la lista plana y sin total que devuelve mto-users, paseada con
 * {@link OffsetPager}. Solo asignaciones directas: quien tiene un rol por un perfil aparece en el
 * perfil, no en el rol, y eso esta documentado alli, no arreglado aqui. Una fila abre la ficha.
 */
class MembersPanel extends VerticalLayout {

    static final int PAGE_SIZE = 50;

    private final H4 title = new H4("Miembros");
    private final Grid<UserDto> grid = new Grid<>();
    private final OffsetPager pager;

    private IntFunction<List<UserDto>> page;

    /** @param idPrefix el del grid ({@code <prefix>-grid}) y del paginador ({@code <prefix>-next}...). */
    MembersPanel(String idPrefix, String note) {
        setPadding(false);
        pager = new OffsetPager(idPrefix, PAGE_SIZE, this::load);
        grid.setId(idPrefix + "-grid");
        grid.addColumn(UserDto::username).setHeader("Usuario").setKey("username").setAutoWidth(true);
        grid.addColumn(UserDto::fullName).setHeader("Nombre").setKey("name").setFlexGrow(1);
        grid.addColumn(dto -> dto.email() == null ? "" : dto.email()).setHeader("Email").setKey("email").setAutoWidth(true);
        grid.addColumn(dto -> dto.isEnabled() ? "Si" : "No").setHeader("Activo").setKey("enabled").setAutoWidth(true);
        grid.setAllRowsVisible(true);
        grid.addItemClickListener(event -> UI.getCurrent().navigate(UserDetailView.class, UserDetailView.parametersOf(event.getItem().id())));
        add(title, new Paragraph(note), grid, pager);
        setVisible(false);
    }

    /** Empieza por la primera pagina de otra lista. */
    void show(String what, IntFunction<List<UserDto>> page) {
        this.page = page;
        title.setText("Miembros de " + what);
        setVisible(true);
        pager.reset();
        load();
    }

    void clear() {
        page = null;
        grid.setItems(List.of());
        setVisible(false);
    }

    private void load() {
        if (page == null) {
            return;
        }
        try {
            List<UserDto> members = page.apply(pager.first());
            grid.setItems(members);
            pager.shown(members.size());
        } catch (BackofficeApiException failure) {
            grid.setItems(List.of());
            pager.shown(0);
            UiErrors.show(failure);
        }
    }
}
