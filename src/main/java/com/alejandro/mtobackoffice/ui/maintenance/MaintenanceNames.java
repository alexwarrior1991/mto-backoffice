package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.configuration.security.SecurityRoles;
import com.alejandro.mtobackoffice.ui.master.RefItem;
import com.alejandro.mtobackoffice.ui.master.ReferenceCatalog;
import com.vaadin.flow.spring.security.AuthenticationContext;

import java.util.List;

/**
 * Los nombres de lo que mto-maintenance solo guarda como id: vias, estaciones y paquetes de
 * mto-configuration. El servicio no los copia (no re-modela los datos maestros), asi que la pantalla
 * los pide a su servicio con el token de la persona, una vez por pantalla ({@link ReferenceCatalog}).
 *
 * <p>Sin {@code config-read} no se llama a mto-configuration: responderia 403 y la notificacion
 * sobraria. Se pinta el id ({@code #12}) y los desplegables de vias y paquetes salen vacios. Los
 * perfiles de mantenimiento del realm llevan {@code config-read} precisamente para esto.</p>
 */
public final class MaintenanceNames {

    private final ReferenceCatalog configuration;

    private MaintenanceNames(ReferenceCatalog configuration) {
        this.configuration = configuration;
    }

    public static MaintenanceNames of(MaintenanceClients clients, AuthenticationContext authentication) {
        return new MaintenanceNames(authentication.hasRole(SecurityRoles.CONFIG_READ)
                ? new ReferenceCatalog(clients.packages(), clients.stations(), clients.tracks(), clients.companies())
                : null);
    }

    public boolean readsConfiguration() {
        return configuration != null;
    }

    public String trackName(Long id) {
        return configuration == null ? idOf(id) : configuration.trackName(id);
    }

    public String stationName(Long id) {
        return configuration == null ? idOf(id) : configuration.stationName(id);
    }

    public String packageName(Long id) {
        return configuration == null ? idOf(id) : configuration.packageName(id);
    }

    /** Para un filtro o un desplegable; vacio sin {@code config-read}. */
    public List<RefItem> tracks() {
        return configuration == null ? List.of() : configuration.tracks();
    }

    public List<RefItem> packages() {
        return configuration == null ? List.of() : configuration.packages();
    }

    public List<RefItem> stations() {
        return configuration == null ? List.of() : configuration.stations();
    }

    private static String idOf(Object id) {
        return id == null ? "" : "#" + id;
    }
}
