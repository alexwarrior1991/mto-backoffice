package com.alejandro.mtobackoffice.client.configuration;

import com.alejandro.mtobackoffice.client.dto.master.TrackDto;
import org.springframework.web.service.annotation.HttpExchange;
import com.alejandro.mtobackoffice.client.dto.master.TrackSchematicDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

/**
 * Las vias: los endpoints de {@link MasterClient} y, ademas, el esquema de una via.
 */
@HttpExchange("/api/configuration/tracks")
public interface TrackClient extends MasterClient<TrackDto> {

    /**
     * El esquema de la via en una llamada: la proyeccion que el servicio cachea con lo justo para
     * dibujarla (README_API.md §6 de mto-configuration). Un 404 es una via que no existe.
     */
    @GetExchange("/{id}/schematic")
    TrackSchematicDto schematic(@PathVariable("id") Long id);
}
