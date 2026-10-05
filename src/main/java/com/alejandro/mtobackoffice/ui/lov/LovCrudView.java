package com.alejandro.mtobackoffice.ui.lov;

import com.alejandro.mtobackoffice.client.configuration.LovClient;
import com.alejandro.mtobackoffice.client.configuration.LovResource;
import com.alejandro.mtobackoffice.client.dto.LovDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.configuration.security.SecurityRoles;
import com.alejandro.mtobackoffice.ui.MainLayout;
import com.alejandro.mtobackoffice.ui.support.TextMatching;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.RolesAllowed;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

/**
 * Los 17 catalogos con una sola pantalla: el recurso va en la ruta ({@code catalogos/pole-types}) y
 * todo lo demas es igual, porque los 17 controladores heredan los mismos ocho endpoints y el mismo
 * DTO. El catalogo entero se lee en memoria (el servicio devuelve una lista, no una pagina) y el
 * filtro es de cliente.
 *
 * <p>Los botones siguen los permisos del servicio: crear y modificar piden {@code config-write} y
 * {@code lov-manage}; borrar, {@code config-delete} y {@code lov-manage}; los lotes,
 * {@code config-import} y {@code lov-manage}. Esconderlos es cortesia: la guarda real esta en el
 * servicio, que responde 403 si se fuerza la llamada.</p>
 */
@Route(value = LovCrudView.ROUTE_PREFIX + "/:" + LovCrudView.RESOURCE_PARAMETER, layout = MainLayout.class)
@RolesAllowed(SecurityRoles.CONFIG_READ)
public class LovCrudView extends VerticalLayout implements BeforeEnterObserver, HasDynamicTitle {

    public static final String ROUTE_PREFIX = "catalogos";
    public static final String RESOURCE_PARAMETER = "resource";
    static final String ACTIONS_COLUMN = "actions";

    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.ROOT);

    private final LovClient client;
    private final boolean canWrite;
    private final boolean canDelete;
    private final boolean canBulk;

    private final H2 title = new H2("Catalogos");
    private final TextField filter = new TextField();
    private final Checkbox onlyEnabled = new Checkbox("Solo activos");
    private final Span count = new Span();
    private final Button create = new Button("Nuevo", VaadinIcon.PLUS.create());
    private final Button bulkCreate = new Button("Alta multiple", VaadinIcon.LIST_OL.create());
    private final Button enableSelected = new Button("Activar seleccionados", VaadinIcon.CHECK.create());
    private final Button disableSelected = new Button("Desactivar seleccionados", VaadinIcon.BAN.create());
    private final Grid<LovDto> grid = new Grid<>();
    private Grid.Column<LovDto> parentColumn;

    private LovResource resource;
    private List<LovDto> catalogue = List.of();

    public static String pathOf(LovResource resource) {
        return ROUTE_PREFIX + "/" + resource.path();
    }

    public LovCrudView(LovClient client, AuthenticationContext authenticationContext) {
        this.client = client;
        this.canWrite = authenticationContext.hasAllRoles(SecurityRoles.CONFIG_WRITE, SecurityRoles.LOV_MANAGE);
        this.canDelete = authenticationContext.hasAllRoles(SecurityRoles.CONFIG_DELETE, SecurityRoles.LOV_MANAGE);
        this.canBulk = authenticationContext.hasAllRoles(SecurityRoles.CONFIG_IMPORT, SecurityRoles.LOV_MANAGE);
        setSizeFull();
        add(title, toolbar(), buildGrid());
        expand(grid);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String path = event.getRouteParameters().get(RESOURCE_PARAMETER).orElse("");
        Optional<LovResource> found = LovResource.byPath(path);
        if (found.isEmpty()) {
            event.rerouteToError(NotFoundException.class, "No existe el catalogo '" + path + "'");
            return;
        }
        resource = found.get();
        title.setText(resource.title());
        parentColumn.setVisible(resource.parent().isPresent());
        resource.parent().ifPresent(type -> parentColumn.setHeader(type.label()));
        load();
    }

    @Override
    public String getPageTitle() {
        return resource == null ? "Catalogos" : resource.title();
    }

    private Component toolbar() {
        filter.setPlaceholder("Filtrar por codigo o descripcion");
        filter.setPrefixComponent(VaadinIcon.SEARCH.create());
        filter.setClearButtonVisible(true);
        filter.setValueChangeMode(ValueChangeMode.LAZY);
        filter.addValueChangeListener(change -> applyFilter());
        onlyEnabled.addValueChangeListener(change -> applyFilter());

        Button reload = new Button("Recargar", VaadinIcon.REFRESH.create(), click -> load());

        create.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        create.addClickListener(click -> openEditor(null));
        create.setVisible(canWrite);

        bulkCreate.addClickListener(click -> openBulkCreate());
        bulkCreate.setVisible(canBulk);
        enableSelected.addClickListener(click -> bulkEnable(true));
        enableSelected.setVisible(canBulk);
        enableSelected.setEnabled(false);
        disableSelected.addClickListener(click -> bulkEnable(false));
        disableSelected.setVisible(canBulk);
        disableSelected.setEnabled(false);

        HorizontalLayout toolbar = new HorizontalLayout(filter, onlyEnabled, reload, count, create, bulkCreate, enableSelected, disableSelected);
        toolbar.setWidthFull();
        toolbar.setAlignItems(FlexComponent.Alignment.BASELINE);
        toolbar.expand(count);
        return toolbar;
    }

    private Component buildGrid() {
        // Orden natural y sin mirar tildes, como la lista de partida: PT2 antes que PT10.
        grid.addColumn(LovDto::code).setHeader("Codigo").setKey("code").setAutoWidth(true)
                .setComparator(Comparator.comparing(LovDto::code, TextMatching.NATURAL_ORDER));
        grid.addColumn(LovDto::description).setHeader("Descripcion").setKey("description").setFlexGrow(1)
                .setComparator(Comparator.comparing(LovDto::description, TextMatching.NATURAL_ORDER));
        // El tipo de los tres catalogos que lo tienen, por su codigo; en los demas no se ve.
        parentColumn = grid.addColumn(dto -> resource == null ? "" : resource.parent()
                        .flatMap(type -> dto.parent(type.field())).map(LovDto::code).orElse(""))
                .setKey("parent").setAutoWidth(true);
        parentColumn.setVisible(false);
        grid.addColumn(dto -> dto.isEnabled() ? "Si" : "No").setHeader("Activo").setKey("enabled").setAutoWidth(true);
        grid.addColumn(dto -> dto.versionDate() == null ? "" : DATE_TIME.format(dto.versionDate()))
                .setHeader("Modificado").setKey("versionDate").setAutoWidth(true);
        grid.addColumn(dto -> dto.versionUser() == null ? "" : dto.versionUser()).setHeader("Por").setKey("versionUser").setAutoWidth(true);
        if (canWrite || canDelete) {
            grid.addColumn(new ComponentRenderer<>(this::rowActions)).setHeader("").setKey(ACTIONS_COLUMN).setAutoWidth(true).setFlexGrow(0);
        }
        grid.setSelectionMode(canBulk ? Grid.SelectionMode.MULTI : Grid.SelectionMode.SINGLE);
        grid.addSelectionListener(selection -> {
            boolean any = !selection.getAllSelectedItems().isEmpty();
            enableSelected.setEnabled(any);
            disableSelected.setEnabled(any);
        });
        if (canWrite) {
            grid.addItemDoubleClickListener(event -> openEditor(event.getItem()));
        }
        grid.setSizeFull();
        return grid;
    }

    private Component rowActions(LovDto dto) {
        HorizontalLayout actions = new HorizontalLayout();
        actions.setSpacing(false);
        if (canWrite) {
            Button edit = new Button(VaadinIcon.EDIT.create(), click -> openEditor(dto));
            edit.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_SMALL);
            edit.setTooltipText("Modificar");
            edit.setId("edit-" + dto.id());
            actions.add(edit);
        }
        if (canDelete) {
            Button delete = new Button(VaadinIcon.TRASH.create(), click -> confirmDelete(dto));
            delete.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);
            delete.setTooltipText("Borrar");
            delete.setId("delete-" + dto.id());
            actions.add(delete);
        }
        return actions;
    }

    void load() {
        try {
            catalogue = client.findAll(resource.path()).stream()
                    .sorted(Comparator.comparing(LovDto::code, TextMatching.NATURAL_ORDER))
                    .toList();
        } catch (BackofficeApiException failure) {
            catalogue = List.of();
            UiErrors.show(failure);
        }
        applyFilter();
    }

    /** El filtro es local (el catalogo llega entero) y no distingue mayusculas ni tildes. */
    private void applyFilter() {
        String text = filter.getValue() == null ? "" : filter.getValue();
        boolean enabledOnly = Boolean.TRUE.equals(onlyEnabled.getValue());
        List<LovDto> shown = catalogue.stream()
                .filter(dto -> !enabledOnly || dto.isEnabled())
                .filter(dto -> TextMatching.contains(dto.code(), text) || TextMatching.contains(dto.description(), text))
                .toList();
        grid.deselectAll();
        grid.setItems(shown);
        count.setText(shown.size() == catalogue.size()
                ? catalogue.size() + " entradas"
                : shown.size() + " de " + catalogue.size() + " entradas");
    }

    private void openEditor(LovDto existing) {
        String path = resource.path();
        new LovEditorDialog(resource, existing, parents(),
                dto -> existing == null ? client.create(path, dto) : client.update(path, existing.id(), dto),
                saved -> load()).open();
    }

    private void openBulkCreate() {
        String path = resource.path();
        new LovBulkCreateDialog(resource, parents(), entries -> client.bulkCreate(path, entries), created -> load()).open();
    }

    /**
     * Las entradas del catalogo del tipo, en los tres catalogos que lo tienen. Si no se pueden leer se
     * avisa y el dialogo se abre igual: una modificacion conserva el tipo que ya tenia la entrada.
     */
    private List<LovDto> parents() {
        return resource.parent().map(type -> {
            try {
                return client.findAll(type.path());
            } catch (BackofficeApiException failure) {
                UiErrors.show(failure);
                return List.<LovDto>of();
            }
        }).orElse(List.of());
    }

    private void confirmDelete(LovDto dto) {
        // El servicio borra la fila de verdad, y no borra una entrada que otro registro usa (409 BUS-002).
        ConfirmDialog dialog = new ConfirmDialog("Borrar " + dto.code(),
                "La entrada se borra del catalogo. Si algun registro la usa, el servicio no la borra: desactivala. ¿Seguro?",
                "Borrar", confirm -> delete(dto), "Cancelar", cancel -> { });
        dialog.setConfirmButtonTheme("error primary");
        dialog.open();
    }

    private void delete(LovDto dto) {
        try {
            client.delete(resource.path(), dto.id());
            Notification.show("Borrada " + dto.code(), 3000, Notification.Position.BOTTOM_START)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            load();
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }

    private void bulkEnable(boolean enabled) {
        Set<LovDto> selected = grid.getSelectedItems();
        if (selected.isEmpty()) {
            return;
        }
        List<LovDto> changes = selected.stream().map(dto -> dto.withEnabled(enabled)).toList();
        try {
            List<LovDto> updated = client.bulkUpdate(resource.path(), changes);
            Notification.show(updated.size() + (enabled ? " entradas activadas" : " entradas desactivadas"), 3000,
                    Notification.Position.BOTTOM_START).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            load();
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }
}
