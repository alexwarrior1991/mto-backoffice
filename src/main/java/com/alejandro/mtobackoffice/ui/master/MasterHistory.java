package com.alejandro.mtobackoffice.ui.master;

import com.alejandro.mtobackoffice.client.dto.master.DisconnectorDto;
import com.alejandro.mtobackoffice.client.dto.master.ExecutionPackageDto;
import com.alejandro.mtobackoffice.client.dto.master.LovRef;
import com.alejandro.mtobackoffice.client.dto.master.ProfileDto;
import com.alejandro.mtobackoffice.client.dto.master.SectionInsulatorDto;
import com.alejandro.mtobackoffice.client.dto.master.StationDto;
import com.alejandro.mtobackoffice.client.dto.master.TrackAccessPointDto;
import com.alejandro.mtobackoffice.client.dto.master.TrackAccessPointKind;
import com.alejandro.mtobackoffice.client.dto.master.TrackAccessPointSide;
import com.alejandro.mtobackoffice.client.dto.master.TrackDto;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * El historial de un maestro de mto-configuration (Envers alli), en el mismo dialogo que almacen y
 * mantenimiento: una linea por recurso que dice como quedo la fila en cada revision, con sus campos
 * principales y los nombres de sus referencias (via, estacion, paquete, catalogos) como los nombra
 * su lista. Los nombres son los de hoy, tambien para el servicio: cada revision guarda a que fila
 * apuntaba, no como se llamaba entonces. Lo que no viene no deja separadores sueltos.
 */
final class MasterHistory {

    /** Una revision sin fila ({@code entity: null}): el servicio no la pudo reconstruir. */
    static final String NO_ENTITY = "Sin datos de la fila en esta revision";

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ROOT);

    private MasterHistory() {
    }

    static String executionPackage(ExecutionPackageDto dto, ReferenceCatalog catalog) {
        return join(dto.getName(), catalog.companyName(dto.getCompanyId()),
                label(dto.getStartDate(), date -> "inicio " + DATE.format(date)), label(dto.getEndDate(), date -> "fin " + DATE.format(date)),
                label(dto.getLength(), length -> "longitud " + length), Boolean.TRUE.equals(dto.getInitialPackage()) ? "inicial" : null,
                dto.getEnabled() ? "activo" : "inactivo");
    }

    static String station(StationDto dto, ReferenceCatalog catalog) {
        return join(dto.getName(), catalog.packageName(dto.getExecutionPackageId()));
    }

    static String track(TrackDto dto, ReferenceCatalog catalog) {
        List<Long> stations = dto.getStationIds();
        return join(dto.getName(), catalog.packageName(dto.getExecutionPackageId()),
                stations == null || stations.isEmpty() ? null
                        : "estaciones: " + stations.stream().map(catalog::stationName).collect(Collectors.joining(", ")),
                state(dto.getEnabled(), "activa", "inactiva"));
    }

    static String profile(ProfileDto dto, ReferenceCatalog catalog) {
        return join(dto.getProfileId(), label(dto.getKp(), kp -> "KP " + kp), catalog.trackName(dto.getTrackId()),
                code("estado ", dto.getProfileStatus()), code("poste ", dto.getPoleType()),
                label(dto.getSpan(), span -> "vano " + span.toPlainString() + " m"),
                label(dto.getCantilevers(), arms -> count(arms.size(), "mensula", "mensulas")));
    }

    static String disconnector(DisconnectorDto dto, ReferenceCatalog catalog) {
        return join(dto.getName(), catalog.stationName(dto.getStationId()),
                label(dto.getDisconnectorFunction(), LovRef::label),
                dto.profileLabel().isEmpty() ? label(dto.getKp(), kp -> "sin poste, KP " + kp) : "perfil " + dto.profileLabel(),
                state(dto.getOnLoad(), "en carga", "sin carga"), state(dto.getNormallyOpen(), "normalmente abierto", "normalmente cerrado"),
                label(dto.getDriveType(), drive -> "accionamiento " + drive.label().toLowerCase(Locale.ROOT)),
                label(dto.getTrackId(), id -> "via propia " + catalog.trackName(id)),
                label(dto.getConnectedTrackId(), id -> "via conectada " + catalog.trackName(id)));
    }

    static String sectionInsulator(SectionInsulatorDto dto, ReferenceCatalog catalog) {
        return join(dto.getName(), catalog.stationName(dto.getStationId()), label(dto.getKp(), kp -> "KP " + plain(kp)),
                label(dto.getInstallationType(), type -> type.label()), catalog.trackName(dto.getTrackId()),
                label(dto.getConnectedTrackId(), id -> "via conectada " + catalog.trackName(id)),
                label(dto.getSwitches(), switches -> count(switches.size(), "aguja", "agujas")),
                state(dto.getEnabled(), "activo", "inactivo"));
    }

    static String trackAccessPoint(TrackAccessPointDto dto, ReferenceCatalog catalog) {
        return join(dto.getName(), catalog.trackName(dto.getTrackId()), catalog.stationName(dto.getStationId()),
                label(dto.getKp(), kp -> "KP " + plain(kp)), TrackAccessPointKind.labelOf(dto.getKind()),
                TrackAccessPointSide.labelOf(dto.getSide()), state(dto.getRoadRailAccess(), "biviales", "sin biviales"),
                state(dto.getEnabled(), "activo", "inactivo"));
    }

    private static String code(String prefix, LovRef ref) {
        return ref == null || ref.code() == null ? null : prefix + ref.code();
    }

    private static String state(Boolean value, String yes, String no) {
        return value == null ? null : (value ? yes : no);
    }

    private static String count(int size, String one, String many) {
        return size + " " + (size == 1 ? one : many);
    }

    private static String plain(BigDecimal value) {
        return value.toPlainString();
    }

    private static <T> String label(T value, Function<T, String> label) {
        return value == null ? null : label.apply(value);
    }

    /** Las partes que hay, separadas por {@code ·}; lo vacio no deja separadores sueltos. */
    private static String join(String... parts) {
        return Arrays.stream(parts).filter(part -> part != null && !part.isBlank()).collect(Collectors.joining(" · "));
    }
}
