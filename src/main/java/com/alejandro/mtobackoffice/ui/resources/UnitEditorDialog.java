package com.alejandro.mtobackoffice.ui.resources;

import com.alejandro.mtobackoffice.client.dto.MergePatch;
import com.alejandro.mtobackoffice.client.dto.resources.OrganizationUnitDto;
import com.alejandro.mtobackoffice.client.dto.resources.OrganizationUnitKind;
import com.alejandro.mtobackoffice.client.dto.resources.OrganizationUnitUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.resources.PersonCategory;
import com.alejandro.mtobackoffice.client.dto.resources.PersonSummaryDto;
import com.alejandro.mtobackoffice.client.dto.resources.UnitSummaryDto;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleDto;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleFilter;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleSummaryDto;
import com.alejandro.mtobackoffice.client.dto.resources.WorkDiscipline;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.error.ValidationApiException;
import com.alejandro.mtobackoffice.ui.master.RefItem;
import com.alejandro.mtobackoffice.ui.support.Required;
import com.alejandro.mtobackoffice.ui.support.Screens;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Alta y modificacion de una unidad del organigrama. Lo que decide sin llamar sale del arbol ya
 * cargado y de las reglas copiadas del servicio, solo para no ofrecer lo que va a fallar: el padre,
 * de un nivel estrictamente superior y de su disciplina; la disciplina, la del padre si la tiene; el
 * jefe, de las categorias de su nivel. Un equipo nace completo: su jefe y de 2 a 6 linemen, que no
 * sean el jefe, comprobados antes de llamar. Lo demas lo dice el servicio ({@code UNI-001},
 * {@code UNI-409}, {@code VAL-001}) con el dialogo abierto. La modificacion no cambia el codigo, el
 * nivel ni la disciplina; manda solo lo que cambio y la version leida. Sin {@code config-read} los EP
 * se ven como {@code #id} y no viajan.
 */
public class UnitEditorDialog extends Dialog {

    public static final String SAVE_ID = "unit-save";
    static final int CODE_LENGTH = 16;
    static final int NAME_LENGTH = 120;

    private final Binder<UnitForm> binder = new Binder<>(UnitForm.class);
    private final UnitForm form;
    private final OrganizationTree tree;
    private final ResourcesNames names;
    private final OrganizationUnitDto existing;

    private final Select<OrganizationUnitKind> kind = new Select<>();
    private final ComboBox<UnitSummaryDto> parent = new ComboBox<>("Depende de");
    private final Select<WorkDiscipline> discipline = new Select<>();
    private final ComboBox<PersonSummaryDto> head;
    private final MultiSelectComboBox<PersonSummaryDto> members;
    private final DatePicker joinedOn = new DatePicker("Entran el");
    private final ComboBox<VehicleSummaryDto> defaultVehicle = new ComboBox<>("Vehiculo por defecto");

    /**
     * @param existing la unidad a modificar, o {@code null} para dar una de alta
     * @param under    al dar de alta, el padre ya elegido (desde su fila o su ficha), o {@code null}
     * @param saved    que hacer con la unidad guardada
     */
    public UnitEditorDialog(OrganizationUnitDto existing, UnitSummaryDto under, OrganizationTree tree, ResourcesClients clients,
                            ResourcesNames names, Consumer<OrganizationUnitDto> saved) {
        this.existing = existing;
        this.tree = tree;
        this.names = names;
        boolean creating = existing == null;
        this.form = creating ? UnitForm.creating(under) : UnitForm.of(existing, names::packageRef);
        setHeaderTitle(creating ? "Nueva unidad" : "Modificar " + existing.label());
        setCloseOnOutsideClick(false);
        setWidth("min(90vw, 720px)");

        TextField code = new TextField("Codigo");
        code.setId("unit-code");
        code.setMaxLength(CODE_LENGTH);
        code.setHelperText("Letras, cifras, - y _; un equipo, el codigo con el que lo conoce mantenimiento");
        code.setReadOnly(!creating);
        TextField name = new TextField("Nombre");
        name.setId("unit-name");
        name.setMaxLength(NAME_LENGTH);
        kind.setLabel("Nivel");
        kind.setId("unit-kind");
        kind.setItems(OrganizationUnitKind.selectable());
        kind.setItemLabelGenerator(ResourcesFormats::kind);
        kind.setReadOnly(!creating);
        parent.setId("unit-parent");
        parent.setItemLabelGenerator(UnitSummaryDto::label);
        discipline.setLabel("Disciplina");
        discipline.setId("unit-discipline");
        discipline.setItems(WorkDiscipline.selectable());
        discipline.setItemLabelGenerator(ResourcesFormats::discipline);
        head = ResourcesPickers.person("Jefe", clients.people(), this::headCategories);
        head.setId("unit-head");
        members = ResourcesPickers.people("Linemen", clients.people(), () -> OrganizationUnitKind.LINEMAN_CATEGORIES);
        members.setId("unit-members");
        joinedOn.setId("unit-joined-on");
        joinedOn.setHelperText("Vacio: hoy");
        defaultVehicle.setId("unit-default-vehicle");
        defaultVehicle.setItemLabelGenerator(VehicleSummaryDto::label);
        defaultVehicle.setClearButtonVisible(true);
        defaultVehicle.setPlaceholder("Escribe su codigo");
        defaultVehicle.setHelperText("El que copia un turno nuevo del equipo si no se dice otro");
        defaultVehicle.setItems(query -> {
            try {
                return clients.vehicles().search(VehicleFilter.availableMatching(query.getFilter().orElse("")), query.getPage(), query.getPageSize(),
                        List.of("code,asc")).content().stream().map(VehicleDto::summary);
            } catch (BackofficeApiException failure) {
                UiErrors.show(failure);
                return Stream.empty();
            }
        });

        binder.forField(code).asRequired(Required.text("El codigo es obligatorio")).bind("code");
        binder.forField(name).asRequired(Required.text("El nombre es obligatorio")).bind("name");
        binder.forField(kind).asRequired("El nivel es obligatorio").bind("kind");
        binder.forField(parent)
                .withValidator(value -> value != null || kind.getValue() == OrganizationUnitKind.PROJECT,
                        "Una unidad cuelga de otra de un nivel superior; solo un proyecto no cuelga de nada")
                .bind("parentId");
        binder.forField(discipline)
                .withValidator(value -> value != null || kind.getValue() == OrganizationUnitKind.PROJECT,
                        "Nada por encima tiene disciplina: dila")
                .bind("discipline");
        binder.forField(head)
                .withValidator(value -> value != null || kind.getValue() != OrganizationUnitKind.TEAM, "Un equipo trabaja siempre con su jefe")
                .bind("headPersonId");
        binder.forField(members)
                .withValidator(this::membersValid, "Un equipo nace con 2 a 6 linemen, sin su jefe entre ellos")
                .bind("memberPersonIds");
        binder.forField(joinedOn).bind("joinedOn");
        binder.forField(defaultVehicle).bind("defaultVehicleId");

        FormLayout layout = new FormLayout(code, name, kind, parent, discipline, head, packagesField(), defaultVehicle);
        if (creating) {
            layout.add(members, joinedOn);
        }
        layout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1), new FormLayout.ResponsiveStep("480px", 2));
        add(layout);

        // Un ComboBox no admite un valor sin items: los candidatos (y el padre ya elegido) antes de leer el formulario.
        parent.setItems(candidates(form.getKind(), form.getParentId()));
        binder.readBean(form);
        kind.addValueChangeListener(change -> levelChanged());
        parent.addValueChangeListener(change -> parentChanged());
        discipline.addValueChangeListener(change -> headCategoriesChanged());
        members.addValueChangeListener(change -> countMembers());
        levelChanged();

        Button save = new Button("Guardar", click -> save(clients, saved));
        save.setId(SAVE_ID);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(new Button("Cancelar", click -> close()), save);
    }

    /** Los EP: de mto-configuration con {@code config-read}; sin el, los leidos como {@code #id}, de solo lectura. */
    private Component packagesField() {
        if (!names.readsConfiguration()) {
            TextField readOnly = new TextField("EP");
            readOnly.setId("unit-packages-readonly");
            readOnly.setValue(existing == null ? "" : names.packageNames(existing.executionPackageIds()));
            readOnly.setReadOnly(true);
            readOnly.setHelperText("Sin permiso para leer infraestructura: se quedan como estan");
            return readOnly;
        }
        MultiSelectComboBox<RefItem> packages = new MultiSelectComboBox<>("EP");
        packages.setId("unit-packages");
        packages.setItemLabelGenerator(RefItem::label);
        List<RefItem> items = new ArrayList<>(names.packages());
        form.getExecutionPackageIds().stream().filter(item -> !items.contains(item)).forEach(items::add);
        packages.setItems(items);
        packages.setHelperText("Los paquetes de ejecucion en los que trabaja");
        binder.forField(packages).bind("executionPackageIds");
        packages.setValue(form.getExecutionPackageIds());
        return packages;
    }

    private List<PersonCategory> headCategories() {
        return kind.getValue() == null ? List.of() : kind.getValue().headCategories(discipline.getValue());
    }

    private boolean membersValid(Set<PersonSummaryDto> chosen) {
        if (existing != null || kind.getValue() != OrganizationUnitKind.TEAM) {
            return true;
        }
        int count = chosen == null ? 0 : chosen.size();
        return count >= 2 && count <= 6 && (head.getValue() == null || !chosen.contains(head.getValue()));
    }

    /** El nivel decide de quien puede colgar, si tiene disciplina, quien lo dirige y si lleva linemen. */
    private void levelChanged() {
        OrganizationUnitKind level = kind.getValue();
        boolean project = level == OrganizationUnitKind.PROJECT;
        boolean team = level == OrganizationUnitKind.TEAM;
        parent.setVisible(!project);
        parent.setRequiredIndicatorVisible(!project);
        refreshParents();
        discipline.setVisible(!project);
        head.setRequiredIndicatorVisible(team);
        head.setHelperText(team ? "Un equipo se dirige siempre: el jefe se sustituye, no se quita" : "Vacio: vacante");
        members.setVisible(team && existing == null);
        members.setRequiredIndicatorVisible(team);
        joinedOn.setVisible(team && existing == null);
        defaultVehicle.setVisible(team);
        parentChanged();
        countMembers();
    }

    private void refreshParents() {
        UnitSummaryDto current = parent.getValue();
        List<UnitSummaryDto> candidates = candidates(kind.getValue(), null);
        parent.setItems(candidates);
        parent.setValue(current != null && candidates.contains(current) ? current : null);
    }

    /**
     * De quien puede colgar una unidad de este nivel, segun el arbol. El padre leido se ve aunque ya no
     * fuera un candidato (desactivado despues), y se puede dejar; {@code chosen}, el que llega elegido
     * desde la fila de su padre, tambien.
     */
    private List<UnitSummaryDto> candidates(OrganizationUnitKind level, UnitSummaryDto chosen) {
        WorkDiscipline own = existing == null ? null : existing.discipline();
        List<UnitSummaryDto> candidates = new ArrayList<>(tree.parentsFor(level, own, existing == null ? null : existing.id()));
        if (existing != null && existing.parent() != null && !candidates.contains(existing.parent())) {
            candidates.addFirst(existing.parent());
        }
        if (chosen != null && !candidates.contains(chosen)) {
            candidates.addFirst(chosen);
        }
        return candidates;
    }

    /** La disciplina del padre manda; bajo un proyecto (o sin padre) se elige. Al modificar no cambia. */
    private void parentChanged() {
        if (kind.getValue() == OrganizationUnitKind.PROJECT) {
            discipline.setValue(null);
        } else if (existing == null) {
            WorkDiscipline inherited = tree.disciplineUnder(parent.getValue());
            if (inherited != null) {
                discipline.setValue(inherited);
            }
            discipline.setReadOnly(inherited != null);
            discipline.setRequiredIndicatorVisible(inherited == null);
            discipline.setHelperText(inherited != null ? "La de " + parent.getValue().code() : "Nada por encima la tiene: dila");
        } else {
            discipline.setReadOnly(true);
        }
        headCategoriesChanged();
    }

    /** El jefe elegido que ya no vale para el nivel se quita, y el desplegable vuelve a buscar con las categorias nuevas. */
    private void headCategoriesChanged() {
        List<PersonCategory> allowed = headCategories();
        if (head.getValue() != null && !allowed.contains(head.getValue().category())) {
            head.clear();
        }
        head.getDataProvider().refreshAll();
    }

    private void countMembers() {
        int count = members.getValue() == null ? 0 : members.getValue().size();
        members.setHelperText(count + " de 2 a 6");
    }

    private void save(ResourcesClients clients, Consumer<OrganizationUnitDto> saved) {
        if (!binder.writeBeanIfValid(form)) {
            return;
        }
        try {
            OrganizationUnitDto result;
            if (existing == null) {
                result = clients.organization().create(form.toRequest());
            } else {
                MergePatch<OrganizationUnitUpdateRequest> patch = form.toPatch(existing, names.readsConfiguration());
                if (patch.changesNothing()) {
                    close();
                    return;
                }
                result = clients.organization().update(existing.id(), patch);
            }
            close();
            Screens.success("Guardada " + result.label());
            saved.accept(result);
        } catch (ValidationApiException validation) {
            Screens.showValidation(binder, validation);
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }
}
