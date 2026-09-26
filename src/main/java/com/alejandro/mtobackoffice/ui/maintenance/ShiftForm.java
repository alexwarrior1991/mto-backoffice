package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.dto.maintenance.AssetSummaryDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.PossessionType;
import com.alejandro.mtobackoffice.client.dto.maintenance.ShiftDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.ShiftRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.ShiftUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.TeamSummaryDto;
import com.alejandro.mtobackoffice.ui.master.RefItem;
import com.alejandro.mtobackoffice.ui.support.Formats;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Modelo mutable del editor de un turno, con las propiedades llamadas como los campos de la
 * peticion ({@code trackIds} guarda las vias elegidas, {@code blockingDisconnectorIds} los
 * seccionadores) para que los errores del servicio caigan en su campo.
 */
public class ShiftForm {

    private LocalDate shiftDate = LocalDate.now();
    private TeamSummaryDto teamId;
    private String baseName = "";
    private String vehicle = "";
    private PossessionType possessionType = PossessionType.PARTIAL;
    private LocalDateTime plannedStart;
    private LocalDateTime plannedEnd;
    private Set<AssetSummaryDto> blockingDisconnectorIds = new LinkedHashSet<>();
    private String earthingPoints = "";
    private String parkingPlace = "";
    private RefItem executionPackageId;
    private Set<RefItem> trackIds = new LinkedHashSet<>();
    private BigDecimal startKp;
    private BigDecimal endKp;
    private String personnel = "";
    private String measurementEquipment = "";
    private String observations = "";

    public static ShiftForm of(ShiftDto dto, MaintenanceNames names) {
        ShiftForm form = new ShiftForm();
        if (dto != null) {
            form.setShiftDate(dto.shiftDate());
            form.setTeamId(dto.team());
            form.setBaseName(orEmpty(dto.baseName()));
            form.setVehicle(orEmpty(dto.vehicle()));
            form.setPossessionType(dto.possessionType());
            form.setPlannedStart(Formats.toLocalDateTime(dto.plannedStart()));
            form.setPlannedEnd(Formats.toLocalDateTime(dto.plannedEnd()));
            form.setBlockingDisconnectorIds(new LinkedHashSet<>(dto.blockingDisconnectors()));
            form.setEarthingPoints(orEmpty(dto.earthingPoints()));
            form.setParkingPlace(orEmpty(dto.parkingPlace()));
            form.setExecutionPackageId(names.packageRef(dto.executionPackageId()));
            form.setTrackIds(dto.trackIds().stream().map(names::trackRef).collect(Collectors.toCollection(LinkedHashSet::new)));
            form.setStartKp(dto.startKp());
            form.setEndKp(dto.endKp());
            form.setPersonnel(orEmpty(dto.personnel()));
            form.setMeasurementEquipment(orEmpty(dto.measurementEquipment()));
            form.setObservations(orEmpty(dto.observations()));
        }
        return form;
    }

    public ShiftRequest toRequest() {
        return new ShiftRequest(shiftDate, teamId == null ? null : teamId.id(), nullIfBlank(baseName), nullIfBlank(vehicle), possessionType,
                Formats.toInstant(plannedStart), Formats.toInstant(plannedEnd),
                blockingDisconnectorIds.isEmpty() ? null : disconnectorIds(), nullIfBlank(earthingPoints), nullIfBlank(parkingPlace),
                executionPackageId == null ? null : executionPackageId.id(), trackIdSet(), startKp, endKp, nullIfBlank(personnel),
                nullIfBlank(measurementEquipment), nullIfBlank(observations));
    }

    /** Solo lo que cambio; vias y seccionadores, si cambiaron, van enteros (un conjunto vacio de seccionadores es «ninguno»). */
    public ShiftUpdateRequest toUpdateRequest(ShiftDto original) {
        Set<UUID> disconnectors = disconnectorIds();
        Set<UUID> originalDisconnectors = original.blockingDisconnectors().stream().map(AssetSummaryDto::id).collect(Collectors.toSet());
        Set<Long> tracks = trackIdSet();
        UUID team = teamId == null ? null : teamId.id();
        UUID originalTeam = original.team() == null ? null : original.team().id();
        Long packageId = executionPackageId == null ? null : executionPackageId.id();
        return new ShiftUpdateRequest(
                Objects.equals(shiftDate, original.shiftDate()) ? null : shiftDate,
                Objects.equals(team, originalTeam) ? null : team,
                changed(baseName, original.baseName()),
                changed(vehicle, original.vehicle()),
                possessionType == original.possessionType() ? null : possessionType,
                changedInstant(plannedStart, original.plannedStart()),
                changedInstant(plannedEnd, original.plannedEnd()),
                disconnectors.equals(originalDisconnectors) ? null : disconnectors,
                changed(earthingPoints, original.earthingPoints()),
                changed(parkingPlace, original.parkingPlace()),
                Objects.equals(packageId, original.executionPackageId()) ? null : packageId,
                tracks.equals(Set.copyOf(original.trackIds())) ? null : tracks,
                sameNumber(startKp, original.startKp()) ? null : startKp,
                sameNumber(endKp, original.endKp()) ? null : endKp,
                changed(personnel, original.personnel()),
                changed(measurementEquipment, original.measurementEquipment()),
                changed(observations, original.observations()));
    }

    private Set<UUID> disconnectorIds() {
        return blockingDisconnectorIds.stream().map(AssetSummaryDto::id).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Set<Long> trackIdSet() {
        return trackIds.stream().map(RefItem::id).collect(Collectors.toCollection(TreeSet::new));
    }

    private static Instant changedInstant(LocalDateTime value, Instant original) {
        Instant current = Formats.toInstant(value);
        return Objects.equals(current, original) ? null : current;
    }

    private static boolean sameNumber(BigDecimal value, BigDecimal original) {
        return value == null ? original == null : original != null && value.compareTo(original) == 0;
    }

    private static String changed(String value, String original) {
        String current = value == null ? "" : value.trim();
        return current.equals(orEmpty(original)) ? null : current;
    }

    private static String nullIfBlank(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private static String orEmpty(String value) {
        return value == null ? "" : value;
    }

    public LocalDate getShiftDate() {
        return shiftDate;
    }

    public void setShiftDate(LocalDate shiftDate) {
        this.shiftDate = shiftDate;
    }

    public TeamSummaryDto getTeamId() {
        return teamId;
    }

    public void setTeamId(TeamSummaryDto teamId) {
        this.teamId = teamId;
    }

    public String getBaseName() {
        return baseName;
    }

    public void setBaseName(String baseName) {
        this.baseName = baseName;
    }

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public PossessionType getPossessionType() {
        return possessionType;
    }

    public void setPossessionType(PossessionType possessionType) {
        this.possessionType = possessionType;
    }

    public LocalDateTime getPlannedStart() {
        return plannedStart;
    }

    public void setPlannedStart(LocalDateTime plannedStart) {
        this.plannedStart = plannedStart;
    }

    public LocalDateTime getPlannedEnd() {
        return plannedEnd;
    }

    public void setPlannedEnd(LocalDateTime plannedEnd) {
        this.plannedEnd = plannedEnd;
    }

    public Set<AssetSummaryDto> getBlockingDisconnectorIds() {
        return blockingDisconnectorIds;
    }

    public void setBlockingDisconnectorIds(Set<AssetSummaryDto> blockingDisconnectorIds) {
        this.blockingDisconnectorIds = copy(blockingDisconnectorIds);
    }

    public String getEarthingPoints() {
        return earthingPoints;
    }

    public void setEarthingPoints(String earthingPoints) {
        this.earthingPoints = earthingPoints;
    }

    public String getParkingPlace() {
        return parkingPlace;
    }

    public void setParkingPlace(String parkingPlace) {
        this.parkingPlace = parkingPlace;
    }

    public RefItem getExecutionPackageId() {
        return executionPackageId;
    }

    public void setExecutionPackageId(RefItem executionPackageId) {
        this.executionPackageId = executionPackageId;
    }

    public Set<RefItem> getTrackIds() {
        return trackIds;
    }

    public void setTrackIds(Set<RefItem> trackIds) {
        this.trackIds = copy(trackIds);
    }

    public BigDecimal getStartKp() {
        return startKp;
    }

    public void setStartKp(BigDecimal startKp) {
        this.startKp = startKp;
    }

    public BigDecimal getEndKp() {
        return endKp;
    }

    public void setEndKp(BigDecimal endKp) {
        this.endKp = endKp;
    }

    public String getPersonnel() {
        return personnel;
    }

    public void setPersonnel(String personnel) {
        this.personnel = personnel;
    }

    public String getMeasurementEquipment() {
        return measurementEquipment;
    }

    public void setMeasurementEquipment(String measurementEquipment) {
        this.measurementEquipment = measurementEquipment;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    private static <T> Set<T> copy(Collection<T> values) {
        return values == null ? new LinkedHashSet<>() : new LinkedHashSet<>(values);
    }
}
