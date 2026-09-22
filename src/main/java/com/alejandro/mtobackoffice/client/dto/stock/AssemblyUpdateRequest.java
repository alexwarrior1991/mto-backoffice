package com.alejandro.mtobackoffice.client.dto.stock;

import java.util.List;

/** Modificacion de un conjunto: la lista de materiales que llega sustituye a la anterior entera. */
public record AssemblyUpdateRequest(String code, String name, boolean active, List<AssemblyComponentRequest> components) {
}
