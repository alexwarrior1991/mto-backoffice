package com.alejandro.mtobackoffice.client.dto.master;


/** Seccionador: cuelga de una estacion y de un perfil, con su funcion de catalogo. Sin hijos. */
public class DisconnectorDto extends MasterDto {

    private String name;
    private Boolean onLoad = Boolean.FALSE;
    private Long stationId;
    private Long profileId;
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
