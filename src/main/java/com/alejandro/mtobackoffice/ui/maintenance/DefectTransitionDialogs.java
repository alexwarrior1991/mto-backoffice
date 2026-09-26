package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.dto.maintenance.DefectDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.OrderDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.OrderFilter;
import com.alejandro.mtobackoffice.client.dto.maintenance.ResolveDefectRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.ShiftDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.ShiftFilter;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;

import java.util.List;
import java.util.function.Consumer;

/**
 * Las transiciones de un defecto con datos: vincularlo a una orden abierta de su via, y resolverlo
 * (con {@code maintenance-supervise}). Con una orden vinculada sin completar, resolverlo pide el
 * turno en el que se corrigio in situ; si no se da, el servicio lo rechaza (409 {@code TRN-001}).
 */
final class DefectTransitionDialogs {

    static final String LINK_CONFIRM_ID = "defect-link-confirm";
    static final String RESOLVE_CONFIRM_ID = "defect-resolve-confirm";

    private DefectTransitionDialogs() {
    }

    static Dialog linkOrder(DefectDto defect, MaintenanceClients clients, Consumer<DefectDto> linked) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Vincular " + defect.code() + " a una orden");
        dialog.setWidth("min(90vw, 640px)");
        ComboBox<OrderDto> order = new ComboBox<>("Orden");
        order.setId("defect-link-order");
        order.setWidthFull();
        order.setItemLabelGenerator(candidate -> candidate.code() + " · " + candidate.title() + " ("
                + (candidate.status() == null ? "" : candidate.status().label().toLowerCase()) + ")");
        order.setItems(openOrdersOn(clients, defect.trackId()));
        order.setHelperText("Las ordenes abiertas de la via del defecto");
        dialog.add(order);
        Button confirm = new Button("Vincular", click -> {
            if (order.getValue() == null) {
                order.setErrorMessage("Elige una orden");
                order.setInvalid(true);
                return;
            }
            try {
                DefectDto result = clients.defects().linkOrder(defect.id(), order.getValue().id());
                dialog.close();
                MaintenanceUi.success(result.code() + " vinculado a " + order.getValue().code());
                linked.accept(result);
            } catch (BackofficeApiException failure) {
                UiErrors.show(failure);
            }
        });
        confirm.setId(LINK_CONFIRM_ID);
        confirm.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        dialog.getFooter().add(new Button("Volver", click -> dialog.close()), confirm);
        return dialog;
    }

    static Dialog resolve(DefectDto defect, MaintenanceClients clients, Consumer<DefectDto> resolved) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Resolver " + defect.code());
        dialog.setWidth("min(90vw, 640px)");
        TextArea notes = new TextArea("Como se resolvio");
        notes.setId("defect-resolve-notes");
        ComboBox<ShiftDto> shift = new ComboBox<>("Turno en el que se corrigio");
        shift.setId("defect-resolve-shift");
        shift.setItemLabelGenerator(candidate -> candidate.code() + " · " + MaintenanceFormats.date(candidate.shiftDate()));
        shift.setItems(recentShiftsOn(clients, defect.trackId()));
        shift.setClearButtonVisible(true);
        shift.setHelperText("Obligatorio si su orden no esta completada");
        TextField correctionType = new TextField("Tipo de correccion");
        correctionType.setId("defect-resolve-correction");
        correctionType.setMaxLength(120);
        correctionType.setValue(defect.correctionType() == null ? "" : defect.correctionType());
        TextField partsReplaced = new TextField("Piezas cambiadas");
        partsReplaced.setId("defect-resolve-parts");
        partsReplaced.setValue(defect.partsReplaced() == null ? "" : defect.partsReplaced());
        FormLayout layout = new FormLayout(notes, shift, correctionType, partsReplaced);
        layout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        dialog.add(layout);
        Button confirm = new Button("Resolver", click -> {
            if (notes.getValue().isBlank()) {
                notes.setErrorMessage("Hay que decir como se resolvio");
                notes.setInvalid(true);
                return;
            }
            try {
                DefectDto result = clients.defects().resolve(defect.id(), new ResolveDefectRequest(notes.getValue().trim(),
                        shift.getValue() == null ? null : shift.getValue().id(), TransitionForm.nullIfBlank(correctionType.getValue()),
                        TransitionForm.nullIfBlank(partsReplaced.getValue())));
                dialog.close();
                MaintenanceUi.success(result.code() + " resuelto");
                resolved.accept(result);
            } catch (BackofficeApiException failure) {
                UiErrors.show(failure);
            }
        });
        confirm.setId(RESOLVE_CONFIRM_ID);
        confirm.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        dialog.getFooter().add(new Button("Volver", click -> dialog.close()), confirm);
        return dialog;
    }

    private static List<OrderDto> openOrdersOn(MaintenanceClients clients, Long trackId) {
        if (trackId == null) {
            return List.of();
        }
        try {
            return clients.orders().search(OrderFilter.onTrack(trackId), 0, 100, List.of("plannedDate,asc")).content().stream()
                    .filter(candidate -> candidate.status() != null && candidate.status().isOpen())
                    .toList();
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
            return List.of();
        }
    }

    /** Los turnos recientes de la via, el mas reciente primero. */
    private static List<ShiftDto> recentShiftsOn(MaintenanceClients clients, Long trackId) {
        if (trackId == null) {
            return List.of();
        }
        try {
            return clients.shifts().search(new ShiftFilter(null, null, null, trackId, null, null, null), 0, 50, List.of("shiftDate,desc"))
                    .content();
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
            return List.of();
        }
    }
}
