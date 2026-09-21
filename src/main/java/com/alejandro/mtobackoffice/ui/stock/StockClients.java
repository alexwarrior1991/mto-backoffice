package com.alejandro.mtobackoffice.ui.stock;

import com.alejandro.mtobackoffice.client.stock.AssemblyClient;
import com.alejandro.mtobackoffice.client.stock.MaterialClient;
import com.alejandro.mtobackoffice.client.stock.MovementClient;
import com.alejandro.mtobackoffice.client.stock.ProjectClient;
import com.alejandro.mtobackoffice.client.stock.ReservationClient;
import com.alejandro.mtobackoffice.client.stock.SupplierClient;
import com.alejandro.mtobackoffice.client.stock.WarehouseClient;

/** Los siete clientes de mto-stock juntos, para las pantallas y dialogos que necesitan varios. */
public record StockClients(MaterialClient materials, WarehouseClient warehouses, SupplierClient suppliers, ProjectClient projects,
                           MovementClient movements, ReservationClient reservations, AssemblyClient assemblies) {
}
