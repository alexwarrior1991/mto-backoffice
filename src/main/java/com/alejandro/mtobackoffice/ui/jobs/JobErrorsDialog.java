package com.alejandro.mtobackoffice.ui.jobs;

import com.alejandro.mtobackoffice.client.dto.jobs.JobDto;
import com.alejandro.mtobackoffice.client.dto.jobs.JobItemError;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Paragraph;

/**
 * Lo que fallo en un trabajo: el error global, si lo hubo, y los primeros errores por elemento
 * (el servicio los acota; el recuento completo esta en {@code failedItems}). Solo el fichero de una
 * importacion es el informe con todos ({@link JobDto#hasErrorReport()}), y solo entonces se dice.
 */
public class JobErrorsDialog extends Dialog {

    public JobErrorsDialog(String label, JobDto job) {
        setHeaderTitle("Errores de " + label);
        setWidth("min(60rem, 96vw)");

        if (job.error() != null && !job.error().isBlank()) {
            add(new Paragraph("Error global: " + job.error()));
        }
        int shown = job.itemErrors().size();
        if (job.failedItems() > shown) {
            add(new Paragraph(truncated(job, shown)));
        }
        if (shown > 0) {
            add(errors(job));
        }
        getFooter().add(new Button("Cerrar", click -> close()));
    }

    /** Cuantos fallaron y cuantos detalla el servicio; solo el informe de una importacion los trae todos. */
    static String truncated(JobDto job, int shown) {
        String what = shown > 0
                ? job.failedItems() + " elementos fallidos; el servicio solo detalla los primeros " + shown + "."
                : job.failedItems() + " elementos fallidos, sin detalle del servicio.";
        return job.hasErrorReport() ? what + " El informe descargable los trae todos." : what;
    }

    private static Grid<JobItemError> errors(JobDto job) {
        Grid<JobItemError> errors = new Grid<>();
        errors.addColumn(error -> error.index() == null ? "" : String.valueOf(error.index())).setHeader("Posicion").setAutoWidth(true);
        errors.addColumn(error -> error.operation() == null ? "" : error.operation()).setHeader("Operacion").setAutoWidth(true);
        errors.addColumn(error -> error.code() == null ? "" : error.code()).setHeader("Codigo").setAutoWidth(true);
        errors.addColumn(error -> error.message() == null ? "" : error.message()).setHeader("Motivo").setFlexGrow(1);
        errors.setItems(job.itemErrors());
        errors.setAllRowsVisible(true);
        return errors;
    }
}
