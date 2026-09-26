package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.dto.maintenance.CompleteTaskRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.DefectSeverity;
import com.alejandro.mtobackoffice.client.dto.maintenance.InlineDefectRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.ShiftDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.ShiftFilter;
import com.alejandro.mtobackoffice.client.dto.maintenance.TaskDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.TaskMaterialRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.TaskTypeDto;
import com.alejandro.mtobackoffice.client.dto.stock.MaterialSummaryDto;
import com.alejandro.mtobackoffice.client.dto.stock.WarehouseSummaryDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.ui.stock.StockPickers;
import com.alejandro.mtobackoffice.ui.support.Formats;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Completar una tarea: la fila del parte del turno. Se trabaja en un turno en curso de la via de la
 * tarea (fijo si se abre desde el turno; a elegir entre los en curso de su via si se abre desde la
 * orden). Lleva los defectos encontrados (resueltos en el turno, o abiertos con fecha de reparacion
 * si el trabajo no se termino) y los materiales usados, que el servicio registra como lineas de la
 * orden ya consumidas y reserva en mto-stock. Elegir materiales pide {@code stock-read}. Si el turno
 * no admite el trabajo, el servicio responde 409 {@code SHF-001}, y el dialogo sigue abierto.
 */
public class CompleteTaskDialog extends Dialog {

    public static final String CONFIRM_ID = "complete-task-confirm";

    /** Un material elegido en el dialogo: lo que se pinta y lo que viaja. */
    record MaterialLine(MaterialSummaryDto material, WarehouseSummaryDto warehouse, BigDecimal quantity, String unit) {

        TaskMaterialRequest toRequest() {
            return new TaskMaterialRequest(material.id(), null, warehouse.id(), quantity, unit);
        }
    }

    private final List<InlineDefectRequest> defects = new ArrayList<>();
    private final List<MaterialLine> materials = new ArrayList<>();
    private final Grid<InlineDefectRequest> defectsGrid = new Grid<>();
    private final Grid<MaterialLine> materialsGrid = new Grid<>();

    /**
     * @param orderId      la orden de la tarea
     * @param trackId      la via en la que buscar turnos en curso si no viene {@code shift}
     * @param shift        el turno, o {@code null} para elegirlo
     * @param pickMaterials si la persona puede elegir materiales ({@code stock-read})
     */
    public CompleteTaskDialog(UUID orderId, Long trackId, TaskDto task, ShiftDto shift, MaintenanceClients clients,
                              MaintenanceCatalogs catalogs, boolean pickMaterials, Runnable done) {
        setHeaderTitle("Completar la tarea " + task.sequence() + (task.asset() == null ? "" : " · " + task.asset().label()));
        setCloseOnOutsideClick(false);
        setWidth("min(95vw, 900px)");

        ComboBox<ShiftDto> shiftPicker = new ComboBox<>("Turno");
        shiftPicker.setId("complete-task-shift");
        shiftPicker.setItemLabelGenerator(candidate -> candidate.code() + " · " + MaintenanceFormats.date(candidate.shiftDate())
                + (candidate.team() == null ? "" : " · " + candidate.team().label()));
        if (shift == null) {
            shiftPicker.setItems(inProgressShifts(clients, trackId));
            shiftPicker.setHelperText("Los turnos en curso de la via de la tarea");
        } else {
            shiftPicker.setItems(List.of(shift));
            shiftPicker.setValue(shift);
            shiftPicker.setReadOnly(true);
        }

        List<TaskTypeDto> catalog = catalogs.taskTypes();
        MultiSelectComboBox<TaskTypeDto> types = MaintenancePickers.taskTypes("Tipos de tarea", catalog);
        types.setId("complete-task-types");
        Set<TaskTypeDto> originalTypes = catalog.stream().filter(type -> task.taskTypeCodes().contains(type.code())).collect(Collectors.toSet());
        types.setValue(originalTypes);
        types.setHelperText("Deciden si el trabajo pide posesion total");
        TextArea notes = new TextArea("Notas");
        notes.setId("complete-task-notes");
        notes.setValue(task.notes() == null ? "" : task.notes());
        TextArea defectsFound = new TextArea("Defectos encontrados (texto del parte)");
        defectsFound.setId("complete-task-defects-found");
        defectsFound.setValue(task.defectsFound() == null ? "" : task.defectsFound());
        Checkbox workComplete = new Checkbox("Trabajo terminado en este turno", true);
        workComplete.setId("complete-task-work-complete");
        workComplete.setHelperText("Si no, los defectos quedan abiertos con su fecha de reparacion");
        DatePicker repairPlannedDate = new DatePicker("Reparacion prevista");
        repairPlannedDate.setId("complete-task-repair-date");
        repairPlannedDate.setEnabled(false);
        workComplete.addValueChangeListener(change -> repairPlannedDate.setEnabled(!change.getValue()));

        FormLayout form = new FormLayout(shiftPicker, types, notes, defectsFound, workComplete, repairPlannedDate);
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1), new FormLayout.ResponsiveStep("520px", 2));
        form.setColspan(types, 2);
        add(form, new H4("Defectos encontrados"), defectsEditor());
        if (pickMaterials) {
            add(new H4("Materiales usados"), materialsEditor(clients));
        } else {
            add(new Paragraph("Elegir materiales pide leer el almacen (stock-read)."));
        }

        Button confirm = new Button("Completar", click -> {
            if (shiftPicker.getValue() == null) {
                shiftPicker.setErrorMessage("Hace falta un turno en curso de su via");
                shiftPicker.setInvalid(true);
                return;
            }
            List<String> codes = types.getValue().stream().map(TaskTypeDto::code).toList();
            CompleteTaskRequest request = new CompleteTaskRequest(shiftPicker.getValue().id(),
                    types.getValue().equals(originalTypes) || codes.isEmpty() ? null : codes,
                    changed(notes.getValue(), task.notes()), changed(defectsFound.getValue(), task.defectsFound()),
                    workComplete.getValue() ? null : Boolean.FALSE, workComplete.getValue() ? null : repairPlannedDate.getValue(),
                    defects.isEmpty() ? null : List.copyOf(defects),
                    materials.isEmpty() ? null : materials.stream().map(MaterialLine::toRequest).toList(), null);
            try {
                clients.orders().completeTask(orderId, task.id(), request);
                close();
                MaintenanceUi.success("Tarea " + task.sequence() + " completada");
                done.run();
            } catch (BackofficeApiException failure) {
                UiErrors.show(failure);
            }
        });
        confirm.setId(CONFIRM_ID);
        confirm.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(new Button("Volver", click -> close()), confirm);
    }

    private static List<ShiftDto> inProgressShifts(MaintenanceClients clients, Long trackId) {
        if (trackId == null) {
            return List.of();
        }
        try {
            return clients.shifts().search(ShiftFilter.inProgressOn(trackId), 0, 50, List.of("shiftDate,desc")).content();
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
            return List.of();
        }
    }

    private com.vaadin.flow.component.Component defectsEditor() {
        defectsGrid.setId("complete-task-defects");
        defectsGrid.addColumn(defect -> defect.severity() == null ? "" : defect.severity().label()).setHeader("Gravedad").setAutoWidth(true);
        defectsGrid.addColumn(InlineDefectRequest::description).setHeader("Descripcion").setFlexGrow(1);
        defectsGrid.addColumn(InlineDefectRequest::correctionType).setHeader("Correccion").setAutoWidth(true);
        defectsGrid.addColumn(new ComponentRenderer<>(defect -> MaintenanceUi.rowButton("complete-task-defect-remove-" + defects.indexOf(defect),
                VaadinIcon.TRASH, "Quitar", click -> {
                    defects.remove(defect);
                    defectsGrid.setItems(defects);
                }))).setAutoWidth(true).setFlexGrow(0);
        defectsGrid.setAllRowsVisible(true);
        defectsGrid.setItems(defects);
        Button add = new Button("Anadir defecto", VaadinIcon.PLUS.create(), click -> new InlineDefectDialog(defect -> {
            defects.add(defect);
            defectsGrid.setItems(defects);
        }).open());
        add.setId("complete-task-defect-add");
        return new com.vaadin.flow.component.orderedlayout.VerticalLayout(defectsGrid, add);
    }

    private com.vaadin.flow.component.Component materialsEditor(MaintenanceClients clients) {
        materialsGrid.setId("complete-task-materials");
        materialsGrid.addColumn(line -> line.material().label()).setHeader("Material").setFlexGrow(1);
        materialsGrid.addColumn(line -> line.warehouse().label()).setHeader("Almacen").setAutoWidth(true);
        materialsGrid.addColumn(line -> Formats.quantity(line.quantity()) + (line.unit() == null ? "" : " " + line.unit()))
                .setHeader("Cantidad").setAutoWidth(true);
        materialsGrid.addColumn(new ComponentRenderer<>(line -> MaintenanceUi.rowButton("complete-task-material-remove-" + materials.indexOf(line),
                VaadinIcon.TRASH, "Quitar", click -> {
                    materials.remove(line);
                    materialsGrid.setItems(materials);
                }))).setAutoWidth(true).setFlexGrow(0);
        materialsGrid.setAllRowsVisible(true);
        materialsGrid.setItems(materials);
        Button add = new Button("Anadir material", VaadinIcon.PLUS.create(), click -> new MaterialLineDialog(clients, line -> {
            materials.add(line);
            materialsGrid.setItems(materials);
        }).open());
        add.setId("complete-task-material-add");
        return new com.vaadin.flow.component.orderedlayout.VerticalLayout(materialsGrid, add);
    }

    private static String changed(String value, String original) {
        String current = value == null ? "" : value.trim();
        return current.equals(original == null ? "" : original) ? null : current;
    }

    /** Un defecto encontrado: gravedad y descripcion obligatorias. */
    static class InlineDefectDialog extends Dialog {

        static final String ADD_ID = "inline-defect-add";

        InlineDefectDialog(java.util.function.Consumer<InlineDefectRequest> added) {
            setHeaderTitle("Defecto encontrado");
            ComboBox<DefectSeverity> severity = new ComboBox<>("Gravedad", DefectSeverity.selectable());
            severity.setId("inline-defect-severity");
            severity.setItemLabelGenerator(DefectSeverity::label);
            TextArea description = new TextArea("Descripcion");
            description.setId("inline-defect-description");
            TextArea technicalNotes = new TextArea("Notas tecnicas");
            TextField correctionType = new TextField("Tipo de correccion");
            correctionType.setMaxLength(120);
            TextField partsReplaced = new TextField("Piezas cambiadas");
            FormLayout layout = new FormLayout(severity, correctionType, description, technicalNotes, partsReplaced);
            layout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1), new FormLayout.ResponsiveStep("480px", 2));
            add(layout);
            Button add = new Button("Anadir", click -> {
                boolean valid = true;
                if (severity.getValue() == null) {
                    severity.setErrorMessage("La gravedad es obligatoria");
                    severity.setInvalid(true);
                    valid = false;
                }
                if (description.getValue().isBlank()) {
                    description.setErrorMessage("La descripcion es obligatoria");
                    description.setInvalid(true);
                    valid = false;
                }
                if (valid) {
                    added.accept(new InlineDefectRequest(severity.getValue(), description.getValue().trim(),
                            TransitionForm.nullIfBlank(technicalNotes.getValue()), TransitionForm.nullIfBlank(correctionType.getValue()),
                            TransitionForm.nullIfBlank(partsReplaced.getValue())));
                    close();
                }
            });
            add.setId(ADD_ID);
            add.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            getFooter().add(new Button("Volver", click -> close()), add);
        }
    }

    /** Un material usado: material y almacen buscados en mto-stock, y una cantidad positiva. */
    static class MaterialLineDialog extends Dialog {

        static final String ADD_ID = "material-line-add";

        MaterialLineDialog(MaintenanceClients clients, java.util.function.Consumer<MaterialLine> added) {
            setHeaderTitle("Material usado");
            ComboBox<MaterialSummaryDto> material = StockPickers.material("Material", clients.materials());
            material.setId("material-line-material");
            ComboBox<WarehouseSummaryDto> warehouse = StockPickers.warehouse("Almacen", clients.warehouses());
            warehouse.setId("material-line-warehouse");
            BigDecimalField quantity = new BigDecimalField("Cantidad");
            quantity.setId("material-line-quantity");
            FormLayout layout = new FormLayout(material, warehouse, quantity);
            layout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
            add(layout);
            Button add = new Button("Anadir", click -> {
                boolean valid = true;
                if (material.getValue() == null) {
                    material.setErrorMessage("El material es obligatorio");
                    material.setInvalid(true);
                    valid = false;
                }
                if (warehouse.getValue() == null) {
                    warehouse.setErrorMessage("El almacen es obligatorio");
                    warehouse.setInvalid(true);
                    valid = false;
                }
                if (quantity.getValue() == null || quantity.getValue().signum() <= 0) {
                    quantity.setErrorMessage("La cantidad tiene que ser mayor que cero");
                    quantity.setInvalid(true);
                    valid = false;
                }
                if (valid) {
                    added.accept(new MaterialLine(material.getValue(), warehouse.getValue(), quantity.getValue(),
                            material.getValue().unitOfMeasure()));
                    close();
                }
            });
            add.setId(ADD_ID);
            add.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            getFooter().add(new Button("Volver", click -> close()), add);
        }
    }
}
