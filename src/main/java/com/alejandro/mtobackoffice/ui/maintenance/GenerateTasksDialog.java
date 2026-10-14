package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.dto.maintenance.AssetSummaryDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.CatenaryAssetType;
import com.alejandro.mtobackoffice.client.dto.maintenance.GenerateTasksRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.GenerateTasksResultDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.GenerationPreviewLineDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.GenerationSelection;
import com.alejandro.mtobackoffice.client.dto.maintenance.OrderDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.TaskTypeDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.ui.master.RefItem;
import com.alejandro.mtobackoffice.ui.support.Formats;
import com.alejandro.mtobackoffice.ui.support.Numbers;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.HasValidation;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Generar las tareas de un preventivo: una por activo de lo que dice cada fila, un tipo de activo en un
 * sitio. Sin tocar nada, los perfiles del tramo de la orden, como siempre. Con EP, ademas, perfiles,
 * aisladores y seccionadores de otro tramo, de una via (lo suyo y lo que conecta con ella), de una
 * estacion, de todo el EP o elegidos, en cualquier via del EP: una orden es de un solo EP, y lo de otro
 * va en otra orden, asi que los desplegables solo ofrecen lo suyo ({@link MaintenanceNames#tracksIn},
 * los activos filtrados por EP).
 *
 * <p>Si todo es el tramo de la orden sin tipos propios viaja la peticion de siempre ({@code {}} o
 * {@code assetTypes}); si no, una seleccion por fila. Sin tipos, cada activo lleva los del plan: grupos
 * 1, 2 y 4 en un perfil, RG-03 en un aislador, RP-06 en un seccionador con motor (uno manual no tiene,
 * y hay que elegirlos). Lo que ya tiene tarea en la orden, lo que tiene una abierta en otra orden y lo de
 * otro EP se salta: repetirlo no duplica. «Previsualizar» pregunta al servicio que pasaria con cada
 * activo sin crear nada, y cualquier cambio en el dialogo la borra. Lo que resulta lo cuenta el servicio
 * y se ensena tal cual.</p>
 */
public class GenerateTasksDialog extends Dialog {

    public static final String CONFIRM_ID = "generate-confirm";
    public static final String PREVIEW_ID = "generate-preview";
    public static final String ADD_ID = "generate-add";
    public static final String PREVIEW_GRID_ID = "generate-preview-grid";
    public static final String PREVIEW_SUMMARY_ID = "generate-preview-summary";

    static final String ALL_TYPES_HELP = "Para todo lo generado. Vacio: grupos 1, 2 y 4 en cada perfil, RG-03 en cada aislador y RP-06 en "
            + "cada seccionador con motor";
    private static final List<CatenaryAssetType> GENERATED =
            List.of(CatenaryAssetType.PROFILE, CatenaryAssetType.SECTION_INSULATOR, CatenaryAssetType.DISCONNECTOR);

    /** Donde busca una fila. Todo menos el tramo de la orden pide una orden con EP. */
    enum Scope {
        ORDER_SECTION("El tramo de la orden"),
        SECTION("Otro tramo del EP"),
        TRACK("Una via del EP"),
        STATION("Una estacion del EP"),
        PACKAGE("Todo el EP"),
        ASSETS("Activos elegidos");

        private final String label;

        Scope(String label) {
            this.label = label;
        }

        String label() {
            return label;
        }
    }

    private final OrderDto order;
    private final MaintenanceClients clients;
    private final MaintenanceCatalogs catalogs;
    private final MaintenanceNames names;
    private final Runnable done;
    private final List<Scope> scopes;
    private final List<Row> rows = new ArrayList<>();
    private final VerticalLayout rowsLayout = new VerticalLayout();
    private final MultiSelectComboBox<TaskTypeDto> types;
    private final Checkbox withChecklist = new Checkbox("Con la checklist de la plantilla de cada activo");
    private final Paragraph previewSummary = new Paragraph();
    private final Grid<GenerationPreviewLineDto> preview = new Grid<>();
    private int created;

    public GenerateTasksDialog(OrderDto order, MaintenanceClients clients, MaintenanceCatalogs catalogs, MaintenanceNames names, Runnable done) {
        this.order = order;
        this.clients = clients;
        this.catalogs = catalogs;
        this.names = names;
        this.done = done;
        this.scopes = scopesOf(order, names.readsConfiguration());
        setHeaderTitle("Generar tareas de " + order.code());
        setCloseOnOutsideClick(false);
        setWidth("min(95vw, 960px)");

        rowsLayout.setPadding(false);
        addRow(CatenaryAssetType.PROFILE, onSection(order) ? Scope.ORDER_SECTION : Scope.PACKAGE);
        Button add = new Button("Anadir que generar", VaadinIcon.PLUS.create(), click -> {
            addRow(CatenaryAssetType.SECTION_INSULATOR, rows.isEmpty() ? scopes.getFirst() : rows.getLast().scope.getValue());
            changed();
        });
        add.setId(ADD_ID);

        types = MaintenancePickers.taskTypes("Tipos de tarea", catalogs.taskTypes());
        types.setId("generate-types");
        types.setWidthFull();
        types.setHelperText(ALL_TYPES_HELP);
        types.addValueChangeListener(change -> changed());
        withChecklist.setId("generate-with-checklist");
        withChecklist.addValueChangeListener(change -> changed());

        previewSummary.setId(PREVIEW_SUMMARY_ID);
        preview.setId(PREVIEW_GRID_ID);
        preview.addColumn(GenerationPreviewLineDto::label).setHeader("Activo").setKey("asset").setAutoWidth(true);
        preview.addColumn(line -> line.type() == null ? "" : line.type().label()).setHeader("Tipo").setKey("type").setAutoWidth(true);
        preview.addColumn(line -> line.trackId() == null ? "" : names.trackName(line.trackId())).setHeader("Via").setKey("track").setAutoWidth(true);
        preview.addColumn(line -> MaintenanceFormats.kp(line.kp())).setHeader("KP").setKey("kp").setAutoWidth(true);
        preview.addColumn(line -> String.join(", ", line.taskTypeCodes())).setHeader("Tipos").setKey("types").setAutoWidth(true);
        preview.addColumn(GenerationPreviewLineDto::outcomeLabel).setHeader("Resultado").setKey("outcome").setFlexGrow(1);
        preview.setAllRowsVisible(true);
        preview.setEmptyStateText("Nada encaja en lo pedido.");
        showPreview(null);

        add(new Paragraph(introText(order)), rowsLayout, add);
        if (order.executionPackageId() == null) {
            add(new Paragraph("La orden no tiene EP: solo genera su tramo. Otros tramos y vias van en una orden con EP."));
        }
        add(types, withChecklist, previewSummary, preview);

        Button previewButton = new Button("Previsualizar", click -> send(true));
        previewButton.setId(PREVIEW_ID);
        Button confirm = new Button("Generar", click -> send(false));
        confirm.setId(CONFIRM_ID);
        confirm.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(new Button("Volver", click -> close()), previewButton, confirm);
    }

    private void send(boolean dryRun) {
        boolean valid = true;
        for (Row row : rows) {
            valid &= row.validate();
        }
        if (!valid) {
            return;
        }
        try {
            GenerateTasksResultDto result = clients.orders().generateTasks(order.id(), request(dryRun));
            if (dryRun) {
                showPreview(result);
                return;
            }
            close();
            MaintenanceUi.success(describe(result, false));
            done.run();
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }

    /** Si todo es el tramo de la orden sin tipos propios, la peticion de siempre; si no, una seleccion por fila. */
    GenerateTasksRequest request(boolean dryRun) {
        List<String> codes = types.getValue().isEmpty() ? null : codesOf(types.getValue());
        Boolean checklist = withChecklist.getValue() ? Boolean.TRUE : null;
        Boolean preview = dryRun ? Boolean.TRUE : null;
        if (rows.stream().allMatch(Row::isPlainOrderSection)) {
            Set<CatenaryAssetType> chosen = rows.stream().map(row -> row.type.getValue()).collect(Collectors.toCollection(() -> EnumSet.noneOf(CatenaryAssetType.class)));
            List<CatenaryAssetType> assetTypes = chosen.equals(EnumSet.of(CatenaryAssetType.PROFILE))
                    ? null
                    : GENERATED.stream().filter(chosen::contains).toList();
            return GenerateTasksRequest.ofSection(codes, checklist, assetTypes, preview);
        }
        return new GenerateTasksRequest(codes, checklist, null, rows.stream().map(Row::selection).toList(), preview);
    }

    private void addRow(CatenaryAssetType type, Scope scope) {
        Row row = new Row(++created, type, scope);
        rows.add(row);
        rowsLayout.add(row);
    }

    /** Cualquier cambio deja vieja la previsualizacion: ya no dice lo que se pediria. */
    private void changed() {
        showPreview(null);
        rows.forEach(row -> row.remove.setVisible(rows.size() > 1));
    }

    private void showPreview(GenerateTasksResultDto result) {
        previewSummary.setVisible(result != null);
        preview.setVisible(result != null);
        previewSummary.setText(result == null ? "" : describe(result, true));
        preview.setItems(result == null ? List.of() : result.assets());
    }

    /** Una fila: un tipo de activo, donde y, con EP, sus propios tipos de tarea. */
    private final class Row extends Div {

        private final int number;
        private final ComboBox<CatenaryAssetType> type = new ComboBox<>("Activos");
        private final ComboBox<Scope> scope = new ComboBox<>("Donde");
        private final FormLayout where = new FormLayout();
        private final MultiSelectComboBox<TaskTypeDto> ownTypes;
        private final Button remove;
        private ComboBox<AssetSummaryDto> section;
        private ComboBox<RefItem> track;
        private BigDecimalField fromKp;
        private BigDecimalField toKp;
        private ComboBox<RefItem> station;
        private MultiSelectComboBox<AssetSummaryDto> chosen;

        Row(int number, CatenaryAssetType initialType, Scope initialScope) {
            this.number = number;
            setId(id(""));
            getElement().setAttribute("aria-label", "Que generar " + number);
            type.setId(id("-type"));
            type.setItems(GENERATED);
            type.setItemLabelGenerator(GenerateTasksDialog::plural);
            type.setValue(initialType);
            type.setAllowCustomValue(false);
            scope.setId(id("-scope"));
            scope.setItems(scopes);
            scope.setItemLabelGenerator(Scope::label);
            scope.setValue(scopes.contains(initialScope) ? initialScope : scopes.getFirst());
            ownTypes = MaintenancePickers.taskTypes("Sus tipos de tarea", catalogs.taskTypes());
            ownTypes.setId(id("-types"));
            ownTypes.setHelperText("Opcional: solo para lo de esta fila");
            // Sin EP solo cabe el tramo de la orden y viaja la peticion de siempre, que no lleva tipos por fila.
            ownTypes.setVisible(order.executionPackageId() != null);
            remove = new Button(VaadinIcon.TRASH.create(), click -> {
                rows.remove(this);
                rowsLayout.remove(this);
                changed();
            });
            remove.setId(id("-remove"));
            remove.setTooltipText("Quitar");
            remove.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
            remove.setVisible(false);

            FormLayout head = new FormLayout(type, scope, ownTypes);
            head.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1), new FormLayout.ResponsiveStep("560px", 3));
            add(new H4("Que generar " + number), head, where, remove);
            type.addValueChangeListener(change -> {
                // Los elegidos son de un tipo: otro tipo, otro buscador.
                if (scope.getValue() == Scope.ASSETS) {
                    rebuildWhere();
                }
                changed();
            });
            scope.addValueChangeListener(change -> {
                rebuildWhere();
                changed();
            });
            ownTypes.addValueChangeListener(change -> changed());
            rebuildWhere();
            rows.forEach(row -> row.remove.setVisible(true));
            remove.setVisible(!rows.isEmpty());
        }

        private String id(String suffix) {
            return "generate-row-" + number + suffix;
        }

        /** Los campos del sitio: el tramo, la via y sus KP, la estacion o los activos, de nuevo con cada tipo o sitio. */
        private void rebuildWhere() {
            where.removeAll();
            section = null;
            track = null;
            fromKp = null;
            toKp = null;
            station = null;
            chosen = null;
            Long packageId = order.executionPackageId();
            if (scope.getValue() == null) {
                return;
            }
            switch (scope.getValue()) {
                case SECTION -> {
                    section = MaintenancePickers.asset("Tramo", clients.assets(), CatenaryAssetType.TRACK_SECTION, packageId);
                    section.setId(id("-section"));
                    section.addValueChangeListener(change -> changed());
                    where.add(section);
                }
                case TRACK -> {
                    track = new ComboBox<>("Via", names.tracksIn(packageId));
                    track.setId(id("-track"));
                    track.setItemLabelGenerator(RefItem::label);
                    track.setHelperText("Lo de esa via y lo que conecta con ella");
                    fromKp = new BigDecimalField("Desde KP");
                    fromKp.setId(id("-from"));
                    toKp = new BigDecimalField("Hasta KP");
                    toKp.setId(id("-to"));
                    Stream.of(fromKp, toKp).forEach(field -> field.addValueChangeListener(change -> changed()));
                    track.addValueChangeListener(change -> changed());
                    where.add(track, fromKp, toKp);
                }
                case STATION -> {
                    station = new ComboBox<>("Estacion", names.stationsIn(packageId));
                    station.setId(id("-station"));
                    station.setItemLabelGenerator(RefItem::label);
                    station.addValueChangeListener(change -> changed());
                    where.add(station);
                }
                case ASSETS -> {
                    CatenaryAssetType kind = type.getValue();
                    chosen = MaintenancePickers.assets(kind == null ? "Activos" : kind.label(), clients.assets(), kind, packageId);
                    chosen.setId(id("-assets"));
                    chosen.addValueChangeListener(change -> changed());
                    where.add(chosen);
                }
                default -> {
                    // El tramo de la orden y todo el EP no piden nada mas.
                }
            }
        }

        boolean isPlainOrderSection() {
            return scope.getValue() == Scope.ORDER_SECTION && ownTypes.getValue().isEmpty();
        }

        /** Lo que falta antes de llamar: el servicio diria lo mismo con un 400. */
        boolean validate() {
            boolean valid = required(type, type.getValue() != null, "Elige que activos")
                    & required(scope, scope.getValue() != null, "Elige donde");
            if (section != null) {
                valid &= required(section, section.getValue() != null, "Elige un tramo");
            }
            if (track != null) {
                valid &= required(track, track.getValue() != null, "Elige una via");
                valid &= Numbers.check(fromKp, Numbers.kp()) & Numbers.check(toKp, Numbers.kp());
                if (valid && fromKp.getValue() != null && toKp.getValue() != null && fromKp.getValue().compareTo(toKp.getValue()) > 0) {
                    toKp.setInvalid(true);
                    toKp.setErrorMessage("El KP final no puede ser menor que el inicial");
                    valid = false;
                }
            }
            if (station != null) {
                valid &= required(station, station.getValue() != null, "Elige una estacion");
            }
            if (chosen != null) {
                valid &= required(chosen, !chosen.getValue().isEmpty(), "Elige al menos uno");
            }
            return valid;
        }

        GenerationSelection selection() {
            List<String> codes = ownTypes.getValue().isEmpty() ? null : codesOf(ownTypes.getValue());
            CatenaryAssetType assetType = type.getValue();
            return switch (scope.getValue()) {
                case ORDER_SECTION -> new GenerationSelection(assetType, order.asset().id(), null, null, null, null, null, codes);
                case SECTION -> new GenerationSelection(assetType, section.getValue().id(), null, null, null, null, null, codes);
                case TRACK -> new GenerationSelection(assetType, null, track.getValue().id(), fromKp.getValue(), toKp.getValue(), null, null, codes);
                case STATION -> new GenerationSelection(assetType, null, null, null, null, station.getValue().id(), null, codes);
                case ASSETS -> new GenerationSelection(assetType, null, null, null, null, null,
                        chosen.getValue().stream().map(AssetSummaryDto::id).toList(), codes);
                case PACKAGE -> new GenerationSelection(assetType, null, null, null, null, null, null, codes);
            };
        }
    }

    private static boolean required(HasValidation field, boolean present, String message) {
        field.setInvalid(!present);
        field.setErrorMessage(present ? null : message);
        return present;
    }

    private static List<String> codesOf(Set<TaskTypeDto> types) {
        return types.stream().map(TaskTypeDto::code).sorted().toList();
    }

    private static boolean onSection(OrderDto order) {
        return order.asset() != null && order.asset().type() == CatenaryAssetType.TRACK_SECTION;
    }

    static List<Scope> scopesOf(OrderDto order, boolean readsConfiguration) {
        List<Scope> scopes = new ArrayList<>();
        if (onSection(order)) {
            scopes.add(Scope.ORDER_SECTION);
        }
        if (order.executionPackageId() != null) {
            scopes.add(Scope.SECTION);
            if (readsConfiguration) {
                scopes.add(Scope.TRACK);
                scopes.add(Scope.STATION);
            }
            scopes.add(Scope.PACKAGE);
            scopes.add(Scope.ASSETS);
        }
        return scopes;
    }

    private static String plural(CatenaryAssetType type) {
        return switch (type) {
            case PROFILE -> "Perfiles";
            case SECTION_INSULATOR -> "Aisladores de seccion";
            case DISCONNECTOR -> "Seccionadores";
            default -> type.label();
        };
    }

    private static String introText(OrderDto order) {
        String section = onSection(order)
                ? "Una tarea por cada perfil habilitado entre los KP " + MaintenanceFormats.kpRange(order.startKp(), order.endKp())
                + " del tramo, si no eliges otra cosa. "
                : "";
        String more = order.executionPackageId() != null ? "Anade aisladores, seccionadores u otros sitios de su EP; una orden es de un solo EP. " : "";
        return section + more + "Lo que ya tiene tarea, aqui o en otra orden abierta, se salta.";
    }

    /**
     * Lo que dice el servicio: lo creado por tipo de activo y lo saltado; con {@code preview}, lo que se
     * crearia. Un servicio anterior no manda lo creado por tipo ni los saltados nuevos.
     */
    static String describe(GenerateTasksResultDto result, boolean preview) {
        StringBuilder text = new StringBuilder(preview ? "Se crearian: " : "Tareas nuevas: ").append(result.createdTasks());
        if (!result.createdByAssetType().isEmpty()) {
            text.append(" (").append(GENERATED.stream().filter(result.createdByAssetType()::containsKey)
                    .map(type -> type.label().toLowerCase() + ": " + result.createdByAssetType().get(type))
                    .collect(Collectors.joining(", "))).append(")");
        }
        text.append("; ya en la orden: ").append(result.skipped());
        if (result.inOtherOrders() > 0) {
            text.append("; en otras ordenes: ").append(result.inOtherOrders()).append(" (").append(String.join(", ", result.otherOrderCodes())).append(")");
        }
        if (result.otherPackage() > 0) {
            text.append("; de otro EP: ").append(result.otherPackage());
        }
        if (result.withoutTaskTypes() > 0) {
            text.append("; sin tipo de tarea: ").append(result.withoutTaskTypes());
        }
        return text.append(". Total: ").append(result.totalTasks()).append(", unos ").append(Formats.quantity(result.estimatedMinutes()))
                .append(" min en ").append(result.estimatedShifts()).append(result.estimatedShifts() == 1 ? " turno." : " turnos.").toString();
    }
}
