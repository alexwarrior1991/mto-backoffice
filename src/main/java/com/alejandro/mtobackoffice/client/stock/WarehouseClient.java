package com.alejandro.mtobackoffice.client.stock;

import com.alejandro.mtobackoffice.client.dto.stock.CatalogueRequest;
import com.alejandro.mtobackoffice.client.dto.stock.CatalogueUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.stock.MaterialStockDto;
import com.alejandro.mtobackoffice.client.dto.stock.WarehouseDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.UUID;

@HttpExchange("/api/stock/warehouses")
public interface WarehouseClient extends StockCatalogueClient<WarehouseDto, CatalogueRequest, CatalogueUpdateRequest> {

    /** Las existencias de un material en este almacen. */
    @GetExchange("/{id}/inventory")
    MaterialStockDto inventory(@PathVariable("id") UUID id, @RequestParam("materialId") UUID materialId);
}
