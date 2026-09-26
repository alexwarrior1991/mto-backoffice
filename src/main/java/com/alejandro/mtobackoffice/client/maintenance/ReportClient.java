package com.alejandro.mtobackoffice.client.maintenance;

import com.alejandro.mtobackoffice.client.dto.maintenance.CatenaryAssetType;
import com.alejandro.mtobackoffice.client.dto.maintenance.MonthlyReportDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.ProgressReportDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.ShiftReportDto;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.time.Instant;
import java.time.YearMonth;
import java.util.UUID;

/**
 * Los informes de mantenimiento. Cada uno se pide en JSON (lo que se pinta) o como fichero
 * ({@code format=xlsx|pdf}, con su nombre en {@code Content-Disposition}), que se sirve a traves de
 * esta aplicacion. El servicio los genera en memoria; el gateway corta a los 15 s.
 */
@HttpExchange("/api/maintenance")
public interface ReportClient {

    @GetExchange("/reports/progress")
    ProgressReportDto progress(@RequestParam(value = "executionPackageId", required = false) Long executionPackageId,
                               @RequestParam(value = "trackId", required = false) Long trackId,
                               @RequestParam(value = "assetType", required = false) CatenaryAssetType assetType,
                               @RequestParam(value = "from", required = false) Instant from,
                               @RequestParam(value = "to", required = false) Instant to);

    @GetExchange("/reports/progress")
    ResponseEntity<byte[]> progressFile(@RequestParam(value = "executionPackageId", required = false) Long executionPackageId,
                                        @RequestParam(value = "trackId", required = false) Long trackId,
                                        @RequestParam(value = "assetType", required = false) CatenaryAssetType assetType,
                                        @RequestParam(value = "from", required = false) Instant from,
                                        @RequestParam(value = "to", required = false) Instant to,
                                        @RequestParam("format") String format);

    /** {@code month} viaja como {@code 2026-09}; es obligatorio (400 {@code REQ-400} si falta). */
    @GetExchange("/reports/monthly")
    MonthlyReportDto monthly(@RequestParam("month") @DateTimeFormat(pattern = "yyyy-MM") YearMonth month,
                             @RequestParam(value = "executionPackageId", required = false) Long executionPackageId);

    @GetExchange("/reports/monthly")
    ResponseEntity<byte[]> monthlyFile(@RequestParam("month") @DateTimeFormat(pattern = "yyyy-MM") YearMonth month,
                                       @RequestParam(value = "executionPackageId", required = false) Long executionPackageId,
                                       @RequestParam("format") String format);

    @GetExchange("/shifts/{id}/report")
    ShiftReportDto shiftReport(@PathVariable("id") UUID id);

    @GetExchange("/shifts/{id}/report")
    ResponseEntity<byte[]> shiftReportFile(@PathVariable("id") UUID id, @RequestParam("format") String format);
}
