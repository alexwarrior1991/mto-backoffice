package com.alejandro.mtobackoffice.client.dto.master;

/** Brazo de atirantado de una mensula (1:1): longitud y tipo de catalogo. */
public class SteadyArmDto extends MasterDto {

    private Long length;
    private LovRef steadyArmType;
    private Long cantileverId;

    public Long getLength() {
        return length;
    }

    public void setLength(Long length) {
        this.length = length;
    }

    public LovRef getSteadyArmType() {
        return steadyArmType;
    }

    public void setSteadyArmType(LovRef steadyArmType) {
        this.steadyArmType = steadyArmType;
    }

    public Long getCantileverId() {
        return cantileverId;
    }

    public void setCantileverId(Long cantileverId) {
        this.cantileverId = cantileverId;
    }

    @Override
    public void forgetChildren() {
        // sin hijos
    }
}
