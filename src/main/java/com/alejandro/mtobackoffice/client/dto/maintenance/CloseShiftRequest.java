package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

/**
 * Cerrar un turno en curso. Sin {@code actualEnd}, ahora; sin {@code netWorkMinutes}, los calcula
 * el servicio desde el corte de tension (o el inicio). Las tareas sin terminar vuelven a su orden.
 * {@code parkings} dice donde quedo cada vehiculo del turno; sin ellos, vale el estacionamiento del turno.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CloseShiftRequest(Instant actualEnd, Instant voltageCutoffAt, Integer netWorkMinutes, String observations,
                                List<VehicleParkingRequest> parkings) {

    public CloseShiftRequest(Instant actualEnd, Instant voltageCutoffAt, Integer netWorkMinutes, String observations) {
        this(actualEnd, voltageCutoffAt, netWorkMinutes, observations, null);
    }
}
