package com.alejandro.mtobackoffice.client.dto.master;

import java.math.BigDecimal;
import java.util.List;

/**
 * Aislador de seccion. {@code TRACK_CONNECTION} lleva via principal y via conectada;
 * {@code IN_TRACK}, solo la principal. Sus agujas no se editan aqui: van a {@code null} al guardar.
 */
public class SectionInsulatorDto extends MasterDto {

    private String name;
    private Boolean enabled = Boolean.TRUE;
    private Long stationId;
    private BigDecimal kp;
    private SectionInsulatorInstallationType installationType;
    private Long trackId;
    private Long connectedTrackId;
    private List<Object> switches;

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

    public Long getStationId() {
        return stationId;
    }

    public void setStationId(Long stationId) {
        this.stationId = stationId;
    }

    public BigDecimal getKp() {
        return kp;
    }

    public void setKp(BigDecimal kp) {
        this.kp = kp;
    }

    public SectionInsulatorInstallationType getInstallationType() {
        return installationType;
    }

    public void setInstallationType(SectionInsulatorInstallationType installationType) {
        this.installationType = installationType;
    }

    public Long getTrackId() {
        return trackId;
    }

    public void setTrackId(Long trackId) {
        this.trackId = trackId;
    }

    public Long getConnectedTrackId() {
        return connectedTrackId;
    }

    public void setConnectedTrackId(Long connectedTrackId) {
        this.connectedTrackId = connectedTrackId;
    }

    public List<Object> getSwitches() {
        return switches;
    }

    public void setSwitches(List<Object> switches) {
        this.switches = switches;
    }

    @Override
    public void forgetChildren() {
        this.switches = null;
    }
}
