package com.alejandro.mtobackoffice.ui.stock;

import com.alejandro.mtobackoffice.client.dto.stock.AssemblyComponentDto;
import com.alejandro.mtobackoffice.client.dto.stock.AssemblyComponentRequest;
import com.alejandro.mtobackoffice.client.dto.stock.MaterialDto;
import com.alejandro.mtobackoffice.client.dto.stock.MaterialSummaryDto;
import com.alejandro.mtobackoffice.client.stock.StockCatalogueClient;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * La lista de materiales de un conjunto dentro de su editor: la tabla y una linea de alta (el
 * material, buscado en el servidor, y la cantidad por conjunto). Anadir un material que ya esta
 * sustituye su cantidad, asi que no hay lineas repetidas y cambiar una es volver a anadirla. Al
 * guardar va <b>entera</b>: para el servicio la lista que llega sustituye a la anterior, y no
 * puede ir vacia.
 */
public class BomEditor extends VerticalLayout {

    /** Una linea: cuanto de que material lleva el conjunto. */
    public record Line(MaterialSummaryDto material, BigDecimal quantity) {
        AssemblyComponentRequest toRequest() {
            return new AssemblyComponentRequest(material.id(), quantity);
        }
    }

    private final Map<UUID, Line> lines = new LinkedHashMap<>();
    private final Grid<Line> grid = new Grid<>();
    private final ComboBox<MaterialSummaryDto> material;
    private final BigDecimalField quantity = new BigDecimalField("Cantidad por conjunto");
    private final Span error = new Span();

    public BomEditor(List<AssemblyComponentDto> initial, StockCatalogueClient<MaterialDto, ?, ?> materials) {
        setPadding(false);
        setSpacing(false);
        for (AssemblyComponentDto component : initial) {
            lines.put(component.material().id(), new Line(component.material(), component.quantity()));
        }
        H4 heading = new H4("Lista de materiales");
        heading.addClassNames(LumoUtility.Margin.Top.MEDIUM, LumoUtility.Margin.Bottom.NONE);
        material = StockPickers.material("Material", materials);
        material.setId("bom-material");
        material.addValueChangeListener(change -> material.setInvalid(false));
        quantity.setId("bom-quantity");
        quantity.addValueChangeListener(change -> quantity.setInvalid(false));
        Button add = new Button("Anadir", VaadinIcon.PLUS.create(), click -> addLine());
        add.setId("bom-add");
        add.addThemeVariants(ButtonVariant.LUMO_SMALL);
        HorizontalLayout entry = new HorizontalLayout(material, quantity, add);
        entry.setAlignItems(FlexComponent.Alignment.BASELINE);
        entry.setWidthFull();
        entry.expand(material);

        grid.setId("bom-grid");
        grid.addColumn(line -> line.material().label()).setHeader("Material").setKey("material").setFlexGrow(1);
        grid.addColumn(line -> StockFormats.quantity(line.quantity()) + " " + unit(line.material())).setHeader("Por conjunto").setKey("quantity").setAutoWidth(true);
        grid.addColumn(new ComponentRenderer<>(this::removeButton)).setHeader("").setKey("actions").setAutoWidth(true).setFlexGrow(0);
        grid.setAllRowsVisible(true);
        error.setId("bom-error");
        error.addClassNames(LumoUtility.TextColor.ERROR, LumoUtility.FontSize.SMALL);
        error.setVisible(false);
        add(heading, entry, grid, error);
        show();
    }

    /** Las lineas tal como quedan, en el orden en que se anadieron. */
    public List<AssemblyComponentRequest> lines() {
        return lines.values().stream().map(Line::toRequest).toList();
    }

    public boolean isEmpty() {
        return lines.isEmpty();
    }

    public void showError(String message) {
        error.setText(message);
        error.setVisible(true);
    }

    private void addLine() {
        boolean valid = true;
        if (material.getValue() == null) {
            material.setErrorMessage("Elige el material");
            material.setInvalid(true);
            valid = false;
        }
        if (quantity.getValue() == null || quantity.getValue().signum() <= 0) {
            quantity.setErrorMessage("Tiene que ser mayor que cero");
            quantity.setInvalid(true);
            valid = false;
        }
        if (!valid) {
            return;
        }
        lines.put(material.getValue().id(), new Line(material.getValue(), quantity.getValue()));
        material.clear();
        quantity.clear();
        error.setVisible(false);
        show();
    }

    private Button removeButton(Line line) {
        Button remove = new Button(VaadinIcon.TRASH.create(), click -> {
            lines.remove(line.material().id());
            show();
        });
        remove.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);
        remove.setTooltipText("Quitar");
        remove.setId("bom-remove-" + line.material().id());
        return remove;
    }

    private void show() {
        grid.setItems(new ArrayList<>(lines.values()));
    }

    private static String unit(MaterialSummaryDto material) {
        return material.unitOfMeasure() == null ? "" : material.unitOfMeasure();
    }
}
