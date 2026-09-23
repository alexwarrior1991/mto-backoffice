package com.alejandro.mtobackoffice.client.dto.master;

import java.util.List;

/**
 * El esquema de una via tal como lo devuelve {@code GET /tracks/{id}/schematic} de
 * mto-configuration (README_API.md §6 alli): la proyeccion, cacheada en el servicio, con lo justo
 * para dibujar la via como una linea. Solo las claves que usa el dibujo; lo desconocido se ignora.
 *
 * <p>Los perfiles vienen en el orden fisico de la via y no se reordenan aqui. Los KP y las medidas
 * viajan como texto plano ({@code "12.345"}); las listas nunca son {@code null}.</p>
 */
public record TrackSchematicDto(
        Long trackId,
        String trackName,
        Boolean enabled,
        String executionPackageName,
        List<String> stations,
        List<ProfileNode> profiles,
        List<InsulatorMark> sectionInsulators
) {

    public TrackSchematicDto {
        stations = stations == null ? List.of() : stations;
        profiles = profiles == null ? List.of() : profiles;
        sectionInsulators = sectionInsulators == null ? List.of() : sectionInsulators;
    }

    /** Un poste: sus datos principales, sus mensulas y, si lo lleva, su seccionador. */
    public record ProfileNode(
            Long id,
            String code,
            String kp,
            Integer orderInTrack,
            String span,
            String poleType,
            String supportType,
            String profileStatus,
            String railPoleDistance,
            List<String> sectionings,
            List<CantileverArm> cantilevers,
            DisconnectorMark disconnector
    ) {

        public ProfileNode {
            sectionings = sectionings == null ? List.of() : sectionings;
            cantilevers = cantilevers == null ? List.of() : cantilevers;
        }
    }

    /** Una mensula con su brazo de atirantado. */
    public record CantileverArm(
            Long id,
            String type,
            String stagger,
            String cwHeight,
            String catenaryHeight,
            String steadyArmType,
            Long steadyArmLength
    ) {
    }

    /** El seccionador que cuelga de un poste. */
    public record DisconnectorMark(
            Long id,
            String name,
            Boolean onLoad,
            String function,
            String station
    ) {
    }

    /** Un aislador de seccion en su KP; entran los de la via y los que conectan con ella. */
    public record InsulatorMark(
            Long id,
            String name,
            String kp,
            String installationType,
            Boolean enabled,
            String station,
            String track,
            String connectedTrack,
            List<SwitchMark> switches
    ) {

        public InsulatorMark {
            switches = switches == null ? List.of() : switches;
        }
    }

    /** Una aguja del aislador. */
    public record SwitchMark(
            Long id,
            String code,
            String kp,
            Integer turnoutDenominator,
            String track
    ) {
    }
}
