package com.alejandro.mtobackoffice.client.stock;

import com.alejandro.mtobackoffice.client.dto.PageResponse;
import com.alejandro.mtobackoffice.client.dto.stock.AdjustmentRequest;
import com.alejandro.mtobackoffice.client.dto.stock.EntryRequest;
import com.alejandro.mtobackoffice.client.dto.stock.MovementDto;
import com.alejandro.mtobackoffice.client.dto.stock.MovementType;
import com.alejandro.mtobackoffice.client.dto.stock.OutputRequest;
import com.alejandro.mtobackoffice.client.dto.stock.TransferRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** El libro de movimientos de mto-stock: solo se anade y se lee; un error se corrige con un ajuste. */
@HttpExchange("/api/stock/movements")
public interface MovementClient {

    /** 201. */
    @PostExchange("/entries")
    MovementDto entry(@RequestBody EntryRequest request);

    /** 201; 409 {@code STK-001} si no hay disponible. */
    @PostExchange("/outputs")
    MovementDto output(@RequestBody OutputRequest request);

    /** 201; pide {@code stock-adjust} ademas de {@code stock-write}. */
    @PostExchange("/adjustments")
    MovementDto adjustment(@RequestBody AdjustmentRequest request);

    /** 201 con los dos apuntes: primero el saliente, luego el entrante. */
    @PostExchange("/transfers")
    List<MovementDto> transfer(@RequestBody TransferRequest request);

    @GetExchange("/{id}")
    MovementDto findById(@PathVariable("id") UUID id);

    @GetExchange
    PageResponse<MovementDto> search(@RequestParam(value = "movementType", required = false) MovementType movementType,
                                     @RequestParam(value = "warehouseId", required = false) UUID warehouseId,
                                     @RequestParam(value = "projectId", required = false) UUID projectId,
                                     @RequestParam(value = "materialId", required = false) UUID materialId,
                                     @RequestParam(value = "dateFrom", required = false) Instant dateFrom,
                                     @RequestParam(value = "dateTo", required = false) Instant dateTo,
                                     @RequestParam(value = "user", required = false) String user,
                                     @RequestParam("page") int page, @RequestParam("size") int size,
                                     @RequestParam("sort") List<String> sort);
}
