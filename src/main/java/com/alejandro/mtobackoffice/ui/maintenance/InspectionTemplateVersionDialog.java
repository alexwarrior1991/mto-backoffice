package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.dto.maintenance.CatenaryAssetType;
import com.alejandro.mtobackoffice.client.dto.maintenance.InspectionTemplateDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.InspectionTemplateItemDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.InspectionTemplateItemRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.InspectionTemplateRequest;
import com.alejandro.mtobackoffice.client.error.ApiFieldError;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.error.ValidationApiException;
import com.alejandro.mtobackoffice.ui.support.Numbers;
import com.alejandro.mtobackoffice.ui.support.Screens;
import com.alejandro.mtobackoffice.ui.support.ServerValidation;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.HasValidation;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * La version siguiente de la plantilla de inspeccion de un tipo de activo: su nombre y la lista ENTERA
 * de puntos en orden, partiendo de los de la version de la que se sale (vacia si el tipo no tiene
 * ninguna). Una version no cambia sus puntos nunca: cambiar un checklist es crear otra version, que
 * nace activa y deja como anterior a la activa. {@code replacesVersion} es el numero de la activa del
 * tipo tal como se leyo, y no viaja si no habia: si entre medias otra persona creo o activo otra, el
 * servicio responde 409 {@code CON-001} sin escribir nada y el dialogo sigue abierto.
 *
 * <p>Lo evidente se comprueba antes de llamar con las reglas del servicio; sus errores por campo caen
 * en el nombre, en el campo de su fila ({@code items[1].code}) o bajo la lista ({@code items}), y lo
 * demas se notifica con su ruta. Pide {@code maintenance-supervise} y {@code maintenance-write}.</p>
 */
public class InspectionTemplateVersionDialog extends Dialog {

    public static final String SAVE_ID = "template-version-save";
    public static final String ADD_ID = "template-item-add";
    public static final String NAME_ID = "template-version-name";
    public static final String NOTICE_ID = "template-version-notice";
    public static final String ITEMS_ERROR_ID = "template-items-error";
    static final int MAX_ITEMS = 100;
    static final int NAME_LENGTH = 120;
    static final int CODE_LENGTH = 32;
    static final int LABEL_LENGTH = 255;
    static final int UNIT_LENGTH = 32;
    /** El patron del servicio: mayusculas, cifras, {@code _ . -}, sin espacios y sin empezar por un signo. */
    static final Pattern CODE = Pattern.compile("[A-Z0-9][A-Z0-9_.-]*");
    static final String NAME_REQUIRED = "El nombre es obligatorio";
    static final String NO_ITEMS = "Una plantilla lleva al menos un punto";
    static final String TOO_MANY_ITEMS = "Como mucho " + MAX_ITEMS + " puntos";
    static final String CODE_REQUIRED = "El codigo es obligatorio";
    static final String CODE_PATTERN = "Mayusculas sin tildes, cifras, '_', '.' y '-', sin espacios y empezando por letra o cifra";
    static final String CODE_REPEATED = "Codigo repetido en la plantilla";
    static final String LABEL_REQUIRED = "El texto es obligatorio";
    static final String RANGE = "El minimo no puede ser mayor que el maximo";
    private static final Pattern ITEMS_ELEMENT = Pattern.compile("items(\\[\\d+])?");

    private final CatenaryAssetType assetType;
    private final Integer replacesVersion;
    private final TextField name = new TextField("Nombre");
    private final VerticalLayout rowsLayout = new VerticalLayout();
    private final Paragraph itemsError = new Paragraph();
    private final Button add = new Button("Anadir punto", VaadinIcon.PLUS.create());
    private final List<Row> rows = new ArrayList<>();

    /**
     * @param assetType el tipo de activo de la version nueva
     * @param base      la version de la que se parte (su nombre y sus puntos), o {@code null} para empezar vacia
     * @param active    la version activa del tipo tal como se leyo, o {@code null} si no tiene
     */
    public InspectionTemplateVersionDialog(CatenaryAssetType assetType, InspectionTemplateDto base, InspectionTemplateDto active,
                                           MaintenanceClients clients, Consumer<InspectionTemplateDto> saved) {
        this.assetType = assetType;
        this.replacesVersion = active == null ? null : active.version();
        setHeaderTitle("Nueva version de la plantilla de " + assetType.label());
        setCloseOnOutsideClick(false);
        setWidth("min(96vw, 1100px)");

        Paragraph notice = new Paragraph(noticeOf(assetType, base, active));
        notice.setId(NOTICE_ID);
        name.setId(NAME_ID);
        name.setMaxLength(NAME_LENGTH);
        name.setRequiredIndicatorVisible(true);
        name.setWidthFull();
        name.setValue(base == null || base.name() == null ? "" : base.name());
        rowsLayout.setPadding(false);
        rowsLayout.setSpacing(false);
        itemsError.setId(ITEMS_ERROR_ID);
        itemsError.getStyle().set("color", "var(--lumo-error-text-color)");
        itemsError.setVisible(false);
        add.setId(ADD_ID);
        add.addClickListener(click -> {
            rows.add(new Row(null));
            render();
        });
        if (base != null) {
            base.items().stream()
                    .sorted(Comparator.comparing((InspectionTemplateItemDto item) -> item.orderIndex() == null ? Integer.MAX_VALUE : item.orderIndex()))
                    .forEach(item -> rows.add(new Row(item)));
        }
        VerticalLayout body = new VerticalLayout(notice, name, new H4("Puntos, en su orden"), rowsLayout, itemsError, add);
        body.setPadding(false);
        add(body);
        render();

        Button save = new Button("Crear y activar", click -> save(clients, saved));
        save.setId(SAVE_ID);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(new Button("Cancelar", click -> close()), save);
    }

    /** Lo que pasa al guardar, dicho antes: la nueva nace activa y la activa de ahora pasa a anterior. */
    static String noticeOf(CatenaryAssetType assetType, InspectionTemplateDto base, InspectionTemplateDto active) {
        String start = base == null ? "Es la primera version de " + assetType.label() + ". "
                : "Parte de los puntos de la version " + base.version() + ". ";
        String effect = active == null ? "La nueva version nace activa. "
                : "La nueva version nace activa y la version " + active.version() + ", la activa ahora, pasa a anterior. ";
        return start + effect + "Los puntos de una version no se cambian despues. Las inspecciones ya creadas no cambian.";
    }

    /** Las filas en su orden, cada una con los ids de su posicion; no se anade con el tope alcanzado. */
    private void render() {
        rowsLayout.removeAll();
        for (int index = 0; index < rows.size(); index++) {
            rowsLayout.add(rows.get(index).at(index, rows.size()));
        }
        add.setEnabled(rows.size() < MAX_ITEMS);
        showItemsError(null);
    }

    private void showItemsError(String message) {
        itemsError.setText(message == null ? "" : message);
        itemsError.setVisible(message != null);
    }

    private void move(Row row, int offset) {
        int from = rows.indexOf(row);
        int to = from + offset;
        if (from < 0 || to < 0 || to >= rows.size()) {
            return;
        }
        rows.remove(from);
        rows.add(to, row);
        render();
    }

    private void remove(Row row) {
        rows.remove(row);
        render();
    }

    private boolean validate() {
        boolean valid = true;
        String written = name.getValue() == null ? "" : name.getValue().trim();
        name.setInvalid(written.isEmpty());
        name.setErrorMessage(written.isEmpty() ? NAME_REQUIRED : null);
        valid &= !written.isEmpty();
        showItemsError(rows.isEmpty() ? NO_ITEMS : rows.size() > MAX_ITEMS ? TOO_MANY_ITEMS : null);
        valid &= !rows.isEmpty() && rows.size() <= MAX_ITEMS;
        Set<String> codes = new HashSet<>();
        for (Row row : rows) {
            valid &= row.validate(codes);
        }
        return valid;
    }

    private void save(MaintenanceClients clients, Consumer<InspectionTemplateDto> saved) {
        if (!validate()) {
            return;
        }
        InspectionTemplateRequest request = new InspectionTemplateRequest(assetType, name.getValue().trim(),
                rows.stream().map(Row::request).toList(), replacesVersion);
        try {
            InspectionTemplateDto created = clients.catalog().createInspectionTemplate(request);
            close();
            Screens.success("Creada la version " + created.version() + " de la plantilla de " + assetType.label() + ", ya activa");
            saved.accept(created);
        } catch (ValidationApiException validation) {
            showValidation(validation);
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }

    /** Cada error del servicio en su campo, en el de su fila o bajo la lista; lo demas, notificado con su ruta. */
    private void showValidation(ValidationApiException validation) {
        if (!validation.getProblem().hasFieldErrors()) {
            UiErrors.show(validation);
            return;
        }
        Map<String, HasValidation> fields = new HashMap<>();
        fields.put("name", name);
        for (int index = 0; index < rows.size(); index++) {
            rows.get(index).fields("items[" + index + "].", fields);
        }
        List<String> unattributed = new ArrayList<>();
        List<String> ofTheList = new ArrayList<>();
        for (ApiFieldError error : validation.getProblem().errors()) {
            String message = ServerValidation.message(error);
            HasValidation field = error.field() == null ? null : fields.get(error.field());
            if (field != null) {
                field.setErrorMessage(message);
                field.setInvalid(true);
            } else if (error.field() != null && ITEMS_ELEMENT.matcher(error.field()).matches()) {
                ofTheList.add("items".equals(error.field()) ? message : "Fila " + rowOf(error.field()) + ": " + message);
            } else {
                unattributed.add(error.field() == null || error.field().isBlank() ? message : error.field() + ": " + message);
            }
        }
        if (!ofTheList.isEmpty()) {
            showItemsError(String.join(". ", ofTheList));
        }
        if (!unattributed.isEmpty()) {
            Notification.show(String.join(". ", unattributed), 8000, Notification.Position.BOTTOM_START)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private static int rowOf(String field) {
        return Integer.parseInt(field.substring(field.indexOf('[') + 1, field.indexOf(']'))) + 1;
    }

    /** Un punto: sus campos y sus botones de orden. Los componentes son los mismos al moverlo; cambian sus ids. */
    private final class Row {

        private final TextField code = new TextField();
        private final TextField label = new TextField();
        private final TextField unit = new TextField();
        private final BigDecimalField min = new BigDecimalField();
        private final BigDecimalField max = new BigDecimalField();
        private final Checkbox measure = new Checkbox("Exige medida");
        private final Button up = new Button(VaadinIcon.ARROW_UP.create(), click -> move(this, -1));
        private final Button down = new Button(VaadinIcon.ARROW_DOWN.create(), click -> move(this, 1));
        private final Button delete = new Button(VaadinIcon.TRASH.create(), click -> remove(this));
        private final HorizontalLayout layout = new HorizontalLayout(code, label, unit, min, max, measure, up, down, delete);

        private Row(InspectionTemplateItemDto item) {
            code.setPlaceholder("Codigo");
            code.setMaxLength(CODE_LENGTH);
            code.setWidth("10em");
            label.setPlaceholder("Texto del punto");
            label.setMaxLength(LABEL_LENGTH);
            unit.setPlaceholder("Unidad");
            unit.setMaxLength(UNIT_LENGTH);
            unit.setWidth("7em");
            min.setPlaceholder("Minimo");
            min.setWidth("8em");
            max.setPlaceholder("Maximo");
            max.setWidth("8em");
            for (Button button : List.of(up, down, delete)) {
                button.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_SMALL);
            }
            layout.setWidthFull();
            layout.setAlignItems(FlexComponent.Alignment.BASELINE);
            layout.expand(label);
            if (item != null) {
                code.setValue(item.code() == null ? "" : item.code());
                label.setValue(item.label() == null ? "" : item.label());
                unit.setValue(item.unit() == null ? "" : item.unit());
                min.setValue(item.minValue());
                max.setValue(item.maxValue());
                measure.setValue(Boolean.TRUE.equals(item.requiresMeasure()));
            }
        }

        private HorizontalLayout at(int index, int size) {
            String position = "Punto " + (index + 1);
            code.setId("template-item-code-" + index);
            code.setAriaLabel(position + ": codigo");
            label.setId("template-item-label-" + index);
            label.setAriaLabel(position + ": texto");
            unit.setId("template-item-unit-" + index);
            unit.setAriaLabel(position + ": unidad");
            min.setId("template-item-min-" + index);
            min.setAriaLabel(position + ": minimo");
            max.setId("template-item-max-" + index);
            max.setAriaLabel(position + ": maximo");
            measure.setId("template-item-measure-" + index);
            up.setId("template-item-up-" + index);
            up.setTooltipText("Subir el punto " + (index + 1));
            up.setEnabled(index > 0);
            down.setId("template-item-down-" + index);
            down.setTooltipText("Bajar el punto " + (index + 1));
            down.setEnabled(index < size - 1);
            delete.setId("template-item-remove-" + index);
            delete.setTooltipText("Quitar el punto " + (index + 1));
            layout.setId("template-item-" + index);
            return layout;
        }

        /** Comprueba el punto y marca lo que no pasa; {@code codes} lleva los codigos de las filas de antes. */
        private boolean validate(Set<String> codes) {
            boolean valid = true;
            String written = trimmed(code);
            String codeError = written.isEmpty() ? CODE_REQUIRED : !CODE.matcher(written).matches() ? CODE_PATTERN
                    : !codes.add(written) ? CODE_REPEATED : null;
            valid &= mark(code, codeError);
            valid &= mark(label, trimmed(label).isEmpty() ? LABEL_REQUIRED : null);
            unit.setInvalid(false);
            boolean numbers = Numbers.check(min, Numbers.measure()) & Numbers.check(max, Numbers.measure());
            if (numbers && min.getValue() != null && max.getValue() != null && min.getValue().compareTo(max.getValue()) > 0) {
                numbers = mark(min, RANGE);
            }
            return valid & numbers;
        }

        private InspectionTemplateItemRequest request() {
            String unitText = trimmed(unit);
            return new InspectionTemplateItemRequest(trimmed(code), trimmed(label), unitText.isEmpty() ? null : unitText, min.getValue(),
                    max.getValue(), measure.getValue());
        }

        private void fields(String prefix, Map<String, HasValidation> fields) {
            fields.put(prefix + "code", code);
            fields.put(prefix + "label", label);
            fields.put(prefix + "unit", unit);
            fields.put(prefix + "minValue", min);
            fields.put(prefix + "maxValue", max);
            fields.put(prefix + "requiresMeasure", label);
        }

        private static String trimmed(TextField field) {
            return field.getValue() == null ? "" : field.getValue().trim();
        }

        private static boolean mark(HasValidation field, String error) {
            field.setInvalid(error != null);
            field.setErrorMessage(error);
            return error == null;
        }
    }
}
