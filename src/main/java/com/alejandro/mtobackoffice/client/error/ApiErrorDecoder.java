package com.alejandro.mtobackoffice.client.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * Convierte cualquier respuesta de error del gateway en una {@link BackofficeApiException}.
 *
 * <p>Tolera los tres formatos que pueden llegar (ver {@link ApiProblem}) y tambien un cuerpo que no
 * sea JSON: entonces el problema queda vacio y el texto, acotado, va al detalle. El id de
 * correlacion se toma de la cabecera de la respuesta, que el gateway siempre devuelve.</p>
 */
public class ApiErrorDecoder implements RestClient.ResponseSpec.ErrorHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiErrorDecoder.class);
    private static final int MAX_RAW_DETAIL = 300;

    private final ObjectMapper objectMapper;
    private final String correlationHeader;

    public ApiErrorDecoder(ObjectMapper objectMapper, String correlationHeader) {
        this.objectMapper = objectMapper;
        this.correlationHeader = correlationHeader;
    }

    @Override
    public void handle(HttpRequest request, ClientHttpResponse response) throws IOException {
        HttpStatusCode status = response.getStatusCode();
        byte[] body = StreamUtils.copyToByteArray(response.getBody());
        ApiProblem problem = parse(response.getHeaders().getContentType(), body);
        String correlationId = response.getHeaders().getFirst(correlationHeader);
        String operation = request.getMethod() + " " + request.getURI().getPath();

        BackofficeApiException exception = BackofficeApiException.of(status, problem, correlationId,
                retryAfter(response.getHeaders()), operation);
        LOGGER.warn("{} (reference={})", exception.getMessage(), exception.getReference());
        throw exception;
    }

    ApiProblem parse(MediaType contentType, byte[] body) {
        if (body == null || body.length == 0) {
            return ApiProblem.empty();
        }
        boolean json = contentType != null
                && (MediaType.APPLICATION_PROBLEM_JSON.isCompatibleWith(contentType)
                || MediaType.APPLICATION_JSON.isCompatibleWith(contentType));
        if (json) {
            try {
                return objectMapper.readValue(body, ApiProblem.class);
            } catch (RuntimeException exception) {
                LOGGER.debug("Error body could not be parsed as a problem: {}", exception.getMessage());
            }
        }
        String raw = new String(body, StandardCharsets.UTF_8).strip();
        String detail = raw.length() > MAX_RAW_DETAIL ? raw.substring(0, MAX_RAW_DETAIL) + "..." : raw;
        return new ApiProblem(null, null, null, detail, null, null, null, null, null, null, null, null);
    }

    private static Duration retryAfter(HttpHeaders headers) {
        String value = headers.getFirst(HttpHeaders.RETRY_AFTER);
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Duration.ofSeconds(Long.parseLong(value.trim()));
        } catch (NumberFormatException notSeconds) {
            // Retry-After tambien admite una fecha HTTP; el gateway manda segundos.
            return null;
        }
    }
}
