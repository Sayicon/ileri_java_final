package com.tbl324.shared.util;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CollectionOpsTest {

    @Test
    void copyAllMovesElementsFromSrcToDest() {
        List<Number> dest = new ArrayList<>();
        List<Integer> src = List.of(1, 2, 3);

        CollectionOps.copyAll(dest, src);

        assertEquals(3, dest.size());
        assertEquals(1, dest.get(0));
    }

    @Test
    void copyAllWithSubtypeWorks() {
        List<Object> dest = new ArrayList<>();
        List<String> src = List.of("a", "b");

        CollectionOps.copyAll(dest, src);

        assertEquals(2, dest.size());
    }

    @Test
    void findMaxReturnsLargestElement() {
        List<Integer> list = List.of(3, 1, 4, 1, 5, 9, 2, 6);
        Integer max = CollectionOps.findMax(list);
        assertEquals(9, max);
    }

    @Test
    void findMaxWorksWithStrings() {
        List<String> list = List.of("banana", "apple", "cherry");
        String max = CollectionOps.findMax(list);
        assertEquals("cherry", max);
    }
}
