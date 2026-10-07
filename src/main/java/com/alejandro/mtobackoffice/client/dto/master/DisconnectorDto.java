package com.alejandro.mtobackoffice.client.dto.master;


/**
 * Seccionador: cuelga de una estacion y, si esta en uno, de un perfil, con su funcion de catalogo.
 * Sin hijos. {@code profileCode} y {@code profileKp} llegan del servicio para ensenar el perfil sin
 * ir a buscarlo; al escribir se ignoran, el perfil se elige por {@code profileId}.
 */
public class DisconnectorDto extends MasterDto {

    private String name;
    private Boolean onLoad = Boolean.FALSE;
    /** Estado normal: {@code true} normalmente abierto, {@code false} normalmente cerrado, {@code null} sin dato. */
    private Boolean normallyOpen;
    /** Accionamiento, {@code null} sin dato. */
    private DisconnectorDriveType driveType;
    private Long stationId;
    /** Opcional: un seccionador que no esta en un poste no lleva perfil. */
    private Long profileId;
    /**
     * KP en metros y via, solo de un seccionador sin poste (V26 de mto-configuration): los de uno en
     * un poste son los de su perfil, y el servicio rechaza los dos con 400. El KP es texto, como el del
     * perfil.
     */
    private String kp;
    private Long trackId;
    /**
     * La otra via de uno que pone dos en paralelo (V27 de mto-configuration), con poste o sin el.
     * Opcional, y nunca la suya: lo decide el servicio.
     */
    private Long connectedTrackId;
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

    public Boolean getNormallyOpen() {
        return normallyOpen;
    }

    public void setNormallyOpen(Boolean normallyOpen) {
        this.normallyOpen = normallyOpen;
    }

    public DisconnectorDriveType getDriveType() {
        return driveType;
    }

    public void setDriveType(DisconnectorDriveType driveType) {
        this.driveType = driveType;
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

    public String getKp() {
        return kp;
    }

    public void setKp(String kp) {
        this.kp = kp;
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
