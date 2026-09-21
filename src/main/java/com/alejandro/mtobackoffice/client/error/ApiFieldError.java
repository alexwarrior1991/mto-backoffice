package com.alejandro.mtobackoffice.client.error;

/** Un error de campo de mto-configuration: {@code errors[]} trae {field, code, message}, tres campos. */
public record ApiFieldError(String field, String code, String message) {
}
