package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.dto.maintenance.MaintenanceOrderStatus;
import com.alejandro.mtobackoffice.client.dto.maintenance.MaterialUsageDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.MaterialUsageRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.MaterialUsageUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.OrderDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.TaskDto;
import com.alejandro.mtobackoffice.client.dto.stock.MaterialSummaryDto;
import com.alejandro.mtobackoffice.client.dto.stock.WarehouseSummaryDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.ui.stock.StockPickers;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.textfield.BigDecimalField;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * Registrar o modificar una linea de material. El alta elige material y almacen en mto-stock (pide
 * {@code stock-read}) y, si se quiere, la tarea; fuera de borrador el servicio la reserva al
 * momento. La modificacion manda solo lo cambiado; lo previsto de una linea reservada no se ofrece,
 * porque el servicio no lo cambia: se quita y se registra otra vez.
 */
public class MaterialUsageDialog extends Dialog {

    public static final String SAVE_ID = "material-save";

    public MaterialUsageDialog(OrderDto order, MaterialUsageDto existing, List<TaskDto> openTasks, MaintenanceClients clients, Runnable saved) {
        boolean creating = existing == null;
        setHeaderTitle(creating ? "Nuevo material en " + order.code() : "Modificar " + existing.materialLabel());
        setCloseOnOutsideClick(false);
        setWidth("min(90vw, 640px)");

        ComboBox<MaterialSummaryDto> material = StockPickers.material("Material", clients.materials());
        material.setId("material-material");
        ComboBox<WarehouseSummaryDto> warehouse = StockPickers.warehouse("Almacen", clients.warehouses());
        warehouse.setId("material-warehouse");
        ComboBox<TaskDto> task = new ComboBox<>("Tarea");
        task.setId("material-task");
        task.setItems(openTasks);
        task.setItemLabelGenerator(candidate -> candidate.sequence() + " · " + candidate.description());
        task.setClearButtonVisible(true);
        BigDecimalField planned = new BigDecimalField("Previsto");
        planned.setId("material-planned");
        BigDecimalField consumed = new BigDecimalField("Consumido");
        consumed.setId("material-consumed");
        Checkbox overConsumption = new Checkbox("Admite consumir mas de lo previsto");
        overConsumption.setId("material-over-consumption");

        FormLayout layout = new FormLayout();
        layout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1), new FormLayout.ResponsiveStep("480px", 2));
        if (creating) {
            layout.add(material, warehouse, planned, task, overConsumption);
            if (order.status() != null && order.status().isOpen() && order.status() != MaintenanceOrderStatus.DRAFT) {
                add(new Paragraph("La orden ya esta planificada: la linea se reserva al momento en el almacen."));
            }
        } else {
            planned.setValue(existing.plannedQuantity());
            consumed.setValue(existing.consumedQuantity());
            overConsumption.setValue(Boolean.TRUE.equals(existing.allowOverConsumption()));
            if (existing.isReserved()) {
                planned.setReadOnly(true);
                planned.setHelperText("Tiene reserva en el almacen: para cambiar lo previsto, quita la linea y registrala de nuevo");
            }
            layout.add(planned, consumed, overConsumption);
        }
        add(layout);

        Button save = new Button("Guardar", click -> {
            try {
                if (creating) {
                    if (!validCreate(material, warehouse, planned)) {
                        return;
                    }
                    clients.orders().registerMaterial(order.id(), new MaterialUsageRequest(material.getValue().id(), null, warehouse.getValue().id(),
                            planned.getValue(), material.getValue().unitOfMeasure(), task.getValue() == null ? null : task.getValue().id(),
                            overConsumption.getValue() ? Boolean.TRUE : null));
                } else {
                    MaterialUsageUpdateRequest request = new MaterialUsageUpdateRequest(
                            existing.isReserved() || same(planned.getValue(), existing.plannedQuantity()) ? null : planned.getValue(),
                            same(consumed.getValue(), existing.consumedQuantity()) ? null : consumed.getValue(),
                            Objects.equals(overConsumption.getValue(), Boolean.TRUE.equals(existing.allowOverConsumption())) ? null
                                    : overConsumption.getValue());
                    if (!request.changesNothing()) {
                        clients.orders().updateMaterial(order.id(), existing.id(), request);
                    }
                }
                close();
                MaintenanceUi.success("Material guardado");
                saved.run();
            } catch (BackofficeApiException failure) {
                UiErrors.show(failure);
            }
        });
        save.setId(SAVE_ID);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(new Button("Cancelar", click -> close()), save);
    }

    private static boolean validCreate(ComboBox<MaterialSummaryDto> material, ComboBox<WarehouseSummaryDto> warehouse, BigDecimalField planned) {
        boolean valid = true;
        if (material.getValue() == null) {
            material.setErrorMessage("El material es obligatorio");
            material.setInvalid(true);
            valid = false;
        }
        if (warehouse.getValue() == null) {
            warehouse.setErrorMessage("El almacen es obligatorio");
            warehouse.setInvalid(true);
            valid = false;
        }
        if (planned.getValue() == null || planned.getValue().signum() < 0) {
            planned.setErrorMessage("Lo previsto es obligatorio y no puede ser negativo");
            planned.setInvalid(true);
            valid = false;
        }
        return valid;
    }

    private static boolean same(BigDecimal value, BigDecimal original) {
        return value == null ? original == null : original != null && value.compareTo(original) == 0;
    }
}
