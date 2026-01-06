package io.github.notenoughupdates.moulconfig.internal;

import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.*;
import java.util.*;

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

    public static Class<?> normalizeToNative(Class<?> clazz) {
        if (clazz == Integer.class) return int.class;
        if (clazz == Float.class) return float.class;
        if (clazz == Double.class) return double.class;
        if (clazz == Boolean.class) return boolean.class;
        if (clazz == Long.class) return long.class;
        if (clazz == Short.class) return short.class;
        if (clazz == Character.class) return char.class;
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

    /**
     * obtain a list of type variable assignments from a generic type instantiation.
     *
     * @see #fillTypeUniverse for the concrete meaning of this
     */
    public static Map<TypeVariable<?>, Type> createTypeUniverse(Type instantiation) {
        val universe = new HashMap<TypeVariable<?>, Type>();
        fillTypeUniverse(instantiation, resolveRawType(instantiation), universe);
        return universe;
    }

    /**
     * Construct a universe of type variable assignments from a given type instantiation. Nota bene: this only returns individual assignments, which need to be resolved to concrete types using {@link #resolveSimpleTypeVariableInUniverse}.
     *
     * @param instantiation a generic type instantiation that is ideally fully specified
     * @param context       the raw type of the {@code instantiation}, is used to know which generic type is being instantiated by the parameterization
     * @param universe      an out param (i know -- bad form -- however this makes it easier to collect all the type assignments across multiple inheritance paths)
     */
    public static void fillTypeUniverse(
        Type instantiation,
        Class<?> context,
        Map<TypeVariable<?>, Type> universe) {
        if (instantiation == null) return;
        if (instantiation instanceof WildcardType) {
            // This is technically incorrect.
            fillTypeUniverse(((WildcardType) instantiation).getUpperBounds()[0], context, universe);
            return;
        }
        if (instantiation instanceof ParameterizedType) {
            val par = ((ParameterizedType) instantiation);
            val con = par.getActualTypeArguments();
            final TypeVariable<?>[] abs = context.getTypeParameters();
            for (int i = 0; i < abs.length; i++) {
                universe.put(abs[i], con[i]);
            }
            fillTypeUniverse(par.getRawType(), context, universe);
            return;
        }
        if (instantiation instanceof GenericArrayType) {
            throw new IllegalArgumentException("Encountered array type while walking the type hierarchy " + instantiation);
        }
        if (instantiation instanceof Class<?>) {
            val cls = ((Class<?>) instantiation);
            val gInters = cls.getGenericInterfaces();
            val inters = cls.getInterfaces();
            for (int i = gInters.length; i-- > 0; ) {
                fillTypeUniverse(gInters[i], inters[i], universe);
            }
            fillTypeUniverse(cls.getGenericSuperclass(), cls.getSuperclass(), universe);
            return;
        }
        throw new IllegalArgumentException("Encountered unknown type kind " + instantiation + " while walking a generic hierarchy");
    }

    public static ParameterizedType instantiateParameterizedType(
        Class<?> rawType,
        Type[] typeArguments
    ) {
        if (rawType.getTypeParameters().length != typeArguments.length)
            Warnings.warn("Invalid instantiation of parameterized type " + rawType);
        return new ParameterizedType() {
            @Override
            public String getTypeName() {
                return getRawType().getTypeName();
            }

            @Override
            public @NotNull Type @NotNull [] getActualTypeArguments() {
                return typeArguments;
            }

            @Override
            public @NotNull Type getRawType() {
                return rawType;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }
        };
    }

    /**
     * Given a universe of assignments for type-variables, resolve type-variables to a concrete non-generic type (at the top level). This will not implicitly resolve constructs like {@code U = List<T>, T = String, U?}, instead keeping resolving {@code U? = List<T>}.
     */
    public static Optional<Type> resolveSimpleTypeVariableInUniverse(Map<TypeVariable<?>, Type> universe, TypeVariable<?> query) {
        Type p = query;
        while (p instanceof TypeVariable<?>) {
            p = universe.get(query);
        }
        return Optional.ofNullable(p); // .get will return null if one type variable along the chain is left unassigned
    }

}
