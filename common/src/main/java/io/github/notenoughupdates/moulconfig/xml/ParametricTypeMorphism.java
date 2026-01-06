package io.github.notenoughupdates.moulconfig.xml;

import io.github.notenoughupdates.moulconfig.observer.GetSetter;
import org.jetbrains.annotations.ApiStatus;

import java.lang.reflect.Type;
import java.util.Optional;

/**
 * Generic interface for transforming {@link GetSetter GetSetters} into GetSetters containing another type.
 */
@ApiStatus.Experimental
public interface ParametricTypeMorphism {
    /**
     * For a given type, return what type this morphism transform each object into.
     */
    Optional<Type> codomain(Type domain);

    /**
     * Transform an object from one type to another, wrapped into a {@link GetSetter}
     * @param domain the type contained in the {@link GetSetter}
     */
    GetSetter<?> apply(Type domain, GetSetter<?> value);
}
