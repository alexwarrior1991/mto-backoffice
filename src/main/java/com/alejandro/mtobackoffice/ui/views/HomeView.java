package com.alejandro.mtobackoffice.ui.views;

import com.alejandro.mtobackoffice.configuration.security.BackofficeUser;
import com.alejandro.mtobackoffice.ui.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

/**
 * Pantalla de inicio: quien ha entrado y con que. El panel de diagnostico existe para cerrar el
 * circuito completo (realm → token → audiencias → gateway → servicio) a simple vista: las cinco
 * APIs tienen que estar en {@code aud} y los permisos tienen que ser roles de cliente
 * ({@code ROLE_CONFIG_*}), nunca solo perfiles de realm ({@code ROLE_REALM_*}).
 */
@Route(value = "", layout = MainLayout.class)
@PageTitle("Inicio")
@Menu(title = "Inicio", order = 0, icon = "vaadin:home")
@PermitAll
public class HomeView extends VerticalLayout {

    /** Las audiencias que un solo token tiene que llevar para valer en todo el dominio. */
    static final List<String> EXPECTED_AUDIENCES = List.of(
            "mto-configuration-api", "mto-stock-api", "mto-maintenance-api", "mto-users-api", "mto-gateway-api");

    public HomeView(AuthenticationContext authenticationContext) {
        add(new H2("Inicio"));
        String name = authenticationContext.getPrincipalName().orElse("nadie");
        add(new Paragraph("Has entrado como " + name + "."));

        authenticationContext.getAuthenticatedUser(Object.class)
                .filter(BackofficeUser.class::isInstance)
                .map(BackofficeUser.class::cast)
                .ifPresent(user -> {
                    add(new H3("Audiencias del access token"));
                    add(audienceList(user.getAccessTokenAudience()));
                });

        add(new H3("Autoridades"));
        List<String> authorities = authenticationContext.getGrantedAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .sorted()
                .toList();
        add(list(authorities.isEmpty() ? List.of("(ninguna)") : authorities));
    }

    private static Component audienceList(List<String> audience) {
        UnorderedList list = new UnorderedList();
        for (String expected : EXPECTED_AUDIENCES) {
            boolean present = audience.contains(expected);
            ListItem item = new ListItem((present ? "✔ " : "✘ ") + expected + (present ? "" : " (falta)"));
            item.getElement().setAttribute("data-audience", expected);
            item.getElement().setAttribute("data-present", String.valueOf(present));
            list.add(item);
        }
        audience.stream().filter(value -> !EXPECTED_AUDIENCES.contains(value))
                .forEach(other -> list.add(new ListItem("• " + other)));
        return list;
    }

    private static Component list(List<String> values) {
        UnorderedList list = new UnorderedList();
        values.forEach(value -> list.add(new ListItem(value)));
        return list;
    }
}
