package com.alejandro.mtobackoffice.ui.stock;

import com.alejandro.mtobackoffice.client.dto.stock.AssemblyAvailabilityComponentDto;
import com.alejandro.mtobackoffice.client.dto.stock.AssemblyAvailabilityDto;
import com.alejandro.mtobackoffice.client.dto.stock.AssemblyDto;
import com.alejandro.mtobackoffice.client.dto.stock.WarehouseDto;
import com.alejandro.mtobackoffice.client.dto.stock.WarehouseSummaryDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.stock.AssemblyClient;
import com.alejandro.mtobackoffice.client.stock.StockCatalogueClient;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.theme.lumo.LumoUtility;

/**
 * Cuantos conjuntos se podrian montar ahora en un almacen y que componente lo limita. El almacen
 * es obligatorio porque el stock es por almacen, y el calculo es del servicio
 * ({@code GET /assemblies/{id}/availability?warehouseId}): un conjunto no tiene stock propio y
 * aqui no se divide nada.
 */
public class AssemblyAvailabilityDialog extends Dialog {

    private final AssemblyDto assembly;
    private final AssemblyClient client;
    private final Span result = new Span();
    private final Span calculatedAt = new Span();
    private final Grid<AssemblyAvailabilityComponentDto> grid = new Grid<>();

    public AssemblyAvailabilityDialog(AssemblyDto assembly, AssemblyClient client, StockCatalogueClient<WarehouseDto, ?, ?> warehouses) {
        this.assembly = assembly;
        this.client = client;
        setHeaderTitle("Disponibilidad de " + assembly.summary().label());
        setWidth("min(64rem, 96vw)");

        ComboBox<WarehouseSummaryDto> warehouse = StockPickers.warehouse("Almacen", warehouses);
        warehouse.setId("availability-warehouse");
        warehouse.setHelperText("El stock es por almacen: elige uno para calcular");
        warehouse.addValueChangeListener(change -> calculate(change.getValue()));

        result.setId("availability-quantity");
        result.addClassNames(LumoUtility.FontSize.XLARGE, LumoUtility.FontWeight.BOLD, LumoUtility.Display.BLOCK);
        calculatedAt.setId("availability-at");
        calculatedAt.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY, LumoUtility.Display.BLOCK);

        grid.setId("availability-grid");
        grid.addColumn(component -> component.material().label()).setHeader("Material").setKey("material").setFlexGrow(1);
        grid.addColumn(component -> StockFormats.quantity(component.requiredQuantityPerAssembly())).setHeader("Por conjunto").setKey("required").setAutoWidth(true);
        grid.addColumn(component -> StockFormats.quantity(component.onHandQuantity())).setHeader("Fisico").setKey("onHand").setAutoWidth(true);
        grid.addColumn(component -> StockFormats.quantity(component.activeReservedQuantity())).setHeader("Reservado").setKey("reserved").setAutoWidth(true);
        grid.addColumn(component -> StockFormats.quantity(component.availableQuantity())).setHeader("Disponible").setKey("available").setAutoWidth(true);
        grid.addColumn(component -> StockFormats.quantity(component.producibleAssemblyQuantity())).setHeader("Montables").setKey("producible").setAutoWidth(true);
        grid.addColumn(component -> component.isLimiting() ? "Limita" : "").setHeader("").setKey("limiting").setAutoWidth(true);
        grid.setPartNameGenerator(component -> component.isLimiting() ? "limiting" : null);
        grid.setAllRowsVisible(true);
        showResult(false);
        add(warehouse, result, calculatedAt, grid);
        getFooter().add(new Button("Cerrar", click -> close()));
    }

    private void calculate(WarehouseSummaryDto warehouse) {
        if (warehouse == null) {
            showResult(false);
            return;
        }
        try {
            AssemblyAvailabilityDto availability = client.availability(assembly.id(), warehouse.id());
            result.setText(StockFormats.quantity(availability.availableQuantity()) + " conjuntos montables en " + warehouse.code());
            calculatedAt.setText("Calculado el " + StockFormats.dateTime(availability.calculatedAt())
                    + "; el componente que limita esta marcado.");
            grid.setItems(availability.components());
            showResult(true);
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
            showResult(false);
        }
    }

    private void showResult(boolean visible) {
        result.setVisible(visible);
        calculatedAt.setVisible(visible);
        grid.setVisible(visible);
    }
}
