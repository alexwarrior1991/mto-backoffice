package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.UUID;

/**
 * Resolver un defecto (pide {@code maintenance-supervise}). Con orden vinculada sin completar, solo
 * si se corrigio in situ en un turno: {@code resolvedInShiftId}.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ResolveDefectRequest(String resolutionNotes, UUID resolvedInShiftId, String correctionType, String partsReplaced) {
}
