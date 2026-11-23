package com.alejandro.mtobackoffice.client.error;

/**
 * Algo que cuelga de un maestro de mto-configuration y que impide borrarlo: el tipo
 * ({@code profiles}, {@code disconnectors}, {@code sectionInsulators}...) y cuantos hay. Llega en
 * {@code dependents} del 409 {@code BUS-002} de un borrado, junto al {@code detail} que dice lo mismo
 * con palabras. Un tipo nuevo se lee igual: es una cadena.
 */
public record ApiDependent(String type, Long count) {
}
