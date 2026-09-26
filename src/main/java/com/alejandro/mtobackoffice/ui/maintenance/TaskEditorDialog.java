package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.dto.maintenance.AssetSummaryDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.MergePatch;
import com.alejandro.mtobackoffice.client.dto.maintenance.OrderDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.TaskDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.TaskTypeDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.TaskUpdateRequest;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.error.ValidationApiException;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

/**
 * Alta y modificacion de una tarea abierta de una orden. El activo se elige en el alta (buscado en
 * el servidor) y ya no cambia; los tipos de tarea son los del plan, y si cambian van enteros. En la
 * modificacion se anaden las notas y los defectos encontrados, y se manda solo lo que cambio.
 */
public class TaskEditorDialog extends Dialog {

    public static final String SAVE_ID = "task-save";
    static final int DESCRIPTION_LENGTH = 500;

    private final Binder<TaskForm> binder = new Binder<>(TaskForm.class);
    private final TaskForm form;

    public TaskEditorDialog(OrderDto order, TaskDto existing, MaintenanceClients clients, MaintenanceCatalogs catalogs, Runnable saved) {
        boolean creating = existing == null;
        this.form = TaskForm.of(existing, catalogs.taskTypes());
        setHeaderTitle(creating ? "Nueva tarea en " + order.code() : "Tarea " + existing.sequence() + " de " + order.code());
        setCloseOnOutsideClick(false);
        setWidth("min(90vw, 720px)");

        TextArea description = new TextArea("Descripcion");
        description.setId("task-description");
        description.setMaxLength(DESCRIPTION_LENGTH);
        description.setRequiredIndicatorVisible(true);
        TextField assignedUser = new TextField("Asignada a");
        assignedUser.setId("task-assigned-user");
        assignedUser.setMaxLength(OrderEditorDialog.USER_LENGTH);
        MultiSelectComboBox<TaskTypeDto> types = MaintenancePickers.taskTypes("Tipos de tarea", catalogs.taskTypes());
        types.setId("task-types");

        FormLayout layout = new FormLayout();
        layout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1), new FormLayout.ResponsiveStep("480px", 2));
        binder.forField(description).asRequired("La descripcion es obligatoria").bind("description");
        binder.forField(assignedUser).bind("assignedUser");
        binder.forField(types).bind("taskTypeCodes");
        layout.add(description);
        layout.setColspan(description, 2);
        if (creating) {
            ComboBox<AssetSummaryDto> asset = MaintenancePickers.asset("Activo", clients.assets(), null);
            asset.setId("task-asset");
            asset.setHelperText("Opcional: el perfil, seccionador o aislador en el que se trabaja");
            Checkbox withChecklist = new Checkbox("Con la checklist de la plantilla del activo");
            withChecklist.setId("task-with-checklist");
            binder.forField(asset).bind("assetId");
            binder.forField(withChecklist).bind("withChecklist");
            layout.add(asset, assignedUser, types, withChecklist);
        } else {
            TextArea notes = new TextArea("Notas");
            notes.setId("task-notes");
            TextArea defectsFound = new TextArea("Defectos encontrados");
            defectsFound.setId("task-defects-found");
            binder.forField(notes).bind("notes");
            binder.forField(defectsFound).bind("defectsFound");
            layout.add(assignedUser, types, notes, defectsFound);
        }
        layout.setColspan(types, 2);
        binder.readBean(form);
        add(layout);

        Button save = new Button("Guardar", click -> save(order, existing, clients, saved));
        save.setId(SAVE_ID);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(new Button("Cancelar", click -> close()), save);
    }

    private void save(OrderDto order, TaskDto existing, MaintenanceClients clients, Runnable saved) {
        if (!binder.writeBeanIfValid(form)) {
            return;
        }
        try {
            if (existing == null) {
                TaskDto created = clients.orders().createTask(order.id(), form.toRequest());
                MaintenanceUi.success("Tarea " + created.sequence() + " anadida a " + order.code());
            } else {
                MergePatch<TaskUpdateRequest> patch = form.toPatch(existing);
                if (!patch.changesNothing()) {
                    clients.orders().updateTask(order.id(), existing.id(), patch);
                    MaintenanceUi.success("Tarea " + existing.sequence() + " guardada");
                }
            }
            close();
            saved.run();
        } catch (ValidationApiException validation) {
            MaintenanceUi.showValidation(binder, validation);
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }
}
