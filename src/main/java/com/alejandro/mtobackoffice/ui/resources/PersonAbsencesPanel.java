package com.alejandro.mtobackoffice.ui.resources;

import com.alejandro.mtobackoffice.client.dto.resources.AbsenceKind;
import com.alejandro.mtobackoffice.client.dto.resources.PersonAbsenceDto;
import com.alejandro.mtobackoffice.client.dto.resources.PersonDto;
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
 * Las ausencias de una persona (vacaciones, bajas, formacion...), la mas reciente primero, con los dias
 * que dura cada una contando los dos extremos. Anadir y modificar piden {@code resources-write}; quitar (un
 * borrado de verdad, que el historial del servicio conserva), {@code resources-delete} y confirmacion. Una de
 * una clase que no se conoce se ve, pero no se toca. Cualquier cambio publica la persona, y mto-maintenance
 * deja fuera de sus turnos a quien esta ausente a la fecha del turno (sin saber por que).
 */
class PersonAbsencesPanel extends LazyPanel {

    static final String GRID_ID = "absences-grid";
    static final String ACTIONS_COLUMN = "actions";

    private final Supplier<PersonDto> person;
    private final ResourcesClients clients;
    private final boolean canWrite;
    private final boolean canDelete;
    private final Span count = new Span();
    private final Button add = new Button("Nueva ausencia", VaadinIcon.PLUS.create());
    private final Grid<PersonAbsenceDto> grid = new Grid<>();

    PersonAbsencesPanel(Supplier<PersonDto> person, ResourcesClients clients, boolean canWrite, boolean canDelete) {
        this.person = person;
        this.clients = clients;
        this.canWrite = canWrite;
        this.canDelete = canDelete;
        count.setId("absences-count");
        add.setId("absence-add");
        add.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        add.setVisible(canWrite);
        add.addClickListener(click -> new PersonAbsenceDialog(person.get(), null, clients, this::reload).open());
        HorizontalLayout toolbar = new HorizontalLayout(count, add);
        toolbar.setAlignItems(FlexComponent.Alignment.BASELINE);
        toolbar.setWidthFull();
        toolbar.expand(count);

        grid.setId(GRID_ID);
        grid.addColumn(PersonAbsenceDto::label).setHeader("Clase").setKey("kind").setAutoWidth(true);
        grid.addColumn(absence -> ResourcesFormats.date(absence.startsOn())).setHeader("Desde").setKey("startsOn").setAutoWidth(true);
        grid.addColumn(absence -> ResourcesFormats.date(absence.endsOn())).setHeader("Hasta").setKey("endsOn").setAutoWidth(true);
        grid.addColumn(PersonAbsenceDto::days).setHeader("Dias").setKey("days").setAutoWidth(true);
        grid.addColumn(PersonAbsenceDto::notes).setHeader("Notas").setKey("notes").setFlexGrow(1);
        grid.addColumn(new ComponentRenderer<>(this::rowActions)).setHeader("").setKey(ACTIONS_COLUMN).setAutoWidth(true).setFlexGrow(0);
        grid.setAllRowsVisible(true);
        grid.setEmptyStateText("Sin ausencias apuntadas.");
        add(toolbar, grid);
    }

    /** «del 02/11/2026 al 13/11/2026», como se dice en los avisos y las confirmaciones. */
    static String period(PersonAbsenceDto absence) {
        return "del " + ResourcesFormats.date(absence.startsOn()) + " al " + ResourcesFormats.date(absence.endsOn());
    }

    /** La columna existe siempre, como en las demas listas; una de una clase desconocida no ofrece nada. */
    private Component rowActions(PersonAbsenceDto absence) {
        HorizontalLayout actions = new HorizontalLayout();
        actions.setSpacing(false);
        if (absence.kind() == null || absence.kind() == AbsenceKind.UNKNOWN) {
            return actions;
        }
        String name = absence.label() + " " + period(absence);
        if (canWrite) {
            actions.add(Screens.rowButton("absence-edit-" + absence.id(), VaadinIcon.EDIT, "Modificar " + name,
                    click -> new PersonAbsenceDialog(person.get(), absence, clients, this::reload).open()));
        }
        if (canDelete) {
            Button remove = Screens.rowButton("absence-remove-" + absence.id(), VaadinIcon.TRASH, "Quitar " + name,
                    click -> Screens.confirm("Quitar la ausencia de " + person.get().label(),
                            "Se borra la ausencia " + period(absence) + "; el historial del servicio la conserva. La persona vuelve a "
                                    + "estar disponible esos dias para los turnos.", "Quitar", () -> remove(absence)));
            remove.addThemeVariants(ButtonVariant.LUMO_ERROR);
            actions.add(remove);
        }
        return actions;
    }

    private void remove(PersonAbsenceDto absence) {
        try {
            clients.people().removeAbsence(person.get().id(), absence.id());
            Screens.success("Quitada la ausencia (" + period(absence) + ")");
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
        reload();
    }

    @Override
    protected void load() {
        List<PersonAbsenceDto> current;
        try {
            current = clients.people().absences(person.get().id());
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
            current = List.of();
        }
        count.setText(current.size() == 1 ? "1 ausencia" : current.size() + " ausencias");
        grid.setItems(current);
    }
}
