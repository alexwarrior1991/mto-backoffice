package com.alejandro.mtobackoffice.client.dto.maintenance;

/** Los ficheros en que el servicio exporta un informe; sin formato, JSON. */
public enum ReportFormat {
    XLSX("xlsx", "Excel"),
    PDF("pdf", "PDF");

    private final String parameter;
    private final String label;

    ReportFormat(String parameter, String label) {
        this.parameter = parameter;
        this.label = label;
    }

    /** Lo que viaja en {@code ?format=}. */
    public String parameter() {
        return parameter;
    }

    public String label() {
        return label;
    }
}
