package com.tbl324.shared.api;

import lombok.Getter;

import java.util.List;

@Getter
public class PagedResult<T> {

    private final List<T> content;
    private final int page;
    private final int size;
    private final long total;
    private final int totalPages;
    private final boolean hasNext;
    private final boolean hasPrevious;

    private PagedResult(List<T> content, int page, int size, long total) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.total = total;
        this.totalPages = (int) Math.ceil((double) total / size);
        this.hasNext = page < totalPages - 1;
        this.hasPrevious = page > 0;
    }

    public static <T> PagedResult<T> of(List<T> content, int page, int size, long total) {
        return new PagedResult<>(content, page, size, total);
    }
}
