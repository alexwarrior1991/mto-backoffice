package com.alejandro.mtobackoffice.client.dto.master;

import java.util.List;

/** Estacion. Sus vias, seccionadores y aisladores no se editan aqui: van a {@code null} al guardar. */
public class StationDto extends MasterDto {

    private String name;
    private Long executionPackageId;
    private List<Object> tracks;
    private List<Object> disconnectors;
    private List<Object> sectionInsulators;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getExecutionPackageId() {
        return executionPackageId;
    }

    public void setExecutionPackageId(Long executionPackageId) {
        this.executionPackageId = executionPackageId;
    }

    public List<Object> getTracks() {
        return tracks;
    }

    public void setTracks(List<Object> tracks) {
        this.tracks = tracks;
    }

    public List<Object> getDisconnectors() {
        return disconnectors;
    }

    public void setDisconnectors(List<Object> disconnectors) {
        this.disconnectors = disconnectors;
    }

    public List<Object> getSectionInsulators() {
        return sectionInsulators;
    }

    public void setSectionInsulators(List<Object> sectionInsulators) {
        this.sectionInsulators = sectionInsulators;
    }

    @Override
    public void forgetChildren() {
        this.tracks = null;
        this.disconnectors = null;
        this.sectionInsulators = null;
    }
}
