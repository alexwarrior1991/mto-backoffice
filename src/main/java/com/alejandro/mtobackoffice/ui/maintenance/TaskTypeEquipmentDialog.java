package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.dto.MergePatch;
import com.alejandro.mtobackoffice.client.dto.maintenance.TaskEquipment;
import com.alejandro.mtobackoffice.client.dto.maintenance.TaskTypeDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.TaskTypeUpdateRequest;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.ui.support.Changes;
import com.alejandro.mtobackoffice.ui.support.Screens;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * El equipo que exige un tipo de tarea: una casilla por cada uno que esta aplicacion conoce
 * ({@link TaskEquipment}). Viaja como merge-patch con la version leida: el conjunto se compara sin orden,
 * quitarlos todos viaja como {@code null} y uno que esta aplicacion no conoce se conserva tal cual. Una
 * version vieja es 409 {@code CON-001}, con el dialogo abierto. Pide {@code maintenance-supervise} y
 * {@code maintenance-write}.
 */
public class TaskTypeEquipmentDialog extends Dialog {

    public static final String SAVE_ID = "task-equipment-save";
    static final String CHECKBOX_PREFIX = "task-equipment-";
    static final String EXPLANATION = "La tarea solo se trabaja en un turno que lo lleva: un vehiculo con plataforma aislada; para la puesta a tierra, "
            + "un vehiculo que la tenga o una pertiga de tierra entre sus instrumentos.";

    private final Map<TaskEquipment, Checkbox> boxes = new LinkedHashMap<>();

    public TaskTypeEquipmentDialog(TaskTypeDto type, MaintenanceClients clients, Consumer<TaskTypeDto> saved) {
        setHeaderTitle("Equipo exigido por " + type.code());
        setCloseOnOutsideClick(false);
        setWidth("min(90vw, 520px)");
        VerticalLayout body = new VerticalLayout(new Paragraph(EXPLANATION));
        body.setPadding(false);
        for (TaskEquipment equipment : TaskEquipment.selectable()) {
            Checkbox box = new Checkbox(equipment.label(), type.requiredEquipment().contains(equipment.name()));
            box.setId(CHECKBOX_PREFIX + equipment.name());
            boxes.put(equipment, box);
            body.add(box);
        }
        Set<String> unknown = unknownOf(type.requiredEquipment());
        if (!unknown.isEmpty()) {
            Paragraph kept = new Paragraph("Exige tambien, y se conserva: " + String.join(", ", unknown));
            kept.setId("task-equipment-unknown");
            body.add(kept);
        }
        add(body);

        Button save = new Button("Guardar", click -> save(type, clients, saved));
        save.setId(SAVE_ID);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(new Button("Cancelar", click -> close()), save);
    }

    /** Los que esta aplicacion no conoce, tal como llegaron. */
    static Set<String> unknownOf(Collection<String> equipment) {
        return equipment.stream().filter(value -> TaskEquipment.of(value) == TaskEquipment.UNKNOWN).collect(Collectors.toCollection(TreeSet::new));
    }

    /** Las casillas marcadas mas lo desconocido leido, contra lo leido, con su version. */
    static MergePatch<TaskTypeUpdateRequest> patchOf(TaskTypeDto type, Collection<TaskEquipment> checked) {
        Set<String> chosen = new TreeSet<>(unknownOf(type.requiredEquipment()));
        checked.forEach(equipment -> chosen.add(equipment.name()));
        Changes changes = new Changes();
        return changes.patch(new TaskTypeUpdateRequest(changes.set("requiredEquipment", chosen, type.requiredEquipment())), type.version());
    }

    private void save(TaskTypeDto type, MaintenanceClients clients, Consumer<TaskTypeDto> saved) {
        Set<TaskEquipment> checked = boxes.entrySet().stream().filter(entry -> entry.getValue().getValue()).map(Map.Entry::getKey)
                .collect(Collectors.toSet());
        MergePatch<TaskTypeUpdateRequest> patch = patchOf(type, checked);
        if (patch.changesNothing()) {
            close();
            return;
        }
        try {
            TaskTypeDto result = clients.catalog().updateTaskType(type.code(), patch);
            close();
            Screens.success("Guardado el equipo de " + result.code());
            saved.accept(result);
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }
}
