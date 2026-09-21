package com.alejandro.mtobackoffice.client.configuration;

import com.alejandro.mtobackoffice.client.dto.LovDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

import java.util.List;

/**
 * Las listas de valores de mto-configuration a traves del gateway ({@code /api/configuration/**}
 * se reescribe a {@code /api/v1/configuration/**}). Parametrizado por recurso porque los 17
 * catalogos heredan los mismos ocho endpoints de {@code AbstractLovController}; con esto una sola
 * vista los sirve todos.
 *
 * <p>Permisos que aplica el servicio: leer pide {@code config-read}; crear y modificar,
 * {@code config-write} <b>y</b> {@code lov-manage}; los lotes ({@code /bulk}), {@code config-import}
 * y {@code lov-manage}; borrar, {@code config-delete} y {@code lov-manage}. El borrado es logico:
 * la entrada desaparece del catalogo. {@code findAll} devuelve una lista, no una pagina: el
 * proveedor de datos es en memoria.</p>
 */
@HttpExchange("/api/configuration")
public interface LovClient {

    @GetExchange("/{resource}")
    List<LovDto> findAll(@PathVariable("resource") String resource);

    @GetExchange("/{resource}/{id}")
    LovDto findById(@PathVariable("resource") String resource, @PathVariable("id") Long id);

    @GetExchange("/{resource}/code/{code}")
    LovDto findByCode(@PathVariable("resource") String resource, @PathVariable("code") String code);

    @PostExchange("/{resource}")
    LovDto create(@PathVariable("resource") String resource, @RequestBody LovDto dto);

    @PostExchange("/{resource}/bulk")
    List<LovDto> bulkCreate(@PathVariable("resource") String resource, @RequestBody List<LovDto> dtos);

    @PutExchange("/{resource}/{id}")
    LovDto update(@PathVariable("resource") String resource, @PathVariable("id") Long id, @RequestBody LovDto dto);

    @PutExchange("/{resource}/bulk")
    List<LovDto> bulkUpdate(@PathVariable("resource") String resource, @RequestBody List<LovDto> dtos);

    @DeleteExchange("/{resource}/{id}")
    void delete(@PathVariable("resource") String resource, @PathVariable("id") Long id);

    default List<LovDto> findAll(LovResource resource) {
        return findAll(resource.path());
    }
}
