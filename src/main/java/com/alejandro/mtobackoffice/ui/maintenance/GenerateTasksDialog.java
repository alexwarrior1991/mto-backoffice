package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.dto.maintenance.GenerateTasksRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.GenerateTasksResultDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.OrderDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.TaskTypeDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.ui.support.Formats;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Paragraph;

/**
 * Generar las tareas de un preventivo sobre un tramo: una por perfil habilitado del tramo, con los
 * tipos elegidos (sin ninguno, los del servicio: grupos 1, 2 y 4). Los perfiles que ya tienen tarea
 * se saltan, asi que repetirlo no duplica. Lo que resulta (creadas, saltadas, estimacion) lo cuenta
 * el servicio y se ensena tal cual.
 */
public class GenerateTasksDialog extends Dialog {

    public static final String CONFIRM_ID = "generate-confirm";

    public GenerateTasksDialog(OrderDto order, MaintenanceClients clients, MaintenanceCatalogs catalogs, Runnable done) {
        setHeaderTitle("Generar tareas de " + order.code());
        setCloseOnOutsideClick(false);
        setWidth("min(90vw, 640px)");
        MultiSelectComboBox<TaskTypeDto> types = MaintenancePickers.taskTypes("Tipos de tarea", catalogs.taskTypes());
        types.setId("generate-types");
        types.setWidthFull();
        types.setHelperText("Vacio: los del plan para cada perfil de via principal (grupos 1, 2 y 4)");
        Checkbox withChecklist = new Checkbox("Con la checklist de la plantilla de perfil");
        withChecklist.setId("generate-with-checklist");
        add(new Paragraph("Una tarea por cada perfil habilitado entre los KP " + MaintenanceFormats.kpRange(order.startKp(), order.endKp())
                + " del tramo; los que ya tienen tarea se saltan."), types, withChecklist);

        Button confirm = new Button("Generar", click -> {
            try {
                GenerateTasksResultDto result = clients.orders().generateTasks(order.id(), new GenerateTasksRequest(
                        types.getValue().isEmpty() ? null : types.getValue().stream().map(TaskTypeDto::code).toList(),
                        withChecklist.getValue() ? Boolean.TRUE : null));
                close();
                MaintenanceUi.success(describe(result));
                done.run();
            } catch (BackofficeApiException failure) {
                UiErrors.show(failure);
            }
        });
        confirm.setId(CONFIRM_ID);
        confirm.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(new Button("Volver", click -> close()), confirm);
    }

    static String describe(GenerateTasksResultDto result) {
        return "Tareas nuevas: " + result.createdTasks() + "; perfiles que ya tenian tarea: " + result.skippedProfiles()
                + ". Total: " + result.totalTasks() + ", unos " + Formats.quantity(result.estimatedMinutes()) + " min en "
                + result.estimatedShifts() + " turnos.";
    }
}
