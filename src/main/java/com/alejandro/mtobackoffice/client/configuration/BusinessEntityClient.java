package com.alejandro.mtobackoffice.client.configuration;

import com.alejandro.mtobackoffice.client.dto.master.BusinessEntityDto;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;

/**
 * Las empresas a las que apunta {@code companyId} de un paquete de ejecucion. Solo lectura en el
 * servicio: se cargan con el maestro de perfiles, no por la API.
 */
@HttpExchange("/api/configuration/business-entities")
public interface BusinessEntityClient {

    @GetExchange
    List<BusinessEntityDto> findAll();
}
