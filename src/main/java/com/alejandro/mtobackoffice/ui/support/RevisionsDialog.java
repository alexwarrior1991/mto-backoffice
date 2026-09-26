package com.alejandro.mtobackoffice.ui.support;

import com.alejandro.mtobackoffice.client.dto.PageResponse;
import com.alejandro.mtobackoffice.client.dto.RevisionDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.error.NotFoundApiException;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.function.Function;
import java.util.stream.Stream;

/**
 * El historial de una fila (Envers en mto-stock y en mto-maintenance, con la misma forma): las
 * revisiones paginadas, la mas reciente primero, con quien, cuando, que operacion y por que camino
 * ({@code HTTP}, {@code MESSAGING}, {@code SYSTEM} o {@code BASELINE}, la foto inicial de lo que ya
 * existia), la referencia de correlacion y una linea con la fila tal como quedo. Sin revisiones el
 * servicio responde 404, y eso es «sin historial todavia», no un error.
 *
 * @param <D> la entidad tal como la devuelve el servicio en cada revision
 */
public class RevisionsDialog<D> extends Dialog {

    /** Una pagina del historial: {@code GET /{recurso}/{id}/revisions?page&size}. */
    @FunctionalInterface
    public interface Source<D> {
        PageResponse<RevisionDto<D>> page(int page, int size);
    }

    public static final int PAGE_SIZE = 20;

    private final Source<D> source;
    private final Span count = new Span();
    private final Span empty = new Span("Sin historial todavia: el servicio no guarda ninguna revision de esta fila.");
    private final Grid<RevisionDto<D>> grid = new Grid<>();
    private boolean none;

    /**
     * @param label    como se llama la fila, para el titulo
     * @param source   de donde salen las paginas
     * @param describe una linea con la entidad tal como quedo en cada revision
     */
    public RevisionsDialog(String label, Source<D> source, Function<D, String> describe) {
        this.source = source;
        setHeaderTitle("Historial de " + label);
        setWidth("min(76rem, 96vw)");
        setHeight("min(40rem, 90vh)");
        count.setId("revisions-count");
        count.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);
        empty.setId("revisions-empty");
        empty.setVisible(false);

        grid.setId("revisions-grid");
        grid.addColumn(row -> row.revision().revision()).setHeader("Revision").setKey("revision").setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(row -> Formats.dateTime(row.revision().revisionAt())).setHeader("Cuando").setKey("revisionAt").setAutoWidth(true);
        grid.addColumn(row -> row.revision().operation() == null ? "" : row.revision().operation().label()).setHeader("Operacion").setKey("operation").setAutoWidth(true);
        grid.addColumn(row -> text(row.revision().author())).setHeader("Quien").setKey("author").setAutoWidth(true);
        grid.addColumn(row -> text(row.revision().source())).setHeader("Origen").setKey("source").setAutoWidth(true);
        grid.addColumn(row -> row.entity() == null ? "" : describe.apply(row.entity())).setHeader("Como quedo").setKey("entity").setFlexGrow(1);
        grid.addColumn(row -> text(row.revision().correlationId())).setHeader("Correlacion").setKey("correlationId").setAutoWidth(true);
        grid.setPageSize(PAGE_SIZE);
        grid.setSizeFull();
        grid.setItems(this::fetch, this::count);

        VerticalLayout layout = new VerticalLayout(count, empty, grid);
        layout.setSizeFull();
        layout.setPadding(false);
        layout.expand(grid);
        add(layout);
        getFooter().add(new Button("Cerrar", click -> close()));
    }

    private Stream<RevisionDto<D>> fetch(Query<RevisionDto<D>, Void> query) {
        int size = Math.max(1, query.getLimit());
        PageResponse<RevisionDto<D>> page = load(query.getOffset() / size, size);
        return page == null ? Stream.empty() : page.content().stream();
    }

    private int count(Query<RevisionDto<D>, Void> query) {
        PageResponse<RevisionDto<D>> page = load(0, 1);
        if (page == null) {
            return 0;
        }
        long total = page.page() == null ? page.content().size() : page.page().totalElements();
        count.setText(total + " revisiones, la mas reciente primero");
        return (int) Math.min(Integer.MAX_VALUE, total);
    }

    /** Una pagina, o {@code null} si no la hay: un 404 es «sin historial» y se dice una vez. */
    private PageResponse<RevisionDto<D>> load(int page, int size) {
        if (none) {
            return null;
        }
        try {
            return source.page(page, size);
        } catch (NotFoundApiException noRevisions) {
            none = true;
            empty.setVisible(true);
            count.setVisible(false);
            grid.setVisible(false);
            return null;
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
            return null;
        }
    }

    private static String text(String value) {
        return value == null ? "" : value;
    }
}
