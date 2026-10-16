package com.alejandro.mtobackoffice.ui.resources;

import com.alejandro.mtobackoffice.client.configuration.BusinessEntityClient;
import com.alejandro.mtobackoffice.client.configuration.ExecutionPackageClient;
import com.alejandro.mtobackoffice.client.configuration.StationClient;
import com.alejandro.mtobackoffice.client.configuration.TrackClient;
import com.alejandro.mtobackoffice.client.resources.OrganizationClient;
import com.alejandro.mtobackoffice.client.resources.PersonClient;
import org.springframework.stereotype.Component;

/**
 * Los clientes que usan las pantallas de recursos, juntos, como {@code MaintenanceClients}: un solo
 * bean para que las vistas y sus dialogos no carguen una lista de parametros que crece con cada
 * pantalla; cada cliente sigue siendo su propio bean. Los de mto-configuration son para nombrar lo
 * que mto-resources solo guarda como id ({@link ResourcesNames}).
 */
@Component
public record ResourcesClients(PersonClient people, OrganizationClient organization, ExecutionPackageClient packages,
                               StationClient stations, TrackClient tracks, BusinessEntityClient companies) {
}
