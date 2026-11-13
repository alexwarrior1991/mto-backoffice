package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.dto.AuditDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.CatenaryAssetType;
import com.alejandro.mtobackoffice.client.dto.maintenance.InspectionTemplateDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.InspectionTemplateItemDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.configuration.security.MaintenanceRoles;
import com.alejandro.mtobackoffice.ui.MainLayout;
import com.alejandro.mtobackoffice.ui.support.Formats;
import com.alejandro.mtobackoffice.ui.support.Screens;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.RolesAllowed;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Las plantillas de inspeccion, versionadas por tipo de activo. Una inspeccion copia los puntos de la
 * version activa de su tipo al crearse; un tipo sin activa da inspecciones sin checklist. Arriba, los
 * cuatro tipos de activo con su version activa (tambien el que no tiene ninguna); debajo, todas las
 * versiones (la activa y las anteriores, con quien creo cada una y cuando) y los puntos de la elegida.
 *
 * <p>Quien solo lee ve eso. Con {@code maintenance-supervise} y {@code maintenance-write} juntos, y si
 * el servicio deja escribirlas (cada version trae su {@code lockVersion}), se mantienen desde aqui: la
 * version siguiente de un tipo ({@link InspectionTemplateVersionDialog}), y de una que existe, su
 * nombre ({@link InspectionTemplateRenameDialog}) y si es la activa. Los puntos de una version no se
 * cambian y nada se borra: no se ofrece. Tras cada escritura se relee la lista entera, porque cambia
 * otra fila del mismo tipo.</p>
 */
@Route(value = MaintenanceRoutes.TEMPLATES, layout = MainLayout.class)
@PageTitle("Plantillas de inspeccion")
@Menu(title = "Plantillas de inspeccion", order = 68, icon = "vaadin:form")
@RolesAllowed(MaintenanceRoles.MAINTENANCE_READ)
public class InspectionTemplatesView extends VerticalLayout {

    public static final String ACTIONS_COLUMN = "actions";
    static final String NO_ACTIVE = "Sin plantilla activa";

    private final MaintenanceClients clients;
    private final boolean supervises;
    private final Grid<CatenaryAssetType> types = new Grid<>();
    private final Grid<InspectionTemplateDto> templates = new Grid<>();
    private final Grid<InspectionTemplateItemDto> items = new Grid<>();
    private final H3 itemsTitle = new H3("Puntos");
    private List<InspectionTemplateDto> all = List.of();
    /** Lo leido deja escribir: ninguna version llego sin su bloqueo (un servicio anterior no los manda). */
    private boolean writable;

    public InspectionTemplatesView(MaintenanceClients clients, AuthenticationContext authentication) {
        this.clients = clients;
        this.supervises = authentication.hasAllRoles(MaintenanceRoles.MAINTENANCE_SUPERVISE, MaintenanceRoles.MAINTENANCE_WRITE);
        setSizeFull();

        types.setId("template-types-grid");
        types.addColumn(CatenaryAssetType::label).setHeader("Tipo de activo").setKey("assetType").setAutoWidth(true);
        types.addColumn(this::activeText).setHeader("Plantilla activa").setKey("active").setFlexGrow(1);
        types.addColumn(type -> versionsOf(type).size()).setHeader("Versiones").setKey("versions").setAutoWidth(true);
        types.addColumn(new ComponentRenderer<>(this::typeActions)).setHeader("").setKey(ACTIONS_COLUMN).setAutoWidth(true).setFlexGrow(0);
        types.setAllRowsVisible(true);
        types.setSelectionMode(Grid.SelectionMode.NONE);

        templates.setId("templates-grid");
        templates.addColumn(template -> template.assetType() == null ? "" : template.assetType().label()).setHeader("Tipo de activo")
                .setKey("assetType").setAutoWidth(true);
        templates.addColumn(InspectionTemplateDto::version).setHeader("Version").setKey("version").setAutoWidth(true);
        templates.addColumn(InspectionTemplateDto::name).setHeader("Nombre").setKey("name").setFlexGrow(1);
        templates.addColumn(template -> template.activeNow() ? "Activa" : "Anterior").setHeader("Estado")
                .setKey("active").setAutoWidth(true);
        templates.addColumn(template -> template.items().size()).setHeader("Puntos").setKey("items").setAutoWidth(true);
        templates.addColumn(template -> created(template.audit())).setHeader("Creada").setKey("created").setAutoWidth(true);
        templates.addColumn(new ComponentRenderer<>(this::versionActions)).setHeader("").setKey(ACTIONS_COLUMN).setAutoWidth(true).setFlexGrow(0);
        templates.setHeight("35%");
        templates.addSelectionListener(selection -> show(selection.getFirstSelectedItem().orElse(null)));

        items.setId("template-items-grid");
        items.addColumn(InspectionTemplateItemDto::code).setHeader("Codigo").setKey("code").setAutoWidth(true);
        items.addColumn(InspectionTemplateItemDto::label).setHeader("Punto").setKey("label").setFlexGrow(1);
        items.addColumn(item -> Boolean.TRUE.equals(item.requiresMeasure()) ? "Si" : "No").setHeader("Medida").setKey("measure")
                .setAutoWidth(true);
        items.addColumn(InspectionTemplateItemDto::unit).setHeader("Unidad").setKey("unit").setAutoWidth(true);
        items.addColumn(item -> Formats.quantity(item.minValue())).setHeader("Minimo").setKey("min").setAutoWidth(true);
        items.addColumn(item -> Formats.quantity(item.maxValue())).setHeader("Maximo").setKey("max").setAutoWidth(true);

        add(new H2("Plantillas de inspeccion"), types, new H3("Versiones"), templates, itemsTitle, items);
        expand(items);
        refresh(null);
    }

    /** Quien creo la version y cuando; una de un servicio anterior, sin auditoria, nada. */
    static String created(AuditDto audit) {
        if (audit == null) {
            return "";
        }
        String when = Formats.dateTime(audit.createdAt());
        String who = audit.createdBy() == null ? "" : audit.createdBy();
        return who.isEmpty() || when.isEmpty() ? who + when : who + " · " + when;
    }

    private List<InspectionTemplateDto> versionsOf(CatenaryAssetType type) {
        return all.stream().filter(template -> template.assetType() == type).toList();
    }

    private Optional<InspectionTemplateDto> activeOf(CatenaryAssetType type) {
        return versionsOf(type).stream().filter(InspectionTemplateDto::activeNow).findFirst();
    }

    private String activeText(CatenaryAssetType type) {
        return activeOf(type).map(active -> "Version " + active.version() + " · " + active.name()).orElse(NO_ACTIVE);
    }

    private boolean canWrite() {
        return supervises && writable;
    }

    /** «Nueva version» de un tipo: parte de su activa; sin activa, de la ultima; sin ninguna, vacia. */
    private Component typeActions(CatenaryAssetType type) {
        HorizontalLayout actions = new HorizontalLayout();
        actions.setSpacing(false);
        if (canWrite()) {
            actions.add(Screens.rowButton("template-new-" + type.name(), VaadinIcon.PLUS, "Nueva version de " + type.label(), click -> {
                InspectionTemplateDto base = activeOf(type).orElseGet(() -> versionsOf(type).stream().findFirst().orElse(null));
                newVersion(type, base);
            }));
        }
        return actions;
    }

    private Component versionActions(InspectionTemplateDto template) {
        HorizontalLayout actions = new HorizontalLayout();
        actions.setSpacing(false);
        if (!canWrite() || !template.writable()) {
            return actions;
        }
        String what = "la version " + template.version() + " de " + template.assetType().label();
        actions.add(Screens.rowButton("template-copy-" + template.id(), VaadinIcon.COPY, "Nueva version a partir de " + what,
                click -> newVersion(template.assetType(), template)));
        actions.add(Screens.rowButton("template-rename-" + template.id(), VaadinIcon.EDIT, "Renombrar " + what,
                click -> new InspectionTemplateRenameDialog(template, clients, saved -> refresh(saved.id())).open()));
        if (template.activeNow()) {
            actions.add(Screens.rowButton("template-deactivate-" + template.id(), VaadinIcon.BAN, "Desactivar " + what,
                    click -> Screens.confirm("Desactivar " + what, deactivateText(template), "Desactivar", () -> setActive(template, false))));
        } else {
            actions.add(Screens.rowButton("template-activate-" + template.id(), VaadinIcon.CHECK, "Activar " + what,
                    click -> Screens.confirm("Activar " + what, activateText(template, activeOf(template.assetType()).orElse(null)), "Activar",
                            () -> setActive(template, true))));
        }
        return actions;
    }

    static String activateText(InspectionTemplateDto template, InspectionTemplateDto active) {
        String type = template.assetType().label();
        return (active == null ? "" : "La version " + active.version() + ", la activa ahora, pasa a anterior. ")
                + "Las inspecciones nuevas de " + type + " copiaran los puntos de la version " + template.version()
                + "; las ya creadas no cambian.";
    }

    static String deactivateText(InspectionTemplateDto template) {
        return template.assetType().label() + " se queda sin plantilla activa: las inspecciones nuevas de ese tipo no tendran checklist "
                + "hasta que se active una version. Las ya creadas no cambian.";
    }

    private void newVersion(CatenaryAssetType type, InspectionTemplateDto base) {
        new InspectionTemplateVersionDialog(type, base, activeOf(type).orElse(null), clients, created -> refresh(created.id())).open();
    }

    /** Activar o desactivar: la lista se relee salga bien o no, porque un rechazo es que lo leido ya no vale. */
    private void setActive(InspectionTemplateDto template, boolean active) {
        try {
            InspectionTemplateDto result = clients.catalog().updateInspectionTemplate(template.id(),
                    InspectionTemplateRenameDialog.activePatch(template, active));
            Screens.success((active ? "Activada" : "Desactivada") + " la version " + result.version() + " de " + template.assetType().label());
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
        refresh(template.id());
    }

    /** Relee la lista entera y deja elegida {@code selected} si sigue ahi; si no, la que lo estaba, o la primera activa. */
    private void refresh(UUID selected) {
        UUID wanted = selected != null ? selected
                : templates.getSelectedItems().stream().findFirst().map(InspectionTemplateDto::id).orElse(null);
        try {
            all = clients.catalog().inspectionTemplates().stream()
                    .sorted(Comparator.comparing((InspectionTemplateDto template) -> template.assetType() == null ? "" : template.assetType().name())
                            .thenComparing(template -> template.version() == null ? 0 : -template.version()))
                    .toList();
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
            return;
        }
        writable = all.stream().allMatch(template -> template.lockVersion() != null);
        types.setItems(CatenaryAssetType.selectable());
        templates.setItems(all);
        all.stream().filter(template -> template.id() != null && template.id().equals(wanted)).findFirst()
                .or(() -> all.stream().filter(InspectionTemplateDto::activeNow).findFirst())
                .ifPresentOrElse(templates::select, () -> show(null));
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
