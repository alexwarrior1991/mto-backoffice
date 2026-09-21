package com.alejandro.mtobackoffice.client.dto.master;

import java.math.BigDecimal;
import java.util.List;

/**
 * Perfil (poste). Las referencias a catalogo van como {@link LovRef}; las tres relaciones N:M
 * ({@code sectionings}, {@code anchorages}, {@code sectioningFeedings}) son listas que reemplazan
 * el conjunto entero. {@code disconnector} es una relacion 1:1 en la que {@code null} significa
 * "desvincular", asi que no se toca: se devuelve tal cual llego. Las mensulas no se editan aqui:
 * van a {@code null} al guardar. {@code kp} es texto en el servicio ({@code \d+(\.\d+)?}).
 */
public class ProfileDto extends MasterDto {

    private String profileId;
    private String kp;
    private Integer orderInTrack;
    private BigDecimal span;
    private BigDecimal heightCantileverSupport;
    private BigDecimal poleGaugeLocation;
    private BigDecimal railPoleDistance;
    private Long trackId;
    private DisconnectorDto disconnector;
    private List<CantileverDto> cantilevers;
    private LovRef profileStatus;
    private LovRef poleType;
    private LovRef foundation;
    private LovRef anchorageFoundation;
    private LovRef portal;
    private LovRef returnSupport;
    private LovRef supportType;
    private LovRef assemblyConfiguration;
    private List<LovRef> sectionings;
    private List<LovRef> anchorages;
    private List<LovRef> sectioningFeedings;

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getKp() {
        return kp;
    }

    public void setKp(String kp) {
        this.kp = kp;
    }

    public Integer getOrderInTrack() {
        return orderInTrack;
    }

    public void setOrderInTrack(Integer orderInTrack) {
        this.orderInTrack = orderInTrack;
    }

    public BigDecimal getSpan() {
        return span;
    }

    public void setSpan(BigDecimal span) {
        this.span = span;
    }

    public BigDecimal getHeightCantileverSupport() {
        return heightCantileverSupport;
    }

    public void setHeightCantileverSupport(BigDecimal heightCantileverSupport) {
        this.heightCantileverSupport = heightCantileverSupport;
    }

    public BigDecimal getPoleGaugeLocation() {
        return poleGaugeLocation;
    }

    public void setPoleGaugeLocation(BigDecimal poleGaugeLocation) {
        this.poleGaugeLocation = poleGaugeLocation;
    }

    public BigDecimal getRailPoleDistance() {
        return railPoleDistance;
    }

    public void setRailPoleDistance(BigDecimal railPoleDistance) {
        this.railPoleDistance = railPoleDistance;
    }

    public Long getTrackId() {
        return trackId;
    }

    public void setTrackId(Long trackId) {
        this.trackId = trackId;
    }

    public DisconnectorDto getDisconnector() {
        return disconnector;
    }

    public void setDisconnector(DisconnectorDto disconnector) {
        this.disconnector = disconnector;
    }

    public List<CantileverDto> getCantilevers() {
        return cantilevers;
    }

    public void setCantilevers(List<CantileverDto> cantilevers) {
        this.cantilevers = cantilevers;
    }

    public LovRef getProfileStatus() {
        return profileStatus;
    }

    public void setProfileStatus(LovRef profileStatus) {
        this.profileStatus = profileStatus;
    }

    public LovRef getPoleType() {
        return poleType;
    }

    public void setPoleType(LovRef poleType) {
        this.poleType = poleType;
    }

    public LovRef getFoundation() {
        return foundation;
    }

    public void setFoundation(LovRef foundation) {
        this.foundation = foundation;
    }

    public LovRef getAnchorageFoundation() {
        return anchorageFoundation;
    }

    public void setAnchorageFoundation(LovRef anchorageFoundation) {
        this.anchorageFoundation = anchorageFoundation;
    }

    public LovRef getPortal() {
        return portal;
    }

    public void setPortal(LovRef portal) {
        this.portal = portal;
    }

    public LovRef getReturnSupport() {
        return returnSupport;
    }

    public void setReturnSupport(LovRef returnSupport) {
        this.returnSupport = returnSupport;
    }

    public LovRef getSupportType() {
        return supportType;
    }

    public void setSupportType(LovRef supportType) {
        this.supportType = supportType;
    }

    public LovRef getAssemblyConfiguration() {
        return assemblyConfiguration;
    }

    public void setAssemblyConfiguration(LovRef assemblyConfiguration) {
        this.assemblyConfiguration = assemblyConfiguration;
    }

    public List<LovRef> getSectionings() {
        return sectionings;
    }

    public void setSectionings(List<LovRef> sectionings) {
        this.sectionings = sectionings;
    }

    public List<LovRef> getAnchorages() {
        return anchorages;
    }

    public void setAnchorages(List<LovRef> anchorages) {
        this.anchorages = anchorages;
    }

    public List<LovRef> getSectioningFeedings() {
        return sectioningFeedings;
    }

    public void setSectioningFeedings(List<LovRef> sectioningFeedings) {
        this.sectioningFeedings = sectioningFeedings;
    }

    @Override
    public void forgetChildren() {
        this.cantilevers = null;
    }
}
