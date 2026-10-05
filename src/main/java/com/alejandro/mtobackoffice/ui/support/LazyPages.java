package com.alejandro.mtobackoffice.ui.support;

import com.alejandro.mtobackoffice.client.dto.PageResponse;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.function.SerializableConsumer;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Stream;

/**
 * Una lista paginada en el servidor con una sola peticion por pagina, como en mto-frontend.
 *
 * <p>Vaadin pide por separado el recuento y cada pagina ({@code setItems(fetch, count)}), pero los
 * servicios devuelven la pagina junto con su total. Pedir el recuento aparte era pedir dos veces la
 * primera pagina (tres si llegaba corta, porque entonces Vaadin vuelve a contar), y un fallo de la
 * pagina se notificaba dos veces. Aqui el recuento pide la primera pagina con el tamano y el orden
 * del Grid (la consulta del recuento no trae el orden: se lee del {@code DataCommunicator}) y la
 * guarda para la peticion de pagina que la sigue; un recuento repetido usa el total que ya llego, y
 * un fallo se notifica una vez y deja la lista vacia hasta la siguiente vez que se pida.</p>
 *
 * <p>Lo guardado dura solo esa ida y vuelta ({@code beforeClientResponse}) o hasta que se recarga la
 * lista: nunca se pinta una pagina vieja.</p>
 *
 * @param <T> la fila de la lista
 */
public final class LazyPages<T> implements Serializable {

    /** Las filas pedidas y el total de la lista. */
    public record Page<T>(List<T> rows, long total) implements Serializable {

        /** La forma {@code {content, page}} de los servicios; sin {@code page}, el total es lo que llego. */
        public static <T> Page<T> of(PageResponse<T> response) {
            long total = response.page() == null ? response.content().size() : response.page().totalElements();
            return new Page<>(response.content(), total);
        }

        public static <T> Page<T> empty() {
            return new Page<>(List.of(), 0);
        }
    }

    /** Las filas desde {@code offset}, como mucho {@code limit}, en ese orden: una peticion al servicio. */
    @FunctionalInterface
    public interface Source<T> extends Serializable {
        Page<T> load(int offset, int limit, List<QuerySortOrder> sort);
    }

    private final Grid<T> grid;
    private final Source<T> source;
    private final SerializableConsumer<Long> onTotal;
    private Kept<T> kept;
    private Long total;
    private boolean failed;
    private boolean forgetting;

    private LazyPages(Grid<T> grid, Source<T> source, SerializableConsumer<Long> onTotal) {
        this.grid = grid;
        this.source = source;
        this.onTotal = onTotal;
    }

    /**
     * Pone en el Grid la lista de {@code source}. {@code onTotal} recibe el total de cada respuesta
     * (y 0 si falla) para pintar el recuento; con el Grid ya configurado (su tamano de pagina).
     */
    public static <T> LazyPages<T> of(Grid<T> grid, Source<T> source, SerializableConsumer<Long> onTotal) {
        LazyPages<T> pages = new LazyPages<>(grid, source, onTotal);
        grid.setItems(pages::fetch, pages::count);
        grid.getDataProvider().addDataProviderListener(change -> pages.forget());
        return pages;
    }

    /** Lo mismo, sin recuento que pintar. */
    public static <T> LazyPages<T> of(Grid<T> grid, Source<T> source) {
        return of(grid, source, total -> { });
    }

    /** Vuelve a pedir la lista desde el principio, con lo que tenga ahora la pantalla. */
    public void refresh() {
        grid.getDataProvider().refreshAll();
    }

    private int count(Query<T, Void> query) {
        if (failed) {
            return 0;
        }
        if (total != null) {
            return clamp(total);
        }
        int size = grid.getPageSize();
        List<QuerySortOrder> sort = List.copyOf(grid.getDataCommunicator().getBackEndSorting());
        Page<T> page = request(0, size, sort);
        if (page == null) {
            return 0;
        }
        kept = new Kept<>(0, size, sort, page.rows());
        return clamp(page.total());
    }

    private Stream<T> fetch(Query<T, Void> query) {
        if (failed) {
            return Stream.empty();
        }
        int offset = query.getOffset();
        int limit = query.getLimit();
        List<QuerySortOrder> sort = query.getSortOrders();
        if (kept != null && kept.covers(offset, limit, sort)) {
            return kept.slice(offset, limit).stream();
        }
        Page<T> page = request(offset, limit, sort);
        return page == null ? Stream.empty() : page.rows().stream();
    }

    private Page<T> request(int offset, int limit, List<QuerySortOrder> sort) {
        forgetAtTheEndOfTheRoundTrip();
        try {
            Page<T> page = source.load(offset, limit, sort);
            total = page.total();
            onTotal.accept(page.total());
            return page;
        } catch (BackofficeApiException failure) {
            failed = true;
            onTotal.accept(0L);
            UiErrors.show(failure);
            return null;
        }
    }

    private void forgetAtTheEndOfTheRoundTrip() {
        if (!forgetting) {
            grid.getUI().ifPresent(ui -> {
                forgetting = true;
                ui.beforeClientResponse(grid, context -> forget());
            });
        }
    }

    private void forget() {
        kept = null;
        total = null;
        failed = false;
        forgetting = false;
    }

    private static int clamp(long total) {
        return (int) Math.min(Integer.MAX_VALUE, total);
    }

    /** La pagina que pidio el recuento, para la peticion de pagina que la sigue. */
    private record Kept<T>(int offset, int limit, List<QuerySortOrder> sort, List<T> rows) implements Serializable {

        boolean covers(int from, int length, List<QuerySortOrder> order) {
            return from >= offset && from + length <= offset + limit && sameSort(order);
        }

        List<T> slice(int from, int length) {
            int start = Math.min(rows.size(), from - offset);
            int end = Math.min(rows.size(), start + length);
            return rows.subList(start, end);
        }

        private boolean sameSort(List<QuerySortOrder> order) {
            if (order.size() != sort.size()) {
                return false;
            }
            for (int i = 0; i < order.size(); i++) {
                if (!order.get(i).getSorted().equals(sort.get(i).getSorted())
                        || order.get(i).getDirection() != sort.get(i).getDirection()) {
                    return false;
                }
            }
            return true;
        }
    }
}
