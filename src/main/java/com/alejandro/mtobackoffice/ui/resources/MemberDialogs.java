package com.alejandro.mtobackoffice.ui.resources;

import com.alejandro.mtobackoffice.client.dto.resources.OrganizationUnitDto;
import com.alejandro.mtobackoffice.client.dto.resources.OrganizationUnitKind;
import com.alejandro.mtobackoffice.client.dto.resources.PersonSummaryDto;
import com.alejandro.mtobackoffice.client.dto.resources.TeamMemberDto;
import com.alejandro.mtobackoffice.client.dto.resources.TeamMemberLeaveRequest;
import com.alejandro.mtobackoffice.client.dto.resources.TeamMemberRequest;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.error.ValidationApiException;
import com.alejandro.mtobackoffice.ui.support.Screens;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Paragraph;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Un lineman que entra en un equipo o sale de el, cada uno con su fecha (vacia, hoy). Que el equipo no
 * pase de 6 ni baje de 2 ya lo cuida la pestana, que no ofrece el boton; si aun asi llega, o la persona
 * ya es lineman o jefe de otro equipo, el servicio responde 409 {@code UNI-001} y el dialogo sigue
 * abierto.
 */
final class MemberDialogs {

    static final String ADD_SAVE_ID = "member-add-save";
    static final String LEAVE_SAVE_ID = "member-leave-save";

    private MemberDialogs() {
    }

    static Dialog add(OrganizationUnitDto team, ResourcesClients clients, Consumer<OrganizationUnitDto> changed) {
        Dialog dialog = dialog("Anadir lineman a " + team.label());
        ComboBox<PersonSummaryDto> person = ResourcesPickers.person("Lineman", clients.people(), () -> OrganizationUnitKind.LINEMAN_CATEGORIES);
        person.setId("member-person");
        person.setRequiredIndicatorVisible(true);
        DatePicker joinedOn = date("member-joined-on", "Entra el");
        dialog.add(new FormLayout(person, joinedOn));
        Button save = new Button("Anadir", click -> {
            if (person.getValue() == null) {
                person.setErrorMessage("Elige quien entra");
                person.setInvalid(true);
                return;
            }
            if (person.getValue().equals(team.head())) {
                person.setErrorMessage(team.head().label() + " es el jefe del equipo: no va tambien de lineman");
                person.setInvalid(true);
                return;
            }
            try {
                OrganizationUnitDto result = clients.organization().addMember(team.id(), new TeamMemberRequest(person.getValue().id(), joinedOn.getValue()));
                dialog.close();
                Screens.success(person.getValue().label() + " entra en " + result.code());
                changed.accept(result);
            } catch (ValidationApiException validation) {
                Screens.showValidation(Map.of("personId", person, "joinedOn", joinedOn), validation);
            } catch (BackofficeApiException failure) {
                UiErrors.show(failure);
            }
        });
        footer(dialog, save, ADD_SAVE_ID);
        return dialog;
    }

    static Dialog leave(OrganizationUnitDto team, TeamMemberDto member, ResourcesClients clients, Consumer<OrganizationUnitDto> changed) {
        Dialog dialog = dialog(member.person().label() + " sale de " + team.label());
        DatePicker leftOn = date("member-left-on", "Sale el");
        dialog.add(new Paragraph("Deja de ser lineman del equipo desde ese dia; su paso por el queda en «Ver tambien los que salieron»."),
                new FormLayout(leftOn));
        Button save = new Button("Sale del equipo", click -> {
            try {
                OrganizationUnitDto result = clients.organization().leave(team.id(), member.person().id(), new TeamMemberLeaveRequest(leftOn.getValue()));
                dialog.close();
                Screens.success(member.person().label() + " sale de " + result.code());
                changed.accept(result);
            } catch (ValidationApiException validation) {
                Screens.showValidation(Map.of("leftOn", leftOn), validation);
            } catch (BackofficeApiException failure) {
                UiErrors.show(failure);
            }
        });
        footer(dialog, save, LEAVE_SAVE_ID);
        return dialog;
    }

    private static Dialog dialog(String title) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(title);
        dialog.setCloseOnOutsideClick(false);
        dialog.setWidth("min(90vw, 520px)");
        return dialog;
    }

    private static DatePicker date(String id, String label) {
        DatePicker picker = new DatePicker(label);
        picker.setId(id);
        picker.setHelperText("Vacio: hoy");
        return picker;
    }

    private static void footer(Dialog dialog, Button save, String id) {
        save.setId(id);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        dialog.getFooter().add(new Button("Cancelar", click -> dialog.close()), save);
    }
}
