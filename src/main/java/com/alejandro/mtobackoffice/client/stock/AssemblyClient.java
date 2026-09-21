package com.alejandro.mtobackoffice.client.stock;

import com.alejandro.mtobackoffice.client.dto.stock.AssemblyAvailabilityDto;
import com.alejandro.mtobackoffice.client.dto.stock.AssemblyDto;
import com.alejandro.mtobackoffice.client.dto.stock.AssemblyRequest;
import com.alejandro.mtobackoffice.client.dto.stock.AssemblyUpdateRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.UUID;

@HttpExchange("/api/stock/assemblies")
public interface AssemblyClient extends StockCatalogueClient<AssemblyDto, AssemblyRequest, AssemblyUpdateRequest> {

    /** Cuantos se podrian montar en ese almacen; el almacen es obligatorio porque el stock es por almacen. */
    @GetExchange("/{id}/availability")
    AssemblyAvailabilityDto availability(@PathVariable("id") UUID id, @RequestParam("warehouseId") UUID warehouseId);
}
