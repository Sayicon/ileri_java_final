package com.tbl324.shared.api;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PagedResultTest {

    @Test
    void hasNextWhenMorePagesExist() {
        PagedResult<String> result = PagedResult.of(List.of("a", "b", "c"), 0, 3, 10L);

        assertTrue(result.isHasNext());
        assertFalse(result.isHasPrevious());
    }

    @Test
    void hasPreviousOnSecondPage() {
        PagedResult<String> result = PagedResult.of(List.of("d", "e", "f"), 1, 3, 10L);

        assertTrue(result.isHasPrevious());
        assertTrue(result.isHasNext());
    }

    @Test
    void lastPageHasNoNext() {
        PagedResult<String> result = PagedResult.of(List.of("j"), 3, 3, 10L);

        assertFalse(result.isHasNext());
        assertTrue(result.isHasPrevious());
    }

    @Test
    void totalPagesCalculatedCorrectly() {
        PagedResult<String> result = PagedResult.of(List.of("a"), 0, 3, 10L);
        assertEquals(4, result.getTotalPages());
    }

    @Test
    void exactDivisionTotalPages() {
        PagedResult<String> result = PagedResult.of(List.of("a"), 0, 5, 10L);
        assertEquals(2, result.getTotalPages());
    }

    @Test
    void genericTypeWorksForDifferentTypes() {
        PagedResult<String> strings = PagedResult.of(List.of("x"), 0, 10, 1L);
        PagedResult<Integer> integers = PagedResult.of(List.of(1, 2), 0, 10, 2L);

        assertEquals(String.class, strings.getContent().get(0).getClass());
        assertEquals(Integer.class, integers.getContent().get(0).getClass());
    }
}
