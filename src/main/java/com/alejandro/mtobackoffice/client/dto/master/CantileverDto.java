package com.alejandro.mtobackoffice.client.dto.master;

import java.math.BigDecimal;

/**
 * Mensula de un perfil: hija de la coleccion {@code cantilevers} (README_API.md §4: la lista que
 * se manda es el estado final). Con su brazo de atirantado 1:1, que se manda tal cual para
 * mantenerlo y a {@code null} para quitarlo.
 */
public class CantileverDto extends MasterDto {

    private BigDecimal cwHeight;
    private BigDecimal stagger;
    private BigDecimal catenaryHeight;
    private BigDecimal cwElevation;
    private BigDecimal windDeflection;
    private BigDecimal armAngle;
    private LovRef cantileverType;
    private Long profileId;
    private SteadyArmDto steadyArm;

    public BigDecimal getCwHeight() {
        return cwHeight;
    }

    public void setCwHeight(BigDecimal cwHeight) {
        this.cwHeight = cwHeight;
    }

    public BigDecimal getStagger() {
        return stagger;
    }

    public void setStagger(BigDecimal stagger) {
        this.stagger = stagger;
    }

    public BigDecimal getCatenaryHeight() {
        return catenaryHeight;
    }

    public void setCatenaryHeight(BigDecimal catenaryHeight) {
        this.catenaryHeight = catenaryHeight;
    }

    public BigDecimal getCwElevation() {
        return cwElevation;
    }

    public void setCwElevation(BigDecimal cwElevation) {
        this.cwElevation = cwElevation;
    }

    public BigDecimal getWindDeflection() {
        return windDeflection;
    }

    public void setWindDeflection(BigDecimal windDeflection) {
        this.windDeflection = windDeflection;
    }

    public BigDecimal getArmAngle() {
        return armAngle;
    }

    public void setArmAngle(BigDecimal armAngle) {
        this.armAngle = armAngle;
    }

    public LovRef getCantileverType() {
        return cantileverType;
    }

    public void setCantileverType(LovRef cantileverType) {
        this.cantileverType = cantileverType;
    }

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    public SteadyArmDto getSteadyArm() {
        return steadyArm;
    }

    public void setSteadyArm(SteadyArmDto steadyArm) {
        this.steadyArm = steadyArm;
    }

    @Override
    public void forgetChildren() {
        // el brazo es 1:1, no una coleccion: se manda como se leyo
    }
}
