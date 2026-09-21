package com.alejandro.mtobackoffice.client.stock;

import com.alejandro.mtobackoffice.client.dto.PageResponse;
import com.alejandro.mtobackoffice.client.dto.stock.MaterialDto;
import com.alejandro.mtobackoffice.client.dto.stock.MaterialRequest;
import com.alejandro.mtobackoffice.client.dto.stock.MaterialStockDto;
import com.alejandro.mtobackoffice.client.dto.stock.MaterialUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.stock.MovementDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@HttpExchange("/api/stock/materials")
public interface MaterialClient extends StockCatalogueClient<MaterialDto, MaterialRequest, MaterialUpdateRequest> {

    /**
     * La busqueda con los filtros de stock: {@code warehouseId} (materiales con movimientos en ese
     * almacen) y {@code belowMinimum} (solo {@code true} filtra; {@code false} no es «por encima»,
     * el servicio lo ignora).
     */
    @GetExchange
    PageResponse<MaterialDto> filter(@RequestParam(value = "search", required = false) String search,
                                     @RequestParam(value = "active", required = false) Boolean active,
                                     @RequestParam(value = "warehouseId", required = false) UUID warehouseId,
                                     @RequestParam(value = "belowMinimum", required = false) Boolean belowMinimum,
                                     @RequestParam("page") int page, @RequestParam("size") int size,
                                     @RequestParam("sort") List<String> sort);

    /** Los activos por debajo de su minimo, en un almacen o en todos. */
    @GetExchange("/low-stock")
    PageResponse<MaterialDto> lowStock(@RequestParam(value = "warehouseId", required = false) UUID warehouseId,
                                       @RequestParam("page") int page, @RequestParam("size") int size,
                                       @RequestParam("sort") List<String> sort);

    /** Sin {@code warehouseId}, la cifra global y {@code warehouse} nulo en la respuesta. */
    @GetExchange("/{id}/stock")
    MaterialStockDto stock(@PathVariable("id") UUID id, @RequestParam(value = "warehouseId", required = false) UUID warehouseId);

    /** El libro de este material; {@code dateFrom} y {@code dateTo} son inclusivos. */
    @GetExchange("/{id}/movements")
    PageResponse<MovementDto> movements(@PathVariable("id") UUID id,
                                        @RequestParam(value = "warehouseId", required = false) UUID warehouseId,
                                        @RequestParam(value = "dateFrom", required = false) Instant dateFrom,
                                        @RequestParam(value = "dateTo", required = false) Instant dateTo,
                                        @RequestParam(value = "user", required = false) String user,
                                        @RequestParam("page") int page, @RequestParam("size") int size,
                                        @RequestParam("sort") List<String> sort);
}
