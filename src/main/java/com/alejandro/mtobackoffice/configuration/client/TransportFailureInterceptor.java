package com.alejandro.mtobackoffice.configuration.client;

import com.alejandro.mtobackoffice.client.error.UnreachableApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * Tipa lo que no llega a ser una respuesta: conexion rechazada, nombre que no se resuelve o
 * {@code read-timeout}. Sin esto el {@code RestClient} lo lanza como {@code ResourceAccessException},
 * que no es un {@code BackofficeApiException}: las vistas no lo capturan y la pantalla acaba en el
 * error interno de Vaadin.
 *
 * <p>Va el ultimo de la cadena, despues de {@link CorrelationIdInterceptor}: asi la peticion ya
 * lleva el id de correlacion que se mando, que es la referencia del fallo.</p>
 */
public class TransportFailureInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransportFailureInterceptor.class);

    private final String correlationHeader;

    public TransportFailureInterceptor(String correlationHeader) {
        this.correlationHeader = correlationHeader;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) {
        try {
            return execution.execute(request, body);
        } catch (IOException failure) {
            UnreachableApiException unreachable = new UnreachableApiException(
                    request.getMethod() + " " + request.getURI().getPath(),
                    request.getHeaders().getFirst(correlationHeader), failure);
            // Como ApiErrorDecoder con una respuesta de error: una linea, con la referencia.
            LOGGER.warn("{} (reference={})", unreachable.getMessage(), unreachable.getReference());
            throw unreachable;
        }
    }
}
