package com.alejandro.mtobackoffice.ui.resources;

import com.alejandro.mtobackoffice.client.dto.MergePatch;
import com.alejandro.mtobackoffice.client.dto.resources.OrganizationUnitDto;
import com.alejandro.mtobackoffice.client.dto.resources.OrganizationUnitKind;
import com.alejandro.mtobackoffice.client.dto.resources.OrganizationUnitUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.resources.PersonSummaryDto;
import com.alejandro.mtobackoffice.client.dto.resources.TeamMemberDto;
import com.alejandro.mtobackoffice.client.dto.resources.TeamMembersRequest;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.error.ValidationApiException;
import com.alejandro.mtobackoffice.ui.support.Screens;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Los linemen de un equipo, enteros y de una vez: cambiar varios sin bajar de 2 ni pasar de 6
 * ({@code PUT /members}: los que faltan salen y los nuevos entran en esa fecha), o reactivar un equipo,
 * que vuelve completo ({@code PATCH active=true} con sus linemen). Lo evidente se comprueba antes de
 * llamar: de 2 a 6 y sin el jefe; lo demas (alguien que ya es lineman de otro equipo, una persona
 * inactiva) lo dice el servicio con el dialogo abierto.
 */
public final class TeamMembersDialog extends Dialog {

    public static final String SAVE_ID = "members-save";

    private final MultiSelectComboBox<PersonSummaryDto> people;
    private final DatePicker date;
    private final Span count = new Span();
    private final OrganizationUnitDto team;

    private TeamMembersDialog(OrganizationUnitDto team, Set<PersonSummaryDto> chosen, boolean reactivating, ResourcesClients clients,
                              Consumer<OrganizationUnitDto> changed) {
        this.team = team;
        setHeaderTitle((reactivating ? "Reactivar " : "Cambiar los linemen de ") + team.label());
        setCloseOnOutsideClick(false);
        setWidth("min(90vw, 640px)");
        people = ResourcesPickers.people("Linemen", clients.people(), () -> OrganizationUnitKind.LINEMAN_CATEGORIES);
        people.setId("members-people");
        people.setRequiredIndicatorVisible(true);
        people.setValue(chosen);
        people.addValueChangeListener(change -> countChosen());
        date = new DatePicker(reactivating ? "Entran el" : "Desde el");
        date.setId("members-date");
        date.setHelperText("Vacio: hoy");
        count.setId("members-count");
        count.getElement().getThemeList().add("badge");
        countChosen();
        FormLayout layout = new FormLayout(people, date);
        layout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        add(new Paragraph(reactivating
                ? "Un equipo vuelve completo: con su jefe (" + ResourcesFormats.head(team.head()) + ") y de 2 a 6 linemen."
                : "La lista entera: los que no esten salen del equipo y los nuevos entran, en la misma fecha."), layout, count);

        Button save = new Button(reactivating ? "Reactivar" : "Guardar", click -> save(reactivating, clients, changed));
        save.setId(SAVE_ID);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(new Button("Cancelar", click -> close()), save);
    }

    static TeamMembersDialog replace(OrganizationUnitDto team, List<TeamMemberDto> current, ResourcesClients clients,
                                     Consumer<OrganizationUnitDto> changed) {
        Set<PersonSummaryDto> chosen = new LinkedHashSet<>();
        current.stream().filter(TeamMemberDto::isCurrent).map(TeamMemberDto::person).forEach(chosen::add);
        return new TeamMembersDialog(team, chosen, false, clients, changed);
    }

    static TeamMembersDialog reactivate(OrganizationUnitDto team, ResourcesClients clients, Consumer<OrganizationUnitDto> changed) {
        return new TeamMembersDialog(team, Set.of(), true, clients, changed);
    }

    private void countChosen() {
        int chosen = people.getValue().size();
        count.setText(chosen + " de 2 a 6");
        count.getElement().getThemeList().set("error", chosen < TeamMembersPanel.MIN_LINEMEN || chosen > TeamMembersPanel.MAX_LINEMEN);
        people.setInvalid(false);
    }

    /** De 2 a 6 y sin el jefe entre ellos; si no, el campo lo dice y no se llama. */
    private boolean valid() {
        Set<PersonSummaryDto> chosen = people.getValue();
        String problem = null;
        if (chosen.size() < TeamMembersPanel.MIN_LINEMEN || chosen.size() > TeamMembersPanel.MAX_LINEMEN) {
            problem = "Un equipo trabaja con 2 a 6 linemen, no con " + chosen.size();
        } else if (team.head() != null && chosen.contains(team.head())) {
            problem = team.head().label() + " es el jefe del equipo: no va tambien de lineman";
        }
        people.setErrorMessage(problem);
        people.setInvalid(problem != null);
        return problem == null;
    }

    private void save(boolean reactivating, ResourcesClients clients, Consumer<OrganizationUnitDto> changed) {
        if (!valid()) {
            return;
        }
        List<UUID> ids = people.getValue().stream().map(PersonSummaryDto::id).toList();
        try {
            OrganizationUnitDto result = reactivating
                    ? clients.organization().update(team.id(), MergePatch.of(OrganizationUnitUpdateRequest.reactivateTeam(ids, date.getValue()),
                    team.version()))
                    : clients.organization().replaceMembers(team.id(), new TeamMembersRequest(ids, date.getValue()));
            close();
            Screens.success((reactivating ? "Reactivado " : "Linemen cambiados en ") + result.label());
            changed.accept(result);
        } catch (ValidationApiException validation) {
            Screens.showValidation(Map.of("memberPersonIds", people, "effectiveOn", date, "joinedOn", date), validation);
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }
}
