package com.alejandro.mtobackoffice.ui.master;

import com.alejandro.mtobackoffice.client.configuration.MasterResource;
import com.alejandro.mtobackoffice.client.dto.master.ExecutionPackageDto;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.converter.StringToLongConverter;

import java.util.function.Consumer;
import java.util.function.Function;

/** Alta o modificacion de un paquete de ejecucion; sus vias y estaciones no se tocan aqui. */
public class ExecutionPackageEditor extends MasterEditorDialog<ExecutionPackageDto> {

    static final int NAME_MAX_LENGTH = 200;

    public ExecutionPackageEditor(ExecutionPackageDto dto, ReferenceCatalog catalog,
                                  Function<ExecutionPackageDto, ExecutionPackageDto> saver, Consumer<ExecutionPackageDto> onSaved) {
        super(MasterResource.EXECUTION_PACKAGES, ExecutionPackageDto.class, dto, saver, onSaved);

        TextField name = text("Nombre", NAME_MAX_LENGTH, true);
        ComboBox<RefItem> company = Pickers.reference("Empresa", catalog.companies());
        company.setRequiredIndicatorVisible(true);
        TextField length = text("Longitud", 12, true);
        DatePicker startDate = new DatePicker("Inicio");
        startDate.setRequiredIndicatorVisible(true);
        DatePicker endDate = new DatePicker("Fin");
        endDate.setRequiredIndicatorVisible(true);
        Checkbox initialPackage = new Checkbox("Paquete inicial");
        Checkbox enabled = new Checkbox("Activo");

        binder.forField(name).asRequired("El nombre es obligatorio").bind("name");
        binder.forField(company).asRequired("La empresa es obligatoria")
                .withConverter(Pickers.refToId(catalog::companyRef)).bind("companyId");
        binder.forField(length).asRequired("La longitud es obligatoria").withNullRepresentation("")
                .withConverter(new StringToLongConverter("Debe ser un numero entero")).bind("length");
        binder.forField(startDate).asRequired("La fecha de inicio es obligatoria").bind("startDate");
        binder.forField(endDate).asRequired("La fecha de fin es obligatoria").bind("endDate");
        binder.forField(initialPackage).bind("initialPackage");
        binder.forField(enabled).bind("enabled");

        form.add(name, company, length, startDate, endDate, initialPackage, enabled);
        wide(name);
        if (dto.getInitialPackage() == null) {
            dto.setInitialPackage(Boolean.FALSE);
        }
        ready();
    }
}
