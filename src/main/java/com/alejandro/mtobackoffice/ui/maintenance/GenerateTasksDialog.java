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
 * Generar las tareas de un preventivo sobre un tramo: una por perfil habilitado del tramo y, si se
 * marca, una por aislador de seccion que lo toca (tambien los de otra via que conectan con esta), con
 * los tipos elegidos. Sin ninguno, los del servicio para cada activo: grupos 1, 2 y 4 en un perfil,
 * RG-03 en un aislador, que pide posesion total. Lo que ya tiene tarea se salta, asi que repetirlo no
 * duplica: para tipos distintos se genera dos veces. Lo que resulta (creadas, saltadas, estimacion) lo
 * cuenta el servicio y se ensena tal cual.
 */
public class GenerateTasksDialog extends Dialog {

    public static final String CONFIRM_ID = "generate-confirm";
    public static final String INSULATORS_ID = "generate-with-insulators";

    private static final String PROFILE_TYPES_HELP = "Vacio: los del plan para cada perfil de via principal (grupos 1, 2 y 4)";
    private static final String ALL_TYPES_HELP = "Para todo lo generado. Vacio: grupos 1, 2 y 4 en cada perfil y RG-03 en cada aislador";

    public GenerateTasksDialog(OrderDto order, MaintenanceClients clients, MaintenanceCatalogs catalogs, Runnable done) {
        setHeaderTitle("Generar tareas de " + order.code());
        setCloseOnOutsideClick(false);
        setWidth("min(90vw, 640px)");
        Checkbox withInsulators = new Checkbox("Tambien los aisladores de seccion del tramo (RG-03, posesion total)");
        withInsulators.setId(INSULATORS_ID);
        withInsulators.setHelperText("Los de esta via y los de otra que conectan con ella, entre los perfiles que los rodean");
        MultiSelectComboBox<TaskTypeDto> types = MaintenancePickers.taskTypes("Tipos de tarea", catalogs.taskTypes());
        types.setId("generate-types");
        types.setWidthFull();
        types.setHelperText(PROFILE_TYPES_HELP);
        withInsulators.addValueChangeListener(change -> types.setHelperText(change.getValue() ? ALL_TYPES_HELP : PROFILE_TYPES_HELP));
        Checkbox withChecklist = new Checkbox("Con la checklist de la plantilla de cada activo");
        withChecklist.setId("generate-with-checklist");
        add(new Paragraph("Una tarea por cada perfil habilitado entre los KP " + MaintenanceFormats.kpRange(order.startKp(), order.endKp())
                + " del tramo; los que ya tienen tarea se saltan."), withInsulators, types, withChecklist);

        Button confirm = new Button("Generar", click -> {
            try {
                GenerateTasksResultDto result = clients.orders().generateTasks(order.id(), new GenerateTasksRequest(
                        types.getValue().isEmpty() ? null : types.getValue().stream().map(TaskTypeDto::code).toList(),
                        withChecklist.getValue() ? Boolean.TRUE : null,
                        withInsulators.getValue() ? GenerateTasksRequest.WITH_INSULATORS : null));
                close();
                MaintenanceUi.success(describe(result, withInsulators.getValue()));
                done.run();
            } catch (BackofficeApiException failure) {
                UiErrors.show(failure);
            }
        });
        confirm.setId(CONFIRM_ID);
        confirm.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(new Button("Volver", click -> close()), confirm);
    }

    static String describe(GenerateTasksResultDto result, boolean withInsulators) {
        String skipped = withInsulators
                ? "activos que ya tenian tarea: " + result.skipped()
                : "perfiles que ya tenian tarea: " + result.skippedProfiles();
        return "Tareas nuevas: " + result.createdTasks() + "; " + skipped
                + ". Total: " + result.totalTasks() + ", unos " + Formats.quantity(result.estimatedMinutes()) + " min en "
                + result.estimatedShifts() + " turnos.";
    }
}
