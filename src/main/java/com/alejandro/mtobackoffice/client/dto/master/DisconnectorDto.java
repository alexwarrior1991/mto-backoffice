package com.alejandro.mtobackoffice.client.dto.master;


/**
 * Seccionador: cuelga de una estacion y de un perfil, con su funcion de catalogo. Sin hijos.
 * {@code profileCode} y {@code profileKp} llegan del servicio para ensenar el perfil sin ir a
 * buscarlo; al escribir se ignoran, el perfil se elige por {@code profileId}.
 */
public class DisconnectorDto extends MasterDto {

    private String name;
    private Boolean onLoad = Boolean.FALSE;
    private Long stationId;
    private Long profileId;
    /** Identificador y KP del perfil, solo de salida: el servicio los rellena para que la lista se lea. */
    private String profileCode;
    private String profileKp;
    private LovRef disconnectorFunction;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getOnLoad() {
        return onLoad;
    }

    public void setOnLoad(Boolean onLoad) {
        this.onLoad = onLoad;
    }

    public Long getStationId() {
        return stationId;
    }

    public void setStationId(Long stationId) {
        this.stationId = stationId;
    }

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    public String getProfileCode() {
        return profileCode;
    }

    public void setProfileCode(String profileCode) {
        this.profileCode = profileCode;
    }

    public String getProfileKp() {
        return profileKp;
    }

    public void setProfileKp(String profileKp) {
        this.profileKp = profileKp;
    }

    /** El perfil como se ensena: identificador y KP si el servicio los manda; si no, el id. */
    public String profileLabel() {
        if (profileCode != null) {
            return profileKp == null ? profileCode : profileCode + " (kp " + profileKp + ")";
        }
        return profileId == null ? "" : "#" + profileId;
    }

    public LovRef getDisconnectorFunction() {
        return disconnectorFunction;
    }

    public void setDisconnectorFunction(LovRef disconnectorFunction) {
        this.disconnectorFunction = disconnectorFunction;
    }

    @Override
    public void forgetChildren() {
        // sin colecciones de hijos
    }
}
