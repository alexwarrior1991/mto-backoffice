package com.alejandro.mtobackoffice.client.resources;

import com.alejandro.mtobackoffice.client.dto.resources.PositionDiscrepancyReportDto;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

/**
 * Los informes de mto-resources ({@code resources-read}). El de puestos y perfiles lo calcula el servicio
 * leyendo Keycloak con su cuenta de servicio de solo lectura; si no puede, 503 {@code KCK-503}.
 */
@HttpExchange("/api/resources/reports")
public interface ResourcesReportClient {

    @GetExchange("/position-discrepancies")
    PositionDiscrepancyReportDto positionDiscrepancies();
}
