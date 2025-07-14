package com.petwal.pwweb.util;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Check {

    private Check() {
    }

    public static <T> T notNull(final String name, final T object) {
        return Objects.requireNonNull(object, "%s cannot be null".formatted(name));
    }

    public static <T> List<T> orEmpty(final List<T> list) {
        return list != null ? list : List.of();
    }

    public static <K, V> Map<K, V> orEmpty(final Map<K, V> map) {
        return map != null ? map : Map.of();
    }

    public static <T> Set<T> orEmpty(final Set<T> set) {
        return set != null ? set : Set.of();
    }

}
