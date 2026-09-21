package com.alejandro.mtobackoffice.client.dto;

import java.util.List;

/**
 * Pagina tal y como la emiten los servicios: la forma {@code PagedModel} de Spring Data que
 * mto-configuration fija con {@code serialization-mode: via_dto}, y la de {@code PageResponse} de
 * mto-stock y mto-maintenance (sus {@code first}/{@code last} de mas se ignoran).
 */
public record PageResponse<T>(List<T> content, PageMetadata page) {

    public PageResponse {
        content = content == null ? List.of() : List.copyOf(content);
    }
}
