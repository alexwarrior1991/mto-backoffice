package com.alejandro.mtobackoffice.ui.resources;

import com.alejandro.mtobackoffice.client.dto.resources.PositionDiscrepancyDto;
import com.alejandro.mtobackoffice.client.dto.resources.PositionDiscrepancyReportDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.error.ServiceUnavailableApiException;
import com.alejandro.mtobackoffice.configuration.security.ResourcesRoles;
import com.alejandro.mtobackoffice.ui.MainLayout;
import com.alejandro.mtobackoffice.ui.support.Formats;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;

/**
 * Puestos y perfiles, como en mto-frontend: el informe de mto-resources que cruza la categoria de cada
 * persona con el perfil de puesto ({@code mto-position-*}) de su usuario en Keycloak. Cuatro secciones:
 * con otro perfil que el de su categoria, con varios, sin ninguno, y usuarios con perfil de puesto y sin
 * ficha. Es una lectura: arreglarlo es cambiar la categoria aqui o el perfil en Usuarios, y lo calcula el
 * servicio cada vez que se pide («Actualizar»). Con Keycloak caido el servicio responde 503 {@code KCK-503}:
 * se dice en el sitio del informe, con «Reintentar», y sin notificacion.
 */
@Route(value = ResourcesRoutes.DISCREPANCIES, layout = MainLayout.class)
@PageTitle("Puestos y perfiles")
@Menu(title = "Puestos y perfiles", order = 78, icon = "vaadin:user-check")
@RolesAllowed(ResourcesRoles.RESOURCES_READ)
public class PositionDiscrepanciesView extends VerticalLayout {

    static final String WRONG_PROFILE = "Con otro perfil";
    static final String SEVERAL_PROFILES = "Con varios perfiles";
    static final String WITHOUT_PROFILE = "Sin perfil de puesto";
    static final String WITHOUT_RECORD = "Con perfil y sin ficha";
    static final String NOTHING = "Nada";
    static final String UNAVAILABLE_ID = "discrepancies-unavailable";
    static final String RETRY_ID = "discrepancies-retry";

    private final ResourcesClients clients;
    private final Div figures = new Div();
    private final VerticalLayout report = new VerticalLayout();

    public PositionDiscrepanciesView(ResourcesClients clients) {
        this.clients = clients;
        setSizeFull();
        figures.setId("discrepancies-figures");
        Button refresh = new Button("Actualizar", VaadinIcon.REFRESH.create(), click -> refresh());
        refresh.setId("discrepancies-refresh");
        HorizontalLayout toolbar = new HorizontalLayout(figures, refresh);
        toolbar.setAlignItems(FlexComponent.Alignment.BASELINE);
        toolbar.setWidthFull();
        toolbar.expand(figures);
        report.setPadding(false);
        report.setId("discrepancies-report");
        add(new H2("Puestos y perfiles"), toolbar, report);
        refresh();
    }

    void refresh() {
        report.removeAll();
        figures.setText("");
        PositionDiscrepancyReportDto loaded;
        try {
            loaded = clients.reports().positionDiscrepancies();
        } catch (ServiceUnavailableApiException unavailable) {
            if (!"KCK-503".equals(unavailable.getProblem().code())) {
                UiErrors.show(unavailable);
                return;
            }
            Div message = new Div(UiErrors.message(unavailable));
            message.setId(UNAVAILABLE_ID);
            Button retry = new Button("Reintentar", VaadinIcon.REFRESH.create(), click -> refresh());
            retry.setId(RETRY_ID);
            report.add(message, retry);
            return;
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
            return;
        }
        figures.setText("Calculado el " + Formats.dateTime(loaded.generatedAt()) + " · " + count(loaded.peopleChecked()) + " personas revisadas · "
                + count(loaded.usersWithPosition()) + " usuarios con perfil de puesto");
        report.add(section("wrong-profile", WRONG_PROFILE, loaded.wrongProfile()),
                section("several-profiles", SEVERAL_PROFILES, loaded.severalProfiles()),
                section("without-profile", WITHOUT_PROFILE, loaded.withoutProfile()),
                section("without-record", WITHOUT_RECORD, loaded.withoutRecord()));
    }

    private static int count(Integer value) {
        return value == null ? 0 : value;
    }

    /** Una seccion con su recuento y su tabla; vacia, dice «Nada». */
    private static Component section(String key, String title, List<PositionDiscrepancyDto> rows) {
        VerticalLayout section = new VerticalLayout(new H4(title + " (" + rows.size() + ")"));
        section.setPadding(false);
        section.setId("discrepancies-" + key);
        if (rows.isEmpty()) {
            Span nothing = new Span(NOTHING);
            nothing.setId("discrepancies-" + key + "-empty");
            section.add(nothing);
            return section;
        }
        Grid<PositionDiscrepancyDto> grid = new Grid<>();
        grid.setId("discrepancies-" + key + "-grid");
        grid.addComponentColumn(row -> person(key, row)).setHeader("Persona").setKey("person").setAutoWidth(true);
        grid.addColumn(PositionDiscrepancyDto::username).setHeader("Usuario").setKey("username").setAutoWidth(true);
        grid.addColumn(row -> row.category() == null ? "" : row.category().label()).setHeader("Categoria").setKey("category").setAutoWidth(true);
        grid.addColumn(PositionDiscrepancyDto::expectedProfile).setHeader("Perfil esperado").setKey("expectedProfile").setAutoWidth(true);
        grid.addColumn(row -> String.join(", ", row.profilesOrEmpty())).setHeader("Perfiles").setKey("profiles").setAutoWidth(true);
        grid.setItems(rows);
        grid.setAllRowsVisible(true);
        section.add(grid);
        return section;
    }

    /** La persona como enlace a su ficha; sin ficha, nada que abrir. */
    private static Component person(String key, PositionDiscrepancyDto row) {
        if (row.personId() == null) {
            return new Span(row.personLabel());
        }
        RouterLink link = new RouterLink(row.personLabel(), PersonDetailView.class, PersonDetailView.parametersOf(row.personId()));
        link.setId("discrepancies-" + key + "-person-" + row.personId());
        return link;
    }
}
