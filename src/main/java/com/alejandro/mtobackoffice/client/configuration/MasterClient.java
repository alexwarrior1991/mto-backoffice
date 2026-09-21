package com.alejandro.mtobackoffice.client.configuration;

import com.alejandro.mtobackoffice.client.dto.PageResponse;
import com.alejandro.mtobackoffice.client.dto.master.MasterDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

import java.util.List;
import java.util.Map;

/**
 * Lo que comparten los maestros de infraestructura de mto-configuration: los endpoints de
 * {@code CRUDController} que usa el backoffice. Cada maestro es una subinterfaz vacia que solo pone
 * la ruta ({@code @HttpExchange("/api/configuration/stations")}) y el tipo del DTO; Spring resuelve
 * {@code D} contra esa subinterfaz, asi que las respuestas llegan tipadas.
 *
 * <p>La lista es siempre {@code POST /filter}: es el unico endpoint con {@code searchText}
 * (busqueda en varias columnas, incluidas las de tablas asociadas) y pagina con los parametros de
 * Spring Data ({@code page}, {@code size}, {@code sort=campo,asc}). Las filas de paquetes,
 * estaciones y vias llegan sin sus colecciones de hijos ({@code null}, README_API.md §1); el resto,
 * completas. Los filtros que no vienen no filtran, incluidos los booleanos.</p>
 *
 * <p>Permisos del servicio: leer, {@code config-read}; crear y modificar, {@code config-write};
 * borrar (logico), {@code config-delete}.</p>
 */
public interface MasterClient<D extends MasterDto> {

    @PostExchange("/filter")
    PageResponse<D> filter(@RequestParam("page") int page, @RequestParam("size") int size,
                           @RequestParam("sort") List<String> sort, @RequestBody Map<String, Object> filter);

    @GetExchange("/{id}")
    D findById(@PathVariable("id") Long id);

    @PostExchange
    D create(@RequestBody D dto);

    @PutExchange("/{id}")
    D update(@PathVariable("id") Long id, @RequestBody D dto);

    @DeleteExchange("/{id}")
    void delete(@PathVariable("id") Long id);

    /** Guarda segun tenga id o no: alta con {@code POST}, modificacion con {@code PUT /{id}}. */
    default D save(D dto) {
        return dto.isNew() ? create(dto) : update(dto.getId(), dto);
    }
}
