package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.dto.maintenance.CheckItemDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.InspectionDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.InspectionResult;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.error.NotFoundApiException;
import com.alejandro.mtobackoffice.configuration.security.MaintenanceRoles;
import com.alejandro.mtobackoffice.ui.MainLayout;
import com.alejandro.mtobackoffice.ui.support.Formats;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.RolesAllowed;

import java.util.Comparator;
import java.util.UUID;

/**
 * La ficha de una inspeccion: su cabecera, sus puntos (contestables con {@code maintenance-write})
 * y lo que genero. Si el resultado encontro algo, se ofrecen «Crear defecto» y «Crear orden
 * correctiva»; una vez creados, en su lugar aparecen los enlaces a ellos (el servicio devolveria lo
 * mismo, porque las dos llamadas son idempotentes).
 */
@Route(value = MaintenanceRoutes.INSPECTIONS + "/:" + InspectionDetailView.INSPECTION_ID_PARAMETER, layout = MainLayout.class)
@RolesAllowed(MaintenanceRoles.MAINTENANCE_READ)
public class InspectionDetailView extends VerticalLayout implements BeforeEnterObserver, HasDynamicTitle {

    public static final String INSPECTION_ID_PARAMETER = "inspectionId";

    private final MaintenanceClients clients;
    private final MaintenanceNames names;
    private final MaintenanceCatalogs catalogs;
    private final boolean canWrite;

    private final H2 title = new H2();
    private final Span resultBadge = new Span();
    private final Div summary = new Div();
    private final HorizontalLayout buttons = new HorizontalLayout();
    private final Grid<CheckItemDto> items = new Grid<>();

    private InspectionDto inspection;

    public static RouteParameters parametersOf(UUID inspectionId) {
        return new RouteParameters(INSPECTION_ID_PARAMETER, inspectionId.toString());
    }

    public InspectionDetailView(MaintenanceClients clients, AuthenticationContext authentication) {
        this.clients = clients;
        this.names = MaintenanceNames.of(clients, authentication);
        this.catalogs = new MaintenanceCatalogs(clients.catalog());
        this.canWrite = authentication.hasRole(MaintenanceRoles.MAINTENANCE_WRITE);
        setSizeFull();
        resultBadge.getElement().getThemeList().add("badge");
        resultBadge.setId("inspection-result-badge");
        HorizontalLayout heading = new HorizontalLayout(title, resultBadge);
        heading.setAlignItems(FlexComponent.Alignment.BASELINE);
        summary.setId("inspection-summary");
        buttons.setAlignItems(FlexComponent.Alignment.BASELINE);
        items.setId("inspection-items-grid");
        items.addColumn(CheckItemDto::code).setHeader("Codigo").setAutoWidth(true);
        items.addColumn(CheckItemDto::label).setHeader("Punto").setFlexGrow(1);
        items.addColumn(item -> Formats.quantity(item.measuredValue()) + (item.unit() == null || item.measuredValue() == null ? "" : " " + item.unit()))
                .setHeader("Medida").setAutoWidth(true);
        items.addColumn(item -> Formats.quantity(item.valueAfterAdjustment())).setHeader("Tras ajuste").setAutoWidth(true);
        items.addColumn(item -> item.itemResult() == null ? "" : item.itemResult().label()).setHeader("Resultado").setAutoWidth(true);
        items.addColumn(item -> Boolean.TRUE.equals(item.outOfRange()) ? "Fuera de rango" : "").setHeader("").setAutoWidth(true);
        items.setAllRowsVisible(true);
        add(heading, summary, buttons, new H3("Puntos"), items);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String id = event.getRouteParameters().get(INSPECTION_ID_PARAMETER).orElse("");
        try {
            paint(clients.inspections().findById(UUID.fromString(id)));
        } catch (IllegalArgumentException | NotFoundApiException missing) {
            Notification.show("No existe la inspeccion " + id, 5000, Notification.Position.BOTTOM_START)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            event.forwardTo(InspectionsView.class);
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
            event.forwardTo(InspectionsView.class);
        }
    }

    @Override
    public String getPageTitle() {
        return inspection == null ? "Inspeccion" : "Inspeccion " + inspection.code();
    }

    private void reload() {
        try {
            paint(clients.inspections().findById(inspection.id()));
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }

    private void paint(InspectionDto loaded) {
        inspection = loaded;
        title.setText(loaded.code() + " · " + MaintenanceFormats.date(loaded.inspectionDate()));
        InspectionResult result = loaded.result();
        resultBadge.setText(result == null ? "" : result.label());
        resultBadge.getElement().getThemeList().set("success", result == InspectionResult.OK);
        resultBadge.getElement().getThemeList().set("error", result == InspectionResult.UNSAFE || result == InspectionResult.MAJOR_DEFECT);
        summary.removeAll();
        summary.add(new Div(String.join(" · ", "Activo: " + (loaded.asset() == null ? "" : loaded.asset().label()),
                "Via: " + names.trackName(loaded.trackId()), "KP " + MaintenanceFormats.kp(loaded.kp()),
                "Tipo: " + (loaded.inspectionKind() == null ? "-" : loaded.inspectionKind().label()),
                "Inspector: " + (loaded.inspector() == null ? "-" : loaded.inspector()))));
        for (String[] text : new String[][]{{"Descripcion", loaded.description()}, {"Defectos observados", loaded.detectedDefects()},
                {"Acciones recomendadas", loaded.recommendedActions()}}) {
            if (text[1] != null && !text[1].isBlank()) {
                summary.add(new Div(text[0] + ": " + text[1]));
            }
        }
        items.setItems(loaded.items().stream()
                .sorted(Comparator.comparing((CheckItemDto item) -> item.orderIndex() == null ? Integer.MAX_VALUE : item.orderIndex()))
                .toList());
        paintButtons(loaded);
    }

    private void paintButtons(InspectionDto loaded) {
        buttons.removeAll();
        buttons.add(new Button("Volver a la lista", VaadinIcon.ARROW_LEFT.create(), click -> UI.getCurrent().navigate(InspectionsView.class)));
        buttons.add(MaintenanceHistory.button("inspection-history", loaded.code(),
                (page, size) -> clients.inspections().revisions(loaded.id(), page, size), MaintenanceHistory::inspection));
        if (loaded.originOrderId() != null) {
            buttons.add(link("inspection-origin-order", "Orden de origen", VaadinIcon.WRENCH,
                    () -> UI.getCurrent().navigate(OrderDetailView.class, OrderDetailView.parametersOf(loaded.originOrderId()))));
        }
        if (canWrite) {
            buttons.add(link("inspection-edit", "Modificar", VaadinIcon.EDIT,
                    () -> new InspectionEditorDialog(loaded, null, clients, updated -> paint(updated)).open()));
            if (!loaded.items().isEmpty()) {
                buttons.add(link("inspection-items", "Contestar puntos", VaadinIcon.CHECK_SQUARE_O,
                        () -> new CheckItemsDialog("Puntos de " + loaded.code(), loaded.items(),
                                (itemId, patch) -> clients.inspections().updateItem(loaded.id(), itemId, patch).items(), this::reload).open()));
            }
        }
        boolean foundSomething = loaded.result() != null && loaded.result().foundSomething();
        if (loaded.generatedDefectId() != null) {
            buttons.add(link("inspection-defect-link", "Ver el defecto", VaadinIcon.WARNING,
                    () -> UI.getCurrent().navigate(DefectDetailView.class, DefectDetailView.parametersOf(loaded.generatedDefectId()))));
        } else if (canWrite && foundSomething) {
            buttons.add(link("inspection-create-defect", "Crear defecto", VaadinIcon.WARNING,
                    () -> InspectionOutcomeDialogs.defect(loaded, clients, defect -> reload()).open()));
        }
        if (loaded.generatedOrderId() != null) {
            buttons.add(link("inspection-order-link", "Ver la orden correctiva", VaadinIcon.WRENCH,
                    () -> UI.getCurrent().navigate(OrderDetailView.class, OrderDetailView.parametersOf(loaded.generatedOrderId()))));
        } else if (canWrite && foundSomething) {
            buttons.add(link("inspection-create-order", "Crear orden correctiva", VaadinIcon.WRENCH,
                    () -> InspectionOutcomeDialogs.correctiveOrder(loaded, clients, catalogs,
                            order -> UI.getCurrent().navigate(OrderDetailView.class, OrderDetailView.parametersOf(order.id()))).open()));
        }
    }

    private static Button link(String id, String text, VaadinIcon icon, Runnable action) {
        Button button = new Button(text, icon.create(), click -> action.run());
        button.setId(id);
        return button;
    }
}
