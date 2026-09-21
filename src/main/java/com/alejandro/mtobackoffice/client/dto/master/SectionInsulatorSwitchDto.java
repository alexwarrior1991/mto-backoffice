package com.alejandro.mtobackoffice.client.dto.master;

import java.math.BigDecimal;

/**
 * Aguja de un aislador de seccion (README_API.md §4 quater): {@code W31}, en su KP y con el
 * denominador de su desvio ({@code 9} para {@code 1:9}). {@code turnoutRate} lo compone el
 * servicio y es de solo lectura.
 */
public class SectionInsulatorSwitchDto extends MasterDto {

    private String code;
    private BigDecimal kp;
    private Integer turnoutDenominator;
    private Long trackId;
    private Boolean enabled;
    private String turnoutRate;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigDecimal getKp() {
        return kp;
    }

    public void setKp(BigDecimal kp) {
        this.kp = kp;
    }

    public Integer getTurnoutDenominator() {
        return turnoutDenominator;
    }

    public void setTurnoutDenominator(Integer turnoutDenominator) {
        this.turnoutDenominator = turnoutDenominator;
    }

    public Long getTrackId() {
        return trackId;
    }

    public void setTrackId(Long trackId) {
        this.trackId = trackId;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getTurnoutRate() {
        return turnoutRate;
    }

    public void setTurnoutRate(String turnoutRate) {
        this.turnoutRate = turnoutRate;
    }

    /** {@code 1:9}, con el denominador que hay aunque el servicio aun no haya compuesto el texto. */
    public String turnoutLabel() {
        return turnoutDenominator == null ? (turnoutRate == null ? "" : turnoutRate) : "1:" + turnoutDenominator;
    }

    @Override
    public void forgetChildren() {
        // sin hijos
    }
}
