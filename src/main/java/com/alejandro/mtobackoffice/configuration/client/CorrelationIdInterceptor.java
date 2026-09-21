package com.alejandro.mtobackoffice.configuration.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.util.UUID;

/**
 * Un identificador nuevo por llamada saliente, en la cabecera y en el MDC mientras dura. El gateway
 * lo acepta tal cual (es un UUID, valido para su filtro) y lo propaga al servicio, de modo que la
 * misma linea aparece en los tres logs.
 */
public class CorrelationIdInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(CorrelationIdInterceptor.class);

    /** Clave en el MDC; es la que imprime {@code logging.pattern.correlation}. */
    public static final String MDC_KEY = "correlationId";

    private final String headerName;

    public CorrelationIdInterceptor(String headerName) {
        this.headerName = headerName;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        String correlationId = UUID.randomUUID().toString();
        request.getHeaders().set(headerName, correlationId);
        String previous = MDC.get(MDC_KEY);
        MDC.put(MDC_KEY, correlationId);
        try {
            LOGGER.debug("-> {} {}", request.getMethod(), request.getURI());
            return execution.execute(request, body);
        } finally {
            if (previous == null) {
                MDC.remove(MDC_KEY);
            } else {
                MDC.put(MDC_KEY, previous);
            }
        }
    }
}
