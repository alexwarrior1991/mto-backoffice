package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.dto.maintenance.TeamDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.TeamRequest;
import com.alejandro.mtobackoffice.ui.master.RefItem;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Modelo mutable del editor de un equipo, con las propiedades llamadas como los campos de la
 * peticion. Se manda entero: el {@code PUT} de equipos es completo.
 */
public class TeamForm {

    private String code = "";
    private String name = "";
    private String baseName = "";
    private String vehicle = "";
    private boolean active = true;
    private Set<RefItem> executionPackageIds = new LinkedHashSet<>();

    public static TeamForm of(TeamDto dto, MaintenanceNames names) {
        TeamForm form = new TeamForm();
        if (dto != null) {
            form.setCode(orEmpty(dto.code()));
            form.setName(orEmpty(dto.name()));
            form.setBaseName(orEmpty(dto.baseName()));
            form.setVehicle(orEmpty(dto.vehicle()));
            form.setActive(dto.isActive());
            form.setExecutionPackageIds(dto.executionPackageIds().stream().sorted().map(names::packageRef)
                    .collect(Collectors.toCollection(LinkedHashSet::new)));
        }
        return form;
    }

    /** Base y vehiculo vacios viajan como {@code null}, que en un {@code PUT} completo los borra. */
    public TeamRequest toRequest() {
        return new TeamRequest(code.trim(), name.trim(), nullIfBlank(baseName), nullIfBlank(vehicle), active,
                executionPackageIds.stream().map(RefItem::id).collect(Collectors.toCollection(TreeSet::new)));
    }

    private static String nullIfBlank(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private static String orEmpty(String value) {
        return value == null ? "" : value;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Set<RefItem> getExecutionPackageIds() {
        return executionPackageIds;
    }

    public void setExecutionPackageIds(Set<RefItem> executionPackageIds) {
        this.executionPackageIds = executionPackageIds == null ? new LinkedHashSet<>() : new LinkedHashSet<>(executionPackageIds);
    }
}
