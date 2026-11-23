package com.alejandro.mtobackoffice.client.configuration;

import com.alejandro.mtobackoffice.client.dto.master.TrackDto;
import org.springframework.web.service.annotation.HttpExchange;
import com.alejandro.mtobackoffice.client.dto.master.TrackSchematicDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;

/**
 * Las vias: los endpoints de {@link MasterClient} y, ademas, el esquema de una via y su borrado
 * entero.
 */
@HttpExchange("/api/configuration/tracks")
public interface TrackClient extends MasterClient<TrackDto> {

    /**
     * El esquema de la via en una llamada: la proyeccion que el servicio cachea con lo justo para
     * dibujarla (README_API.md §6 de mto-configuration). Un 404 es una via que no existe.
     */
    @GetExchange("/{id}/schematic")
    TrackSchematicDto schematic(@PathVariable("id") Long id);

    /**
     * Borra la via entera ({@code cascade=true}, con {@code config-delete} como el borrado normal): la
     * via con sus perfiles (y sus mensulas y brazos), sus seccionadores, sus aisladores (y sus agujas)
     * y sus puntos de acceso, en una sola transaccion. No toca nada de otra via: si algo de otra via la
     * usa, 409 {@code BUS-002} con su detalle y no se borra nada. Con miles de perfiles tarda varios
     * segundos. {@link #delete} no cambia: sin parametro, y con algo colgando es ese mismo 409.
     */
    @DeleteExchange("/{id}?cascade=true")
    void deleteWhole(@PathVariable("id") Long id);
}
