package com.alejandro.mtobackoffice.ui.resources;

import com.alejandro.mtobackoffice.client.dto.resources.OrganizationUnitDto;
import com.alejandro.mtobackoffice.client.dto.resources.TeamMemberDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.ui.support.LazyPanel;
import com.alejandro.mtobackoffice.ui.support.Screens;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Los linemen de un equipo, con la regla de 2 a 6 a la vista: «Anadir lineman» se deshabilita con 6
 * y «Sale del equipo» con 2, porque el servicio los rechazaria (409 {@code UNI-001}); cambiar varios a
 * la vez es «Cambiar los linemen», la lista entera de una vez ({@code PUT /members}). Con «Ver tambien
 * los que salieron» se ve quien estuvo y hasta cuando. Escribir pide {@code resources-write} y un
 * equipo activo.
 */
class TeamMembersPanel extends LazyPanel {

    static final int MIN_LINEMEN = 2;
    static final int MAX_LINEMEN = 6;

    private final Supplier<OrganizationUnitDto> unit;
    private final ResourcesClients clients;
    private final boolean canWrite;
    private final Consumer<OrganizationUnitDto> changed;

    private final Checkbox includeFormer = new Checkbox("Ver tambien los que salieron");
    private final Span count = new Span();
    private final Button add = new Button("Anadir lineman", VaadinIcon.PLUS.create());
    private final Button replace = new Button("Cambiar los linemen", VaadinIcon.EXCHANGE.create());
    private final Grid<TeamMemberDto> grid = new Grid<>();
    private List<TeamMemberDto> current = List.of();

    TeamMembersPanel(Supplier<OrganizationUnitDto> unit, ResourcesClients clients, boolean canWrite, Consumer<OrganizationUnitDto> changed) {
        this.unit = unit;
        this.clients = clients;
        this.canWrite = canWrite;
        this.changed = changed;
        includeFormer.setId("members-include-former");
        includeFormer.addValueChangeListener(change -> reload());
        count.setId("members-count-summary");
        add.setId("member-add");
        add.addClickListener(click -> MemberDialogs.add(unit.get(), clients, changed).open());
        replace.setId("members-replace");
        replace.addClickListener(click -> TeamMembersDialog.replace(unit.get(), current, clients, changed).open());
        HorizontalLayout toolbar = new HorizontalLayout(includeFormer, count, add, replace);
        toolbar.setAlignItems(FlexComponent.Alignment.BASELINE);
        toolbar.setWidthFull();
        toolbar.expand(count);

        grid.setId("members-grid");
        grid.addColumn(member -> member.person().code()).setHeader("Codigo").setKey("code").setAutoWidth(true);
        grid.addColumn(member -> member.person().fullName()).setHeader("Nombre").setKey("name").setAutoWidth(true);
        grid.addColumn(member -> ResourcesFormats.category(member.person().category())).setHeader("Categoria").setKey("category").setAutoWidth(true);
        grid.addColumn(member -> member.person().username()).setHeader("Usuario").setKey("username").setAutoWidth(true);
        grid.addColumn(member -> ResourcesFormats.date(member.joinedOn())).setHeader("Entro").setKey("joinedOn").setAutoWidth(true);
        grid.addColumn(member -> ResourcesFormats.date(member.leftOn())).setHeader("Salio").setKey("leftOn").setAutoWidth(true);
        grid.addColumn(new ComponentRenderer<>(this::rowActions)).setHeader("").setKey("actions").setAutoWidth(true).setFlexGrow(0);
        grid.setAllRowsVisible(true);
        add(toolbar, grid);
    }

    private Component rowActions(TeamMemberDto member) {
        HorizontalLayout actions = new HorizontalLayout();
        actions.setSpacing(false);
        if (writable() && member.isCurrent()) {
            Button leave = Screens.rowButton("member-leave-" + member.person().id(), VaadinIcon.SIGN_OUT,
                    "Sale del equipo " + member.person().label(), click -> MemberDialogs.leave(unit.get(), member, clients, changed).open());
            if (current.size() <= MIN_LINEMEN) {
                leave.setEnabled(false);
                leave.setTooltipText("Un equipo no baja de 2 linemen: anade otro antes o cambia los linemen de una vez");
            }
            actions.add(leave);
        }
        return actions;
    }

    private boolean writable() {
        return canWrite && unit.get().isActive();
    }

    @Override
    protected void load() {
        List<TeamMemberDto> members;
        try {
            members = clients.organization().members(unit.get().id(), includeFormer.getValue());
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
            members = List.of();
        }
        current = members.stream().filter(TeamMemberDto::isCurrent).toList();
        count.setText(current.size() + " de 2 a 6 linemen");
        add.setVisible(writable());
        replace.setVisible(writable());
        boolean full = current.size() >= MAX_LINEMEN;
        add.setEnabled(!full);
        add.setTooltipText(full ? "Un equipo no pasa de 6 linemen: saca a uno antes o cambia los linemen de una vez" : null);
        grid.setItems(members);
    }
}
