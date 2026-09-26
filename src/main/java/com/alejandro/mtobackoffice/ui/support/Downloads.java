package com.alejandro.mtobackoffice.ui.support;

import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.server.streams.DownloadHandler;
import com.vaadin.flow.server.streams.DownloadResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayInputStream;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Un fichero del servicio servido a traves de esta aplicacion: al pulsar el enlace, el fichero se
 * pide desde aqui, con el token de la persona, y se devuelve al navegador en la misma respuesta. El
 * navegador nunca habla con el gateway, porque no tiene token (y no debe tenerlo).
 *
 * <p>Lo usan los trabajos (el fichero de una importacion o exportacion) y los informes de
 * mantenimiento (xlsx y pdf). El nombre sale de {@code Content-Disposition}; si el servicio no lo
 * manda, el de reserva.</p>
 */
public final class Downloads {

    private static final Logger LOGGER = LoggerFactory.getLogger(Downloads.class);

    private Downloads() {
    }

    /**
     * @param id           el id del enlace, para las pruebas
     * @param text         lo que se lee en el enlace
     * @param fallbackName el nombre si el servicio no manda {@code Content-Disposition}
     * @param file         la llamada que trae el fichero; se hace al pulsar, no al pintar
     */
    public static Anchor link(String id, String text, String fallbackName, Supplier<ResponseEntity<byte[]>> file) {
        DownloadHandler handler = DownloadHandler.fromInputStream(event -> response(file, fallbackName), fallbackName);
        Anchor anchor = new Anchor(handler, text);
        anchor.setId(id);
        return anchor;
    }

    /** Lo que se sirve: el cuerpo tal cual, con su nombre y su tipo. Un fallo del servicio se devuelve con su estado. */
    public static DownloadResponse response(Supplier<ResponseEntity<byte[]>> file, String fallbackName) {
        try {
            ResponseEntity<byte[]> response = file.get();
            byte[] body = response.getBody() == null ? new byte[0] : response.getBody();
            String fileName = Optional.ofNullable(response.getHeaders().getContentDisposition().getFilename()).orElse(fallbackName);
            String contentType = Optional.ofNullable(response.getHeaders().getContentType()).map(MediaType::toString)
                    .orElse(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            return new DownloadResponse(new ByteArrayInputStream(body), fileName, contentType, body.length);
        } catch (BackofficeApiException failure) {
            LOGGER.warn("No se ha podido descargar {}: {}", fallbackName, failure.getMessage());
            return DownloadResponse.error(failure.getStatus().value());
        }
    }
}
