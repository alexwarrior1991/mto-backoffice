package com.alejandro.mtobackoffice.ui.users;

import com.alejandro.mtobackoffice.client.dto.users.UserEnabledRequest;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.users.UsersClient;

import java.util.ArrayList;
import java.util.List;

/**
 * «Sacar a la persona»: lo que el README de mto-users deja en manos del cliente, en el orden que
 * manda. Desactivar solo bloquea el siguiente login; las sesiones abiertas siguen vivas hasta
 * cerrarlas, y un token offline sobrevive a las dos cosas hasta que se revoca. Tres llamadas, en
 * ese orden, y en el primer fallo se para: lo hecho queda hecho y se dice que paso fallo.
 */
public final class TakeOut {

    public enum Step {
        DISABLE("desactivar"),
        SESSIONS("cerrar las sesiones"),
        OFFLINE("revocar las sesiones offline");

        private final String label;

        Step(String label) {
            this.label = label;
        }

        public String label() {
            return label;
        }
    }

    /** Los pasos completados y, si algo fallo, el paso y el fallo. */
    public record Result(List<Step> done, Step failed, BackofficeApiException failure) {

        public boolean isComplete() {
            return failed == null;
        }
    }

    private TakeOut() {
    }

    public static Result run(UsersClient client, String userId) {
        List<Step> done = new ArrayList<>();
        try {
            client.setEnabled(userId, new UserEnabledRequest(false));
            done.add(Step.DISABLE);
            client.revokeAllSessions(userId);
            done.add(Step.SESSIONS);
            client.revokeAllOfflineSessions(userId);
            done.add(Step.OFFLINE);
            return new Result(List.copyOf(done), null, null);
        } catch (BackofficeApiException failure) {
            return new Result(List.copyOf(done), Step.values()[done.size()], failure);
        }
    }
}
