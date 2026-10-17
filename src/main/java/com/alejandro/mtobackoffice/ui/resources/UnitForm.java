package com.alejandro.mtobackoffice.ui.resources;

import com.alejandro.mtobackoffice.client.dto.MergePatch;
import com.alejandro.mtobackoffice.client.dto.resources.OrganizationUnitDto;
import com.alejandro.mtobackoffice.client.dto.resources.OrganizationUnitKind;
import com.alejandro.mtobackoffice.client.dto.resources.OrganizationUnitRequest;
import com.alejandro.mtobackoffice.client.dto.resources.OrganizationUnitUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.resources.PersonSummaryDto;
import com.alejandro.mtobackoffice.client.dto.resources.UnitSummaryDto;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleSummaryDto;
import com.alejandro.mtobackoffice.client.dto.resources.WorkDiscipline;
import com.alejandro.mtobackoffice.ui.master.RefItem;
import com.alejandro.mtobackoffice.ui.support.Changes;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Modelo mutable del editor de una unidad. Sus propiedades se llaman como los campos de la peticion
 * para que un {@code validationErrors[].field} del servicio caiga en su campo, aunque guarden lo
 * elegido (el padre, el jefe, los EP, los linemen) y no solo su id.
 */
public class UnitForm {

    private String code = "";
    private String name = "";
    private OrganizationUnitKind kind;
    private WorkDiscipline discipline;
    private UnitSummaryDto parentId;
    private PersonSummaryDto headPersonId;
    private Set<RefItem> executionPackageIds = new LinkedHashSet<>();
    private Set<PersonSummaryDto> memberPersonIds = new LinkedHashSet<>();
    private VehicleSummaryDto defaultVehicleId;
    private LocalDate joinedOn;

    /** Una unidad nueva, colgando de {@code parent} si se dio de alta desde la fila o la ficha de su padre. */
    public static UnitForm creating(UnitSummaryDto parent) {
        UnitForm form = new UnitForm();
        form.setParentId(parent);
        return form;
    }

    /** @param packageRef el EP para el desplegable, con su nombre o con {@code #id}, nunca {@code null} */
    public static UnitForm of(OrganizationUnitDto dto, Function<Long, RefItem> packageRef) {
        UnitForm form = new UnitForm();
        form.setCode(dto.code());
        form.setName(dto.name() == null ? "" : dto.name());
        form.setKind(dto.kind());
        form.setDiscipline(dto.discipline());
        form.setParentId(dto.parent());
        form.setHeadPersonId(dto.head());
        form.setExecutionPackageIds(dto.executionPackageIds().stream().sorted().map(packageRef).collect(Collectors.toCollection(LinkedHashSet::new)));
        form.setDefaultVehicleId(dto.defaultVehicle());
        return form;
    }

    /**
     * El alta; lo vacio no viaja. Un proyecto no tiene disciplina ni padre; solo un equipo lleva sus
     * linemen y su fecha de entrada. Sin {@code config-read} no se eligen EP y no viaja ninguno.
     */
    public OrganizationUnitRequest toRequest() {
        boolean team = kind == OrganizationUnitKind.TEAM;
        boolean project = kind == OrganizationUnitKind.PROJECT;
        return new OrganizationUnitRequest(code.trim(), name.trim(), kind, project ? null : discipline, project || parentId == null ? null : parentId.id(),
                headPersonId == null ? null : headPersonId.id(), packageIds(executionPackageIds), team && defaultVehicleId != null ? defaultVehicleId.id() : null,
                team ? memberPersonIds.stream().map(PersonSummaryDto::id).toList() : List.of(), team ? joinedOn : null);
    }

    /**
     * Lo que cambio respecto a lo leido, lo vaciado y la version. El jefe se vacia salvo en un equipo
     * (el editor lo pide obligatorio), los EP se comparan como conjunto y quitarlos todos viaja como
     * {@code null}; el vehiculo por defecto, solo de un equipo, se cambia o se vacia. Sin {@code config-read} los EP no se tocan: los leidos no se mandan a vaciar.
     */
    public MergePatch<OrganizationUnitUpdateRequest> toPatch(OrganizationUnitDto original, boolean readsPackages) {
        Changes changes = new Changes();
        OrganizationUnitUpdateRequest values = new OrganizationUnitUpdateRequest(
                changes.text("name", name, original.name()),
                changes.value("parentId", idOf(parentId), original.parent() == null ? null : original.parent().id()),
                changes.value("headPersonId", headPersonId == null ? null : headPersonId.id(), original.head() == null ? null : original.head().id()),
                readsPackages ? changes.set("executionPackageIds", packageIds(executionPackageIds), original.executionPackageIds()) : null,
                original.isTeam() ? changes.value("defaultVehicleId", idOf(defaultVehicleId),
                        original.defaultVehicle() == null ? null : original.defaultVehicle().id()) : null,
                null, null, null);
        return changes.patch(values, original.version());
    }

    private static Set<Long> packageIds(Set<RefItem> items) {
        return items == null ? Set.of() : items.stream().map(RefItem::id).collect(Collectors.toCollection(TreeSet::new));
    }

    private static UUID idOf(UnitSummaryDto unit) {
        return unit == null ? null : unit.id();
    }

    private static UUID idOf(VehicleSummaryDto vehicle) {
        return vehicle == null ? null : vehicle.id();
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

    public OrganizationUnitKind getKind() {
        return kind;
    }

    public void setKind(OrganizationUnitKind kind) {
        this.kind = kind;
    }

    public WorkDiscipline getDiscipline() {
        return discipline;
    }

    public void setDiscipline(WorkDiscipline discipline) {
        this.discipline = discipline;
    }

    public UnitSummaryDto getParentId() {
        return parentId;
    }

    public void setParentId(UnitSummaryDto parentId) {
        this.parentId = parentId;
    }

    public PersonSummaryDto getHeadPersonId() {
        return headPersonId;
    }

    public void setHeadPersonId(PersonSummaryDto headPersonId) {
        this.headPersonId = headPersonId;
    }

    public Set<RefItem> getExecutionPackageIds() {
        return executionPackageIds;
    }

    public void setExecutionPackageIds(Set<RefItem> executionPackageIds) {
        this.executionPackageIds = executionPackageIds == null ? new LinkedHashSet<>() : new LinkedHashSet<>(executionPackageIds);
    }

    public Set<PersonSummaryDto> getMemberPersonIds() {
        return memberPersonIds;
    }

    public void setMemberPersonIds(Set<PersonSummaryDto> memberPersonIds) {
        this.memberPersonIds = memberPersonIds == null ? new LinkedHashSet<>() : new LinkedHashSet<>(memberPersonIds);
    }

    public VehicleSummaryDto getDefaultVehicleId() {
        return defaultVehicleId;
    }

    public void setDefaultVehicleId(VehicleSummaryDto defaultVehicleId) {
        this.defaultVehicleId = defaultVehicleId;
    }

    public LocalDate getJoinedOn() {
        return joinedOn;
    }

    public void setJoinedOn(LocalDate joinedOn) {
        this.joinedOn = joinedOn;
    }
}
