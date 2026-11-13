package com.alejandro.mtobackoffice.ui.notification;

import com.alejandro.mtobackoffice.client.dto.notification.TestEmailRequest;

/** Lo que el Binder del correo de prueba lee y escribe; {@code to} se llama como en la peticion. */
public class TestEmailForm {

    /** El tope de {@code TestEmailRequest.to} en el servicio. */
    public static final int MAX_LENGTH = 320;

    private String to = "";

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public TestEmailRequest toRequest() {
        return new TestEmailRequest(to.trim());
    }
}
