package io.github.notenoughupdates.moulconfig.xml;

import io.github.notenoughupdates.moulconfig.internal.TypeUtils;
import io.github.notenoughupdates.moulconfig.observer.GetSetter;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.var;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Data
public class XMLBoundProperties {
    final Map<String, Field> namedProperties = new HashMap<>();
    final Map<String, Method> namedFunctions = new HashMap<>();
    private static MethodHandles.Lookup lookup = MethodHandles.lookup();

    @SneakyThrows
    public <T> Consumer<T> getBoundFunction(String name, Object object, Class<T> tClass) {
        Method method = namedFunctions.get(name);
        if (method.getReturnType() != void.class) {
            throw new IllegalArgumentException("Return type of bound method " + name + " must be void on object " + object);
        }
        if (method.getParameterCount() != 1) {
            throw new IllegalArgumentException("Bound method " + name + " should only take one argument on object " + object);
        }
        if (!TypeUtils.doesAExtendB(tClass, method.getParameterTypes()[0])) {
            throw new IllegalArgumentException("Bound method " + name + " should take one argument of type " + tClass + " instead of " + method.getParameterTypes()[0]);
        }
        MethodHandle methodHandle = lookup.unreflect(method).bindTo(object);
        return new Consumer<T>() {
            @SneakyThrows
            @Override
            public void accept(T t) {
                methodHandle.invoke(t);
            }
        };
    }

    @SneakyThrows
    public Runnable getBoundFunction(String name, Object object) {
        Method method = namedFunctions.get(name);
        if (method.getReturnType() != void.class) {
            throw new IllegalArgumentException("Return type of bound method " + name + " must be void on object " + object);
        }
        if (method.getParameterCount() != 0) {
            throw new IllegalArgumentException("Bound method " + name + " should only take one argument on object " + object);
        }
        MethodHandle methodHandle = lookup.unreflect(method).bindTo(object);
        return new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                methodHandle.invoke();
            }
        };
    }

    @SneakyThrows
    public <T> GetSetter<T> getBoundProperty(String name, Class<T> clazz, Object object) {
        if (name.equals("this")) {
            if (!TypeUtils.doesAExtendB(object.getClass(), clazz)) {
                throw new IllegalArgumentException("Bind target " + name + " is of the wrong type " + object.getClass() + " instead of " + clazz);
            }
            return new GetSetter<T>() {
                @Override
                public T get() {
                    return (T) object;
                }

                @Override
                public void set(T newValue) {
                    throw new UnsupportedOperationException();
                }
            };
        }
        var field = namedProperties.get(name);
        if (field == null) {
            Method method = namedFunctions.get(name);
            if (method == null)
                throw new NullPointerException("Could not find bind target for " + name + " in " + clazz);
            if (!TypeUtils.doesAExtendB(method.getReturnType(), clazz))
                throw new IllegalArgumentException("Bind target " + method + " is of the wrong type " + method.getReturnType() + " instead of " + clazz);
            if (method.getParameterCount() != 0)
                throw new RuntimeException("Bind target " + method + " is not a pure getter");
            var unreflect = bindSometimes(lookup.unreflect(method), method.getModifiers(), object);
            return new GetSetter<T>() {
                @SneakyThrows
                @Override
                public T get() {
                    return (T) unreflect.invoke();
                }

                @Override
                public void set(T newValue) {
                    throw new UnsupportedOperationException();
                }
            };
        }
        if (!TypeUtils.doesAExtendB(field.getType(), clazz))
            throw new IllegalArgumentException("Bind target " + name + " is of the wrong type " + field.getType() + " instead of " + clazz);
        field.setAccessible(true);
        var getter = bindSometimes(lookup.unreflectGetter(field), field.getModifiers(), object);
        var setter = bindSometimes(lookup.unreflectSetter(field), field.getModifiers(), object);
        return new GetSetter<T>() {

            @SneakyThrows
            @Override
            public T get() {
                return (T) getter.invoke();
            }

            @SneakyThrows
            @Override
            public void set(T newValue) {
                setter.invoke(newValue);
            }
        };
    }

    private static MethodHandle bindSometimes(MethodHandle methodHandle, int modifiers, Object object) {
        if (Modifier.isStatic(modifiers))
            return methodHandle;
        return methodHandle.bindTo(object);
    }
}
