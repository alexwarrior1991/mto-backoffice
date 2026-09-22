package com.alejandro.mtobackoffice.client.dto.stock;

import java.util.List;

/** Alta de un conjunto con su lista de materiales, que no puede ir vacia. */
public record AssemblyRequest(String code, String name, List<AssemblyComponentRequest> components) {
}
