package com.alejandro.mtobackoffice.client.stock;

import com.alejandro.mtobackoffice.client.dto.stock.CatalogueRequest;
import com.alejandro.mtobackoffice.client.dto.stock.CatalogueUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.stock.ProjectDto;
import org.springframework.web.service.annotation.HttpExchange;

/** Los proyectos del almacen; los sincronizados desde mto-configuration no admiten {@code update} (422 {@code PRJ-001}). */
@HttpExchange("/api/stock/projects")
public interface ProjectClient extends StockCatalogueClient<ProjectDto, CatalogueRequest, CatalogueUpdateRequest> {
}
