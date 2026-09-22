package com.alejandro.mtobackoffice.client.stock;

import com.alejandro.mtobackoffice.client.dto.stock.CatalogueRequest;
import com.alejandro.mtobackoffice.client.dto.stock.CatalogueUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.stock.SupplierDto;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange("/api/stock/suppliers")
public interface SupplierClient extends StockCatalogueClient<SupplierDto, CatalogueRequest, CatalogueUpdateRequest> {
}
