package com.alejandro.mtobackoffice.ui.resources;

import com.alejandro.mtobackoffice.client.configuration.MasterFilters;
import com.alejandro.mtobackoffice.client.dto.MergePatch;
import com.alejandro.mtobackoffice.client.dto.resources.QualificationKind;
import com.alejandro.mtobackoffice.client.dto.resources.QualificationTypeDto;
import com.alejandro.mtobackoffice.client.dto.resources.QualificationTypeFilter;
import com.alejandro.mtobackoffice.client.dto.resources.QualificationTypeUpdateRequest;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.configuration.security.ResourcesRoles;
import com.alejandro.mtobackoffice.ui.MainLayout;
import com.alejandro.mtobackoffice.ui.master.EnabledFilter;
import com.alejandro.mtobackoffice.ui.support.LazyPages;
import com.alejandro.mtobackoffice.ui.support.Screens;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;

/**
 * El catalogo de tipos de cualificacion de mto-resources, paginado, buscado y filtrado en el servidor,
 * como en mto-frontend: los permisos de conducir (con cual), las habilitaciones para manejar un vehiculo
 * y las de seguridad. Darlos de alta y modificarlos pide {@code resources-write}; retirarlos (un
 * {@code DELETE}: no se ofrece a nadie mas, y quien ya lo tiene lo conserva), {@code resources-delete};
 * volver a ofrecerlo es un {@code PATCH active=true}. No tiene historial: es un catalogo.
 */
@Route(value = ResourcesRoutes.QUALIFICATION_TYPES, layout = MainLayout.class)
@PageTitle("Tipos de cualificacion")
@Menu(title = "Cualificaciones", order = 76, icon = "vaadin:diploma")
@RolesAllowed(ResourcesRoles.RESOURCES_READ)
public class QualificationTypesView extends VerticalLayout {

    static final int PAGE_SIZE = 50;
    public static final String ACTIONS_COLUMN = "actions";
    private static final List<String> DEFAULT_SORT = List.of("code,asc");

    private final ResourcesClients clients;
    private final boolean canWrite;
    private final boolean canDelete;

    private final TextField search = new TextField("Buscar");
    private final Select<QualificationKind> kind = new Select<>();
    private final Select<EnabledFilter> state = EnabledFilter.select("Estado", "Todos", "Activos", "Retirados");
    private final Span count = new Span();
    private final Grid<QualificationTypeDto> grid = new Grid<>();
    private LazyPages<QualificationTypeDto> pages;

    public QualificationTypesView(ResourcesClients clients, AuthenticationContext authentication) {
        this.clients = clients;
        this.canWrite = authentication.hasRole(ResourcesRoles.RESOURCES_WRITE);
        this.canDelete = authentication.hasRole(ResourcesRoles.RESOURCES_DELETE);
        setSizeFull();

        search.setId("qualification-types-search");
        search.setPlaceholder("Codigo, nombre");
        search.setClearButtonVisible(true);
        search.setValueChangeMode(ValueChangeMode.LAZY);
        kind.setLabel("Clase");
        kind.setId("qualification-types-kind");
        kind.setItems(QualificationKind.selectable());
        kind.setItemLabelGenerator(value -> value == null ? "Todas" : value.label());
        kind.setEmptySelectionAllowed(true);
        kind.setEmptySelectionCaption("Todas");
        state.setId("qualification-types-state");
        for (HasValue<?, ?> filter : List.<HasValue<?, ?>>of(search, kind, state)) {
            filter.addValueChangeListener(change -> refresh());
        }

        Button create = new Button("Nuevo tipo", VaadinIcon.PLUS.create(),
                click -> new QualificationTypeEditorDialog(null, clients, this::refresh).open());
        create.setId("qualification-type-create");
        create.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        create.setVisible(canWrite);

        FlexLayout filters = new FlexLayout(search, kind, state);
        filters.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        filters.setAlignItems(FlexComponent.Alignment.BASELINE);
        filters.getStyle().set("gap", "var(--lumo-space-s)");
        HorizontalLayout toolbar = new HorizontalLayout(count, create);
        toolbar.setAlignItems(FlexComponent.Alignment.BASELINE);
        toolbar.setWidthFull();
        toolbar.expand(count);
        add(new H2("Tipos de cualificacion"), filters, toolbar, buildGrid());
        expand(grid);
    }

    private Grid<QualificationTypeDto> buildGrid() {
        grid.setId("qualification-types-grid");
        grid.addColumn(QualificationTypeDto::code).setHeader("Codigo").setKey("code").setSortProperty("code").setSortable(true).setAutoWidth(true);
        grid.addColumn(QualificationTypeDto::name).setHeader("Nombre").setKey("name").setSortProperty("name").setSortable(true).setAutoWidth(true);
        grid.addColumn(type -> type.kind() == null ? "" : type.kind().label()).setHeader("Clase").setKey("kind").setSortProperty("kind")
                .setSortable(true).setAutoWidth(true);
        grid.addColumn(type -> type.drivingLicence() == null ? "" : type.drivingLicence().label()).setHeader("Permiso").setKey("licence")
                .setAutoWidth(true);
        grid.addColumn(type -> type.validityMonths() == null ? "" : type.validityMonths() + " meses").setHeader("Vigencia habitual")
                .setKey("validity").setAutoWidth(true);
        grid.addColumn(type -> type.isActive() ? "Activo" : "Retirado").setHeader("Estado").setKey("active").setSortProperty("active")
                .setSortable(true).setAutoWidth(true);
        grid.addColumn(new ComponentRenderer<>(this::rowActions)).setHeader("").setKey(ACTIONS_COLUMN).setAutoWidth(true).setFlexGrow(0);
        if (canWrite) {
            grid.addItemDoubleClickListener(event -> new QualificationTypeEditorDialog(event.getItem(), clients, this::refresh).open());
        }
        grid.setPageSize(PAGE_SIZE);
        grid.setMultiSort(false);
        grid.setSizeFull();
        pages = LazyPages.of(grid, this::load, total -> count.setText(total == 1 ? "1 tipo" : total + " tipos"));
        return grid;
    }

    /** La columna existe siempre, como en las demas listas, aunque quien solo lee no tenga nada que hacer en ella. */
    private Component rowActions(QualificationTypeDto type) {
        HorizontalLayout actions = new HorizontalLayout();
        actions.setSpacing(false);
        if (canWrite) {
            actions.add(Screens.rowButton("qualification-type-edit-" + type.id(), VaadinIcon.EDIT, "Modificar " + type.code(),
                    click -> new QualificationTypeEditorDialog(type, clients, this::refresh).open()));
        }
        if (type.isActive() && canDelete) {
            Button retire = Screens.rowButton("qualification-type-retire-" + type.id(), VaadinIcon.BAN, "Retirar " + type.code(),
                    click -> Screens.confirm("Retirar " + type.label(),
                            "No se borra: deja de ofrecerse a las personas y a los modelos; quien ya la tiene la conserva. Se puede volver a ofrecer.",
                            "Retirar", () -> retire(type)));
            retire.addThemeVariants(ButtonVariant.LUMO_ERROR);
            actions.add(retire);
        } else if (!type.isActive() && canWrite) {
            actions.add(Screens.rowButton("qualification-type-activate-" + type.id(), VaadinIcon.CHECK_CIRCLE, "Volver a ofrecer " + type.code(),
                    click -> activate(type)));
        }
        return actions;
    }

    private void retire(QualificationTypeDto type) {
        try {
            clients.qualificationTypes().deactivate(type.id());
            Screens.success("Retirado " + type.label());
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
        refresh();
    }

    private void activate(QualificationTypeDto type) {
        try {
            clients.qualificationTypes().update(type.id(), MergePatch.of(QualificationTypeUpdateRequest.active(true), type.version()));
            Screens.success("Se vuelve a ofrecer " + type.label());
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
        refresh();
    }

    void refresh() {
        pages.refresh();
    }

    private QualificationTypeFilter filter() {
        return new QualificationTypeFilter(search.getValue(), kind.getValue(), state.getValue() == null ? null : state.getValue().value());
    }

    private LazyPages.Page<QualificationTypeDto> load(int offset, int limit, List<QuerySortOrder> sort) {
        int size = Math.max(1, limit);
        List<String> order = MasterFilters.sort(sort);
        return LazyPages.Page.of(clients.qualificationTypes().search(filter(), offset / size, size, order.isEmpty() ? DEFAULT_SORT : order));
    }
}
