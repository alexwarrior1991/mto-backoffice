package com.alejandro.mtobackoffice.client.configuration;

import com.alejandro.mtobackoffice.client.dto.master.ExecutionPackageDto;
import org.springframework.web.service.annotation.HttpExchange;

/** Los paquetes de ejecucion. Solo pone la ruta y el tipo: los endpoints son los de {@link MasterClient}. */
@HttpExchange("/api/configuration/execution-packages")
public interface ExecutionPackageClient extends MasterClient<ExecutionPackageDto> {
}
