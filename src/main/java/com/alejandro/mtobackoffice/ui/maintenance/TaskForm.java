package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.dto.maintenance.AssetSummaryDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.TaskDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.TaskRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.TaskTypeDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.TaskUpdateRequest;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/** Modelo mutable del editor de una tarea, con las propiedades llamadas como los campos de la peticion. */
public class TaskForm {

    private String description = "";
    private AssetSummaryDto assetId;
    private String assignedUser = "";
    private Set<TaskTypeDto> taskTypeCodes = new LinkedHashSet<>();
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
            form.setNotes(orEmpty(dto.notes()));
            form.setDefectsFound(orEmpty(dto.defectsFound()));
        }
        return form;
    }

    public TaskRequest toRequest() {
        return new TaskRequest(description.trim(), assetId == null ? null : assetId.id(), nullIfBlank(assignedUser),
                codes().isEmpty() ? null : codes(), withChecklist ? Boolean.TRUE : null);
    }

    /** Solo lo que cambio; los tipos, si cambiaron, van enteros (sustituyen a los que tenia). */
    public TaskUpdateRequest toUpdateRequest(TaskDto original) {
        List<String> codes = codes();
        return new TaskUpdateRequest(
                changed(description, original.description()),
                changed(assignedUser, original.assignedUser()),
                Set.copyOf(codes).equals(Set.copyOf(original.taskTypeCodes())) ? null : codes,
                changed(notes, original.notes()),
                changed(defectsFound, original.defectsFound()),
                null);
    }

    private List<String> codes() {
        return taskTypeCodes.stream().map(TaskTypeDto::code).toList();
    }

    private static String changed(String value, String original) {
        String current = value == null ? "" : value.trim();
        return current.equals(orEmpty(original)) ? null : current;
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
