package com.alejandro.mtobackoffice.ui.resources;

import com.alejandro.mtobackoffice.client.configuration.MasterFilters;
import com.alejandro.mtobackoffice.client.dto.MergePatch;
import com.alejandro.mtobackoffice.client.dto.resources.ParkingSiteDto;
import com.alejandro.mtobackoffice.client.dto.resources.ParkingSiteFilter;
import com.alejandro.mtobackoffice.client.dto.resources.ParkingSiteKind;
import com.alejandro.mtobackoffice.client.dto.resources.ParkingSiteUpdateRequest;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.configuration.security.ResourcesRoles;
import com.alejandro.mtobackoffice.ui.MainLayout;
import com.alejandro.mtobackoffice.ui.master.EnabledFilter;
import com.alejandro.mtobackoffice.ui.master.RefItem;
import com.alejandro.mtobackoffice.ui.support.LazyPages;
import com.alejandro.mtobackoffice.ui.support.Screens;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
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
 * El catalogo de sitios de aparcamiento de mto-resources, paginado y filtrado en el servidor, como en
 * mto-frontend. Es opcional: un vehiculo se puede dejar en una via y un kp, o con una descripcion, sin
 * sitio. Escribir pide {@code resources-write}; retirar, {@code resources-delete}. EP y via se nombran
 * con mto-configuration ({@code #id} sin {@code config-read}).
 */
@Route(value = ResourcesRoutes.PARKING_SITES, layout = MainLayout.class)
@PageTitle("Sitios de aparcamiento")
@Menu(title = "Sitios de aparcamiento", order = 75, icon = "vaadin:car")
@RolesAllowed(ResourcesRoles.RESOURCES_READ)
public class ParkingSitesView extends VerticalLayout {

    static final int PAGE_SIZE = 50;
    public static final String ACTIONS_COLUMN = "actions";
    private static final List<String> DEFAULT_SORT = List.of("code,asc");

    private final ResourcesClients clients;
    private final ResourcesNames names;
    private final boolean canWrite;
    private final boolean canDelete;

    private final TextField search = new TextField("Buscar");
    private final Select<ParkingSiteKind> kind = new Select<>();
    private final ComboBox<RefItem> executionPackage = new ComboBox<>("EP");
    private final Select<EnabledFilter> state = EnabledFilter.select("Estado", "Todos", "Activos", "Retirados");
    private final Span count = new Span();
    private final Grid<ParkingSiteDto> grid = new Grid<>();
    private LazyPages<ParkingSiteDto> pages;

    public ParkingSitesView(ResourcesClients clients, AuthenticationContext authentication) {
        this.clients = clients;
        this.names = ResourcesNames.of(clients, authentication);
        this.canWrite = authentication.hasRole(ResourcesRoles.RESOURCES_WRITE);
        this.canDelete = authentication.hasRole(ResourcesRoles.RESOURCES_DELETE);
        setSizeFull();

        search.setId("parking-sites-search");
        search.setPlaceholder("Codigo, nombre, direccion");
        search.setClearButtonVisible(true);
        search.setValueChangeMode(ValueChangeMode.LAZY);
        kind.setLabel("Tipo");
        kind.setId("parking-sites-kind");
        kind.setItems(ParkingSiteKind.selectable());
        kind.setItemLabelGenerator(value -> value == null ? "Todos" : value.label());
        kind.setEmptySelectionAllowed(true);
        kind.setEmptySelectionCaption("Todos");
        executionPackage.setId("parking-sites-package");
        executionPackage.setItems(names.packages());
        executionPackage.setItemLabelGenerator(RefItem::label);
        executionPackage.setClearButtonVisible(true);
        executionPackage.setVisible(names.readsConfiguration());
        state.setId("parking-sites-state");
        for (HasValue<?, ?> filter : List.<HasValue<?, ?>>of(search, kind, executionPackage, state)) {
            filter.addValueChangeListener(change -> refresh());
        }

        Button create = new Button("Nuevo sitio", VaadinIcon.PLUS.create(),
                click -> new ParkingSiteEditorDialog(null, clients, names, this::refresh).open());
        create.setId("parking-site-create");
        create.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        create.setVisible(canWrite);

        FlexLayout filters = new FlexLayout(search, kind, executionPackage, state);
        filters.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        filters.setAlignItems(FlexComponent.Alignment.BASELINE);
        filters.getStyle().set("gap", "var(--lumo-space-s)");
        HorizontalLayout toolbar = new HorizontalLayout(count, create);
        toolbar.setAlignItems(FlexComponent.Alignment.BASELINE);
        toolbar.setWidthFull();
        toolbar.expand(count);
        add(new H2("Sitios de aparcamiento"), filters, toolbar, buildGrid());
        expand(grid);
    }

    private Grid<ParkingSiteDto> buildGrid() {
        grid.setId("parking-sites-grid");
        grid.addColumn(ParkingSiteDto::code).setHeader("Codigo").setKey("code").setSortProperty("code").setSortable(true).setAutoWidth(true);
        grid.addColumn(ParkingSiteDto::name).setHeader("Nombre").setKey("name").setSortProperty("name").setSortable(true).setAutoWidth(true);
        grid.addColumn(site -> site.kind() == null ? "" : site.kind().label()).setHeader("Tipo").setKey("kind").setSortProperty("kind")
                .setSortable(true).setAutoWidth(true);
        grid.addColumn(site -> site.executionPackageId() == null ? "" : names.packageName(site.executionPackageId())).setHeader("EP")
                .setKey("package").setAutoWidth(true);
        grid.addColumn(this::where).setHeader("Via y kp").setKey("where").setAutoWidth(true);
        grid.addColumn(ResourcesFormats::access).setHeader("Accesos").setKey("access").setAutoWidth(true);
        grid.addColumn(site -> site.isActive() ? "Activo" : "Retirado").setHeader("Estado").setKey("active").setSortProperty("active")
                .setSortable(true).setAutoWidth(true);
        grid.addColumn(new ComponentRenderer<>(this::rowActions)).setHeader("").setKey(ACTIONS_COLUMN).setAutoWidth(true).setFlexGrow(0);
        if (canWrite) {
            grid.addItemDoubleClickListener(event -> new ParkingSiteEditorDialog(event.getItem(), clients, names, this::refresh).open());
        }
        grid.setPageSize(PAGE_SIZE);
        grid.setMultiSort(false);
        grid.setSizeFull();
        pages = LazyPages.of(grid, this::load, total -> count.setText(total == 1 ? "1 sitio" : total + " sitios"));
        return grid;
    }

    private String where(ParkingSiteDto site) {
        return ResourcesHistory.join(site.trackId() == null ? null : names.trackName(site.trackId()),
                site.kp() == null ? null : "kp " + site.kp().toPlainString());
    }

    private Component rowActions(ParkingSiteDto site) {
        HorizontalLayout actions = new HorizontalLayout();
        actions.setSpacing(false);
        if (canWrite) {
            actions.add(Screens.rowButton("parking-site-edit-" + site.id(), VaadinIcon.EDIT, "Modificar " + site.code(),
                    click -> new ParkingSiteEditorDialog(site, clients, names, this::refresh).open()));
        }
        if (site.isActive() && canDelete) {
            Button retire = Screens.rowButton("parking-site-retire-" + site.id(), VaadinIcon.BAN, "Retirar " + site.code(),
                    click -> Screens.confirm("Retirar " + site.label(),
                            "Sale del catalogo; las ubicaciones registradas alli lo siguen nombrando. Se puede volver a ofrecer.",
                            "Retirar", () -> retire(site)));
            retire.addThemeVariants(ButtonVariant.LUMO_ERROR);
            actions.add(retire);
        } else if (!site.isActive() && canWrite) {
            actions.add(Screens.rowButton("parking-site-activate-" + site.id(), VaadinIcon.CHECK_CIRCLE, "Volver a ofrecer " + site.code(),
                    click -> activate(site)));
        }
        return actions;
    }

    private void retire(ParkingSiteDto site) {
        try {
            clients.parkingSites().deactivate(site.id());
            Screens.success("Retirado " + site.label());
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
        refresh();
    }

    private void activate(ParkingSiteDto site) {
        try {
            clients.parkingSites().update(site.id(), MergePatch.of(ParkingSiteUpdateRequest.active(true), site.version()));
            Screens.success("Se vuelve a ofrecer " + site.label());
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
        refresh();
    }

    void refresh() {
        pages.refresh();
    }

    private ParkingSiteFilter filter() {
        return new ParkingSiteFilter(search.getValue(), kind.getValue(), executionPackage.getValue() == null ? null : executionPackage.getValue().id(),
                null, null, null, state.getValue() == null ? null : state.getValue().value());
    }

    private LazyPages.Page<ParkingSiteDto> load(int offset, int limit, List<QuerySortOrder> sort) {
        int size = Math.max(1, limit);
        List<String> order = MasterFilters.sort(sort);
        return LazyPages.Page.of(clients.parkingSites().search(filter(), offset / size, size, order.isEmpty() ? DEFAULT_SORT : order));
    }
}
