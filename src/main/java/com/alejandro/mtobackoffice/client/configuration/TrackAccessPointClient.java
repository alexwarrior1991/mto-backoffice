package com.alejandro.mtobackoffice.client.configuration;

import com.alejandro.mtobackoffice.client.dto.master.TrackAccessPointDto;
import org.springframework.web.service.annotation.HttpExchange;

/**
 * Los puntos de acceso a la via (V28 de mto-configuration): por donde un vehiculo bivial sale de la
 * carretera y se encarrila. Solo pone la ruta y el tipo: los endpoints son los de {@link MasterClient}.
 * El {@code /filter} admite {@code name}, {@code trackId}, {@code trackName}, {@code stationName},
 * {@code kind}, {@code side}, {@code roadRailAccess}, {@code enabled} y {@code searchText} (el nombre,
 * la via o la estacion).
 */
@HttpExchange("/api/configuration/track-access-points")
public interface TrackAccessPointClient extends MasterClient<TrackAccessPointDto> {
}
