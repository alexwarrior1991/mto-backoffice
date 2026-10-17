package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.configuration.BusinessEntityClient;
import com.alejandro.mtobackoffice.client.configuration.ExecutionPackageClient;
import com.alejandro.mtobackoffice.client.configuration.StationClient;
import com.alejandro.mtobackoffice.client.configuration.TrackClient;
import com.alejandro.mtobackoffice.client.maintenance.AssetClient;
import com.alejandro.mtobackoffice.client.maintenance.DefectClient;
import com.alejandro.mtobackoffice.client.maintenance.InspectionClient;
import com.alejandro.mtobackoffice.client.maintenance.MaintenanceCatalogClient;
import com.alejandro.mtobackoffice.client.maintenance.OrderClient;
import com.alejandro.mtobackoffice.client.maintenance.ReportClient;
import com.alejandro.mtobackoffice.client.maintenance.ShiftClient;
import com.alejandro.mtobackoffice.client.resources.InstrumentClient;
import com.alejandro.mtobackoffice.client.resources.ParkingSiteClient;
import com.alejandro.mtobackoffice.client.resources.PersonClient;
import com.alejandro.mtobackoffice.client.resources.VehicleClient;
import com.alejandro.mtobackoffice.client.stock.MaterialClient;
import com.alejandro.mtobackoffice.client.stock.ProjectClient;
import com.alejandro.mtobackoffice.client.stock.WarehouseClient;
import org.springframework.stereotype.Component;

/**
 * Los clientes que usan las pantallas de mantenimiento, juntos: los de mto-maintenance y los de los
 * servicios a los que pertenece lo que mantenimiento guarda como id (vias, estaciones y paquetes de
 * mto-configuration; materiales, almacenes y proyectos de mto-stock; personas, vehiculos y sitios de
 * aparcamiento de mto-resources, y los instrumentos que se llevan a un turno), y la generacion de
 * tareas que sigue en el servicio ({@link TaskGeneration}). Un solo bean para que las vistas y sus dialogos no carguen una lista de
 * parametros que crece con cada pantalla; cada cliente sigue siendo su propio bean.
 */
@Component
public record MaintenanceClients(OrderClient orders, AssetClient assets, MaintenanceCatalogClient catalog, ShiftClient shifts,
                                 InspectionClient inspections, DefectClient defects, ReportClient reports,
                                 ExecutionPackageClient packages, StationClient stations, TrackClient tracks,
                                 BusinessEntityClient companies, MaterialClient materials, WarehouseClient warehouses,
                                 ProjectClient projects, PersonClient people, VehicleClient vehicles, ParkingSiteClient parkingSites,
                                 InstrumentClient instruments, TaskGeneration generation) {
}
