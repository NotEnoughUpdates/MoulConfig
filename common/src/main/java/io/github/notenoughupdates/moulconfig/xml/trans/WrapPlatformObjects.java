package io.github.notenoughupdates.moulconfig.xml.trans;

import io.github.notenoughupdates.moulconfig.observer.GetSetter;
import io.github.notenoughupdates.moulconfig.xml.ParametricTypeMorphism;

import java.lang.reflect.Type;
import java.util.Optional;

public class WrapPlatformObjects implements ParametricTypeMorphism {
    @Override
    public Optional<Type> codomain(Type domain) {
        return Optional.empty();
    }

    @Override
    public GetSetter<?> apply(Type domain, GetSetter<?> value) {
        return null;
    }
}
