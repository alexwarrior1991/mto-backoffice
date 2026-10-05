package com.alejandro.mtobackoffice.ui.stock;

import com.alejandro.mtobackoffice.client.configuration.MasterFilters;
import com.alejandro.mtobackoffice.client.dto.PageResponse;
import com.alejandro.mtobackoffice.client.dto.stock.MaterialDto;
import com.alejandro.mtobackoffice.client.dto.stock.MaterialSummaryDto;
import com.alejandro.mtobackoffice.client.dto.stock.ProjectDto;
import com.alejandro.mtobackoffice.client.dto.stock.ProjectSummaryDto;
import com.alejandro.mtobackoffice.client.dto.stock.SupplierDto;
import com.alejandro.mtobackoffice.client.dto.stock.SupplierSummaryDto;
import com.alejandro.mtobackoffice.client.dto.stock.WarehouseDto;
import com.alejandro.mtobackoffice.client.dto.stock.WarehouseSummaryDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.stock.StockCatalogueClient;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.combobox.ComboBox;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Desplegables de referencias del almacen que buscan <b>en el servidor</b> mientras se escribe
 * ({@code search} por codigo o nombre): materiales y proyectos son miles y no se cargan enteros. El
 * valor es el resumen del catalogo, que es lo que llevan los movimientos y las reservas.
 *
 * <p>En un dialogo solo se ofrece lo activo, porque el servicio rechaza lo retirado. En un filtro y
 * en Existencias ({@code includeRetired}) tambien lo retirado, marcado como tal, para encontrar lo
 * de antes, como en mto-frontend.</p>
 */
public final class StockPickers {

    private static final List<String> BY_CODE = MasterFilters.withTieBreak(List.of("code,asc"), MasterFilters.BY_ID);
    static final String RETIRED = " (retirado)";

    private StockPickers() {
    }

    public static ComboBox<MaterialSummaryDto> material(String label, StockCatalogueClient<MaterialDto, ?, ?> materials) {
        return material(label, materials, false);
    }

    public static ComboBox<MaterialSummaryDto> material(String label, StockCatalogueClient<MaterialDto, ?, ?> materials, boolean includeRetired) {
        return lazy(label, "Escribe el codigo o el nombre del material", materials, MaterialDto::summary, MaterialSummaryDto::label,
                MaterialSummaryDto::active, includeRetired);
    }

    public static ComboBox<WarehouseSummaryDto> warehouse(String label, StockCatalogueClient<WarehouseDto, ?, ?> warehouses) {
        return warehouse(label, warehouses, false);
    }

    public static ComboBox<WarehouseSummaryDto> warehouse(String label, StockCatalogueClient<WarehouseDto, ?, ?> warehouses, boolean includeRetired) {
        return lazy(label, "Escribe el codigo o el nombre del almacen", warehouses, WarehouseDto::summary, WarehouseSummaryDto::label,
                WarehouseSummaryDto::active, includeRetired);
    }

    public static ComboBox<SupplierSummaryDto> supplier(String label, StockCatalogueClient<SupplierDto, ?, ?> suppliers) {
        return lazy(label, "Escribe el codigo o el nombre del proveedor", suppliers, SupplierDto::summary, SupplierSummaryDto::label,
                SupplierSummaryDto::active, false);
    }

    public static ComboBox<ProjectSummaryDto> project(String label, StockCatalogueClient<ProjectDto, ?, ?> projects) {
        return project(label, projects, false);
    }

    public static ComboBox<ProjectSummaryDto> project(String label, StockCatalogueClient<ProjectDto, ?, ?> projects, boolean includeRetired) {
        return lazy(label, "Escribe el codigo o el nombre del proyecto", projects, ProjectDto::summary, ProjectSummaryDto::label,
                ProjectSummaryDto::active, includeRetired);
    }

    private static <D, S> ComboBox<S> lazy(String label, String placeholder, StockCatalogueClient<D, ?, ?> client,
                                           Function<D, S> summary, Function<S, String> itemLabel, Function<S, Boolean> active,
                                           boolean includeRetired) {
        ComboBox<S> combo = new ComboBox<>(label);
        combo.setItemLabelGenerator(item -> Boolean.FALSE.equals(active.apply(item)) ? itemLabel.apply(item) + RETIRED : itemLabel.apply(item));
        combo.setClearButtonVisible(true);
        combo.setPlaceholder(placeholder);
        combo.setItems(query -> {
            try {
                String text = query.getFilter().orElse("").trim();
                PageResponse<D> page = client.search(text.isEmpty() ? null : text, includeRetired ? null : Boolean.TRUE,
                        query.getPage(), query.getPageSize(), BY_CODE);
                return page.content().stream().map(summary);
            } catch (BackofficeApiException failure) {
                UiErrors.show(failure);
                return Stream.empty();
            }
        });
        return combo;
    }
}
