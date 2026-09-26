package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.dto.maintenance.AssignOrderRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.CommentRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.CompleteOrderRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.OrderDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.PlanOrderRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.TeamSummaryDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.error.ValidationApiException;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import java.time.LocalDate;
import java.util.function.Consumer;

/**
 * Una transicion de la orden con sus datos: planificar (con la fecha, que no puede ser pasada, y es
 * cuando se reservan los materiales), asignar (equipo, persona o los dos), iniciar y completar
 * (notas de cierre y, con {@code maintenance-supervise}, {@code force}). La pantalla solo la ofrece
 * en los estados de origen; si aun asi el servicio la rechaza, el dialogo sigue abierto.
 */
public class OrderTransitionDialog extends Dialog {

    public static final String CONFIRM_ID = "order-transition-confirm";

    /** Las transiciones con datos; cancelar va aparte, con su motivo. */
    public enum Kind {
        PLAN("Planificar"),
        ASSIGN("Asignar"),
        START("Iniciar"),
        COMPLETE("Completar");

        private final String label;

        Kind(String label) {
            this.label = label;
        }

        public String label() {
            return label;
        }
    }

    private final Binder<TransitionForm> binder = new Binder<>(TransitionForm.class);
    private final TransitionForm form = new TransitionForm();

    /**
     * @param canForce si se ofrece {@code force} al completar (write + supervise)
     * @param done     que hacer con la orden que devuelve el servicio
     */
    public OrderTransitionDialog(Kind kind, OrderDto order, MaintenanceClients clients, MaintenanceCatalogs catalogs, boolean canForce,
                                 Consumer<OrderDto> done) {
        setHeaderTitle(kind.label() + " " + order.code());
        setCloseOnOutsideClick(false);
        FormLayout layout = new FormLayout();
        layout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        TextArea comment = new TextArea("Comentario para el historial");
        comment.setId("order-transition-comment");

        switch (kind) {
            case PLAN -> {
                add(new Paragraph("Al planificar se reservan en el almacen los materiales de la orden."));
                DatePicker plannedDate = new DatePicker("Prevista");
                plannedDate.setId("order-transition-planned-date");
                plannedDate.setMin(LocalDate.now());
                binder.forField(plannedDate).asRequired("La fecha es obligatoria").bind("plannedDate");
                form.setPlannedDate(order.plannedDate() == null || order.plannedDate().isBefore(LocalDate.now()) ? LocalDate.now() : order.plannedDate());
                layout.add(plannedDate);
            }
            case ASSIGN -> {
                ComboBox<TeamSummaryDto> team = MaintenancePickers.team("Equipo", catalogs.activeTeams());
                team.setId("order-transition-team");
                TextField assignedUser = new TextField("Persona");
                assignedUser.setId("order-transition-assigned-user");
                assignedUser.setMaxLength(OrderEditorDialog.USER_LENGTH);
                binder.forField(team)
                        .withValidator(selected -> selected != null || !assignedUser.getValue().isBlank(), "Hace falta un equipo, una persona o los dos")
                        .bind("teamId");
                binder.forField(assignedUser).bind("assignedUser");
                form.setTeamId(order.team());
                form.setAssignedUser(order.assignedUser() == null ? "" : order.assignedUser());
                layout.add(team, assignedUser);
            }
            case START -> add(new Paragraph("La orden pasa a en curso: sus tareas ya se pueden trabajar en un turno de su via."));
            case COMPLETE -> {
                TextArea closingNotes = new TextArea("Notas de cierre");
                closingNotes.setId("order-transition-closing-notes");
                closingNotes.setHelperText("Obligatorias si no se completo ninguna tarea");
                binder.forField(closingNotes).bind("closingNotes");
                form.setClosingNotes(order.closingNotes() == null ? "" : order.closingNotes());
                layout.add(closingNotes);
                if (canForce) {
                    Checkbox force = new Checkbox("Completar aunque haya lineas de material sin sincronizar con el almacen");
                    force.setId("order-transition-force");
                    force.setHelperText("Queda escrito en las notas de cierre");
                    binder.forField(force).bind("force");
                    layout.add(force);
                }
            }
        }
        binder.forField(comment).bind("comment");
        layout.add(comment);
        binder.readBean(form);
        add(layout);

        Button confirm = new Button(kind.label(), click -> confirm(kind, order, clients, done));
        confirm.setId(CONFIRM_ID);
        confirm.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(new Button("Volver", click -> close()), confirm);
    }

    private void confirm(Kind kind, OrderDto order, MaintenanceClients clients, Consumer<OrderDto> done) {
        if (!binder.writeBeanIfValid(form)) {
            return;
        }
        String comment = TransitionForm.nullIfBlank(form.getComment());
        try {
            OrderDto result = switch (kind) {
                case PLAN -> clients.orders().plan(order.id(), new PlanOrderRequest(form.getPlannedDate(), comment));
                case ASSIGN -> clients.orders().assign(order.id(), new AssignOrderRequest(
                        form.getTeamId() == null ? null : form.getTeamId().id(), TransitionForm.nullIfBlank(form.getAssignedUser()), comment));
                case START -> clients.orders().start(order.id(), new CommentRequest(comment));
                case COMPLETE -> clients.orders().complete(order.id(), new CompleteOrderRequest(
                        TransitionForm.nullIfBlank(form.getClosingNotes()), form.isForce() ? Boolean.TRUE : null, comment));
            };
            close();
            MaintenanceUi.success(result.code() + ": " + result.status().label().toLowerCase());
            done.accept(result);
        } catch (ValidationApiException validation) {
            MaintenanceUi.showValidation(binder, validation);
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }
}
