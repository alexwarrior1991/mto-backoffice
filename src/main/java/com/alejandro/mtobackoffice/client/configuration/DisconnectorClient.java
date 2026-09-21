package com.alejandro.mtobackoffice.client.configuration;

import com.alejandro.mtobackoffice.client.dto.master.DisconnectorDto;
import org.springframework.web.service.annotation.HttpExchange;

/** Los seccionadores. Solo pone la ruta y el tipo: los endpoints son los de {@link MasterClient}. */
@HttpExchange("/api/configuration/disconnectors")
public interface DisconnectorClient extends MasterClient<DisconnectorDto> {
}
