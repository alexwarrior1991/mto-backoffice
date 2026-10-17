package com.alejandro.mtobackoffice.ui.resources;

import com.alejandro.mtobackoffice.client.dto.resources.VehicleDocumentDto;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleDocumentKind;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.ui.support.LazyPanel;
import com.alejandro.mtobackoffice.ui.support.Screens;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import java.util.List;
import java.util.function.Supplier;

/**
 * Los documentos de un vehiculo que caducan (la ITV, el seguro, los certificados de la grua y de la
 * plataforma), uno de cada tipo, con su estado como el de las cualificaciones ({@link Expiry}). Anadir y
 * modificar piden {@code resources-fleet-write}; quitar (un borrado de verdad), {@code resources-delete}
 * y confirmacion. mto-maintenance deja fuera de sus turnos un vehiculo con uno caducado a la fecha del
 * turno.
 */
class VehicleDocumentsPanel extends LazyPanel {

    static final String GRID_ID = "documents-grid";
    static final String ACTIONS_COLUMN = "actions";
    static final String EXPIRED = "Caducado";

    private final Supplier<VehicleDto> vehicle;
    private final ResourcesClients clients;
    private final boolean canWrite;
    private final boolean canDelete;
    private final Span count = new Span();
    private final Button add = new Button("Anadir documento", VaadinIcon.PLUS.create());
    private final Grid<VehicleDocumentDto> grid = new Grid<>();
    private List<VehicleDocumentDto> current = List.of();

    VehicleDocumentsPanel(Supplier<VehicleDto> vehicle, ResourcesClients clients, boolean canWrite, boolean canDelete) {
        this.vehicle = vehicle;
        this.clients = clients;
        this.canWrite = canWrite;
        this.canDelete = canDelete;
        count.setId("documents-count");
        add.setId("document-add");
        add.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        add.setVisible(canWrite);
        add.addClickListener(click -> new VehicleDocumentDialog(vehicle.get(), null, current, clients, this::reload).open());
        HorizontalLayout toolbar = new HorizontalLayout(count, add);
        toolbar.setAlignItems(FlexComponent.Alignment.BASELINE);
        toolbar.setWidthFull();
        toolbar.expand(count);

        grid.setId(GRID_ID);
        grid.addColumn(VehicleDocumentDto::label).setHeader("Tipo").setKey("kind").setAutoWidth(true);
        grid.addColumn(VehicleDocumentDto::reference).setHeader("Referencia").setKey("reference").setAutoWidth(true);
        grid.addColumn(document -> ResourcesFormats.date(document.issuedOn())).setHeader("Expedido").setKey("issuedOn").setAutoWidth(true);
        grid.addColumn(document -> ResourcesFormats.date(document.expiresOn())).setHeader("Caduca").setKey("expiresOn").setAutoWidth(true);
        grid.addColumn(VehicleDocumentsPanel::state).setHeader("Estado").setKey("state").setAutoWidth(true);
        grid.addColumn(new ComponentRenderer<>(this::rowActions)).setHeader("").setKey(ACTIONS_COLUMN).setAutoWidth(true).setFlexGrow(0);
        grid.setAllRowsVisible(true);
        grid.setEmptyStateText("Sin documentos apuntados.");
        add(toolbar, grid);
    }

    static String state(VehicleDocumentDto document) {
        return Expiry.state(document.expiresOn(), document.expired(), EXPIRED, "");
    }

    /**
     * La columna existe siempre, como en las demas listas, aunque quien solo lee no tenga nada que hacer en
     * ella. Un documento de un tipo que esta aplicacion no conoce se ve, pero no abre nada.
     */
    private Component rowActions(VehicleDocumentDto document) {
        HorizontalLayout actions = new HorizontalLayout();
        actions.setSpacing(false);
        if (document.kind() == VehicleDocumentKind.UNKNOWN) {
            return actions;
        }
        if (canWrite) {
            actions.add(Screens.rowButton("document-edit-" + document.id(), VaadinIcon.EDIT, "Modificar " + document.label(),
                    click -> new VehicleDocumentDialog(vehicle.get(), document, current, clients, this::reload).open()));
        }
        if (canDelete) {
            Button remove = Screens.rowButton("document-remove-" + document.id(), VaadinIcon.TRASH, "Quitar " + document.label(),
                    click -> Screens.confirm("Quitar " + document.label() + " de " + vehicle.get().label(),
                            "Se borra el documento del vehiculo. Renovarlo no es esto: es cambiar su caducidad.", "Quitar",
                            () -> remove(document)));
            remove.addThemeVariants(ButtonVariant.LUMO_ERROR);
            actions.add(remove);
        }
        return actions;
    }

    private void remove(VehicleDocumentDto document) {
        try {
            clients.vehicles().removeDocument(vehicle.get().id(), document.id());
            Screens.success("Quitado " + document.label());
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
        reload();
    }

    @Override
    protected void load() {
        try {
            current = clients.vehicles().documents(vehicle.get().id());
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
            current = List.of();
        }
        count.setText(current.size() == 1 ? "1 documento" : current.size() + " documentos");
        grid.setItems(current);
    }
}
