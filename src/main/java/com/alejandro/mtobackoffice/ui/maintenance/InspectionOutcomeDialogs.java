package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.dto.maintenance.CreateCorrectiveOrderRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.CreateDefectFromInspectionRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.DefectDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.DefectSeverity;
import com.alejandro.mtobackoffice.client.dto.maintenance.InspectionDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.InspectionResult;
import com.alejandro.mtobackoffice.client.dto.maintenance.MaintenancePriority;
import com.alejandro.mtobackoffice.client.dto.maintenance.OrderDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.TeamSummaryDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
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

import java.util.function.Consumer;

/**
 * Lo que una inspeccion genera: su defecto y su orden correctiva. Lo que se deja vacio lo pone el
 * servicio (la gravedad sale del resultado; una inspeccion insegura da una orden urgente y
 * critica). Las dos llamadas son idempotentes, asi que repetirlas devuelve lo mismo.
 */
final class InspectionOutcomeDialogs {

    static final String DEFECT_CONFIRM_ID = "inspection-defect-confirm";
    static final String ORDER_CONFIRM_ID = "inspection-order-confirm";

    private InspectionOutcomeDialogs() {
    }

    /** Un defecto leve solo se registra marcando {@code force}; el servicio lo rechaza si no. */
    static Dialog defect(InspectionDto inspection, MaintenanceClients clients, Consumer<DefectDto> created) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Defecto de " + inspection.code());
        ComboBox<DefectSeverity> severity = new ComboBox<>("Gravedad", DefectSeverity.selectable());
        severity.setId("inspection-defect-severity");
        severity.setItemLabelGenerator(DefectSeverity::label);
        severity.setClearButtonVisible(true);
        severity.setHelperText("Vacia: la que corresponde al resultado de la inspeccion");
        TextArea description = new TextArea("Descripcion");
        description.setId("inspection-defect-description");
        description.setHelperText("Vacia: los defectos observados en la inspeccion");
        TextArea technicalNotes = new TextArea("Notas tecnicas");
        technicalNotes.setId("inspection-defect-notes");
        Checkbox force = new Checkbox("Registrar como defecto aunque sea leve");
        force.setId("inspection-defect-force");
        FormLayout layout = new FormLayout(severity, description, technicalNotes);
        layout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        if (inspection.result() == InspectionResult.MINOR_DEFECT) {
            layout.add(force);
        }
        dialog.add(layout);
        Button confirm = new Button("Crear el defecto", click -> {
            try {
                DefectDto defect = clients.inspections().createDefect(inspection.id(), new CreateDefectFromInspectionRequest(severity.getValue(),
                        TransitionForm.nullIfBlank(description.getValue()), TransitionForm.nullIfBlank(technicalNotes.getValue()),
                        force.getValue() ? Boolean.TRUE : null));
                dialog.close();
                MaintenanceUi.success("Defecto " + defect.code() + " creado");
                created.accept(defect);
            } catch (BackofficeApiException failure) {
                UiErrors.show(failure);
            }
        });
        confirm.setId(DEFECT_CONFIRM_ID);
        confirm.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        dialog.getFooter().add(new Button("Volver", click -> dialog.close()), confirm);
        return dialog;
    }

    static Dialog correctiveOrder(InspectionDto inspection, MaintenanceClients clients, MaintenanceCatalogs catalogs, Consumer<OrderDto> created) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Orden correctiva de " + inspection.code());
        TextField title = new TextField("Titulo");
        title.setId("inspection-order-title");
        title.setMaxLength(255);
        title.setHelperText("Vacio: uno con la inspeccion y el activo");
        TextArea description = new TextArea("Descripcion");
        description.setId("inspection-order-description");
        ComboBox<MaintenancePriority> priority = new ComboBox<>("Prioridad", MaintenancePriority.selectable());
        priority.setId("inspection-order-priority");
        priority.setItemLabelGenerator(MaintenancePriority::label);
        priority.setClearButtonVisible(true);
        DatePicker plannedDate = new DatePicker("Prevista");
        plannedDate.setId("inspection-order-planned-date");
        ComboBox<TeamSummaryDto> team = MaintenancePickers.team("Equipo", catalogs.activeTeams());
        team.setId("inspection-order-team");
        FormLayout layout = new FormLayout(title, priority, plannedDate, team, description);
        layout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1), new FormLayout.ResponsiveStep("480px", 2));
        layout.setColspan(description, 2);
        if (inspection.result() == InspectionResult.UNSAFE) {
            dialog.add(new Paragraph("La inspeccion es insegura: la orden sera urgente y critica."));
        }
        dialog.add(layout);
        Button confirm = new Button("Crear la orden", click -> {
            try {
                OrderDto order = clients.inspections().createCorrectiveOrder(inspection.id(), new CreateCorrectiveOrderRequest(
                        TransitionForm.nullIfBlank(title.getValue()), TransitionForm.nullIfBlank(description.getValue()), priority.getValue(),
                        plannedDate.getValue(), team.getValue() == null ? null : team.getValue().id()));
                dialog.close();
                MaintenanceUi.success("Orden " + order.code() + " creada");
                created.accept(order);
            } catch (BackofficeApiException failure) {
                UiErrors.show(failure);
            }
        });
        confirm.setId(ORDER_CONFIRM_ID);
        confirm.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        dialog.getFooter().add(new Button("Volver", click -> dialog.close()), confirm);
        return dialog;
    }
}
