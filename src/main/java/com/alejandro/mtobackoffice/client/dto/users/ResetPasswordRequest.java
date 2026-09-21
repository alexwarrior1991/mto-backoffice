package com.alejandro.mtobackoffice.client.dto.users;

/** Una contrasena nueva; temporal ({@code null} tambien) obliga a cambiarla al entrar. Nunca se loguea. */
public record ResetPasswordRequest(String password, Boolean temporary) {

    @Override
    public String toString() {
        return "ResetPasswordRequest[password=***, temporary=" + temporary + "]";
    }
}
