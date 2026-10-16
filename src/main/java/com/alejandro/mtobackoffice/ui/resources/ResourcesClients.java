package com.alejandro.mtobackoffice.ui.resources;

import com.alejandro.mtobackoffice.client.resources.PersonClient;
import org.springframework.stereotype.Component;

/**
 * Los clientes que usan las pantallas de recursos, juntos, como {@code MaintenanceClients}: un solo
 * bean para que las vistas y sus dialogos no carguen una lista de parametros que crece con cada
 * pantalla; cada cliente sigue siendo su propio bean.
 */
@Component
public record ResourcesClients(PersonClient people) {
}
