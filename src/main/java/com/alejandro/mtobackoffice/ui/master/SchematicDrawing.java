package com.alejandro.mtobackoffice.ui.master;

import com.alejandro.mtobackoffice.client.dto.master.TrackSchematicDto;
import com.alejandro.mtobackoffice.client.dto.master.TrackSchematicDto.CantileverArm;
import com.alejandro.mtobackoffice.client.dto.master.TrackSchematicDto.DisconnectorMark;
import com.alejandro.mtobackoffice.client.dto.master.TrackSchematicDto.InsulatorMark;
import com.alejandro.mtobackoffice.client.dto.master.TrackSchematicDto.ProfileNode;
import com.alejandro.mtobackoffice.client.dto.master.TrackSchematicDto.SwitchMark;
import org.springframework.web.util.HtmlUtils;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * El esquema de una via como SVG: una linea recta (la via, a nivel de carril) y, sobre ella, un
 * poste por perfil a distancia uniforme, en el orden en que el servicio los manda.
 *
 * <p>Es esquematico a proposito, no el layout CAD: la distancia entre postes es siempre la misma y
 * el KP se escribe debajo de cada uno. De cada poste se pintan su codigo, el tipo de poste y el
 * estado, sus seccionamientos, sus mensulas como brazos cortos (con el tipo, hacia el lado que da el
 * signo de {@code railPoleDistance}: negativo, a la izquierda) y, si lo lleva, su seccionador como
 * un rombo con su nombre. Los aisladores de seccion van sobre la linea, colocados entre los dos
 * postes vecinos por KP (interpolacion lineal; fuera de rango, en el extremo), con sus agujas. El
 * detalle completo de cada elemento va en su {@code <title>}, que el navegador enseña al pasar por
 * encima.</p>
 *
 * <p>Java puro, sin Vaadin, para poder probarlo solo. <b>Todo texto que viene del servicio pasa por
 * {@link HtmlUtils#htmlEscape}</b>: {@code Svg} vuelca la cadena en {@code innerHTML} tal cual.</p>
 */
public final class SchematicDrawing {

    /** Distancia horizontal entre dos postes, en px: la misma para todos, sea cual sea el vano. */
    public static final int STEP = 140;
    /** Margen a cada lado del primer y el ultimo poste. */
    public static final int MARGIN = 90;
    /** La altura de la linea de la via. */
    public static final int LINE_Y = 250;
    /** La altura del extremo superior de cada poste. */
    public static final int POLE_TOP = 100;
    public static final int HEIGHT = 350;
    private static final int MIN_WIDTH = 480;
    private static final int ARM_LENGTH = 38;
    private static final String UTF_8 = StandardCharsets.UTF_8.name();

    private SchematicDrawing() {
    }

    /** El ancho del dibujo para ese numero de postes. */
    public static int width(int profiles) {
        return Math.max(MIN_WIDTH, 2 * MARGIN + STEP * Math.max(0, profiles - 1));
    }

    /** La abscisa del poste que ocupa esa posicion en la via. */
    public static double xOf(int index) {
        return MARGIN + (double) index * STEP;
    }

    /** {@code -1} si el poste esta a la izquierda de la via ({@code railPoleDistance} negativo); {@code 1} si no. */
    public static int armDirection(ProfileNode profile) {
        BigDecimal distance = number(profile.railPoleDistance());
        return distance != null && distance.signum() < 0 ? -1 : 1;
    }

    /**
     * Donde va un aislador de ese KP: entre los dos postes vecinos que lo encierran, a la
     * distancia proporcional; antes del primero o despues del ultimo, medio paso fuera; sin KP, al
     * final. Se compara tramo a tramo y vale el primero que lo contiene, asi que una via con dos
     * tramos y la kilometracion reiniciada (README_API.md §4 del servicio) tambien lo coloca.
     */
    public static double insulatorX(List<ProfileNode> profiles, String kp) {
        BigDecimal value = number(kp);
        if (profiles.isEmpty() || value == null) {
            return xOf(Math.max(0, profiles.size() - 1)) + STEP / 2.0;
        }
        for (int i = 0; i + 1 < profiles.size(); i++) {
            BigDecimal from = number(profiles.get(i).kp());
            BigDecimal to = number(profiles.get(i + 1).kp());
            if (from == null || to == null) {
                continue;
            }
            if (value.compareTo(from.min(to)) >= 0 && value.compareTo(from.max(to)) <= 0) {
                if (from.compareTo(to) == 0) {
                    return xOf(i) + STEP / 2.0;
                }
                double fraction = value.subtract(from).doubleValue() / to.subtract(from).doubleValue();
                return xOf(i) + fraction * STEP;
            }
        }
        BigDecimal first = number(profiles.getFirst().kp());
        if (first != null && value.compareTo(first) < 0) {
            return Math.max(MARGIN / 3.0, xOf(0) - STEP / 2.0);
        }
        return xOf(profiles.size() - 1) + STEP / 2.0;
    }

    /** El SVG entero, listo para {@code innerHTML}. */
    public static String svg(TrackSchematicDto schematic) {
        List<ProfileNode> profiles = schematic.profiles();
        int width = width(profiles.size());
        StringBuilder out = new StringBuilder(2048 + 700 * profiles.size());
        out.append("<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"").append(width)
                .append("\" height=\"").append(HEIGHT)
                .append("\" viewBox=\"0 0 ").append(width).append(' ').append(HEIGHT)
                .append("\" role=\"img\" aria-label=\"Esquema de la via ").append(esc(schematic.trackName())).append("\">");
        out.append("<style>"
                + ".track{stroke:#263238;stroke-width:4}"
                + ".pole{stroke:#455a64;stroke-width:3}"
                + ".base{fill:#455a64}"
                + ".arm{stroke:#1565c0;stroke-width:2.5;fill:none}"
                + ".wire{fill:#1565c0}"
                + ".code{font:600 12px sans-serif;fill:#263238;text-anchor:middle}"
                + ".small{font:10px sans-serif;fill:#546e7a;text-anchor:middle}"
                + ".name{font:600 11px sans-serif;fill:#263238}"
                + ".kp{font:11px monospace;fill:#37474f;text-anchor:middle}"
                + ".sect{font:600 10px sans-serif;fill:#2e7d32;text-anchor:middle}"
                + ".arm-label{font:10px sans-serif;fill:#1565c0}"
                + ".disc{fill:#f9a825;stroke:#6d4c41;stroke-width:1.5}"
                + ".disc-label{font:600 10px sans-serif;fill:#6d4c41}"
                + ".ins{fill:#c62828}"
                + ".ins-label{font:600 10px sans-serif;fill:#c62828;text-anchor:middle}"
                + "</style>");
        int lineStart = MARGIN / 3;
        line(out, "track", lineStart, LINE_Y, width - lineStart, LINE_Y);
        out.append("<text class=\"name\" x=\"").append(lineStart).append("\" y=\"").append(LINE_Y - 8).append("\">")
                .append(esc(schematic.trackName())).append("</text>");
        for (int i = 0; i < profiles.size(); i++) {
            pole(out, profiles.get(i), xOf(i));
        }
        for (InsulatorMark mark : schematic.sectionInsulators()) {
            insulator(out, mark, insulatorX(profiles, mark.kp()), schematic.trackName());
        }
        out.append("</svg>");
        return out.toString();
    }

    private static void pole(StringBuilder out, ProfileNode profile, double x) {
        out.append("<g id=\"pole-").append(profile.id()).append("\"><title>").append(esc(poleTitle(profile))).append("</title>");
        line(out, "pole", x, POLE_TOP, x, LINE_Y);
        out.append("<circle class=\"base\" cx=\"").append(fmt(x)).append("\" cy=\"").append(LINE_Y).append("\" r=\"4\"/>");

        int direction = armDirection(profile);
        List<CantileverArm> arms = profile.cantilevers();
        for (int k = 0; k < arms.size(); k++) {
            CantileverArm arm = arms.get(k);
            double y = POLE_TOP + 8 + k * 16.0;
            double tip = x + direction * ARM_LENGTH;
            out.append("<g id=\"arm-").append(arm.id()).append("\"><title>").append(esc(armTitle(arm))).append("</title>");
            out.append("<path class=\"arm\" d=\"M").append(fmt(x)).append(' ').append(fmt(y))
                    .append(" L").append(fmt(tip)).append(' ').append(fmt(y + 10)).append("\"/>");
            out.append("<circle class=\"wire\" cx=\"").append(fmt(tip)).append("\" cy=\"").append(fmt(y + 10)).append("\" r=\"2.5\"/>");
            if (arm.type() != null) {
                text(out, "arm-label", tip + direction * 4, y + 13, direction < 0 ? "end" : "start", arm.type());
            }
            out.append("</g>");
        }

        text(out, "code", x, POLE_TOP - 26, null, profile.code());
        String under = join(" · ", profile.poleType(), profile.profileStatus());
        if (!under.isEmpty()) {
            text(out, "small", x, POLE_TOP - 12, null, under);
        }
        text(out, "kp", x, LINE_Y + 22, null, "KP " + (profile.kp() == null ? "?" : profile.kp()));
        if (!profile.sectionings().isEmpty()) {
            text(out, "sect", x, LINE_Y + 38, null, String.join(", ", profile.sectionings()));
        }

        DisconnectorMark disconnector = profile.disconnector();
        if (disconnector != null) {
            double cx = x - direction * 16;
            double cy = LINE_Y - 60;
            out.append("<g id=\"disconnector-").append(disconnector.id()).append("\"><title>")
                    .append(esc(disconnectorTitle(disconnector))).append("</title>");
            out.append("<path class=\"disc\" d=\"M").append(fmt(cx)).append(' ').append(fmt(cy - 8))
                    .append(" L").append(fmt(cx + 8)).append(' ').append(fmt(cy))
                    .append(" L").append(fmt(cx)).append(' ').append(fmt(cy + 8))
                    .append(" L").append(fmt(cx - 8)).append(' ').append(fmt(cy)).append(" Z\"/>");
            text(out, "disc-label", cx - direction * 12, cy + 4, direction < 0 ? "start" : "end", disconnector.name());
            if (disconnector.station() != null) {
                text(out, "small", cx - direction * 12, cy + 16, direction < 0 ? "start" : "end", disconnector.station());
            }
            out.append("</g>");
        }
        out.append("</g>");
    }

    private static void insulator(StringBuilder out, InsulatorMark mark, double x, String trackName) {
        out.append("<g id=\"insulator-").append(mark.id()).append("\"><title>").append(esc(insulatorTitle(mark))).append("</title>");
        out.append("<rect class=\"ins\" x=\"").append(fmt(x - 5)).append("\" y=\"").append(LINE_Y - 9)
                .append("\" width=\"10\" height=\"18\" rx=\"2\"/>");
        text(out, "ins-label", x, LINE_Y + 58, null, mark.name());
        String other = otherTrack(mark, trackName);
        String detail = join(" · ", installation(mark.installationType()), other == null ? null : "↔ " + other, mark.station());
        if (!detail.isEmpty()) {
            text(out, "small", x, LINE_Y + 72, null, detail);
        }
        if (!mark.switches().isEmpty()) {
            text(out, "small", x, LINE_Y + 86, null, mark.switches().stream().map(SchematicDrawing::switchLabel)
                    .collect(Collectors.joining(", ")));
        }
        out.append("</g>");
    }

    static String poleTitle(ProfileNode profile) {
        List<String> parts = new ArrayList<>();
        parts.add(profile.code() == null ? "Perfil" : "Perfil " + profile.code());
        parts.add("KP " + (profile.kp() == null ? "?" : profile.kp()));
        if (profile.orderInTrack() != null) {
            parts.add("orden " + profile.orderInTrack());
        }
        addIf(parts, "vano ", profile.span());
        addIf(parts, "poste ", profile.poleType());
        addIf(parts, "apoyo ", profile.supportType());
        addIf(parts, "estado ", profile.profileStatus());
        addIf(parts, "distancia carril-poste ", profile.railPoleDistance());
        if (!profile.sectionings().isEmpty()) {
            parts.add("seccionamiento " + String.join(", ", profile.sectionings()));
        }
        return String.join(" · ", parts);
    }

    static String armTitle(CantileverArm arm) {
        List<String> parts = new ArrayList<>();
        parts.add(arm.type() == null ? "Mensula" : "Mensula " + arm.type());
        addIf(parts, "descentramiento ", arm.stagger());
        addIf(parts, "altura hilo ", arm.cwHeight());
        addIf(parts, "altura catenaria ", arm.catenaryHeight());
        if (arm.steadyArmType() != null || arm.steadyArmLength() != null) {
            parts.add(join(" ", "brazo", arm.steadyArmType(), arm.steadyArmLength() == null ? null : arm.steadyArmLength() + " mm"));
        }
        return String.join(" · ", parts);
    }

    static String disconnectorTitle(DisconnectorMark disconnector) {
        List<String> parts = new ArrayList<>();
        parts.add(disconnector.name() == null ? "Seccionador" : "Seccionador " + disconnector.name());
        if (disconnector.onLoad() != null) {
            parts.add(Boolean.TRUE.equals(disconnector.onLoad()) ? "en carga" : "sin carga");
        }
        addIf(parts, "funcion ", disconnector.function());
        addIf(parts, "estacion ", disconnector.station());
        return String.join(" · ", parts);
    }

    static String insulatorTitle(InsulatorMark mark) {
        List<String> parts = new ArrayList<>();
        parts.add(mark.name() == null ? "Aislador de seccion" : "Aislador de seccion " + mark.name());
        parts.add("KP " + (mark.kp() == null ? "?" : mark.kp()));
        addIf(parts, "", installation(mark.installationType()));
        if (mark.track() != null || mark.connectedTrack() != null) {
            parts.add(join(" ↔ ", mark.track(), mark.connectedTrack()));
        }
        addIf(parts, "estacion ", mark.station());
        if (Boolean.FALSE.equals(mark.enabled())) {
            parts.add("inactivo");
        }
        if (!mark.switches().isEmpty()) {
            parts.add("agujas " + mark.switches().stream()
                    .map(aguja -> switchLabel(aguja) + (aguja.kp() == null ? "" : " KP " + aguja.kp()))
                    .collect(Collectors.joining(", ")));
        }
        return String.join(" · ", parts);
    }

    static String installation(String installationType) {
        if (installationType == null) {
            return null;
        }
        return switch (installationType) {
            case "TRACK_CONNECTION" -> "conexion de vias";
            case "IN_TRACK" -> "en via";
            default -> installationType;
        };
    }

    /** La otra via del aislador: la que no es la dibujada. */
    static String otherTrack(InsulatorMark mark, String trackName) {
        if (mark.connectedTrack() != null && !mark.connectedTrack().equals(trackName)) {
            return mark.connectedTrack();
        }
        if (mark.track() != null && !mark.track().equals(trackName)) {
            return mark.track();
        }
        return null;
    }

    private static String switchLabel(SwitchMark aguja) {
        String code = aguja.code() == null ? "aguja" : aguja.code();
        return aguja.turnoutDenominator() == null ? code : code + " 1:" + aguja.turnoutDenominator();
    }

    private static void addIf(List<String> parts, String prefix, String value) {
        if (value != null && !value.isBlank()) {
            parts.add(prefix + value);
        }
    }

    private static String join(String separator, String... values) {
        return java.util.Arrays.stream(values).filter(Objects::nonNull).filter(v -> !v.isBlank())
                .collect(Collectors.joining(separator));
    }

    private static void line(StringBuilder out, String cls, double x1, double y1, double x2, double y2) {
        out.append("<line class=\"").append(cls).append("\" x1=\"").append(fmt(x1)).append("\" y1=\"").append(fmt(y1))
                .append("\" x2=\"").append(fmt(x2)).append("\" y2=\"").append(fmt(y2)).append("\"/>");
    }

    private static void text(StringBuilder out, String cls, double x, double y, String anchor, String content) {
        out.append("<text class=\"").append(cls).append("\" x=\"").append(fmt(x)).append("\" y=\"").append(fmt(y)).append('"');
        if (anchor != null) {
            out.append(" text-anchor=\"").append(anchor).append('"');
        }
        out.append('>').append(esc(content)).append("</text>");
    }

    private static String fmt(double value) {
        return String.format(Locale.ROOT, "%.1f", value);
    }

    private static BigDecimal number(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException notANumber) {
            return null;
        }
    }

    /** Solo {@code < > & " '}: con UTF-8 no hay que convertir acentos en entidades. */
    private static String esc(String value) {
        return value == null ? "" : HtmlUtils.htmlEscape(value, UTF_8);
    }
}
