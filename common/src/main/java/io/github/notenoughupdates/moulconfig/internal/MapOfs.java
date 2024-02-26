package io.github.notenoughupdates.moulconfig.internal;

import java.util.HashMap;
import java.util.Map;

public class MapOfs {
    public static <K, V> Map<K, V> of() {
        return new HashMap<>();
    }

    public static <K, V> Map<K, V> of(K k, V v) {
        return new HashMap<K, V>() {{
            put(k, v);
        }};
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2) {
        return new HashMap<K, V>() {{
            put(k1, v1);
            put(k2, v2);
        }};
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3) {
        return new HashMap<K, V>() {{
            put(k1, v1);
            put(k2, v2);
            put(k3, v3);
        }};
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        return new HashMap<K, V>() {{
            put(k1, v1);
            put(k2, v2);
            put(k3, v3);
            put(k4, v4);
        }};
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
        return new HashMap<K, V>() {{
            put(k1, v1);
            put(k2, v2);
            put(k3, v3);
            put(k4, v4);
            put(k5, v5);
        }};
    }
}
