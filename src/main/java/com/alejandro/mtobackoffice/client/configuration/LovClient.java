package com.alejandro.mtobackoffice.client.configuration;

import com.alejandro.mtobackoffice.client.dto.LovDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;

/**
 * Las listas de valores de mto-configuration, a traves del gateway ({@code /api/configuration/**}
 * se reescribe a {@code /api/v1/configuration/**}). Parametrizado por recurso porque los 17
 * catalogos heredan los mismos endpoints de {@code AbstractLovController}.
 *
 * <p>{@code findAll} devuelve una lista, no una pagina: el proveedor de datos es en memoria.</p>
 */
@HttpExchange("/api/configuration")
public interface LovClient {

    @GetExchange("/{resource}")
    List<LovDto> findAll(@PathVariable("resource") String resource);

    default List<LovDto> findAll(LovResource resource) {
        return findAll(resource.path());
    }
}
