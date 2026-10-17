package com.alejandro.mtobackoffice.ui.resources;

import com.alejandro.mtobackoffice.client.dto.resources.PersonCategory;
import com.alejandro.mtobackoffice.client.dto.resources.PersonFilter;
import com.alejandro.mtobackoffice.client.dto.resources.PersonSummaryDto;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleDto;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleFilter;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleSummaryDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.resources.PersonClient;
import com.alejandro.mtobackoffice.client.resources.VehicleClient;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.data.provider.CallbackDataProvider;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Los desplegables de personas y vehiculos de mto-resources, buscados en el servidor mientras se
 * escribe: solo las personas activas y de las categorias que admite lo que se elige (el jefe de una
 * unidad segun su nivel, los linemen operarios o jefes de equipo, el jefe de un turno cualquiera menos un
 * operario), y solo los vehiculos activos y disponibles, para no ofrecer lo que el servicio va a
 * rechazar. Quien decide sigue siendo el servicio (409 {@code UNI-001}, o {@code SHF-001} en un turno).
 * Los usan el organigrama y los turnos de mantenimiento.
 */
public final class ResourcesPickers {

    private static final List<String> BY_NAME = List.of("lastName,asc", "firstName,asc");
    private static final List<String> BY_CODE = List.of("code,asc");

    private ResourcesPickers() {
    }

    /** Una persona; {@code categories} se lee en cada busqueda, porque cambia con el nivel de la unidad. */
    public static ComboBox<PersonSummaryDto> person(String label, PersonClient people, Supplier<Collection<PersonCategory>> categories) {
        ComboBox<PersonSummaryDto> combo = new ComboBox<>(label);
        combo.setItemLabelGenerator(PersonSummaryDto::label);
        combo.setClearButtonVisible(true);
        combo.setPlaceholder("Escribe su nombre o su codigo");
        combo.setItems(provider(people, categories));
        return combo;
    }

    public static MultiSelectComboBox<PersonSummaryDto> people(String label, PersonClient people, Supplier<Collection<PersonCategory>> categories) {
        MultiSelectComboBox<PersonSummaryDto> combo = new MultiSelectComboBox<>(label);
        combo.setItemLabelGenerator(PersonSummaryDto::label);
        combo.setClearButtonVisible(true);
        combo.setPlaceholder("Escribe su nombre o su codigo");
        combo.setItems(provider(people, categories));
        return combo;
    }

    /** Los vehiculos activos y disponibles, para los de un turno. */
    public static MultiSelectComboBox<VehicleSummaryDto> vehicles(String label, VehicleClient vehicles) {
        MultiSelectComboBox<VehicleSummaryDto> combo = new MultiSelectComboBox<>(label);
        combo.setItemLabelGenerator(VehicleSummaryDto::label);
        combo.setClearButtonVisible(true);
        combo.setPlaceholder("Escribe su codigo");
        combo.setItems(query -> {
            try {
                return vehicles.search(VehicleFilter.availableMatching(query.getFilter().orElse("")), query.getPage(), query.getPageSize(), BY_CODE)
                        .content().stream().map(VehicleDto::summary);
            } catch (BackofficeApiException failure) {
                UiErrors.show(failure);
                return Stream.empty();
            }
        });
        return combo;
    }

    private static CallbackDataProvider.FetchCallback<PersonSummaryDto, String> provider(PersonClient people,
                                                                                       Supplier<Collection<PersonCategory>> categories) {
        return query -> {
            Collection<PersonCategory> allowed = categories.get();
            if (allowed.isEmpty()) {
                return Stream.empty();
            }
            try {
                PersonFilter filter = PersonFilter.activeOf(query.getFilter().orElse(""), EnumSet.copyOf(allowed));
                return people.search(filter, query.getPage(), query.getPageSize(), BY_NAME).content().stream().map(PersonSummaryDto::of);
            } catch (BackofficeApiException failure) {
                UiErrors.show(failure);
                return Stream.empty();
            }
        };
    }
}
