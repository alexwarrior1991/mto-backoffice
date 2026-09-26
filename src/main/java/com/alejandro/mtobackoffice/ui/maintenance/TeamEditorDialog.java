package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.dto.maintenance.TeamDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.error.ValidationApiException;
import com.alejandro.mtobackoffice.ui.master.Pickers;
import com.alejandro.mtobackoffice.ui.master.RefItem;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

/**
 * Alta o modificacion de un equipo. El {@code PUT} es completo: se manda todo lo que el editor
 * ensena, y base o vehiculo vaciados se borran. No hay borrado; un equipo se retira desmarcando
 * «Activo». Un codigo repetido es un 409 {@code TEA-409} que llega como notificacion.
 */
public class TeamEditorDialog extends Dialog {

    public static final String SAVE_ID = "team-save";
    static final int CODE_LENGTH = 16;
    static final int TEXT_LENGTH = 120;

    private final Binder<TeamForm> binder = new Binder<>(TeamForm.class);
    private final TeamForm form;

    public TeamEditorDialog(TeamDto existing, MaintenanceClients clients, MaintenanceNames names, Runnable saved) {
        boolean creating = existing == null;
        this.form = TeamForm.of(existing, names);
        setHeaderTitle(creating ? "Nuevo equipo" : "Modificar equipo " + existing.code());
        setCloseOnOutsideClick(false);

        TextField code = new TextField("Codigo");
        code.setId("team-code");
        code.setMaxLength(CODE_LENGTH);
        code.setRequiredIndicatorVisible(true);
        TextField name = new TextField("Nombre");
        name.setId("team-name");
        name.setMaxLength(TEXT_LENGTH);
        name.setRequiredIndicatorVisible(true);
        TextField baseName = new TextField("Base");
        baseName.setId("team-base");
        baseName.setMaxLength(TEXT_LENGTH);
        TextField vehicle = new TextField("Vehiculo");
        vehicle.setId("team-vehicle");
        vehicle.setMaxLength(TEXT_LENGTH);
        MultiSelectComboBox<RefItem> packages = Pickers.references("Paquetes de ejecucion", names.packages());
        packages.setId("team-packages");
        Checkbox active = new Checkbox("Activo");
        active.setId("team-active");
        active.setHelperText("Desmarcarlo lo retira: deja de ofrecerse al asignar ordenes y turnos");

        binder.forField(code).asRequired("El codigo es obligatorio").bind("code");
        binder.forField(name).asRequired("El nombre es obligatorio").bind("name");
        binder.forField(baseName).bind("baseName");
        binder.forField(vehicle).bind("vehicle");
        binder.forField(packages).bind("executionPackageIds");
        FormLayout layout = new FormLayout(code, name, baseName, vehicle, packages);
        if (!creating) {
            binder.forField(active).bind("active");
            layout.add(active);
        }
        binder.readBean(form);
        layout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1), new FormLayout.ResponsiveStep("480px", 2));
        layout.setColspan(packages, 2);
        add(layout);

        Button save = new Button("Guardar", click -> save(existing, clients, saved));
        save.setId(SAVE_ID);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(new Button("Cancelar", click -> close()), save);
    }

    private void save(TeamDto existing, MaintenanceClients clients, Runnable saved) {
        if (!binder.writeBeanIfValid(form)) {
            return;
        }
        try {
            TeamDto result = existing == null
                    ? clients.catalog().createTeam(form.toRequest())
                    : clients.catalog().updateTeam(existing.id(), form.toRequest());
            close();
            MaintenanceUi.success("Guardado " + result.label());
            saved.run();
        } catch (ValidationApiException validation) {
            MaintenanceUi.showValidation(binder, validation);
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }
}
