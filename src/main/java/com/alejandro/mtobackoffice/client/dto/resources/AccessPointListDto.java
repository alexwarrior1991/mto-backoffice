package com.alejandro.mtobackoffice.client.dto.resources;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * La respuesta de {@code GET /track-access-points} de mto-resources: una lista entera, sin paginar. Se lee
 * tambien si llegara como una pagina ({@code {content, page}}), como en mto-frontend: el JSON de una lista
 * entra por el creador de lista y el de una pagina, por {@code content}.
 */
public record AccessPointListDto(List<AccessPointDto> content) {

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public AccessPointListDto(@JsonProperty("content") List<AccessPointDto> content) {
        this.content = content == null ? List.of() : List.copyOf(content);
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static AccessPointListDto of(List<AccessPointDto> items) {
        return new AccessPointListDto(items);
    }
}
