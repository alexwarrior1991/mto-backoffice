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

    /**
     * La via como opcion de un desplegable: la del catalogo si esta, y si no (sin
     * {@code config-read}, o una via que ya no existe) una con el id, para que el valor leido se vea.
     */
    public RefItem trackRef(Long id) {
        return refOf(id, configuration == null ? null : configuration.trackRef(id).orElse(null));
    }

    public RefItem stationRef(Long id) {
        return refOf(id, configuration == null ? null : configuration.stationRef(id).orElse(null));
    }

    public RefItem packageRef(Long id) {
        return refOf(id, configuration == null ? null : configuration.packageRef(id).orElse(null));
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

    private static RefItem refOf(Long id, RefItem found) {
        if (id == null) {
            return null;
        }
        return found != null ? found : new RefItem(id, idOf(id));
    }

    private static String idOf(Object id) {
        return id == null ? "" : "#" + id;
    }
}
