package com.alejandro.mtobackoffice.ui.jobs;

import com.alejandro.mtobackoffice.client.dto.jobs.JobDto;
import com.alejandro.mtobackoffice.client.dto.jobs.JobItemError;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Paragraph;

/**
 * Lo que fallo en un trabajo: el error global, si lo hubo, y los primeros errores por elemento
 * (el servicio los acota; el recuento completo esta en {@code failedItems}).
 */
public class JobErrorsDialog extends Dialog {

    public JobErrorsDialog(String label, JobDto job) {
        setHeaderTitle("Errores de " + label);
        setWidth("min(60rem, 96vw)");

        if (job.error() != null && !job.error().isBlank()) {
            add(new Paragraph("Error global: " + job.error()));
        }
        if (job.failedItems() > job.itemErrors().size()) {
            add(new Paragraph(job.failedItems() + " elementos fallidos; el servicio solo detalla los primeros "
                    + job.itemErrors().size() + ". El informe descargable los trae todos."));
        }
        Grid<JobItemError> errors = new Grid<>();
        errors.addColumn(error -> error.index() == null ? "" : String.valueOf(error.index())).setHeader("Posicion").setAutoWidth(true);
        errors.addColumn(error -> error.operation() == null ? "" : error.operation()).setHeader("Operacion").setAutoWidth(true);
        errors.addColumn(error -> error.code() == null ? "" : error.code()).setHeader("Codigo").setAutoWidth(true);
        errors.addColumn(error -> error.message() == null ? "" : error.message()).setHeader("Motivo").setFlexGrow(1);
        errors.setItems(job.itemErrors());
        errors.setAllRowsVisible(true);
        add(errors);
        getFooter().add(new Button("Cerrar", click -> close()));
    }
}
