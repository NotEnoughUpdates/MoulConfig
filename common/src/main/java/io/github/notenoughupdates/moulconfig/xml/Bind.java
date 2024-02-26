package io.github.notenoughupdates.moulconfig.xml;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Bind {
    String value() default "";
}
