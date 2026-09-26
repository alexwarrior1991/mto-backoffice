package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.dto.maintenance.TeamSummaryDto;

import java.time.LocalDate;

/**
 * Modelo mutable de una transicion de orden, con las propiedades llamadas como los campos de sus
 * peticiones ({@code plannedDate}, {@code teamId}, {@code assignedUser}, {@code closingNotes},
 * {@code force}, {@code comment}).
 */
public class TransitionForm {

    private LocalDate plannedDate;
    private TeamSummaryDto teamId;
    private String assignedUser = "";
    private String closingNotes = "";
    private boolean force;
    private String comment = "";

    public LocalDate getPlannedDate() {
        return plannedDate;
    }

    public void setPlannedDate(LocalDate plannedDate) {
        this.plannedDate = plannedDate;
    }

    public TeamSummaryDto getTeamId() {
        return teamId;
    }

    public void setTeamId(TeamSummaryDto teamId) {
        this.teamId = teamId;
    }

    public String getAssignedUser() {
        return assignedUser;
    }

    public void setAssignedUser(String assignedUser) {
        this.assignedUser = assignedUser;
    }

    public String getClosingNotes() {
        return closingNotes;
    }

    public void setClosingNotes(String closingNotes) {
        this.closingNotes = closingNotes;
    }

    public boolean isForce() {
        return force;
    }

    public void setForce(boolean force) {
        this.force = force;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    static String nullIfBlank(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
