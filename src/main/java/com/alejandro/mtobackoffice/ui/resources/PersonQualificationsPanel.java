package com.alejandro.mtobackoffice.ui.resources;

import com.alejandro.mtobackoffice.client.dto.resources.PersonDto;
import com.alejandro.mtobackoffice.client.dto.resources.PersonQualificationDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.ui.support.LazyPanel;
import com.alejandro.mtobackoffice.ui.support.Screens;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import java.util.List;
import java.util.function.Supplier;

/**
 * Las cualificaciones de una persona, enteras y por el codigo de su tipo, con su estado: «Caducada» si
 * el servicio lo dice, «Caduca en N dias» hasta 30, «Vigente» o «No caduca» ({@link Expiry}). Anadir y
 * modificar piden {@code resources-write}; quitar (un borrado de verdad, que el historial del servicio
 * conserva), {@code resources-delete} y confirmacion. Cualquier cambio publica la persona, y
 * mto-maintenance deja fuera de sus turnos a quien tenga una caducada a la fecha del turno.
 */
class PersonQualificationsPanel extends LazyPanel {

    static final String GRID_ID = "qualifications-grid";
    static final String ACTIONS_COLUMN = "actions";
    static final String EXPIRED = "Caducada";
    static final String NO_EXPIRY = "No caduca";

    private final Supplier<PersonDto> person;
    private final ResourcesClients clients;
    private final boolean canWrite;
    private final boolean canDelete;
    private final Span count = new Span();
    private final Button add = new Button("Anadir cualificacion", VaadinIcon.PLUS.create());
    private final Grid<PersonQualificationDto> grid = new Grid<>();
    private List<PersonQualificationDto> current = List.of();

    PersonQualificationsPanel(Supplier<PersonDto> person, ResourcesClients clients, boolean canWrite, boolean canDelete) {
        this.person = person;
        this.clients = clients;
        this.canWrite = canWrite;
        this.canDelete = canDelete;
        count.setId("qualifications-count");
        add.setId("qualification-add");
        add.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        add.setVisible(canWrite);
        add.addClickListener(click -> new PersonQualificationDialog(person.get(), null, current, clients, this::reload).open());
        HorizontalLayout toolbar = new HorizontalLayout(count, add);
        toolbar.setAlignItems(FlexComponent.Alignment.BASELINE);
        toolbar.setWidthFull();
        toolbar.expand(count);

        grid.setId(GRID_ID);
        grid.addColumn(PersonQualificationDto::label).setHeader("Tipo").setKey("type").setAutoWidth(true);
        grid.addColumn(qualification -> qualification.qualificationType() == null || qualification.qualificationType().kind() == null ? ""
                : qualification.qualificationType().kind().label()).setHeader("Clase").setKey("kind").setAutoWidth(true);
        grid.addColumn(PersonQualificationDto::reference).setHeader("Referencia").setKey("reference").setAutoWidth(true);
        grid.addColumn(qualification -> ResourcesFormats.date(qualification.issuedOn())).setHeader("Expedida").setKey("issuedOn").setAutoWidth(true);
        grid.addColumn(qualification -> ResourcesFormats.date(qualification.expiresOn())).setHeader("Caduca").setKey("expiresOn").setAutoWidth(true);
        grid.addColumn(PersonQualificationsPanel::state).setHeader("Estado").setKey("state").setAutoWidth(true);
        grid.addColumn(new ComponentRenderer<>(this::rowActions)).setHeader("").setKey(ACTIONS_COLUMN).setAutoWidth(true).setFlexGrow(0);
        grid.setAllRowsVisible(true);
        grid.setEmptyStateText("Sin cualificaciones apuntadas.");
        add(toolbar, grid);
    }

    static String state(PersonQualificationDto qualification) {
        return Expiry.state(qualification.expiresOn(), qualification.expired(), EXPIRED, NO_EXPIRY);
    }

    /** La columna existe siempre, como en las demas listas, aunque quien solo lee no tenga nada que hacer en ella. */
    private Component rowActions(PersonQualificationDto qualification) {
        HorizontalLayout actions = new HorizontalLayout();
        actions.setSpacing(false);
        String code = qualification.qualificationType() == null ? "" : qualification.qualificationType().code();
        if (canWrite) {
            actions.add(Screens.rowButton("qualification-edit-" + qualification.id(), VaadinIcon.EDIT, "Modificar " + code,
                    click -> new PersonQualificationDialog(person.get(), qualification, current, clients, this::reload).open()));
        }
        if (canDelete) {
            Button remove = Screens.rowButton("qualification-remove-" + qualification.id(), VaadinIcon.TRASH, "Quitar " + code,
                    click -> Screens.confirm("Quitar " + qualification.label() + " de " + person.get().label(),
                            "Se borra la cualificacion de la persona; el historial del servicio la conserva. Renovarla no es esto: es cambiar "
                                    + "su caducidad.", "Quitar", () -> remove(qualification)));
            remove.addThemeVariants(ButtonVariant.LUMO_ERROR);
            actions.add(remove);
        }
        return actions;
    }

    private void remove(PersonQualificationDto qualification) {
        try {
            clients.people().removeQualification(person.get().id(), qualification.id());
            Screens.success("Quitada " + qualification.label());
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
        reload();
    }

    @Override
    protected void load() {
        try {
            current = clients.people().qualifications(person.get().id());
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
            current = List.of();
        }
        count.setText(current.size() == 1 ? "1 cualificacion" : current.size() + " cualificaciones");
        grid.setItems(current);
    }
}
