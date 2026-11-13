package com.alejandro.mtobackoffice.client.dto.notification;

/**
 * {@code POST /admin/test-email}: a que direccion. El servicio exige un email de 320 caracteres
 * como mucho.
 */
public record TestEmailRequest(String to) {
}
