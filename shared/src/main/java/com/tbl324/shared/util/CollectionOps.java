package com.tbl324.shared.util;

import java.util.List;

public class CollectionOps {

    // ? super T: dest en az T'yi kabul etmeli (T veya üstü)
    // ? extends T: src en fazla T verebilir (T veya altı)
    public static <T> void copyAll(List<? super T> dest, List<? extends T> src) {
        dest.addAll(src);
    }

    // ? extends T: listenin T veya alt tipi içerdiğini garanti eder
    // Comparable<? super T>: T'nin üst tipinde karşılaştırma yeterli
    public static <T extends Comparable<? super T>> T findMax(List<? extends T> list) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("liste boş olamaz");
        }
        T max = list.get(0);
        for (T item : list) {
            if (item.compareTo(max) > 0) {
                max = item;
            }
        }
        return max;
    }
}
