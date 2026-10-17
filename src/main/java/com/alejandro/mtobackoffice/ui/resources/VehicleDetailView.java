package com.alejandro.mtobackoffice.ui.resources;

import com.alejandro.mtobackoffice.client.dto.MergePatch;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleDto;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleLocationDto;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleLocationKind;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleUpdateRequest;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.error.NotFoundApiException;
import com.alejandro.mtobackoffice.configuration.security.MaintenanceRoles;
import com.alejandro.mtobackoffice.configuration.security.ResourcesRoles;
import com.alejandro.mtobackoffice.ui.MainLayout;
import com.alejandro.mtobackoffice.ui.support.LazyPanel;
import com.alejandro.mtobackoffice.ui.support.Screens;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.RolesAllowed;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * La ficha de un vehiculo: su modelo, sus datos, «Donde esta» (el tipo, el sitio, la via y el kp, el
 * acceso, el origen y la hora; uno que va con otro dice con quien y donde esta su portador) y las
 * pestanas «Ubicaciones», el historial, y «Documentos», los que caducan, que piden sus datos al abrirse.
 * Registrar donde esta pide {@code resources-location-write}; modificar y escribir sus documentos,
 * {@code resources-fleet-write}; retirar y quitar un documento, {@code resources-delete}. Los segmentos
 * literales de la flota ({@code tablero}, {@code modelos}, {@code aparcamientos}) ganan a
 * {@code :vehicleId}. Es el enlace de los avisos de caducidad de sus documentos.
 */
@Route(value = ResourcesRoutes.FLEET + "/:" + VehicleDetailView.VEHICLE_ID_PARAMETER, layout = MainLayout.class)
@RolesAllowed(ResourcesRoles.RESOURCES_READ)
public class VehicleDetailView extends VerticalLayout implements BeforeEnterObserver, HasDynamicTitle {

    public static final String VEHICLE_ID_PARAMETER = "vehicleId";
    public static final String LOCATIONS_TAB = "Ubicaciones";
    public static final String DOCUMENTS_TAB = "Documentos";
    static final String RETIRE_TEXT = "No se borra: sus ubicaciones y los turnos lo siguen nombrando, y deja de ofrecerse en los turnos nuevos. "
            + "Si es el vehiculo por defecto de un equipo, el servicio no lo deja: cambialo antes en el organigrama. Se puede reactivar.";

    private final ResourcesClients clients;
    private final ResourcesNames names;
    private final boolean canWrite;
    private final boolean canDelete;
    private final boolean canLocate;
    private final boolean readsMaintenance;

    private final H2 title = new H2();
    private final Span statusBadge = new Span();
    private final Span stateBadge = new Span();
    private final Div summary = new Div();
    private final Div position = new Div();
    private final HorizontalLayout buttons = new HorizontalLayout();
    private final VerticalLayout tabsHolder = new VerticalLayout();
    private final List<LazyPanel> panels = new ArrayList<>();
    private VehicleDto vehicle;

    public static RouteParameters parametersOf(UUID vehicleId) {
        return new RouteParameters(VEHICLE_ID_PARAMETER, vehicleId.toString());
    }

    public VehicleDetailView(ResourcesClients clients, AuthenticationContext authentication) {
        this.clients = clients;
        this.names = ResourcesNames.of(clients, authentication);
        this.canWrite = authentication.hasRole(ResourcesRoles.RESOURCES_FLEET_WRITE);
        this.canDelete = authentication.hasRole(ResourcesRoles.RESOURCES_DELETE);
        this.canLocate = authentication.hasRole(ResourcesRoles.RESOURCES_LOCATION_WRITE);
        this.readsMaintenance = authentication.hasRole(MaintenanceRoles.MAINTENANCE_READ);
        setSizeFull();
        statusBadge.getElement().getThemeList().add("badge");
        statusBadge.setId("vehicle-status-badge");
        stateBadge.getElement().getThemeList().add("badge");
        stateBadge.setId("vehicle-state");
        HorizontalLayout heading = new HorizontalLayout(title, statusBadge, stateBadge);
        heading.setAlignItems(FlexComponent.Alignment.BASELINE);
        summary.setId("vehicle-summary");
        position.setId("vehicle-position");
        buttons.setAlignItems(FlexComponent.Alignment.BASELINE);
        buttons.getStyle().set("flex-wrap", "wrap");
        tabsHolder.setPadding(false);
        tabsHolder.setSizeFull();
        add(heading, summary, new H4("Donde esta"), position, buttons, tabsHolder);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String id = event.getRouteParameters().get(VEHICLE_ID_PARAMETER).orElse("");
        load(id, target -> event.forwardTo(target));
    }

    private void load(String id, Consumer<Class<? extends Component>> toList) {
        try {
            show(clients.vehicles().findById(UUID.fromString(id)));
        } catch (IllegalArgumentException | NotFoundApiException missing) {
            Notification.show("No existe el vehiculo " + id, 5000, Notification.Position.BOTTOM_START).addThemeVariants(NotificationVariant.LUMO_ERROR);
            toList.accept(VehiclesView.class);
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
            Screens.showLoadFailure(this, "el vehiculo", failure, () -> UI.getCurrent().navigate(VehiclesView.class),
                    () -> load(id, target -> UI.getCurrent().navigate(target)));
        }
    }

    @Override
    public String getPageTitle() {
        return vehicle == null ? "Vehiculo" : vehicle.label();
    }

    private void show(VehicleDto loaded) {
        Screens.clearLoadFailure(this);
        paint(loaded);
        TabSheet tabs = new TabSheet();
        tabs.setWidthFull();
        panels.clear();
        VehicleLocationsPanel locations = new VehicleLocationsPanel(() -> vehicle.id(), clients, names, readsMaintenance);
        panels.add(locations);
        tabs.add(LOCATIONS_TAB, locations);
        VehicleDocumentsPanel documents = new VehicleDocumentsPanel(() -> vehicle, clients, canWrite, canDelete);
        panels.add(documents);
        tabs.add(DOCUMENTS_TAB, documents);
        tabs.addSelectedChangeListener(change -> loadTab(tabs, change.getSelectedTab()));
        tabsHolder.removeAll();
        tabsHolder.add(tabs);
        loadTab(tabs, tabs.getSelectedTab());
    }

    private static void loadTab(TabSheet tabs, Tab tab) {
        if (tab != null && tabs.getComponent(tab) instanceof LazyPanel panel) {
            panel.ensureLoaded();
        }
    }

    private void reload() {
        try {
            paint(clients.vehicles().findById(vehicle.id()));
            panels.forEach(LazyPanel::reloadIfLoaded);
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }

    private void paint(VehicleDto loaded) {
        vehicle = loaded;
        title.setText(loaded.label());
        statusBadge.setText(ResourcesFormats.operational(loaded.operationalStatus(), null));
        statusBadge.getElement().getThemeList().set("success", Boolean.TRUE.equals(loaded.available()));
        statusBadge.getElement().getThemeList().set("error", !Boolean.TRUE.equals(loaded.available()));
        stateBadge.setText(ResourcesFormats.vehicleState(loaded.active()));
        stateBadge.getElement().getThemeList().set("contrast", !loaded.isActive());
        summary.removeAll();
        summary.add(new Div(ResourcesHistory.join(
                loaded.type() == null ? null : "Modelo: " + loaded.type().label(),
                loaded.mobility() == null ? null : loaded.mobility().label(),
                loaded.registrationPlate() == null ? null : "Matricula " + loaded.registrationPlate(),
                loaded.railVehicleNumber() == null ? null : "N. ferroviario " + loaded.railVehicleNumber(),
                ResourcesFormats.owner(loaded))));
        summary.add(new Div(ResourcesHistory.join(
                loaded.statusReason() == null ? null : "Motivo: " + loaded.statusReason(),
                loaded.serialNumber() == null ? null : "N. serie " + loaded.serialNumber(),
                loaded.manufactureYear() == null ? null : "Ano " + loaded.manufactureYear(),
                loaded.odometerKm() == null ? null : loaded.odometerKm() + " km",
                loaded.engineHours() == null ? null : loaded.engineHours().toPlainString() + " h de motor")));
        paintPosition(loaded.position());
        paintButtons(loaded);
    }

    /** Donde esta; uno que va con otro, ademas, donde esta su portador, que es donde esta de verdad. */
    private void paintPosition(VehicleLocationDto location) {
        position.removeAll();
        Div where = new Div(ResourcesFormats.place(location, names));
        where.setId("vehicle-position-place");
        position.add(where);
        if (location == null) {
            return;
        }
        String details = ResourcesHistory.join(location.executionPackageId() == null ? null : "EP " + names.packageName(location.executionPackageId()),
                location.accessPointLabel() == null ? null : "Acceso: " + location.accessPointLabel(),
                location.latitude() == null ? null : location.latitude().toPlainString() + ", " + location.longitude().toPlainString());
        if (!details.isEmpty()) {
            position.add(new Div(details));
        }
        position.add(new Div(ResourcesFormats.origin(location)));
        if (location.kind() == VehicleLocationKind.ATTACHED && location.carrier() != null) {
            try {
                VehicleDto carrier = clients.vehicles().findById(location.carrier().id());
                Div carried = new Div(carrier.code() + " esta: " + ResourcesFormats.place(carrier.position(), names));
                carried.setId("vehicle-carrier-position");
                position.add(carried);
            } catch (BackofficeApiException failure) {
                position.add(new Div("No se ha podido leer donde esta " + location.carrier().code() + ": " + UiErrors.message(failure)));
            }
        }
    }

    private void paintButtons(VehicleDto loaded) {
        buttons.removeAll();
        buttons.add(new Button("Volver a la flota", VaadinIcon.ARROW_LEFT.create(), click -> UI.getCurrent().navigate(VehiclesView.class)));
        Button history = new Button("Historial", VaadinIcon.CLOCK.create(),
                click -> ResourcesHistory.dialog(loaded.code(), (page, size) -> clients.vehicles().revisions(loaded.id(), page, size),
                        ResourcesHistory::vehicle).open());
        history.setId("vehicle-history");
        buttons.add(history);
        if (canLocate && loaded.isActive()) {
            Button locate = new Button("Registrar ubicacion", VaadinIcon.MAP_MARKER.create(),
                    click -> new VehicleLocationDialog(loaded, clients, names, recorded -> reload()).open());
            locate.setId("vehicle-locate");
            locate.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            buttons.add(locate);
        }
        if (canWrite) {
            Button edit = new Button("Modificar", VaadinIcon.EDIT.create(), click -> new VehicleEditorDialog(loaded, clients, saved -> reload()).open());
            edit.setId("vehicle-edit");
            buttons.add(edit);
        }
        if (canDelete && loaded.isActive()) {
            Button retire = new Button("Retirar", VaadinIcon.BAN.create(),
                    click -> Screens.confirm("Retirar " + loaded.label(), RETIRE_TEXT, "Retirar", this::retire));
            retire.setId("vehicle-retire");
            retire.addThemeVariants(ButtonVariant.LUMO_ERROR);
            buttons.add(retire);
        }
        if (canWrite && !loaded.isActive()) {
            Button reactivate = new Button("Reactivar", VaadinIcon.CHECK_CIRCLE.create(), click -> reactivate());
            reactivate.setId("vehicle-reactivate");
            buttons.add(reactivate);
        }
    }

    private void retire() {
        try {
            clients.vehicles().deactivate(vehicle.id());
            Screens.success("Retirado " + vehicle.label());
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
        reload();
    }

    private void reactivate() {
        try {
            paint(clients.vehicles().update(vehicle.id(), MergePatch.of(VehicleUpdateRequest.active(true), vehicle.version())));
            Screens.success("Reactivado " + vehicle.label());
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }
}
