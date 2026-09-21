package com.alejandro.mtobackoffice.client.dto.master;

import java.time.LocalDate;
import java.util.List;

/**
 * Paquete de ejecucion. {@code enabled} es primitivo en el servicio y viaja siempre. Sus vias y
 * estaciones no se editan aqui: van a {@code null} al guardar.
 */
public class ExecutionPackageDto extends MasterDto {

    private String name;
    private Boolean initialPackage = Boolean.FALSE;
    private Long length;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean enabled = true;
    private Long companyId;
    private List<Object> tracks;
    private List<Object> stations;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getInitialPackage() {
        return initialPackage;
    }

    public void setInitialPackage(Boolean initialPackage) {
        this.initialPackage = initialPackage;
    }

    public Long getLength() {
        return length;
    }

    public void setLength(Long length) {
        this.length = length;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public List<Object> getTracks() {
        return tracks;
    }

    public void setTracks(List<Object> tracks) {
        this.tracks = tracks;
    }

    public List<Object> getStations() {
        return stations;
    }

    public void setStations(List<Object> stations) {
        this.stations = stations;
    }

    @Override
    public void forgetChildren() {
        this.tracks = null;
        this.stations = null;
    }
}
