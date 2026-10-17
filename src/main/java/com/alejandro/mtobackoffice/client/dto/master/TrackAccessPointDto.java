package com.alejandro.mtobackoffice.client.dto.master;

import java.math.BigDecimal;

/**
 * Punto de acceso a la via (V28 de mto-configuration, README_API.md §4 quinquies). La via es
 * obligatoria y la estacion no: muchos estan en plena via. Los decimales viajan como numeros, como el
 * KP del aislador: el KP en metros ({@code numeric(12,3)}), las toneladas ({@code numeric(8,3)}) y las
 * coordenadas en grados decimales ({@code numeric(9,6)}, las dos o ninguna). No tiene hijos, y como
 * los demas maestros se devuelve entero: un {@code PUT} sin un campo opcional lo deja vacio. La clase y
 * el lado se guardan como la cadena leida, para que vuelvan exactamente asi aunque el servicio estrene un
 * valor ({@link TrackAccessPointKind#labelOf}, {@link TrackAccessPointSide#labelOf}).
 */
public class TrackAccessPointDto extends MasterDto {

    private String name;
    private Long trackId;
    private Long stationId;
    private BigDecimal kp;
    private String kind;
    private String side;
    private Boolean roadRailAccess = Boolean.TRUE;
    private BigDecimal maxAxleLoadT;
    private BigDecimal maxGrossWeightT;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Boolean enabled = Boolean.TRUE;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTrackId() {
        return trackId;
    }

    public void setTrackId(Long trackId) {
        this.trackId = trackId;
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

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public Boolean getRoadRailAccess() {
        return roadRailAccess;
    }

    public void setRoadRailAccess(Boolean roadRailAccess) {
        this.roadRailAccess = roadRailAccess;
    }

    public BigDecimal getMaxAxleLoadT() {
        return maxAxleLoadT;
    }

    public void setMaxAxleLoadT(BigDecimal maxAxleLoadT) {
        this.maxAxleLoadT = maxAxleLoadT;
    }

    public BigDecimal getMaxGrossWeightT() {
        return maxGrossWeightT;
    }

    public void setMaxGrossWeightT(BigDecimal maxGrossWeightT) {
        this.maxGrossWeightT = maxGrossWeightT;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /** No tiene colecciones de hijos: ni la via ni la estacion lo llevan dentro. */
    @Override
    public void forgetChildren() {
    }
}
