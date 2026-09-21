package com.alejandro.mtobackoffice.client.stock;

import com.alejandro.mtobackoffice.client.dto.PageResponse;
import com.alejandro.mtobackoffice.client.dto.stock.RevisionDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

import java.util.List;
import java.util.UUID;

/**
 * Lo que comparten los catalogos de mto-stock (almacenes, proveedores, proyectos, materiales y
 * conjuntos): lista paginada con {@code search} (codigo o nombre) y {@code active}, lectura, alta,
 * modificacion e historial. Sin borrado: un catalogo se retira con {@code PUT} y {@code active=false}.
 * Cada subinterfaz pone la ruta y los tipos, y Spring resuelve el generico contra ella.
 *
 * @param <D> la respuesta
 * @param <C> el cuerpo del alta
 * @param <U> el cuerpo de la modificacion (lleva {@code active})
 */
public interface StockCatalogueClient<D, C, U> {

    /** {@code sort} va como {@code campo,asc} y solo admite atributos de la entidad del servicio. */
    @GetExchange
    PageResponse<D> search(@RequestParam(value = "search", required = false) String search,
                           @RequestParam(value = "active", required = false) Boolean active,
                           @RequestParam("page") int page, @RequestParam("size") int size,
                           @RequestParam("sort") List<String> sort);

    @GetExchange("/{id}")
    D findById(@PathVariable("id") UUID id);

    /** 201. */
    @PostExchange
    D create(@RequestBody C request);

    @PutExchange("/{id}")
    D update(@PathVariable("id") UUID id, @RequestBody U request);

    /** La mas reciente primero; 404 si no hay ninguna. */
    @GetExchange("/{id}/revisions")
    PageResponse<RevisionDto<D>> revisions(@PathVariable("id") UUID id, @RequestParam("page") int page, @RequestParam("size") int size);
}
