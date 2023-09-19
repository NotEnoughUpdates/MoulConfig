package io.github.moulberry.moulconfig.xml;

import io.github.moulberry.moulconfig.internal.TypeUtils;
import io.github.moulberry.moulconfig.observer.GetSetter;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.var;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
        if (!TypeUtils.areTypesEquals(method.getParameterTypes()[0], tClass)) {
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

    public <T> GetSetter<T> getBoundProperty(String name, Class<T> clazz, Object object) {
        if (name.equals("this")) {
            if (!TypeUtils.areTypesEquals(clazz, object.getClass())) {
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
        if (field == null) throw new NullPointerException("Could not find bind target for " + name + " in " + clazz);
        if (!TypeUtils.areTypesEquals(field.getType(), clazz))
            throw new IllegalArgumentException("Bind target " + name + " is of the wrong type " + field.getType() + " instead of " + clazz);
        field.setAccessible(true);
        try {
            var getter = lookup.unreflectGetter(field).bindTo(object);
            var setter = lookup.unreflectSetter(field).bindTo(object);
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
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
