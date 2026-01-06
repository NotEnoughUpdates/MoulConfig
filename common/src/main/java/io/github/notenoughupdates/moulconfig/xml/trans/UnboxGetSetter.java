package io.github.notenoughupdates.moulconfig.xml.trans;

import io.github.notenoughupdates.moulconfig.internal.TypeUtils;
import io.github.notenoughupdates.moulconfig.observer.GetSetter;
import io.github.notenoughupdates.moulconfig.xml.ParametricTypeMorphism;
import lombok.val;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Optional;

public class UnboxGetSetter implements ParametricTypeMorphism {
    private TypeVariable<?> typeVariable = GetSetter.class.getTypeParameters()[0];

    @Override
    public Optional<Type> codomain(Type domain) {
        val typUniverse = TypeUtils.createTypeUniverse(domain);
        return TypeUtils.resolveSimpleTypeVariableInUniverse(typUniverse, typeVariable);
    }

    @Override
    public GetSetter<?> apply(Type domain, GetSetter<?> value) {
        @SuppressWarnings("unchecked") val cast = (GetSetter<GetSetter<Object>>) value;
        return new GetSetter<Object>() {
            @Override
            public Object get() {
                return cast.get().get();
            }

            @Override
            public void set(Object newValue) {
                cast.get().set(newValue);
            }
        };
    }
}
