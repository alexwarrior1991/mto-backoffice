package com.alejandro.mtobackoffice.client.dto;

/**
 * Una entrada de lista de valores de mto-configuration. Solo las claves que usa la UI: un campo
 * nuevo alla no rompe nada aqui (los campos de auditoria de BaseDTO se ignoran).
 */
public record LovDto(Long id, String code, String description, String type, Boolean enabled) {
}
