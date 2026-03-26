package io.github.notenoughupdates.moulconfig.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigOverride {
    /**
     * Controls placement of this field relative to its siblings, inheriting the overridden parent
     * field's {@link ConfigOrder} value by default. Set explicitly to override that behavior.
     * Uses {@link Integer#MIN_VALUE} as a sentinel to indicate inheritance. Do not use that value directly.
     */
    int overrideOrder() default Integer.MIN_VALUE;
}
