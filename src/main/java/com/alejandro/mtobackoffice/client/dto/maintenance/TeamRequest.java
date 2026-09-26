package com.alejandro.mtobackoffice.client.dto.maintenance;

import java.util.Set;

/**
 * Alta y modificacion de un equipo. El {@code PUT} es completo, no parcial como los demas: base y
 * vehiculo a {@code null} se borran, asi que el {@code null} tiene que viajar (sin
 * {@code NON_NULL}). No hay borrado: un equipo se retira con {@code active=false}.
 */
public record TeamRequest(String code, String name, String baseName, String vehicle, Boolean active, Set<Long> executionPackageIds) {
}
