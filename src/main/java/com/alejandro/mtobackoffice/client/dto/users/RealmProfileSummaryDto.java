package com.alejandro.mtobackoffice.client.dto.users;

/**
 * Un perfil: un rol compuesto de realm con el prefijo {@code mto-} ({@code mto-users-admin}...).
 * No confundir con los perfiles ferroviarios de mto-configuration ({@code ProfileDto}).
 */
public record RealmProfileSummaryDto(String name, String description) {
}
