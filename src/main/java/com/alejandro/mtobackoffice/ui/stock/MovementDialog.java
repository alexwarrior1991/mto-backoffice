package com.alejandro.mtobackoffice.ui.stock;

import com.alejandro.mtobackoffice.client.dto.stock.AdjustmentDirection;
import com.alejandro.mtobackoffice.client.dto.stock.MaterialSummaryDto;
import com.alejandro.mtobackoffice.client.dto.stock.MovementDto;
import com.alejandro.mtobackoffice.client.dto.stock.ProjectSummaryDto;
import com.alejandro.mtobackoffice.client.dto.stock.SupplierSummaryDto;
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
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import java.util.List;
import java.util.function.Consumer;

/**
 * Un apunte nuevo en el libro de movimientos: entrada (con proveedor opcional), salida (con
 * proyecto opcional, o consumiendo una reserva), transferencia entre dos almacenes distintos o
 * ajuste (positivo o negativo; pide {@code stock-adjust} ademas de {@code stock-write}). Aqui solo
 * se exige lo evidente; que haya stock lo dice el servicio (409 {@code STK-001}) y que el
 * material o el almacen esten activos, tambien (400 {@code VAL-001}, 422 {@code WH-001}).
 */
public class MovementDialog extends Dialog {

    public static final String SAVE_ID = "movement-save";
    static final int REFERENCE_LENGTH = 128;

    public enum Kind {
        ENTRY("Entrada", "Entrada registrada"),
        OUTPUT("Salida", "Salida registrada"),
        TRANSFER("Transferencia", "Transferencia registrada"),
        ADJUSTMENT("Ajuste de inventario", "Ajuste registrado");

        private final String title;
        private final String done;

        Kind(String title, String done) {
            this.title = title;
            this.done = done;
        }

        public String title() {
            return title;
        }
    }

    private final Kind kind;
    private final StockClients clients;
    private final Binder<MovementForm> binder = new Binder<>(MovementForm.class);
    private final MovementForm form;

    /**
     * @param initial lo que ya se sabe (material y almacen elegidos en la pantalla, o la reserva a
     *                consumir); {@code null} para empezar en blanco
     * @param done    que hacer con lo registrado (la salida y el ajuste devuelven un apunte; la
     *                transferencia, dos: se pasa el primero)
     */
    public MovementDialog(Kind kind, MovementForm initial, StockClients clients, Consumer<MovementDto> done) {
        this.kind = kind;
        this.clients = clients;
        this.form = initial == null ? new MovementForm() : initial;
        setHeaderTitle(kind.title());
        setCloseOnOutsideClick(false);
        setWidth("min(44rem, 96vw)");
        boolean consumingReservation = form.getReservationId() != null;

        ComboBox<MaterialSummaryDto> material = StockPickers.material("Material", clients.materials());
        material.setId("movement-material");
        ComboBox<WarehouseSummaryDto> warehouse = StockPickers.warehouse(kind == Kind.TRANSFER ? "Almacen de origen" : "Almacen", clients.warehouses());
        warehouse.setId("movement-warehouse");
        ComboBox<WarehouseSummaryDto> target = StockPickers.warehouse("Almacen de destino", clients.warehouses());
        target.setId("movement-target");
        ComboBox<SupplierSummaryDto> supplier = StockPickers.supplier("Proveedor", clients.suppliers());
        supplier.setId("movement-supplier");
        ComboBox<ProjectSummaryDto> project = StockPickers.project("Proyecto", clients.projects());
        project.setId("movement-project");
        Select<AdjustmentDirection> direction = new Select<>();
        direction.setId("movement-direction");
        direction.setLabel("Sentido");
        direction.setItems(AdjustmentDirection.values());
        direction.setItemLabelGenerator(AdjustmentDirection::label);
        BigDecimalField quantity = new BigDecimalField("Cantidad");
        quantity.setId("movement-quantity");
        quantity.setRequiredIndicatorVisible(true);
        DateTimePicker occurredAt = new DateTimePicker("Fecha y hora");
        occurredAt.setHelperText("Vacio: ahora");
        TextField reference = new TextField("Referencia externa");
        reference.setMaxLength(REFERENCE_LENGTH);
        reference.setHelperText("El albaran, la orden de trabajo...");
        TextArea notes = new TextArea("Notas");

        binder.forField(material).asRequired("Elige el material").bind("materialId");
        binder.forField(warehouse).asRequired("Elige el almacen").bind("warehouseId");
        binder.forField(quantity).asRequired("La cantidad es obligatoria")
                .withValidator(value -> value.signum() > 0, "Tiene que ser mayor que cero")
                .bind("quantity");
        binder.forField(occurredAt).bind("occurredAt");
        binder.forField(reference).bind("externalReference");
        binder.forField(notes).bind("notes");
        FormLayout layout = new FormLayout();
        layout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1), new FormLayout.ResponsiveStep("36em", 2));
        if (consumingReservation) {
            add(new Paragraph("Esta salida consume la reserva: el material, el almacen y la cantidad son los reservados."));
            material.setReadOnly(true);
            warehouse.setReadOnly(true);
            quantity.setReadOnly(true);
        }
        layout.add(material, warehouse);
        switch (kind) {
            case ENTRY -> {
                binder.forField(supplier).bind("supplierId");
                layout.add(supplier);
            }
            case OUTPUT -> {
                binder.forField(project).bind("projectId");
                layout.add(project);
            }
            case TRANSFER -> {
                binder.forField(target).asRequired("Elige el almacen de destino")
                        .withValidator(value -> warehouse.getValue() == null || !value.id().equals(warehouse.getValue().id()),
                                "El destino tiene que ser otro almacen")
                        .bind("targetWarehouseId");
                layout.add(target);
            }
            case ADJUSTMENT -> {
                binder.forField(direction).asRequired("Elige el sentido").bind("direction");
                layout.add(direction);
            }
        }
        layout.add(quantity, occurredAt, reference, notes);
        layout.setColspan(notes, 2);
        binder.readBean(form);
        add(layout);

        Button save = new Button("Registrar", click -> save(done));
        save.setId(SAVE_ID);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(new Button("Cancelar", click -> close()), save);
    }

    private void save(Consumer<MovementDto> done) {
        if (!binder.writeBeanIfValid(form)) {
            return;
        }
        try {
            MovementDto movement = switch (kind) {
                case ENTRY -> clients.movements().entry(form.toEntry());
                case OUTPUT -> clients.movements().output(form.toOutput());
                case TRANSFER -> clients.movements().transfer(form.toTransfer()).getFirst();
                case ADJUSTMENT -> clients.movements().adjustment(form.toAdjustment());
            };
            close();
            Notification.show(kind.done + ": " + StockFormats.quantity(form.getQuantity()) + " " + unit(form.getMaterialId())
                            + " de " + form.getMaterialId().code(), 4000, Notification.Position.BOTTOM_START)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            done.accept(movement);
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
        return material.unitOfMeasure() == null ? "" : material.unitOfMeasure();
    }
}
