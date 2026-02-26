package io.github.notenoughupdates.moulconfig.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigOptionOrder {
    /**
     * The order of this option within its category.
     * Lower values appear first. Options without this annotation
     * default to 0, and appear in declaration order.
     */
    int value() default 0;
}
