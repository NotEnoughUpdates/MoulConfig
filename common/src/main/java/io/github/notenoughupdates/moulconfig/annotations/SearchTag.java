package io.github.notenoughupdates.moulconfig.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Add additional search tags to your {@link ConfigOption}. These search tags will not appear anywhere user facing, but
 * the {@link io.github.notenoughupdates.moulconfig.gui.GuiOptionEditor#fulfillsSearch} function will use them to filter
 * additional elements in the search.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Repeatable(SearchTag.Container.class)
public @interface SearchTag {
    String value();

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface Container {
        SearchTag[] value();
    }
}
