package com.alejandro.mtobackoffice.ui.resources;

import com.alejandro.mtobackoffice.configuration.security.SecurityRoles;
import com.alejandro.mtobackoffice.ui.master.RefItem;
import com.alejandro.mtobackoffice.ui.master.ReferenceCatalog;
import com.vaadin.flow.spring.security.AuthenticationContext;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Los nombres de lo que mto-resources solo guarda como id: los EP de una unidad y, en la flota, las
 * vias y estaciones de mto-configuration, pedidos a su servicio con el token de la persona una vez
 * por pantalla ({@link ReferenceCatalog}), como {@code MaintenanceNames}. Sin {@code config-read} no
 * se llama (responderia 403): se pinta {@code #id}, los desplegables quedan vacios y lo leido no se
 * manda a vaciar.
 */
public final class ResourcesNames {

    private final ReferenceCatalog configuration;

    private ResourcesNames(ReferenceCatalog configuration) {
        this.configuration = configuration;
    }

    public static ResourcesNames of(ResourcesClients clients, AuthenticationContext authentication) {
        return new ResourcesNames(authentication.hasRole(SecurityRoles.CONFIG_READ)
                ? new ReferenceCatalog(clients.packages(), clients.stations(), clients.tracks(), clients.companies())
                : null);
    }

    public boolean readsConfiguration() {
        return configuration != null;
    }

    public String packageName(Long id) {
        return configuration == null ? idOf(id) : configuration.packageName(id);
    }

    /** Los EP de una unidad por su nombre, en el orden de sus ids. */
    public String packageNames(Collection<Long> ids) {
        return ids == null ? "" : ids.stream().sorted().map(this::packageName).collect(Collectors.joining(", "));
    }

    /** El EP para un desplegable: con su nombre, o con el id si no se puede nombrar, nunca {@code null}. */
    public RefItem packageRef(Long id) {
        if (id == null) {
            return null;
        }
        return configuration == null ? new RefItem(id, idOf(id)) : configuration.packageRef(id).orElse(new RefItem(id, idOf(id)));
    }

    /** Para un desplegable; vacio sin {@code config-read}. */
    public List<RefItem> packages() {
        return configuration == null ? List.of() : configuration.packages();
    }

    public String trackName(Long id) {
        return configuration == null ? idOf(id) : configuration.trackName(id);
    }

    public String stationName(Long id) {
        return configuration == null ? idOf(id) : configuration.stationName(id);
    }

    public RefItem trackRef(Long id) {
        if (id == null) {
            return null;
        }
        return configuration == null ? new RefItem(id, idOf(id)) : configuration.trackRef(id).orElse(new RefItem(id, idOf(id)));
    }

    public RefItem stationRef(Long id) {
        if (id == null) {
            return null;
        }
        return configuration == null ? new RefItem(id, idOf(id)) : configuration.stationRef(id).orElse(new RefItem(id, idOf(id)));
    }

    /** Las vias: las de un EP si se eligio, o todas; vacio sin {@code config-read}. */
    public List<RefItem> tracks(Long packageId) {
        if (configuration == null) {
            return List.of();
        }
        return packageId == null ? configuration.tracks() : configuration.tracksIn(packageId);
    }

    /** Las estaciones, como {@link #tracks}. */
    public List<RefItem> stations(Long packageId) {
        if (configuration == null) {
            return List.of();
        }
        return packageId == null ? configuration.stations() : configuration.stationsIn(packageId);
    }

    private static String idOf(Long id) {
        return id == null ? "" : "#" + id;
    }
}
