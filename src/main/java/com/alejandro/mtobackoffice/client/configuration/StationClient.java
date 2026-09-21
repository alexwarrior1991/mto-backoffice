package com.alejandro.mtobackoffice.client.configuration;

import com.alejandro.mtobackoffice.client.dto.master.StationDto;
import org.springframework.web.service.annotation.HttpExchange;

/** Las estaciones. Solo pone la ruta y el tipo: los endpoints son los de {@link MasterClient}. */
@HttpExchange("/api/configuration/stations")
public interface StationClient extends MasterClient<StationDto> {
}
