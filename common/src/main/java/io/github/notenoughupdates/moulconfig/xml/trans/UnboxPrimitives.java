package io.github.notenoughupdates.moulconfig.xml.trans;

import io.github.notenoughupdates.moulconfig.internal.TypeUtils;
import io.github.notenoughupdates.moulconfig.observer.GetSetter;
import io.github.notenoughupdates.moulconfig.xml.ParametricTypeMorphism;
import lombok.val;

import java.lang.reflect.Type;
import java.util.Optional;

public class UnboxPrimitives implements ParametricTypeMorphism {
    @Override
    public Optional<Type> codomain(Type domain) {
        val t = TypeUtils.resolveRawType(domain);
        val n = TypeUtils.normalizeToNative(t);
        if (n != t) return Optional.of(n);
        return Optional.empty();
    }

    @Override
    public GetSetter<?> apply(Type domain, GetSetter<?> value) {
        return value; // valid because the generic interface needs to box anyway. this entire class is just type system masturbation.
    }
}
