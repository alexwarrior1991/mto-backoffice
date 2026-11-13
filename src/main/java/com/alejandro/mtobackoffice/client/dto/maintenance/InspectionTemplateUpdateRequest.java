package com.alejandro.mtobackoffice.client.dto.maintenance;

/**
 * Lo unico que cambia de una version de una plantilla, para un {@code MergePatch}: su nombre y si es la
 * activa de su tipo ({@code null} en lo que no se toca; ninguno de los dos se puede vaciar). El bloqueo
 * optimista viaja aqui, como {@code lockVersion}, y no en la {@code version} del {@code MergePatch}
 * (que se deja a {@code null}): en una plantilla {@code version} es el numero de la version, y el
 * servicio rechaza esa clave en el parche.
 */
public record InspectionTemplateUpdateRequest(String name, Boolean active, Long lockVersion) {

    /** Ni el nombre ni el estado cambian: no se llama. El bloqueo solo no es un cambio. */
    public boolean changesNothing() {
        return name == null && active == null;
    }
}
