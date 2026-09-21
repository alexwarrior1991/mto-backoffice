package com.alejandro.mtobackoffice.client.dto.master;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Base de los maestros de infraestructura de mto-configuration, pensada para la regla de
 * {@code README_API.md} §4: <b>lee el recurso, modifica sobre lo leido y devuelvelo entero</b>.
 *
 * <p>Por eso son clases mutables y no records: el {@code Binder} escribe sobre el mismo objeto que
 * llego del servicio y ese objeto es lo que vuelve en el {@code PUT}. Y por eso hay dos cosas que
 * un DTO "solo con las claves que usa la UI" no tendria:</p>
 * <ul>
 *   <li><b>Lo que la UI no conoce se devuelve tal cual.</b> Cualquier clave que el servicio mande y
 *       aqui no tenga campo cae en {@link #extras()} y viaja de vuelta sin tocar. Sin esto, un campo
 *       nuevo del servicio —o uno que la pantalla no edita— llegaria como {@code null} al modificar
 *       y el servicio lo borraria.</li>
 *   <li><b>Las colecciones de hijos van a {@code null} antes de guardar</b> ({@link #forgetChildren()}):
 *       para el servicio {@code null} es "de esta coleccion no digo nada" y la deja intacta,
 *       mientras que una lista vacia u omitida borra a todos los hijos. Es la salida que
 *       {@code README_API.md} reserva para quien construye la peticion desde codigo, y por eso estas
 *       clases <b>no</b> llevan {@code @JsonInclude(NON_NULL)}: el {@code null} tiene que viajar.</li>
 * </ul>
 *
 * <p>Los campos de auditoria se leen para ensenarlos y se devuelven sin mas: el servicio los ignora
 * al escribir. {@code versionNumber} si cuenta: es el bloqueo optimista, y devolver el que se leyo
 * es lo que convierte un cambio concurrente en un 409 en vez de pisarlo.</p>
 */
public abstract class MasterDto {

    private Long id;
    private Integer versionNumber;
    private String createUser;
    private LocalDateTime createDate;
    private String versionUser;
    private LocalDateTime versionDate;
    private final Map<String, Object> extras = new LinkedHashMap<>();

    /** Deja a {@code null} las colecciones de hijos: "de esta coleccion no digo nada". */
    public abstract void forgetChildren();

    @JsonAnySetter
    public void putExtra(String key, Object value) {
        extras.put(key, value);
    }

    /** Lo que llego del servicio y aqui no tiene campo; vuelve tal cual en un PUT. */
    @JsonAnyGetter
    public Map<String, Object> extras() {
        return extras;
    }

    public boolean isNew() {
        return id == null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Integer versionNumber) {
        this.versionNumber = versionNumber;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public String getVersionUser() {
        return versionUser;
    }

    public void setVersionUser(String versionUser) {
        this.versionUser = versionUser;
    }

    public LocalDateTime getVersionDate() {
        return versionDate;
    }

    public void setVersionDate(LocalDateTime versionDate) {
        this.versionDate = versionDate;
    }
}
