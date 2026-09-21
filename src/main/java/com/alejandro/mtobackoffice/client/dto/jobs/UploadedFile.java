package com.alejandro.mtobackoffice.client.dto.jobs;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

/**
 * Un fichero subido desde el navegador, entero en memoria: los maestros son workbooks de cientos
 * de KB. Se convierte en el {@code Resource} de la parte multipart, con su nombre, que es lo que el
 * servicio ve como {@code filename}.
 */
public record UploadedFile(String fileName, String contentType, byte[] content) {

    public Resource asResource() {
        return new ByteArrayResource(content) {
            @Override
            public String getFilename() {
                return fileName;
            }
        };
    }

    public int size() {
        return content == null ? 0 : content.length;
    }
}
