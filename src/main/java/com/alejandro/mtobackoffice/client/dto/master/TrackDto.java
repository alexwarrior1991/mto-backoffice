package com.alejandro.mtobackoffice.client.dto.master;

import java.util.List;

/**
 * Via. {@code stationIds} son las estaciones que atraviesa: mandar la lista declara cuales son
 * todas (la vacia las desliga todas) y {@code null} no las toca. Sus perfiles no se editan aqui:
 * van a {@code null} al guardar.
 */
public class TrackDto extends MasterDto {

    private String name;
    private Boolean enabled = Boolean.TRUE;
    private Long executionPackageId;
    private List<Long> stationIds;
    private List<Object> profiles;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Long getExecutionPackageId() {
        return executionPackageId;
    }

    public void setExecutionPackageId(Long executionPackageId) {
        this.executionPackageId = executionPackageId;
    }

    public List<Long> getStationIds() {
        return stationIds;
    }

    public void setStationIds(List<Long> stationIds) {
        this.stationIds = stationIds;
    }

    public List<Object> getProfiles() {
        return profiles;
    }

    public void setProfiles(List<Object> profiles) {
        this.profiles = profiles;
    }

    @Override
    public void forgetChildren() {
        this.profiles = null;
    }
}
