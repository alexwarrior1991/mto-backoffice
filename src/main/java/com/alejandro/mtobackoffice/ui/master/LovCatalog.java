package com.alejandro.mtobackoffice.ui.master;

import com.alejandro.mtobackoffice.client.configuration.LovClient;
import com.alejandro.mtobackoffice.client.configuration.LovResource;
import com.alejandro.mtobackoffice.client.dto.LovDto;
import com.alejandro.mtobackoffice.client.dto.master.LovRef;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.ui.support.UiErrors;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Las entradas de catalogo como referencias para los desplegables de un editor, pedidas una vez
 * por catalogo y por pantalla. Se ofrecen todas, tambien las desactivadas: la referencia que ya
 * tiene un perfil puede apuntar a una entrada dada de baja, y tiene que seguir viendose.
 */
public final class LovCatalog {

    private final LovClient client;
    private final Map<LovResource, List<LovRef>> loaded = new EnumMap<>(LovResource.class);

    public LovCatalog(LovClient client) {
        this.client = client;
    }

    public List<LovRef> of(LovResource resource) {
        return loaded.computeIfAbsent(resource, this::load);
    }

    private List<LovRef> load(LovResource resource) {
        try {
            return client.findAll(resource.path()).stream().map(LovCatalog::ref).toList();
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
            return List.of();
        }
    }

    static LovRef ref(LovDto dto) {
        return new LovRef(dto.id(), dto.code(), dto.description());
    }
}
