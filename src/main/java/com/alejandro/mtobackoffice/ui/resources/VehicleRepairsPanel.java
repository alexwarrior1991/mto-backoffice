package com.alejandro.mtobackoffice.ui.resources;

import com.alejandro.mtobackoffice.client.dto.resources.RepairStatus;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleDto;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleRepairDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.ui.support.Formats;
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
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * El taller de un vehiculo: sus reparaciones (averias, revisiones, inspecciones, modificaciones), con su
 * estado y si inmovilizan el vehiculo. Una que inmoviliza manda en el estado del vehiculo: abrirla lo pone
 * «En reparacion» y cerrar o cancelar la ultima abierta lo devuelve a «Operativo»; planificada o abierta,
 * el vehiculo no entra en los turnos de esas fechas. Las transiciones se ofrecen solo en su estado de
 * origen ({@link RepairStatus}), se confirman diciendo su efecto y las decide el servicio (409
 * {@code REP-001}); todo pide {@code resources-fleet-write}. Tras cualquier escritura se relee la ficha
 * entera, porque el estado del vehiculo puede haber cambiado. No se borra nada: se cancela.
 */
class VehicleRepairsPanel extends LazyPanel {

    static final String GRID_ID = "repairs-grid";
    static final String ACTIONS_COLUMN = "actions";

    private final Supplier<VehicleDto> vehicle;
    private final ResourcesClients clients;
    private final boolean canWrite;
    private final Runnable changed;
    private final Span count = new Span();
    private final Button add = new Button("Nueva reparacion", VaadinIcon.PLUS.create());
    private final Grid<VehicleRepairDto> grid = new Grid<>();

    /** @param changed releer la ficha del vehiculo y sus pestanas abiertas, esta tambien */
    VehicleRepairsPanel(Supplier<VehicleDto> vehicle, ResourcesClients clients, boolean canWrite, Runnable changed) {
        this.vehicle = vehicle;
        this.clients = clients;
        this.canWrite = canWrite;
        this.changed = changed;
        count.setId("repairs-count");
        add.setId("repair-add");
        add.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        add.setVisible(canWrite);
        add.addClickListener(click -> new VehicleRepairDialog(vehicle.get(), null, clients, changed).open());
        HorizontalLayout toolbar = new HorizontalLayout(count, add);
        toolbar.setAlignItems(FlexComponent.Alignment.BASELINE);
        toolbar.setWidthFull();
        toolbar.expand(count);

        grid.setId(GRID_ID);
        grid.addColumn(VehicleRepairDto::code).setHeader("Codigo").setKey("code").setAutoWidth(true);
        grid.addColumn(repair -> repair.kind() == null ? "" : repair.kind().label()).setHeader("Clase").setKey("kind").setAutoWidth(true);
        grid.addColumn(repair -> repair.state().label()).setHeader("Estado").setKey("status").setAutoWidth(true);
        grid.addColumn(repair -> Screens.yesNo(repair.immobilises())).setHeader("Inmoviliza").setKey("immobilises").setAutoWidth(true);
        grid.addColumn(VehicleRepairsPanel::planned).setHeader("Previsto").setKey("planned").setAutoWidth(true);
        grid.addColumn(repair -> Formats.dateTime(repair.openedAt())).setHeader("Abierta").setKey("openedAt").setAutoWidth(true);
        grid.addColumn(repair -> Formats.dateTime(repair.closedAt())).setHeader("Cerrada").setKey("closedAt").setAutoWidth(true);
        grid.addColumn(VehicleRepairDto::workshop).setHeader("Taller").setKey("workshop").setAutoWidth(true);
        grid.addColumn(repair -> repair.cost() == null ? "" : repair.cost().toPlainString()).setHeader("Coste").setKey("cost").setAutoWidth(true);
        grid.addColumn(new ComponentRenderer<>(this::rowActions)).setHeader("").setKey(ACTIONS_COLUMN).setAutoWidth(true).setFlexGrow(0);
        grid.setAllRowsVisible(true);
        grid.setEmptyStateText("Sin reparaciones apuntadas.");
        add(toolbar, grid);
    }

    /** Las fechas previstas, «desde – hasta»; sin ninguna, nada. */
    static String planned(VehicleRepairDto repair) {
        if (repair.plannedFrom() == null && repair.plannedTo() == null) {
            return "";
        }
        return ResourcesFormats.date(repair.plannedFrom()) + " – " + ResourcesFormats.date(repair.plannedTo());
    }

    /**
     * La columna existe siempre, aunque quien solo lee no tenga nada que hacer en ella. Planificada: abrir,
     * modificar y cancelar; abierta: cerrar, modificar y cancelar; cerrada o cancelada, solo modificar (su
     * coste y sus notas); en un estado desconocido, nada.
     */
    private Component rowActions(VehicleRepairDto repair) {
        HorizontalLayout actions = new HorizontalLayout();
        actions.setSpacing(false);
        RepairStatus status = repair.state();
        if (!canWrite || !status.isChangeable()) {
            return actions;
        }
        if (status.canOpen()) {
            actions.add(Screens.rowButton("repair-open-" + repair.id(), VaadinIcon.PLAY, "Abrir " + repair.code(),
                    click -> Screens.confirm("Abrir " + repair.code(), openEffect(repair), "Abrir",
                            () -> transition(repair, clients.vehicles()::openRepair, "Abierta "))));
        }
        if (status.canClose()) {
            actions.add(Screens.rowButton("repair-close-" + repair.id(), VaadinIcon.CHECK, "Cerrar " + repair.code(),
                    click -> Screens.confirm("Cerrar " + repair.code(), closeEffect(repair), "Cerrar",
                            () -> transition(repair, clients.vehicles()::closeRepair, "Cerrada "))));
        }
        actions.add(Screens.rowButton("repair-edit-" + repair.id(), VaadinIcon.EDIT, "Modificar " + repair.code(),
                click -> new VehicleRepairDialog(vehicle.get(), repair, clients, changed).open()));
        if (status.canCancel()) {
            Button cancel = Screens.rowButton("repair-cancel-" + repair.id(), VaadinIcon.BAN, "Cancelar " + repair.code(),
                    click -> Screens.confirm("Cancelar " + repair.code(), cancelEffect(repair), "Cancelar la reparacion",
                            () -> transition(repair, clients.vehicles()::cancelRepair, "Cancelada ")));
            cancel.addThemeVariants(ButtonVariant.LUMO_ERROR);
            actions.add(cancel);
        }
        return actions;
    }

    static String openEffect(VehicleRepairDto repair) {
        return repair.isImmobilising()
                ? "Queda abierta desde ahora y el vehiculo pasa a En reparacion: no entra en los turnos hasta que se cierre o se cancele."
                : "Queda abierta desde ahora. No inmoviliza el vehiculo: su estado no cambia.";
    }

    static String closeEffect(VehicleRepairDto repair) {
        return repair.isImmobilising()
                ? "Queda cerrada desde ahora. Si es la ultima abierta que inmoviliza, el vehiculo vuelve a Operativo."
                : "Queda cerrada desde ahora. No inmovilizaba el vehiculo: su estado no cambia.";
    }

    static String cancelEffect(VehicleRepairDto repair) {
        if (!repair.isImmobilising()) {
            return "No se borra: queda cancelada. No inmovilizaba el vehiculo: su estado no cambia.";
        }
        return repair.state() == RepairStatus.OPEN
                ? "No se borra: queda cancelada. Si es la ultima abierta que inmoviliza, el vehiculo vuelve a Operativo."
                : "No se borra: queda cancelada, y el vehiculo vuelve a entrar en los turnos de esas fechas.";
    }

    private void transition(VehicleRepairDto repair, BiFunction<UUID, UUID, VehicleRepairDto> call, String done) {
        try {
            VehicleRepairDto result = call.apply(vehicle.get().id(), repair.id());
            Screens.success(done + result.code());
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
        changed.run();
    }

    @Override
    protected void load() {
        List<VehicleRepairDto> current;
        try {
            current = clients.vehicles().repairs(vehicle.get().id());
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
            current = List.of();
        }
        count.setText(current.size() == 1 ? "1 reparacion" : current.size() + " reparaciones");
        grid.setItems(current);
    }
}
