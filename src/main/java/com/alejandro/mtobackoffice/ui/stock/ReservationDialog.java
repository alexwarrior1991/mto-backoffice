package com.alejandro.mtobackoffice.ui.stock;

import com.alejandro.mtobackoffice.client.dto.stock.MaterialSummaryDto;
import com.alejandro.mtobackoffice.client.dto.stock.ProjectSummaryDto;
import com.alejandro.mtobackoffice.client.dto.stock.ReservationDto;
import com.alejandro.mtobackoffice.client.dto.stock.WarehouseSummaryDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.error.ValidationApiException;
import com.alejandro.mtobackoffice.ui.support.ServerValidation;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.data.binder.Binder;

import java.util.List;

/**
 * Alta o modificacion de una reserva: material (fijo al modificar: el servicio no lo cambia),
 * almacen, proyecto y cantidad. Nace activa y reduce el disponible; si no lo hay, el servicio
 * responde 409 {@code STK-001}. Solo una reserva activa se modifica (422 {@code RES-001}).
 */
public class ReservationDialog extends Dialog {

    public static final String SAVE_ID = "reservation-save";

    private final Binder<ReservationForm> binder = new Binder<>(ReservationForm.class);
    private final ReservationForm form;

    public ReservationDialog(ReservationDto existing, StockClients clients, Runnable saved) {
        boolean creating = existing == null;
        this.form = ReservationForm.of(existing);
        setHeaderTitle(creating ? "Nueva reserva" : "Modificar reserva de " + existing.material().code());
        setCloseOnOutsideClick(false);
        setWidth("min(44rem, 96vw)");

        ComboBox<MaterialSummaryDto> material = StockPickers.material("Material", clients.materials());
        material.setId("reservation-material");
        ComboBox<WarehouseSummaryDto> warehouse = StockPickers.warehouse("Almacen", clients.warehouses());
        warehouse.setId("reservation-warehouse");
        ComboBox<ProjectSummaryDto> project = StockPickers.project("Proyecto", clients.projects());
        project.setId("reservation-project");
        BigDecimalField quantity = new BigDecimalField("Cantidad");
        quantity.setId("reservation-quantity");
        quantity.setRequiredIndicatorVisible(true);
        DateTimePicker reservedAt = new DateTimePicker("Reservada el");
        reservedAt.setHelperText("Vacio: ahora");

        binder.forField(material).asRequired("Elige el material").bind("materialId");
        binder.forField(warehouse).asRequired("Elige el almacen").bind("warehouseId");
        binder.forField(project).asRequired("Elige el proyecto").bind("projectId");
        binder.forField(quantity).asRequired("La cantidad es obligatoria")
                .withValidator(value -> value.signum() > 0, "Tiene que ser mayor que cero")
                .bind("quantity");
        if (creating) {
            binder.forField(reservedAt).bind("reservedAt");
        } else {
            material.setReadOnly(true);
        }
        binder.readBean(form);

        FormLayout layout = new FormLayout(material, warehouse, project, quantity);
        if (creating) {
            layout.add(reservedAt);
        }
        layout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1), new FormLayout.ResponsiveStep("36em", 2));
        add(layout);

        Button save = new Button("Guardar", click -> save(existing, clients, saved));
        save.setId(SAVE_ID);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(new Button("Cancelar", click -> close()), save);
    }

    private void save(ReservationDto existing, StockClients clients, Runnable saved) {
        if (!binder.writeBeanIfValid(form)) {
            return;
        }
        try {
            ReservationDto reservation = existing == null
                    ? clients.reservations().create(form.toCreateRequest())
                    : clients.reservations().update(existing.id(), form.toUpdateRequest());
            close();
            Notification.show((existing == null ? "Reserva registrada: " : "Reserva modificada: ") + StockFormats.quantity(reservation.quantity())
                            + " " + unit(reservation.material()) + " de " + reservation.material().code() + " para " + reservation.project().code(),
                    4000, Notification.Position.BOTTOM_START).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            saved.run();
        } catch (ValidationApiException validation) {
            List<String> unattributed = ServerValidation.apply(binder, validation);
            if (!unattributed.isEmpty()) {
                Notification.show(String.join(". ", unattributed), 8000, Notification.Position.BOTTOM_START)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }

    private static String unit(MaterialSummaryDto material) {
        return material == null || material.unitOfMeasure() == null ? "" : material.unitOfMeasure();
    }
}
