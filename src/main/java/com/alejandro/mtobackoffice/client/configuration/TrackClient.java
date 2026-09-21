package com.alejandro.mtobackoffice.client.configuration;

import com.alejandro.mtobackoffice.client.dto.master.TrackDto;
import org.springframework.web.service.annotation.HttpExchange;

/** Las vias. Solo pone la ruta y el tipo: los endpoints son los de {@link MasterClient}. */
@HttpExchange("/api/configuration/tracks")
public interface TrackClient extends MasterClient<TrackDto> {
}
