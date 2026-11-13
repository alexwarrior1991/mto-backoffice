package com.alejandro.mtobackoffice.ui.notification;

import com.alejandro.mtobackoffice.client.dto.notification.ActivitySeverity;
import com.alejandro.mtobackoffice.client.dto.notification.AudienceKind;
import com.alejandro.mtobackoffice.client.dto.notification.BroadcastChannel;
import com.alejandro.mtobackoffice.client.dto.notification.BroadcastRequest;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Lo que el Binder del aviso general lee y escribe; las propiedades se llaman como los campos de
 * {@code BroadcastRequest} en el servicio, para que sus errores caigan en su campo. Las audiencias se
 * escriben una por linea.
 */
public class BroadcastForm {

    /** Los topes de {@code BroadcastRequest} en el servicio. */
    public static final int TITLE_MAX = 255;
    public static final int BODY_MAX = 4000;
    public static final int LINK_MAX = 500;

    private String title = "";
    private String body = "";
    private String link = "";
    private ActivitySeverity severity = ActivitySeverity.INFO;
    private String audiences = "";
    private Set<BroadcastChannel> channels = EnumSet.of(BroadcastChannel.INBOX);

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public ActivitySeverity getSeverity() {
        return severity;
    }

    public void setSeverity(ActivitySeverity severity) {
        this.severity = severity;
    }

    public String getAudiences() {
        return audiences;
    }

    public void setAudiences(String audiences) {
        this.audiences = audiences;
    }

    public Set<BroadcastChannel> getChannels() {
        return channels;
    }

    public void setChannels(Set<BroadcastChannel> channels) {
        this.channels = channels;
    }

    /** Las audiencias escritas, una por linea: sin blancos alrededor, sin lineas vacias y sin repetidas, en su orden. */
    public static List<String> audiencesOf(String text) {
        Set<String> lines = new LinkedHashSet<>();
        Arrays.stream((text == null ? "" : text).split("\\R")).map(String::trim).filter(line -> !line.isEmpty()).forEach(lines::add);
        return List.copyOf(lines);
    }

    /** Lo primero que el servicio rechazaria de las audiencias escritas; vacio si valen. */
    public static Optional<String> audiencesProblem(String text) {
        List<String> lines = audiencesOf(text);
        if (lines.isEmpty()) {
            return Optional.of("Al menos una audiencia");
        }
        return lines.stream().map(AudienceKind::problemOf).filter(problem -> problem != null).findFirst();
    }

    /** Lo vacio no viaja: sin cuerpo ni enlace el aviso no los lleva. Los canales, por su nombre en el servicio y en su orden. */
    public BroadcastRequest toRequest() {
        return new BroadcastRequest(title.trim(), blankToNull(body), blankToNull(link), severity, audiencesOf(audiences),
                Arrays.stream(BroadcastChannel.values()).filter(channels::contains).map(BroadcastChannel::wire).toList());
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
