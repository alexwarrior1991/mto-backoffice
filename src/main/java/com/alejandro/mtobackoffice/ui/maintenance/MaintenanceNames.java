package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.dto.resources.ParkingSiteDto;
import com.alejandro.mtobackoffice.client.dto.resources.ParkingSiteFilter;
import com.alejandro.mtobackoffice.client.dto.stock.ProjectSummaryDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.error.NotFoundApiException;
import com.alejandro.mtobackoffice.configuration.security.ResourcesRoles;
import com.alejandro.mtobackoffice.configuration.security.SecurityRoles;
import com.alejandro.mtobackoffice.configuration.security.StockRoles;
import com.alejandro.mtobackoffice.ui.master.RefItem;
import com.alejandro.mtobackoffice.ui.master.ReferenceCatalog;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.spring.security.AuthenticationContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Los nombres de lo que mto-maintenance solo guarda como id: vias, estaciones y paquetes de
 * mto-configuration, almacenes y proyectos de mto-stock, y los sitios de aparcamiento de
 * mto-resources. El servicio no los copia (no re-modela los datos maestros), asi que la pantalla los
 * pide a su servicio con el token de la persona: vias, estaciones y paquetes una vez por pantalla
 * ({@link ReferenceCatalog}); almacenes y proyectos por id, recordados mientras la pantalla vive; los
 * sitios, todos los activos de una vez, la primera vez que hacen falta.
 *
 * <p>Sin {@code config-read}, {@code stock-read} o {@code resources-read} no se llama a ese servicio:
 * responderia 403 y la notificacion sobraria. Se pinta el id ({@code #12}, o el principio del UUID) y
 * los desplegables de ese servicio salen vacios. Los perfiles de mantenimiento del realm llevan los tres
 * roles de lectura precisamente para esto.</p>
 */
public final class MaintenanceNames {

    /** Los sitios de aparcamiento de una pantalla: todos los activos en una pagina. */
    static final int ALL_SITES = 200;

    private final ReferenceCatalog configuration;
    private final MaintenanceClients stock;
    private final MaintenanceClients resources;
    private List<ParkingSiteDto> parkingSites;
    private boolean resourcesFailureShown;
    private final Map<UUID, String> warehouses = new HashMap<>();
    private final Map<UUID, ProjectSummaryDto> projects = new HashMap<>();
    private boolean stockFailureShown;

    private MaintenanceNames(ReferenceCatalog configuration, MaintenanceClients stock, MaintenanceClients resources) {
        this.configuration = configuration;
        this.stock = stock;
        this.resources = resources;
    }

    public static MaintenanceNames of(MaintenanceClients clients, AuthenticationContext authentication) {
        return new MaintenanceNames(authentication.hasRole(SecurityRoles.CONFIG_READ)
                ? new ReferenceCatalog(clients.packages(), clients.stations(), clients.tracks(), clients.companies())
                : null,
                authentication.hasRole(StockRoles.STOCK_READ) ? clients : null,
                authentication.hasRole(ResourcesRoles.RESOURCES_READ) ? clients : null);
    }

    public boolean readsConfiguration() {
        return configuration != null;
    }

    public boolean readsStock() {
        return stock != null;
    }

    /** La dotacion, los vehiculos y los sitios de un turno se eligen y se nombran con {@code resources-read}. */
    public boolean readsResources() {
        return resources != null;
    }

    /**
     * Los sitios de aparcamiento activos de mto-resources, pedidos una vez (son pocos: un catalogo de
     * sitios conocidos); vacio sin {@code resources-read} o si no responde, que se notifica una vez.
     */
    public List<ParkingSiteDto> parkingSites() {
        if (resources == null) {
            return List.of();
        }
        if (parkingSites == null) {
            try {
                parkingSites = resources.parkingSites().search(new ParkingSiteFilter(null, null, null, null, null, null, true), 0, ALL_SITES,
                        List.of("code,asc")).content();
            } catch (BackofficeApiException failure) {
                parkingSites = List.of();
                if (!resourcesFailureShown) {
                    resourcesFailureShown = true;
                    UiErrors.show(failure);
                }
            }
        }
        return parkingSites;
    }

    /** El sitio donde se dejo un vehiculo; el id si no se puede nombrar (o ya no esta activo). */
    public String parkingSiteName(UUID id) {
        if (id == null) {
            return "";
        }
        return parkingSites().stream().filter(site -> id.equals(site.id())).findFirst().map(ParkingSiteDto::code).orElse(idOf(id));
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

    /** Las vias de un EP, para lo que se genera en una orden, que es de uno solo; vacio sin {@code config-read}. */
    public List<RefItem> tracksIn(Long packageId) {
        return configuration == null ? List.of() : configuration.tracksIn(packageId);
    }

    public List<RefItem> stationsIn(Long packageId) {
        return configuration == null ? List.of() : configuration.stationsIn(packageId);
    }

    public String warehouseName(UUID id) {
        if (id == null) {
            return "";
        }
        if (stock == null) {
            return idOf(id);
        }
        return warehouses.computeIfAbsent(id, key -> fromStock(() -> stock.warehouses().findById(key).summary().label(), idOf(key)));
    }

    /** El proyecto de almacen, para pintarlo y para el desplegable; {@code null} si no hay o no se puede leer. */
    public ProjectSummaryDto project(UUID id) {
        if (id == null || stock == null) {
            return null;
        }
        return projects.computeIfAbsent(id, key -> fromStock(() -> stock.projects().findById(key).summary(), null));
    }

    /**
     * El proyecto para un formulario: el de mto-stock o, si no se puede nombrar (sin {@code stock-read},
     * o el almacen no responde), uno con solo su id, como {@link #trackRef}. Nunca {@code null} para un
     * id: un formulario que lo leyera vacio lo mandaria a vaciar.
     */
    public ProjectSummaryDto projectRef(UUID id) {
        if (id == null) {
            return null;
        }
        ProjectSummaryDto project = project(id);
        return project != null ? project : new ProjectSummaryDto(id, idOf(id), null, null);
    }

    public String projectName(UUID id) {
        ProjectSummaryDto project = project(id);
        return project != null ? project.label() : id == null ? "" : idOf(id);
    }

    /** Un fallo de mto-stock se notifica una vez por pantalla; lo que no existe ya no se pide mas. */
    private <T> T fromStock(Supplier<T> lookup, T fallback) {
        try {
            return lookup.get();
        } catch (NotFoundApiException missing) {
            return fallback;
        } catch (BackofficeApiException failure) {
            if (!stockFailureShown) {
                stockFailureShown = true;
                UiErrors.show(failure);
            }
            return fallback;
        }
    }

    private static RefItem refOf(Long id, RefItem found) {
        if (id == null) {
            return null;
        }
        return found != null ? found : new RefItem(id, idOf(id));
    }

    private static String idOf(Object id) {
        if (id == null) {
            return "";
        }
        return id instanceof UUID uuid ? "#" + uuid.toString().substring(0, 8) : "#" + id;
    }
}
