package com.alejandro.mtobackoffice.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.server.menu.MenuConfiguration;
import com.vaadin.flow.server.menu.MenuEntry;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;

import java.util.List;

/**
 * Marco de todas las pantallas: barra con la persona y «Salir», y menu lateral con las vistas
 * anotadas con {@code @Menu} <b>a las que esta persona puede entrar</b>.
 *
 * <p>El filtro del menu es el mismo que aplica el control de navegacion ({@code @RolesAllowed} de
 * cada vista, comprobado por {@link AccessAnnotationChecker}): una pantalla que el servicio
 * respondera con 403 no se ofrece. Es solo cortesia: la guarda real esta en la vista y en el
 * servicio.</p>
 *
 * <p>{@code @PermitAll} porque Vaadin comprueba tambien el layout padre: basta con haber entrado,
 * y son las vistas las que piden un rol concreto.</p>
 */
@PermitAll
public class MainLayout extends AppLayout {

    private final AuthenticationContext authenticationContext;
    private final AccessAnnotationChecker accessChecker;

    public MainLayout(AuthenticationContext authenticationContext, AccessAnnotationChecker accessChecker) {
        this.authenticationContext = authenticationContext;
        this.accessChecker = accessChecker;
        setPrimarySection(Section.DRAWER);
        addToNavbar(header());
        addToDrawer(menu());
    }

    private Component header() {
        H1 title = new H1("MTO Backoffice");
        title.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        Span user = new Span(authenticationContext.getPrincipalName().orElse("sin sesion"));
        user.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);

        Button logout = new Button("Salir", new Icon("vaadin", "sign-out"), event -> authenticationContext.logout());
        logout.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);

        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), title, user, logout);
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.expand(title);
        header.addClassNames(LumoUtility.Padding.Horizontal.MEDIUM);
        return header;
    }

    private Component menu() {
        SideNav nav = new SideNav();
        for (MenuEntry entry : visibleMenuEntries()) {
            SideNavItem item = new SideNavItem(entry.title(), entry.path());
            if (entry.icon() != null && entry.icon().contains(":")) {
                String[] icon = entry.icon().split(":", 2);
                item.setPrefixComponent(new Icon(icon[0], icon[1]));
            }
            nav.addItem(item);
        }
        return nav;
    }

    /** Las entradas de menu registradas por Vaadin, filtradas por lo que esta persona puede abrir. */
    List<MenuEntry> visibleMenuEntries() {
        return MenuConfiguration.getMenuEntries().stream()
                .filter(entry -> entry.menuClass() == null || accessChecker.hasAccess(entry.menuClass()))
                .toList();
    }
}
