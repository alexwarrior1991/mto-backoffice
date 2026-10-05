package com.alejandro.mtobackoffice.configuration.vaadin;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

/**
 * La SPA ({@code app.frontend.*}), la otra aplicacion web del dominio: {@code mto-frontend} tiene las
 * mismas pantallas y las mismas rutas, y las dos se usan indistintamente. «Abrir en mto-frontend», en
 * la barra, lleva a la misma pantalla alli.
 *
 * @param url raiz de la SPA tal como la ve el navegador (en local, {@code http://localhost:4200});
 *            vacia, o si no es http(s), la barra no ofrece el enlace
 */
@ConfigurationProperties(prefix = "app.frontend")
public record FrontendProperties(String url) {

    /**
     * La misma pantalla en la SPA.
     *
     * @param pathWithQuery la ruta de esta aplicacion con su query, sin barra inicial, como la da
     *                      {@code Location.getPathWithQueryParameters()} ({@code ""} es Inicio)
     * @return el enlace, o vacio si la SPA no esta configurada
     */
    public Optional<String> linkTo(String pathWithQuery) {
        return root().map(root -> root + "/" + (pathWithQuery == null ? "" : pathWithQuery));
    }

    private Optional<String> root() {
        if (url == null || url.isBlank()) {
            return Optional.empty();
        }
        String trimmed = url.trim().replaceAll("/+$", "");
        try {
            URI uri = new URI(trimmed);
            boolean web = "http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme());
            return web && uri.getHost() != null ? Optional.of(trimmed) : Optional.empty();
        } catch (URISyntaxException invalid) {
            return Optional.empty();
        }
    }
}
