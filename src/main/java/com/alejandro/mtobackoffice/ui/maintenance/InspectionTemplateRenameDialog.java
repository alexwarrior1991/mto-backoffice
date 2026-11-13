package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.dto.MergePatch;
import com.alejandro.mtobackoffice.client.dto.maintenance.InspectionTemplateDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.InspectionTemplateUpdateRequest;
import com.alejandro.mtobackoffice.client.error.ApiFieldError;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.error.ValidationApiException;
import com.alejandro.mtobackoffice.ui.support.Changes;
import com.alejandro.mtobackoffice.ui.support.Screens;
import com.alejandro.mtobackoffice.ui.support.ServerValidation;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.textfield.TextField;

import java.util.function.Consumer;

/**
 * El nombre de una version de una plantilla de inspeccion, que es lo unico suyo que se escribe en su
 * sitio (ademas de activarla o desactivarla, que va con su confirmacion desde la fila). Viaja como
 * merge-patch con el {@code lockVersion} leido, nunca con {@code version}; sin cambios no se llama, y
 * un 409 {@code CON-001} deja el dialogo abierto. Pide {@code maintenance-supervise} y
 * {@code maintenance-write}.
 */
public class InspectionTemplateRenameDialog extends Dialog {

    public static final String SAVE_ID = "template-rename-save";
    public static final String NAME_ID = "template-rename-name";

    private final TextField name = new TextField("Nombre");

    public InspectionTemplateRenameDialog(InspectionTemplateDto template, MaintenanceClients clients, Consumer<InspectionTemplateDto> saved) {
        setHeaderTitle("Renombrar la version " + template.version() + " de " + template.assetType().label());
        setCloseOnOutsideClick(false);
        setWidth("min(90vw, 520px)");
        name.setId(NAME_ID);
        name.setMaxLength(InspectionTemplateVersionDialog.NAME_LENGTH);
        name.setRequiredIndicatorVisible(true);
        name.setWidthFull();
        name.setValue(template.name() == null ? "" : template.name());
        add(name);

        Button save = new Button("Guardar", click -> save(template, clients, saved));
        save.setId(SAVE_ID);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(new Button("Cancelar", click -> close()), save);
    }

    /** El nombre si cambio, con el bloqueo leido; la {@code version} del parche no viaja: aqui es el numero de la version. */
    static MergePatch<InspectionTemplateUpdateRequest> renamePatch(InspectionTemplateDto template, String written) {
        return MergePatch.of(new InspectionTemplateUpdateRequest(new Changes().text("name", written, template.name()), null,
                template.lockVersion()), null);
    }

    /** Activar o desactivar, con el bloqueo leido. */
    static MergePatch<InspectionTemplateUpdateRequest> activePatch(InspectionTemplateDto template, boolean active) {
        return MergePatch.of(new InspectionTemplateUpdateRequest(null, active, template.lockVersion()), null);
    }

    private void save(InspectionTemplateDto template, MaintenanceClients clients, Consumer<InspectionTemplateDto> saved) {
        String written = name.getValue() == null ? "" : name.getValue().trim();
        name.setInvalid(written.isEmpty());
        name.setErrorMessage(written.isEmpty() ? InspectionTemplateVersionDialog.NAME_REQUIRED : null);
        if (written.isEmpty()) {
            return;
        }
        MergePatch<InspectionTemplateUpdateRequest> patch = renamePatch(template, written);
        if (patch.values().changesNothing()) {
            close();
            return;
        }
        try {
            InspectionTemplateDto result = clients.catalog().updateInspectionTemplate(template.id(), patch);
            close();
            Screens.success("Renombrada la version " + result.version() + " de " + template.assetType().label());
            saved.accept(result);
        } catch (ValidationApiException validation) {
            ApiFieldError ofTheName = validation.getProblem().errors().stream().filter(error -> "name".equals(error.field())).findFirst().orElse(null);
            if (ofTheName == null) {
                UiErrors.show(validation);
            } else {
                name.setErrorMessage(ServerValidation.message(ofTheName));
                name.setInvalid(true);
            }
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }
}
