package com.alejandro.mtobackoffice.client.resources;

import com.alejandro.mtobackoffice.client.dto.resources.AccessPointDto;
import com.alejandro.mtobackoffice.client.dto.resources.AccessPointListDto;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;

/**
 * Los puntos de acceso a la via que mto-resources ha recibido de mto-configuration (V9, solo lectura,
 * {@code resources-read}): lo que ofrece «Registrar ubicacion» entre los de la via elegida. Una lista
 * entera, sin paginar (tambien se lee una pagina, {@link AccessPointListDto}); un filtro ausente no
 * filtra. Se editan en el maestro, en Infraestructura.
 */
@HttpExchange("/api/resources/track-access-points")
public interface AccessPointClient {

    @GetExchange
    AccessPointListDto list(@RequestParam(value = "trackId", required = false) Long trackId,
                              @RequestParam(value = "executionPackageId", required = false) Long executionPackageId,
                              @RequestParam(value = "enabled", required = false) Boolean enabled);

    /** Los activos de una via: los que se pueden elegir al registrar donde esta un vehiculo. */
    default List<AccessPointDto> enabledOnTrack(Long trackId) {
        AccessPointListDto list = list(trackId, null, true);
        return list == null ? List.of() : list.content();
    }
}
