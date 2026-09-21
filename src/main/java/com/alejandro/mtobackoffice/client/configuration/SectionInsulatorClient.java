package com.alejandro.mtobackoffice.client.configuration;

import com.alejandro.mtobackoffice.client.dto.master.SectionInsulatorDto;
import org.springframework.web.service.annotation.HttpExchange;

/** Los aisladores de seccion. Solo pone la ruta y el tipo: los endpoints son los de {@link MasterClient}. */
@HttpExchange("/api/configuration/section-insulators")
public interface SectionInsulatorClient extends MasterClient<SectionInsulatorDto> {
}
