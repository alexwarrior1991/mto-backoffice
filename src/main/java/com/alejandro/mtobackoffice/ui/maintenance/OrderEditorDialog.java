package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.dto.maintenance.AssetSummaryDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.MaintenanceOrderType;
import com.alejandro.mtobackoffice.client.dto.maintenance.MaintenancePriority;
import com.alejandro.mtobackoffice.client.dto.maintenance.OrderDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.OrderUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.TeamSummaryDto;
import com.alejandro.mtobackoffice.client.dto.stock.ProjectSummaryDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.error.ValidationApiException;
import com.alejandro.mtobackoffice.ui.stock.StockPickers;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import java.util.function.Consumer;

/**
 * Alta y modificacion de una orden. El alta elige el activo (buscado en el servidor, solo los
 * activos); despues ya no cambia. En borrador y planificada se modifica todo lo demas; asignada o
 * en curso, solo descripcion, prioridad y notas de cierre, porque es lo que el servicio admite (el
 * resto seria 409 {@code TRN-001}). Se manda solo lo que cambio, y lo que tenia valor no se deja
 * vaciar: para el servicio {@code null} es «no tocar».
 */
public class OrderEditorDialog extends Dialog {

    public static final String SAVE_ID = "order-save";
    static final int TITLE_LENGTH = 255;
    static final int USER_LENGTH = 100;

    private final Binder<OrderForm> binder = new Binder<>(OrderForm.class);
    private final OrderForm form;

    /**
     * @param existing la orden a modificar, o {@code null} para un alta
     * @param saved    que hacer con la orden guardada
     */
    public OrderEditorDialog(OrderDto existing, MaintenanceClients clients, MaintenanceCatalogs catalogs, MaintenanceNames names,
                             Consumer<OrderDto> saved) {
        boolean creating = existing == null;
        boolean full = creating || existing.status().allowsFullUpdate();
        this.form = OrderForm.of(existing, names);
        setHeaderTitle(creating ? "Nueva orden" : "Modificar " + existing.code());
        setCloseOnOutsideClick(false);
        setWidth("min(90vw, 720px)");

        TextField title = new TextField("Titulo");
        title.setId("order-title");
        title.setMaxLength(TITLE_LENGTH);
        title.setRequiredIndicatorVisible(true);
        TextArea description = new TextArea("Descripcion");
        description.setId("order-description");
        ComboBox<MaintenanceOrderType> type = new ComboBox<>("Tipo", MaintenanceOrderType.selectable());
        type.setId("order-type");
        type.setItemLabelGenerator(MaintenanceOrderType::label);
        type.setHelperText("Una urgente arranca sin planificar y es siempre critica");
        ComboBox<MaintenancePriority> priority = new ComboBox<>("Prioridad", MaintenancePriority.selectable());
        priority.setId("order-priority");
        priority.setItemLabelGenerator(MaintenancePriority::label);
        ComboBox<AssetSummaryDto> asset = MaintenancePickers.asset("Activo", clients.assets(), null);
        asset.setId("order-asset");
        asset.setRequiredIndicatorVisible(true);
        DatePicker plannedDate = new DatePicker("Prevista");
        plannedDate.setId("order-planned-date");
        ComboBox<TeamSummaryDto> team = MaintenancePickers.team("Equipo", catalogs.activeTeams());
        team.setId("order-team");
        TextField assignedUser = new TextField("Asignada a");
        assignedUser.setId("order-assigned-user");
        assignedUser.setMaxLength(USER_LENGTH);
        TextArea closingNotes = new TextArea("Notas de cierre");
        closingNotes.setId("order-closing-notes");

        FormLayout layout = new FormLayout();
        layout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1), new FormLayout.ResponsiveStep("480px", 2));
        if (creating) {
            binder.forField(type).asRequired("El tipo es obligatorio").bind("type");
            binder.forField(asset).asRequired("El activo es obligatorio").bind("assetId");
            layout.add(asset, type);
        }
        if (full) {
            binder.forField(title).asRequired("El titulo es obligatorio").bind("title");
            binder.forField(plannedDate)
                    .withValidator(date -> creating || existing.plannedDate() == null || date != null, MaintenanceUi.CANNOT_CLEAR)
                    .bind("plannedDate");
            binder.forField(team)
                    .withValidator(selected -> creating || existing.team() == null || selected != null, MaintenanceUi.CANNOT_CLEAR)
                    .bind("teamId");
            binder.forField(assignedUser).bind("assignedUser");
            layout.add(title, priority, plannedDate, team, assignedUser);
            if (names.readsStock()) {
                ComboBox<ProjectSummaryDto> project = StockPickers.project("Proyecto de almacen", clients.projects());
                project.setId("order-stock-project");
                project.setHelperText("Vacio: el del paquete de ejecucion, al planificar");
                binder.forField(project)
                        .withValidator(selected -> creating || existing.stockProjectId() == null || selected != null, MaintenanceUi.CANNOT_CLEAR)
                        .bind("stockProjectId");
                layout.add(project);
            }
        } else {
            add(new Paragraph("La orden esta " + existing.status().label().toLowerCase()
                    + ": ya solo se cambian la descripcion, la prioridad y las notas de cierre."));
            binder.forField(closingNotes).bind("closingNotes");
            layout.add(priority, closingNotes);
            layout.setColspan(closingNotes, 2);
        }
        binder.forField(priority).asRequired("La prioridad es obligatoria").bind("priority");
        binder.forField(description).bind("description");
        layout.add(description);
        layout.setColspan(description, 2);
        binder.readBean(form);
        add(layout);

        Button save = new Button("Guardar", click -> save(existing, full, clients, saved));
        save.setId(SAVE_ID);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(new Button("Cancelar", click -> close()), save);
    }

    private void save(OrderDto existing, boolean full, MaintenanceClients clients, Consumer<OrderDto> saved) {
        if (!binder.writeBeanIfValid(form)) {
            return;
        }
        try {
            OrderDto result;
            if (existing == null) {
                result = clients.orders().create(form.toRequest());
            } else {
                OrderUpdateRequest request = form.toUpdateRequest(existing, full);
                if (request.changesNothing()) {
                    close();
                    return;
                }
                result = clients.orders().update(existing.id(), request);
            }
            close();
            MaintenanceUi.success("Guardada " + result.code());
            saved.accept(result);
        } catch (ValidationApiException validation) {
            MaintenanceUi.showValidation(binder, validation);
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }
}
