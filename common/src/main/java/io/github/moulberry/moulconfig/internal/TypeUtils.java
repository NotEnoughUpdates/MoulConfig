package io.github.moulberry.moulconfig.internal;

public class TypeUtils {
    public static boolean areTypesEquals(Class<?> a, Class<?> b) {
        return normalizeNative(a) == normalizeNative(b);
    }
    public static boolean doesAExtendB(Class<?> a, Class<?> b) {
        return normalizeNative(b).isAssignableFrom(normalizeNative(a));
    }

    public static Class<?> normalizeNative(Class<?> clazz) {
        if (clazz == int.class) return Integer.class;
        if (clazz == float.class) return Float.class;
        if (clazz == double.class) return Double.class;
        if (clazz == boolean.class) return Boolean.class;
        if (clazz == long.class) return Long.class;
        if (clazz == short.class) return Short.class;
        if (clazz == char.class) return Character.class;
        return clazz;
    }
}
