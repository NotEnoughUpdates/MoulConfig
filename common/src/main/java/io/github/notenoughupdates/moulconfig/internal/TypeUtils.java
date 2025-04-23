package io.github.notenoughupdates.moulconfig.internal;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Arrays;

public class TypeUtils {
    public static boolean areTypesEquals(Class<?> a, Class<?> b) {
        return normalizeNative(a) == normalizeNative(b);
    }

    public static boolean doesAExtendB(Type a, Type b) {
        return normalizeRawAll(b).isAssignableFrom(normalizeRawAll(a));
    }

    public static Class<?> normalizeRawAll(Type t) {
        return normalizeNative(resolveRawType(t));
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

    public static Class<?> resolveRawType(Type t) {
        if (t instanceof Class<?>) return (Class<?>) t;
        if (t instanceof WildcardType) return resolveRawType(((WildcardType) t).getUpperBounds()[0]);
        if (t instanceof ParameterizedType) return resolveRawType(((ParameterizedType) t).getRawType());
        if (t instanceof GenericArrayType) {
            Class<?> component = resolveRawType(((GenericArrayType) t).getGenericComponentType());
            return Array.newInstance(component, 0).getClass();
        }
        throw new IllegalArgumentException("Could not resolve type " + t + " to a raw type");
    }
}
