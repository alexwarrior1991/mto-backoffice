package com.alejandro.mtobackoffice.ui.master;

import com.alejandro.mtobackoffice.client.configuration.MasterFilters;
import com.alejandro.mtobackoffice.client.configuration.ProfileClient;
import com.alejandro.mtobackoffice.client.dto.master.LovRef;
import com.alejandro.mtobackoffice.client.dto.master.ProfileDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.converter.Converter;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Desplegables y conversores para las referencias de un maestro: a otro maestro ({@link RefItem},
 * que en el DTO es un id) y a un catalogo ({@link LovRef}, que en el DTO viaja entero).
 */
public final class Pickers {

    private Pickers() {
    }

    public static ComboBox<LovRef> lov(String label, List<LovRef> items) {
        ComboBox<LovRef> combo = new ComboBox<>(label);
        combo.setItems(items);
        combo.setItemLabelGenerator(LovRef::label);
        combo.setClearButtonVisible(true);
        return combo;
    }

    public static MultiSelectComboBox<LovRef> lovs(String label, List<LovRef> items) {
        MultiSelectComboBox<LovRef> combo = new MultiSelectComboBox<>(label);
        combo.setItems(items);
        combo.setItemLabelGenerator(LovRef::label);
        combo.setClearButtonVisible(true);
        return combo;
    }

    public static ComboBox<RefItem> reference(String label, List<RefItem> items) {
        ComboBox<RefItem> combo = new ComboBox<>(label);
        combo.setItems(items);
        combo.setItemLabelGenerator(RefItem::label);
        combo.setClearButtonVisible(true);
        return combo;
    }

    public static MultiSelectComboBox<RefItem> references(String label, List<RefItem> items) {
        MultiSelectComboBox<RefItem> combo = new MultiSelectComboBox<>(label);
        combo.setItems(items);
        combo.setItemLabelGenerator(RefItem::label);
        combo.setClearButtonVisible(true);
        return combo;
    }

    /**
     * Un perfil se busca en el servidor mientras se escribe: son miles y no caben en un
     * desplegable. El valor actual se resuelve aparte con {@link #profileRef}.
     */
    public static ComboBox<RefItem> lazyProfile(String label, ProfileClient profiles) {
        ComboBox<RefItem> combo = new ComboBox<>(label);
        combo.setItemLabelGenerator(RefItem::label);
        combo.setClearButtonVisible(true);
        combo.setPlaceholder("Escribe el identificador del perfil");
        combo.setItems(query -> {
            try {
                return profiles.filter(query.getPage(), query.getPageSize(), List.of("profileId,asc"),
                                MasterFilters.of("searchText", query.getFilter().orElse("")))
                        .content().stream().map(Pickers::profileRef);
            } catch (BackofficeApiException failure) {
                UiErrors.show(failure);
                return Stream.empty();
            }
        });
        return combo;
    }

    public static RefItem profileRef(ProfileDto profile) {
        return new RefItem(profile.getId(), profile.getProfileId() + " (kp " + profile.getKp() + ")");
    }

    /** El perfil al que apunta un id, para ensenarlo mientras se edita; {@code #id} si no se encuentra. */
    public static Function<Long, RefItem> profileResolver(ProfileClient profiles) {
        return id -> {
            try {
                return profileRef(profiles.findById(id));
            } catch (BackofficeApiException failure) {
                return new RefItem(id, "#" + id);
            }
        };
    }

    /** {@code RefItem} en el desplegable, id en el DTO. */
    public static Converter<RefItem, Long> refToId(Function<Long, Optional<RefItem>> resolver) {
        return Converter.from(
                item -> Result.ok(item == null ? null : item.id()),
                id -> id == null ? null : resolver.apply(id).orElse(new RefItem(id, "#" + id)));
    }

    /** {@code RefItem} en el desplegable, id en el DTO, resolviendo el id con una llamada. */
    public static Converter<RefItem, Long> refToId(Function<Long, RefItem> resolver, boolean lookup) {
        return Converter.from(
                item -> Result.ok(item == null ? null : item.id()),
                id -> id == null ? null : resolver.apply(id));
    }

    /** Seleccion multiple de referencias, lista de ids en el DTO (vacia si no hay ninguna). */
    public static Converter<Set<RefItem>, List<Long>> refsToIds(Function<Long, Optional<RefItem>> resolver) {
        return Converter.from(
                items -> Result.ok(items == null ? new ArrayList<>() : items.stream().map(RefItem::id).toList()),
                ids -> {
                    Set<RefItem> items = new LinkedHashSet<>();
                    if (ids != null) {
                        ids.forEach(id -> items.add(resolver.apply(id).orElse(new RefItem(id, "#" + id))));
                    }
                    return items;
                });
    }

    /** Seleccion multiple de catalogo, lista en el DTO (vacia si no hay ninguna: reemplaza el conjunto). */
    public static Converter<Set<LovRef>, List<LovRef>> lovSetToList() {
        return Converter.from(
                items -> Result.ok(items == null ? new ArrayList<>() : new ArrayList<>(items)),
                list -> list == null ? new LinkedHashSet<>() : new LinkedHashSet<>(list));
    }

    static Map<String, Object> noFilter() {
        return Map.of();
    }
}
