package io.github.moulberry.moulconfig.xml;

import io.github.moulberry.moulconfig.internal.TypeUtils;
import io.github.moulberry.moulconfig.observer.GetSetter;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.var;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Data
public class XMLBoundProperties {
    final Map<String, Field> namedProperties = new HashMap<>();
    private static MethodHandles.Lookup lookup = MethodHandles.lookup();

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
