package com.alejandro.mtobackoffice.ui.master;

import com.alejandro.mtobackoffice.client.configuration.MasterResource;
import com.alejandro.mtobackoffice.client.dto.master.StationDto;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.TextField;

import java.util.function.Consumer;
import java.util.function.Function;

/** Alta o modificacion de una estacion; sus tres colecciones de hijos no se tocan aqui. */
public class StationEditor extends MasterEditorDialog<StationDto> {

    static final int NAME_MAX_LENGTH = 200;

    public StationEditor(StationDto dto, ReferenceCatalog catalog,
                         Function<StationDto, StationDto> saver, Consumer<StationDto> onSaved) {
        super(MasterResource.STATIONS, StationDto.class, dto, saver, onSaved);

        TextField name = text("Nombre", NAME_MAX_LENGTH, true);
        ComboBox<RefItem> executionPackage = Pickers.reference("Paquete de ejecucion", catalog.packages());
        executionPackage.setRequiredIndicatorVisible(true);

        binder.forField(name).asRequired("El nombre es obligatorio").bind("name");
        binder.forField(executionPackage).asRequired("El paquete de ejecucion es obligatorio")
                .withConverter(Pickers.refToId(catalog::packageRef)).bind("executionPackageId");

        form.add(name, executionPackage);
        ready();
    }
}
