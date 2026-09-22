package com.alejandro.mtobackoffice.client.dto.stock;

/** Modificacion de almacen, proveedor o proyecto: {@code active} es obligatorio y es como se retira uno. */
public record CatalogueUpdateRequest(String code, String name, boolean active) {
}
