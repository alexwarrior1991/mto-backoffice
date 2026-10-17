package com.alejandro.mtobackoffice.ui.resources;

import com.alejandro.mtobackoffice.client.dto.MergePatch;
import com.alejandro.mtobackoffice.client.dto.resources.DrivingLicence;
import com.alejandro.mtobackoffice.client.dto.resources.RailGearType;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleCapability;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleCategory;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleMobility;
import com.alejandro.mtobackoffice.client.dto.resources.VehiclePower;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleTypeDto;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleTypeRequest;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleTypeUpdateRequest;
import com.alejandro.mtobackoffice.ui.support.Changes;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Modelo mutable del editor de un modelo de vehiculo. Son muchas cifras, asi que se guardan por el
 * nombre de su campo en la peticion ({@link Figure}), que es tambien el que trae un error del servicio.
 *
 * <p>Lo que la movilidad no admite no viaja: las cifras de via en uno de solo carretera, las de bivial
 * fuera de una bivial, las de carretera en uno de solo via; y las cifras de una capacidad, sin ella. Al
 * modificar, quitar una capacidad vacia sus cifras.</p>
 */
public class VehicleTypeForm {

    /** Donde va una cifra en el editor: lo general, la via, la bivial, la carretera o una capacidad. */
    enum Section {
        GENERAL, TRACK, ROAD_RAIL, ROAD, CAPABILITY
    }

    /**
     * Una cifra del modelo: su campo, su etiqueta y la columna del servicio ({@code decimals} 0 es un
     * entero, con su minimo). {@code capability}, la capacidad sin la que no existe.
     */
    record Figure(String field, String label, Section section, int integers, int decimals, int min, VehicleCapability capability) {

        boolean decimal() {
            return decimals > 0;
        }
    }

    static final List<Figure> FIGURES = List.of(
            new Figure("lengthM", "Largo (m)", Section.GENERAL, 4, 2, 0, null),
            new Figure("widthM", "Ancho (m)", Section.GENERAL, 3, 2, 0, null),
            new Figure("heightM", "Alto (m)", Section.GENERAL, 3, 2, 0, null),
            new Figure("tareT", "Tara (t)", Section.GENERAL, 6, 2, 0, null),
            new Figure("maxGrossT", "Masa maxima (t)", Section.GENERAL, 6, 2, 0, null),
            new Figure("maxAxleLoadT", "Carga por eje (t)", Section.GENERAL, 4, 2, 0, null),
            new Figure("axles", "Ejes", Section.GENERAL, 0, 0, 1, null),
            new Figure("seats", "Plazas", Section.GENERAL, 0, 0, 0, null),
            new Figure("payloadT", "Carga util (t)", Section.GENERAL, 6, 2, 0, null),
            new Figure("trackGaugeMm", "Ancho de via (mm)", Section.TRACK, 0, 0, 1, null),
            new Figure("maxSpeedTrackWorkingKmh", "Velocidad de trabajo (km/h)", Section.TRACK, 0, 0, 0, null),
            new Figure("maxSpeedTrackTransportKmh", "Velocidad de transporte (km/h)", Section.TRACK, 0, 0, 0, null),
            new Figure("maxGradientPermille", "Pendiente maxima (por mil)", Section.TRACK, 4, 1, 0, null),
            new Figure("minCurveRadiusM", "Radio minimo (m)", Section.TRACK, 0, 0, 0, null),
            new Figure("maxWorkingCantMm", "Peralte maximo de trabajo (mm)", Section.TRACK, 0, 0, 0, null),
            new Figure("onTrackingMinutes", "Minutos para encarrilar", Section.ROAD_RAIL, 0, 0, 0, null),
            new Figure("maxSpeedRoadKmh", "Velocidad por carretera (km/h)", Section.ROAD, 0, 0, 0, null),
            new Figure("craneCapacityT", "Grua: capacidad (t)", Section.CAPABILITY, 4, 2, 0, VehicleCapability.CRANE),
            new Figure("craneReachM", "Grua: alcance (m)", Section.CAPABILITY, 3, 2, 0, VehicleCapability.CRANE),
            new Figure("platformHeightM", "Plataforma: altura (m)", Section.CAPABILITY, 3, 2, 0, VehicleCapability.ELEVATING_PLATFORM),
            new Figure("platformLoadKg", "Plataforma: carga (kg)", Section.CAPABILITY, 0, 0, 0, VehicleCapability.ELEVATING_PLATFORM),
            new Figure("platformInsulationKv", "Plataforma: aislamiento (kV)", Section.CAPABILITY, 4, 1, 0, VehicleCapability.INSULATED_PLATFORM),
            new Figure("towingCapacityT", "Masa remolcable (t)", Section.CAPABILITY, 6, 2, 0, VehicleCapability.TOWING));

    private String code = "";
    private String name = "";
    private String manufacturer = "";
    private String model = "";
    private VehicleMobility mobility;
    private VehicleCategory category;
    private boolean selfPropelled = true;
    private boolean portable;
    private VehiclePower power;
    private boolean transportModeAuthorised;
    private RailGearType railGearType;
    private boolean needsPreparedAccess;
    private DrivingLicence requiredLicence;
    private Set<String> capabilities = new LinkedHashSet<>();
    private String notes = "";
    private final Map<String, Object> figures = new HashMap<>();

    public static VehicleTypeForm of(VehicleTypeDto dto) {
        VehicleTypeForm form = new VehicleTypeForm();
        if (dto == null) {
            return form;
        }
        form.code = dto.code();
        form.name = orEmpty(dto.name());
        form.manufacturer = orEmpty(dto.manufacturer());
        form.model = orEmpty(dto.model());
        form.mobility = dto.mobility();
        form.category = dto.category();
        form.selfPropelled = !Boolean.FALSE.equals(dto.selfPropelled());
        form.portable = Boolean.TRUE.equals(dto.portable());
        form.power = dto.power();
        form.transportModeAuthorised = Boolean.TRUE.equals(dto.transportModeAuthorised());
        form.railGearType = dto.railGearType();
        form.needsPreparedAccess = Boolean.TRUE.equals(dto.needsPreparedAccess());
        form.requiredLicence = dto.requiredLicence();
        form.capabilities = new LinkedHashSet<>(dto.capabilities());
        form.notes = orEmpty(dto.notes());
        for (Figure figure : FIGURES) {
            form.figures.put(figure.field(), figureOf(dto, figure.field()));
        }
        return form;
    }

    /** Si una seccion se ofrece con esta movilidad. */
    static boolean offered(Section section, VehicleMobility mobility) {
        return switch (section) {
            case GENERAL, CAPABILITY -> true;
            case TRACK -> mobility != null && mobility.runsOnTrack();
            case ROAD_RAIL -> mobility == VehicleMobility.ROAD_RAIL;
            case ROAD -> mobility != null && mobility.runsOnRoad();
        };
    }

    /** Si una cifra viaja: su seccion se ofrece y, si es de una capacidad, el modelo la tiene. */
    boolean travels(Figure figure) {
        return offered(figure.section(), mobility) && (figure.capability() == null || capabilities.contains(figure.capability().name()));
    }

    private BigDecimal decimal(String field) {
        Figure figure = figure(field);
        return travels(figure) ? (BigDecimal) figures.get(field) : null;
    }

    private Integer integer(String field) {
        Figure figure = figure(field);
        return travels(figure) ? (Integer) figures.get(field) : null;
    }

    /** El alta; lo vacio no viaja. */
    public VehicleTypeRequest toRequest() {
        boolean track = offered(Section.TRACK, mobility);
        boolean roadRail = offered(Section.ROAD_RAIL, mobility);
        boolean road = offered(Section.ROAD, mobility);
        return new VehicleTypeRequest(code.trim(), name.trim(), blankToNull(manufacturer), blankToNull(model), mobility, category, selfPropelled,
                portable, decimal("lengthM"), decimal("widthM"), decimal("heightM"), decimal("tareT"), decimal("maxGrossT"),
                decimal("maxAxleLoadT"), integer("axles"), integer("seats"), decimal("payloadT"), power, integer("trackGaugeMm"),
                integer("maxSpeedTrackWorkingKmh"), integer("maxSpeedTrackTransportKmh"), decimal("maxGradientPermille"),
                integer("minCurveRadiusM"), integer("maxWorkingCantMm"), track ? transportModeAuthorised : null,
                roadRail ? railGearType : null, integer("onTrackingMinutes"), roadRail ? needsPreparedAccess : null, integer("maxSpeedRoadKmh"),
                road ? requiredLicence : null, new TreeSet<>(capabilities), decimal("craneCapacityT"), decimal("craneReachM"),
                decimal("platformHeightM"), integer("platformLoadKg"), decimal("platformInsulationKv"), decimal("towingCapacityT"),
                blankToNull(notes));
    }

    /**
     * Lo que cambio respecto a lo leido, lo vaciado y la version. Las capacidades se comparan como
     * conjunto (y las que esta aplicacion no conoce vuelven tal cual); una casilla sin marcar no vacia un
     * dato que el modelo no tenia.
     */
    public MergePatch<VehicleTypeUpdateRequest> toPatch(VehicleTypeDto original) {
        Changes changes = new Changes();
        boolean track = offered(Section.TRACK, mobility);
        boolean roadRail = offered(Section.ROAD_RAIL, mobility);
        boolean road = offered(Section.ROAD, mobility);
        VehicleTypeUpdateRequest values = new VehicleTypeUpdateRequest(
                changes.text("name", name, original.name()),
                changes.text("manufacturer", manufacturer, original.manufacturer()),
                changes.text("model", model, original.model()),
                changes.value("category", category, original.category()),
                flag(changes, "selfPropelled", selfPropelled, original.selfPropelled(), true),
                flag(changes, "portable", portable, original.portable(), false),
                changes.number("lengthM", decimal("lengthM"), original.lengthM()),
                changes.number("widthM", decimal("widthM"), original.widthM()),
                changes.number("heightM", decimal("heightM"), original.heightM()),
                changes.number("tareT", decimal("tareT"), original.tareT()),
                changes.number("maxGrossT", decimal("maxGrossT"), original.maxGrossT()),
                changes.number("maxAxleLoadT", decimal("maxAxleLoadT"), original.maxAxleLoadT()),
                changes.value("axles", integer("axles"), original.axles()),
                changes.value("seats", integer("seats"), original.seats()),
                changes.number("payloadT", decimal("payloadT"), original.payloadT()),
                changes.value("power", power, original.power()),
                changes.value("trackGaugeMm", integer("trackGaugeMm"), original.trackGaugeMm()),
                changes.value("maxSpeedTrackWorkingKmh", integer("maxSpeedTrackWorkingKmh"), original.maxSpeedTrackWorkingKmh()),
                changes.value("maxSpeedTrackTransportKmh", integer("maxSpeedTrackTransportKmh"), original.maxSpeedTrackTransportKmh()),
                changes.number("maxGradientPermille", decimal("maxGradientPermille"), original.maxGradientPermille()),
                changes.value("minCurveRadiusM", integer("minCurveRadiusM"), original.minCurveRadiusM()),
                changes.value("maxWorkingCantMm", integer("maxWorkingCantMm"), original.maxWorkingCantMm()),
                track ? flag(changes, "transportModeAuthorised", transportModeAuthorised, original.transportModeAuthorised(), false) : null,
                roadRail ? changes.value("railGearType", railGearType, original.railGearType()) : null,
                changes.value("onTrackingMinutes", integer("onTrackingMinutes"), original.onTrackingMinutes()),
                roadRail ? flag(changes, "needsPreparedAccess", needsPreparedAccess, original.needsPreparedAccess(), false) : null,
                changes.value("maxSpeedRoadKmh", integer("maxSpeedRoadKmh"), original.maxSpeedRoadKmh()),
                road ? changes.value("requiredLicence", requiredLicence, original.requiredLicence()) : null,
                changes.set("capabilities", capabilities, original.capabilities()),
                changes.number("craneCapacityT", decimal("craneCapacityT"), original.craneCapacityT()),
                changes.number("craneReachM", decimal("craneReachM"), original.craneReachM()),
                changes.number("platformHeightM", decimal("platformHeightM"), original.platformHeightM()),
                changes.value("platformLoadKg", integer("platformLoadKg"), original.platformLoadKg()),
                changes.number("platformInsulationKv", decimal("platformInsulationKv"), original.platformInsulationKv()),
                changes.number("towingCapacityT", decimal("towingCapacityT"), original.towingCapacityT()),
                changes.text("notes", notes, original.notes()),
                null);
        return changes.patch(values, original.version());
    }

    /** Una casilla: igual a lo leido (o a lo que el servicio entiende sin dato) no viaja; nunca vacia. */
    private static Boolean flag(Changes changes, String field, boolean value, Boolean original, boolean whenMissing) {
        boolean before = original == null ? whenMissing : original;
        return value == before ? null : changes.value(field, value, original);
    }

    static Figure figure(String field) {
        return FIGURES.stream().filter(figure -> figure.field().equals(field)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No figure " + field));
    }

    /** El accesor del record se llama como el campo: asi la tabla de cifras es la unica lista de sus nombres. */
    private static Object figureOf(VehicleTypeDto dto, String field) {
        try {
            return VehicleTypeDto.class.getMethod(field).invoke(dto);
        } catch (ReflectiveOperationException failure) {
            throw new IllegalStateException("Cannot read " + field, failure);
        }
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private static String orEmpty(String value) {
        return value == null ? "" : value;
    }

    Object getFigure(String field) {
        return figures.get(field);
    }

    void setFigure(String field, Object value) {
        figures.put(field, value);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public VehicleMobility getMobility() {
        return mobility;
    }

    public void setMobility(VehicleMobility mobility) {
        this.mobility = mobility;
    }

    public VehicleCategory getCategory() {
        return category;
    }

    public void setCategory(VehicleCategory category) {
        this.category = category;
    }

    public boolean isSelfPropelled() {
        return selfPropelled;
    }

    public void setSelfPropelled(boolean selfPropelled) {
        this.selfPropelled = selfPropelled;
    }

    public boolean isPortable() {
        return portable;
    }

    public void setPortable(boolean portable) {
        this.portable = portable;
    }

    public VehiclePower getPower() {
        return power;
    }

    public void setPower(VehiclePower power) {
        this.power = power;
    }

    public boolean isTransportModeAuthorised() {
        return transportModeAuthorised;
    }

    public void setTransportModeAuthorised(boolean transportModeAuthorised) {
        this.transportModeAuthorised = transportModeAuthorised;
    }

    public RailGearType getRailGearType() {
        return railGearType;
    }

    public void setRailGearType(RailGearType railGearType) {
        this.railGearType = railGearType;
    }

    public boolean isNeedsPreparedAccess() {
        return needsPreparedAccess;
    }

    public void setNeedsPreparedAccess(boolean needsPreparedAccess) {
        this.needsPreparedAccess = needsPreparedAccess;
    }

    public DrivingLicence getRequiredLicence() {
        return requiredLicence;
    }

    public void setRequiredLicence(DrivingLicence requiredLicence) {
        this.requiredLicence = requiredLicence;
    }

    public Set<String> getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(Set<String> capabilities) {
        this.capabilities = capabilities == null ? new LinkedHashSet<>() : new LinkedHashSet<>(capabilities);
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
