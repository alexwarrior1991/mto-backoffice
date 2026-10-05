package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.dto.maintenance.AssetSummaryDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.MergePatch;
import com.alejandro.mtobackoffice.client.dto.maintenance.TaskDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.TaskRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.TaskTypeDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.TaskUpdateRequest;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Modelo mutable del editor de una tarea, con las propiedades llamadas como los campos de la peticion. */
public class TaskForm {

    private String description = "";
    private AssetSummaryDto assetId;
    private String assignedUser = "";
    private Set<TaskTypeDto> taskTypeCodes = new LinkedHashSet<>();
    /** Los codigos de la tarea que el catalogo no trae (un tipo retirado): no se ofrecen, pero vuelven tal cual. */
    private List<String> unknownCodes = List.of();
    private boolean withChecklist;
    private String notes = "";
    private String defectsFound = "";

    /** @param types los tipos del catalogo, para convertir los codigos de la tarea en opciones */
    public static TaskForm of(TaskDto dto, Collection<TaskTypeDto> types) {
        TaskForm form = new TaskForm();
        if (dto != null) {
            form.setDescription(orEmpty(dto.description()));
            form.setAssetId(dto.asset());
            form.setAssignedUser(orEmpty(dto.assignedUser()));
            form.setTaskTypeCodes(types.stream().filter(type -> dto.taskTypeCodes().contains(type.code()))
                    .collect(Collectors.toCollection(LinkedHashSet::new)));
            form.unknownCodes = unknownCodes(dto.taskTypeCodes(), types);
            form.setNotes(orEmpty(dto.notes()));
            form.setDefectsFound(orEmpty(dto.defectsFound()));
        }
        return form;
    }

    public TaskRequest toRequest() {
        return new TaskRequest(description.trim(), assetId == null ? null : assetId.id(), nullIfBlank(assignedUser),
                codes().isEmpty() ? null : codes(), withChecklist ? Boolean.TRUE : null);
    }

    /**
     * Lo que cambio, lo vaciado y la version leida. Los tipos se comparan como conjunto y, si
     * cambiaron, van enteros (sustituyen a los que tenia), con los que el catalogo no trae; sin
     * ninguno, se vacian ({@code null} en el merge-patch), como en mto-frontend.
     */
    public MergePatch<TaskUpdateRequest> toPatch(TaskDto original) {
        Changes changes = new Changes();
        List<String> codes = codes();
        boolean sameTypes = Set.copyOf(codes).equals(Set.copyOf(original.taskTypeCodes()));
        TaskUpdateRequest values = new TaskUpdateRequest(
                changes.text("description", description, original.description()),
                changes.text("assignedUser", assignedUser, original.assignedUser()),
                sameTypes ? null : codes.isEmpty() ? changes.value("taskTypeCodes", null, original.taskTypeCodes()) : codes,
                changes.text("notes", notes, original.notes()),
                changes.text("defectsFound", defectsFound, original.defectsFound()),
                null);
        return changes.patch(values, original.version());
    }

    private List<String> codes() {
        return Stream.concat(taskTypeCodes.stream().map(TaskTypeDto::code), unknownCodes.stream()).distinct().toList();
    }

    /** Los codigos de una tarea que el catalogo no trae: lo que la pantalla no sabe nombrar no se pierde. */
    static List<String> unknownCodes(List<String> codes, Collection<TaskTypeDto> types) {
        return codes.stream().filter(code -> types.stream().noneMatch(type -> type.code().equals(code))).toList();
    }

    private static String nullIfBlank(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private static String orEmpty(String value) {
        return value == null ? "" : value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AssetSummaryDto getAssetId() {
        return assetId;
    }

    public void setAssetId(AssetSummaryDto assetId) {
        this.assetId = assetId;
    }

    public String getAssignedUser() {
        return assignedUser;
    }

    public void setAssignedUser(String assignedUser) {
        this.assignedUser = assignedUser;
    }

    public Set<TaskTypeDto> getTaskTypeCodes() {
        return taskTypeCodes;
    }

    public void setTaskTypeCodes(Set<TaskTypeDto> taskTypeCodes) {
        this.taskTypeCodes = taskTypeCodes == null ? new LinkedHashSet<>() : new LinkedHashSet<>(taskTypeCodes);
    }

    public boolean isWithChecklist() {
        return withChecklist;
    }

    public void setWithChecklist(boolean withChecklist) {
        this.withChecklist = withChecklist;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getDefectsFound() {
        return defectsFound;
    }

    public void setDefectsFound(String defectsFound) {
        this.defectsFound = defectsFound;
    }
}
