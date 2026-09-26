package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.dto.maintenance.AssetDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.DefectDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.InspectionDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.OrderDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.ShiftDto;
import com.alejandro.mtobackoffice.ui.support.RevisionsDialog;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * El historial de mantenimiento: el de Envers en mto-maintenance, en el mismo {@link RevisionsDialog}
 * que almacen, con una linea por recurso que dice como quedo la fila en cada revision. Es lectura:
 * lo ofrece la ficha (o la fila de un activo) a quien puede abrirla. Un activo que solo ha llegado
 * por datos maestros no tiene revisiones (alli es SQL nativo): el 404 se dice «sin historial».
 */
final class MaintenanceHistory {

    private MaintenanceHistory() {
    }

    /** El boton de la cabecera de una ficha; {@code code} es lo que se lee en el titulo del dialogo. */
    static <D> Button button(String id, String code, RevisionsDialog.Source<D> source, Function<D, String> describe) {
        Button button = new Button("Historial", VaadinIcon.CLOCK.create(), click -> dialog(code, source, describe).open());
        button.setId(id);
        return button;
    }

    static <D> RevisionsDialog<D> dialog(String code, RevisionsDialog.Source<D> source, Function<D, String> describe) {
        return new RevisionsDialog<>(code, source, describe);
    }

    static String order(OrderDto order) {
        return join(order.title(), label(order.status(), status -> status.label()), label(order.priority(), priority -> "prioridad " + priority.label()),
                label(order.plannedDate(), date -> "plan " + MaintenanceFormats.date(date)), label(order.team(), team -> team.code()),
                order.assignedUser());
    }

    static String shift(ShiftDto shift) {
        return join(label(shift.status(), status -> status.label()), MaintenanceFormats.date(shift.shiftDate()), label(shift.team(), team -> team.code()),
                label(shift.possessionType(), possession -> "ocupacion " + possession.label()),
                label(shift.netWorkMinutes(), minutes -> minutes + " min netos"), shift.observations());
    }

    static String inspection(InspectionDto inspection) {
        return join(label(inspection.inspectionKind(), kind -> kind.label()), MaintenanceFormats.date(inspection.inspectionDate()),
                inspection.inspector(), label(inspection.result(), result -> result.label()));
    }

    static String defect(DefectDto defect) {
        return join(label(defect.severity(), severity -> severity.label()), label(defect.status(), status -> status.label()),
                label(defect.repairPlannedDate(), date -> "reparar el " + MaintenanceFormats.date(date)), defect.resolutionNotes(),
                defect.discardReason());
    }

    static String asset(AssetDto asset) {
        return join(asset.name(), label(MaintenanceFormats.kpRange(asset.startKp(), asset.endKp()), range -> range.isEmpty() ? null : "KP " + range),
                asset.isEnabled() ? "activo" : "desactivado",
                label(asset.preventiveIntervalDays(), days -> "preventivo cada " + days + " dias"), asset.description());
    }

    private static <T> String label(T value, Function<T, String> label) {
        return value == null ? null : label.apply(value);
    }

    /** Las partes que hay, separadas por {@code ·}; lo vacio no deja separadores sueltos. */
    private static String join(String... parts) {
        return Arrays.stream(parts).filter(part -> part != null && !part.isBlank()).collect(Collectors.joining(" · "));
    }
}
