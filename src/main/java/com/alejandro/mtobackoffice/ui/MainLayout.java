package com.alejandro.mtobackoffice.ui;

import com.alejandro.mtobackoffice.client.configuration.LovResource;
import com.alejandro.mtobackoffice.ui.lov.LovCrudView;
import com.alejandro.mtobackoffice.ui.maintenance.MaintenanceRoutes;
import com.alejandro.mtobackoffice.ui.master.MasterView;
import com.alejandro.mtobackoffice.ui.stock.StockRoutes;
import com.alejandro.mtobackoffice.ui.users.UsersView;
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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    /** Un grupo del menu: las vistas cuya ruta empieza por su prefijo cuelgan de el. */
    private record MenuGroup(String label, String icon) {
    }

    /**
     * Prefijo de ruta (primer segmento) → grupo. La lista de usuarios, la de existencias y la de
     * ordenes son a la vez el nodo de su grupo.
     */
    private static final Map<String, MenuGroup> GROUPS = Map.of(
            MasterView.ROUTE_PREFIX, new MenuGroup("Infraestructura", "train"),
            UsersView.ROUTE_PREFIX, new MenuGroup("Usuarios", "users"),
            StockRoutes.PREFIX, new MenuGroup("Almacen", "storage"),
            MaintenanceRoutes.PREFIX, new MenuGroup("Mantenimiento", "wrench"));

    private Component menu() {
        SideNav nav = new SideNav();
        // Las vistas con @Menu cuya ruta empieza por un prefijo conocido cuelgan de su grupo, y asi
        // el menu no se hace una lista plana. Una entrada cuya ruta ES el prefijo (la lista de
        // usuarios, en "usuarios") es el propio nodo del grupo: se navega a ella y de ella cuelgan
        // las demas; si no hay tal entrada, el nodo es solo una etiqueta (Infraestructura).
        List<MenuEntry> entries = visibleMenuEntries();
        Map<String, SideNavItem> groups = new LinkedHashMap<>();
        for (MenuEntry entry : entries) {
            String path = pathOf(entry);
            if (GROUPS.containsKey(path)) {
                groups.put(path, navItem(entry));
            }
        }
        for (MenuEntry entry : entries) {
            String path = pathOf(entry);
            String prefix = path.contains("/") ? path.substring(0, path.indexOf('/')) : path;
            MenuGroup definition = GROUPS.get(prefix);
            if (definition == null) {
                nav.addItem(navItem(entry));
                continue;
            }
            SideNavItem group = groups.get(prefix);
            if (group == null) {
                group = new SideNavItem(definition.label());
                group.setPrefixComponent(new Icon("vaadin", definition.icon()));
                groups.put(prefix, group);
            }
            if (group.getParent().isEmpty()) {
                group.setExpanded(true);
                nav.addItem(group);
            }
            if (!path.equals(prefix)) {
                group.addItem(navItem(entry));
            }
        }
        // Los catalogos son una sola vista con el recurso en la ruta, asi que no pueden anotarse
        // con @Menu: se listan a mano, y solo si la persona puede abrir la vista.
        if (accessChecker.hasAccess(LovCrudView.class)) {
            SideNavItem catalogues = new SideNavItem("Catalogos");
            catalogues.setPrefixComponent(new Icon("vaadin", "list"));
            for (LovResource resource : LovResource.values()) {
                catalogues.addItem(new SideNavItem(resource.title(), LovCrudView.pathOf(resource)));
            }
            nav.addItem(catalogues);
        }
        return nav;
    }

    private static SideNavItem navItem(MenuEntry entry) {
        SideNavItem item = new SideNavItem(entry.title(), entry.path());
        if (entry.icon() != null && entry.icon().contains(":")) {
            String[] icon = entry.icon().split(":", 2);
            item.setPrefixComponent(new Icon(icon[0], icon[1]));
        }
        return item;
    }

    private static String pathOf(MenuEntry entry) {
        return entry.path().startsWith("/") ? entry.path().substring(1) : entry.path();
    }

    /** Las entradas de menu registradas por Vaadin, filtradas por lo que esta persona puede abrir. */
    List<MenuEntry> visibleMenuEntries() {
        return MenuConfiguration.getMenuEntries().stream()
                .filter(entry -> entry.menuClass() == null || accessChecker.hasAccess(entry.menuClass()))
                .toList();
    }
}
