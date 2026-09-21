package com.alejandro.mtobackoffice.ui.master;

import com.alejandro.mtobackoffice.client.configuration.BusinessEntityClient;
import com.alejandro.mtobackoffice.client.configuration.ExecutionPackageClient;
import com.alejandro.mtobackoffice.client.configuration.MasterClient;
import com.alejandro.mtobackoffice.client.configuration.StationClient;
import com.alejandro.mtobackoffice.client.configuration.TrackClient;
import com.alejandro.mtobackoffice.client.dto.master.BusinessEntityDto;
import com.alejandro.mtobackoffice.client.dto.master.MasterDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.ui.support.UiErrors;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Los nombres de los maestros a los que apuntan los ids de una fila ({@code executionPackageId},
 * {@code stationId}, {@code trackId}, {@code companyId}): el servicio devuelve ids, y una persona
 * quiere leer nombres y elegir de un desplegable.
 *
 * <p>Se carga perezosamente y una sola vez por pantalla: paquetes, estaciones y vias son pocos
 * cientos de filas y desde que las listas van sin hijos pesan poco. Los perfiles no estan aqui
 * (son miles): quien tenga que elegir uno lo busca en el servidor ({@link Pickers#lazyProfile}).</p>
 */
public final class ReferenceCatalog {

    /** Mas que cualquiera de estos maestros; por debajo del tope de Spring Data. */
    static final int ALL = 1000;

    private final ExecutionPackageClient packages;
    private final StationClient stations;
    private final TrackClient tracks;
    private final BusinessEntityClient companies;

    private List<RefItem> packageItems;
    private List<RefItem> stationItems;
    private List<RefItem> trackItems;
    private List<RefItem> companyItems;

    public ReferenceCatalog(ExecutionPackageClient packages, StationClient stations, TrackClient tracks, BusinessEntityClient companies) {
        this.packages = packages;
        this.stations = stations;
        this.tracks = tracks;
        this.companies = companies;
    }

    public List<RefItem> packages() {
        if (packageItems == null) {
            packageItems = load(packages, dto -> new RefItem(dto.getId(), dto.getName()));
        }
        return packageItems;
    }

    public List<RefItem> stations() {
        if (stationItems == null) {
            stationItems = load(stations, dto -> new RefItem(dto.getId(), withPackage(dto.getName(), dto.getExecutionPackageId())));
        }
        return stationItems;
    }

    /** Las vias llevan el paquete en la etiqueta: {@code TRACK 1} existe en varios paquetes. */
    public List<RefItem> tracks() {
        if (trackItems == null) {
            trackItems = load(tracks, dto -> new RefItem(dto.getId(), withPackage(dto.getName(), dto.getExecutionPackageId())));
        }
        return trackItems;
    }

    public List<RefItem> companies() {
        if (companyItems == null) {
            try {
                companyItems = companies.findAll().stream().map(dto -> new RefItem(dto.id(), dto.label())).toList();
            } catch (BackofficeApiException failure) {
                UiErrors.show(failure);
                companyItems = List.of();
            }
        }
        return companyItems;
    }

    public String packageName(Long id) {
        return labelOf(packages(), id);
    }

    public String stationName(Long id) {
        return labelOf(stations(), id);
    }

    public String trackName(Long id) {
        return labelOf(tracks(), id);
    }

    public String companyName(Long id) {
        return labelOf(companies(), id);
    }

    public Optional<RefItem> packageRef(Long id) {
        return find(packages(), id);
    }

    public Optional<RefItem> stationRef(Long id) {
        return find(stations(), id);
    }

    public Optional<RefItem> trackRef(Long id) {
        return find(tracks(), id);
    }

    public Optional<RefItem> companyRef(Long id) {
        return find(companies(), id);
    }

    /** Tras un alta o una baja los desplegables tienen que volver a pedirse. */
    public void invalidate() {
        packageItems = null;
        stationItems = null;
        trackItems = null;
        companyItems = null;
    }

    private String withPackage(String name, Long packageId) {
        String packageName = packageName(packageId);
        return packageName.isEmpty() ? name : name + " (" + packageName + ")";
    }

    private static Optional<RefItem> find(List<RefItem> items, Long id) {
        return id == null ? Optional.empty() : items.stream().filter(item -> id.equals(item.id())).findFirst();
    }

    /** El nombre, o el id entre almohadillas si no esta (borrado, o fuera del catalogo cargado). */
    private static String labelOf(List<RefItem> items, Long id) {
        if (id == null) {
            return "";
        }
        return find(items, id).map(RefItem::label).orElse("#" + id);
    }

    private <D extends MasterDto> List<RefItem> load(MasterClient<D> client, Function<D, RefItem> mapper) {
        try {
            return client.filter(0, ALL, List.of("name,asc"), Map.of()).content().stream().map(mapper).toList();
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
            return List.of();
        }
    }

    static String labelOfCompany(BusinessEntityDto dto) {
        return dto.label();
    }
}
