package com.alejandro.mtobackoffice.ui.master;

import com.alejandro.mtobackoffice.client.dto.master.TrackSchematicDto;
import com.alejandro.mtobackoffice.client.dto.master.TrackSchematicDto.ProfileNode;
import com.vaadin.flow.component.Svg;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.List;
import java.util.Objects;

/**
 * La ventana con el esquema de una via: la cabecera (via y paquete), un resumen con los recuentos y
 * las estaciones, y el dibujo de {@link SchematicDrawing} en un {@code Scroller} horizontal, porque
 * una via de 600 perfiles mide 600 pasos.
 *
 * <p>Recibe la proyeccion que devolvio el servicio y no vuelve a pedir nada: lo que se ve es lo
 * que llego en esa llamada. Una via sin perfiles lo dice en vez de dibujar una linea vacia.</p>
 */
public class TrackSchematicDialog extends Dialog {

    public static final String SUMMARY_ID = "schematic-summary";
    public static final String EMPTY_ID = "schematic-empty";
    public static final String LEGEND_ID = "schematic-legend";

    static final String LEGEND = "Un poste por perfil, a distancia uniforme y en el orden fisico de la via, con el codigo encima "
            + "y el KP debajo; los brazos azules son las mensulas (con su tipo), el rombo un seccionador y la marca roja "
            + "sobre la via un aislador de seccion. El detalle de cada elemento sale al pasar por encima.";

    public TrackSchematicDialog(TrackSchematicDto schematic) {
        setHeaderTitle(title(schematic));
        setWidth("min(96vw, 110rem)");
        setHeight("min(92vh, 48rem)");
        setResizable(true);
        setDraggable(true);

        Span summary = new Span(summary(schematic));
        summary.setId(SUMMARY_ID);
        VerticalLayout content = new VerticalLayout(summary);
        content.setPadding(false);
        content.setSizeFull();

        if (schematic.profiles().isEmpty()) {
            Span empty = new Span("Esta via no tiene perfiles: no hay nada que dibujar.");
            empty.setId(EMPTY_ID);
            content.add(empty);
        } else {
            Svg drawing = new Svg(SchematicDrawing.svg(schematic));
            Scroller scroller = new Scroller(drawing, Scroller.ScrollDirection.HORIZONTAL);
            scroller.setSizeFull();
            Span legend = new Span(LEGEND);
            legend.setId(LEGEND_ID);
            legend.getStyle().set("font-size", "var(--lumo-font-size-s)").set("color", "var(--lumo-secondary-text-color)");
            content.add(scroller, legend);
            content.expand(scroller);
        }
        add(content);
        getFooter().add(new Button("Cerrar", click -> close()));
    }

    static String title(TrackSchematicDto schematic) {
        String name = schematic.trackName() == null ? "#" + schematic.trackId() : schematic.trackName();
        return "Esquema · " + name
                + (schematic.executionPackageName() == null ? "" : " (" + schematic.executionPackageName() + ")");
    }

    static String summary(TrackSchematicDto schematic) {
        List<ProfileNode> profiles = schematic.profiles();
        long cantilevers = profiles.stream().mapToLong(profile -> profile.cantilevers().size()).sum();
        long disconnectors = profiles.stream().map(ProfileNode::disconnector).filter(Objects::nonNull).count();
        String stations = schematic.stations().isEmpty() ? "sin estaciones" : "Estaciones: " + String.join(", ", schematic.stations());
        return count(profiles.size(), "perfil", "perfiles")
                + " · " + count(cantilevers, "mensula", "mensulas")
                + " · " + count(disconnectors, "seccionador", "seccionadores")
                + " · " + count(schematic.sectionInsulators().size(), "aislador", "aisladores")
                + " · " + stations
                + (Boolean.FALSE.equals(schematic.enabled()) ? " · via inactiva" : "");
    }

    private static String count(long n, String singular, String plural) {
        return n + " " + (n == 1 ? singular : plural);
    }
}
