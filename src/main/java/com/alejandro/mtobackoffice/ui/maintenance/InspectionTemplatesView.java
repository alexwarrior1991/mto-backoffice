package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.dto.maintenance.InspectionTemplateDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.InspectionTemplateItemDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.configuration.security.MaintenanceRoles;
import com.alejandro.mtobackoffice.ui.MainLayout;
import com.alejandro.mtobackoffice.ui.support.Formats;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.Comparator;
import java.util.List;

/**
 * Las plantillas de inspeccion por tipo de activo y version, de solo lectura, con los puntos de la
 * que se elija. Una inspeccion copia los de la plantilla activa al crearse.
 */
@Route(value = MaintenanceRoutes.TEMPLATES, layout = MainLayout.class)
@PageTitle("Plantillas de inspeccion")
@Menu(title = "Plantillas de inspeccion", order = 68, icon = "vaadin:form")
@RolesAllowed(MaintenanceRoles.MAINTENANCE_READ)
public class InspectionTemplatesView extends VerticalLayout {

    private final Grid<InspectionTemplateDto> templates = new Grid<>();
    private final Grid<InspectionTemplateItemDto> items = new Grid<>();
    private final H3 itemsTitle = new H3("Puntos");

    public InspectionTemplatesView(MaintenanceClients clients) {
        setSizeFull();
        templates.setId("templates-grid");
        templates.addColumn(template -> template.assetType() == null ? "" : template.assetType().label()).setHeader("Tipo de activo")
                .setKey("assetType").setAutoWidth(true);
        templates.addColumn(InspectionTemplateDto::version).setHeader("Version").setKey("version").setAutoWidth(true);
        templates.addColumn(InspectionTemplateDto::name).setHeader("Nombre").setKey("name").setFlexGrow(1);
        templates.addColumn(template -> Boolean.TRUE.equals(template.active()) ? "Activa" : "Anterior").setHeader("Estado")
                .setKey("active").setAutoWidth(true);
        templates.addColumn(template -> template.items().size()).setHeader("Puntos").setKey("items").setAutoWidth(true);
        templates.setHeight("40%");
        templates.addSelectionListener(selection -> show(selection.getFirstSelectedItem().orElse(null)));

        items.setId("template-items-grid");
        items.addColumn(InspectionTemplateItemDto::code).setHeader("Codigo").setKey("code").setAutoWidth(true);
        items.addColumn(InspectionTemplateItemDto::label).setHeader("Punto").setKey("label").setFlexGrow(1);
        items.addColumn(item -> Boolean.TRUE.equals(item.requiresMeasure()) ? "Si" : "No").setHeader("Medida").setKey("measure")
                .setAutoWidth(true);
        items.addColumn(InspectionTemplateItemDto::unit).setHeader("Unidad").setKey("unit").setAutoWidth(true);
        items.addColumn(item -> Formats.quantity(item.minValue())).setHeader("Minimo").setKey("min").setAutoWidth(true);
        items.addColumn(item -> Formats.quantity(item.maxValue())).setHeader("Maximo").setKey("max").setAutoWidth(true);

        add(new H2("Plantillas de inspeccion"), templates, itemsTitle, items);
        expand(items);
        try {
            List<InspectionTemplateDto> all = clients.catalog().inspectionTemplates().stream()
                    .sorted(Comparator.comparing((InspectionTemplateDto template) -> template.assetType() == null ? "" : template.assetType().name())
                            .thenComparing(template -> template.version() == null ? 0 : -template.version()))
                    .toList();
            templates.setItems(all);
            all.stream().filter(template -> Boolean.TRUE.equals(template.active())).findFirst().ifPresent(templates::select);
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }

    private void show(InspectionTemplateDto template) {
        if (template == null) {
            itemsTitle.setText("Puntos");
            items.setItems(List.of());
            return;
        }
        itemsTitle.setText("Puntos de " + template.name() + " (version " + template.version() + ")");
        items.setItems(template.items().stream()
                .sorted(Comparator.comparing((InspectionTemplateItemDto item) -> item.orderIndex() == null ? Integer.MAX_VALUE : item.orderIndex()))
                .toList());
    }
}
