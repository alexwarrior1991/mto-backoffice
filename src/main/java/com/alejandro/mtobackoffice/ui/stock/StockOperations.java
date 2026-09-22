package com.alejandro.mtobackoffice.ui.stock;

import com.alejandro.mtobackoffice.client.dto.stock.MovementDto;
import com.alejandro.mtobackoffice.configuration.security.StockRoles;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.spring.security.AuthenticationContext;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Los cuatro botones que abren un {@link MovementDialog}. Entrada, salida y transferencia piden
 * {@code stock-write}; el ajuste, ademas {@code stock-adjust}. Sin permiso no hay boton.
 */
public final class StockOperations {

    private StockOperations() {
    }

    /**
     * @param prefill lo que la pantalla ya sabe (material y almacen elegidos), nuevo cada vez
     * @param done    que hacer con el apunte registrado (recargar)
     */
    public static HorizontalLayout buttons(AuthenticationContext authentication, StockClients clients,
                                           Supplier<MovementForm> prefill, Consumer<MovementDto> done) {
        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setSpacing(true);
        if (authentication.hasRole(StockRoles.STOCK_WRITE)) {
            buttons.add(button("Entrada", "operation-entry", VaadinIcon.ARROW_DOWN, MovementDialog.Kind.ENTRY, clients, prefill, done));
            buttons.add(button("Salida", "operation-output", VaadinIcon.ARROW_UP, MovementDialog.Kind.OUTPUT, clients, prefill, done));
            buttons.add(button("Transferencia", "operation-transfer", VaadinIcon.EXCHANGE, MovementDialog.Kind.TRANSFER, clients, prefill, done));
            if (authentication.hasRole(StockRoles.STOCK_ADJUST)) {
                buttons.add(button("Ajuste", "operation-adjustment", VaadinIcon.SLIDERS, MovementDialog.Kind.ADJUSTMENT, clients, prefill, done));
            }
        }
        return buttons;
    }

    private static Button button(String text, String id, VaadinIcon icon, MovementDialog.Kind kind, StockClients clients,
                                 Supplier<MovementForm> prefill, Consumer<MovementDto> done) {
        Button button = new Button(text, icon.create(), click -> new MovementDialog(kind, prefill.get(), clients, done).open());
        button.setId(id);
        button.addThemeVariants(ButtonVariant.LUMO_SMALL);
        return button;
    }
}
