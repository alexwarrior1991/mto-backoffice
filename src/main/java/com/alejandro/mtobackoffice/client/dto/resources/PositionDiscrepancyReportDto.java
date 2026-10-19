package com.alejandro.mtobackoffice.client.dto.resources;

import java.time.Instant;
import java.util.List;

/**
 * El informe de mto-resources que compara el perfil de puesto de Keycloak con la categoria de cada
 * persona. Lo calcula el servicio al pedirlo, leyendo Keycloak (503 {@code KCK-503} si no puede); aqui
 * no se cuenta nada.
 */
public record PositionDiscrepancyReportDto(Instant generatedAt, Integer peopleChecked, Integer usersWithPosition,
                                           List<PositionDiscrepancyDto> wrongProfile, List<PositionDiscrepancyDto> severalProfiles,
                                           List<PositionDiscrepancyDto> withoutProfile, List<PositionDiscrepancyDto> withoutRecord) {

    public PositionDiscrepancyReportDto {
        wrongProfile = wrongProfile == null ? List.of() : wrongProfile;
        severalProfiles = severalProfiles == null ? List.of() : severalProfiles;
        withoutProfile = withoutProfile == null ? List.of() : withoutProfile;
        withoutRecord = withoutRecord == null ? List.of() : withoutRecord;
    }
}
