package com.alejandro.mtobackoffice.ui.master;

/**
 * Una referencia a otro maestro para elegirla en un desplegable: el id que se envia y la etiqueta
 * que se ensena. La igualdad es por id, que es lo que permite a un ComboBox reconocer como suyo el
 * valor que llego del servicio aunque la etiqueta se haya construido aparte.
 */
public record RefItem(Long id, String label) {

    @Override
    public boolean equals(Object other) {
        return other instanceof RefItem that && id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }
}
