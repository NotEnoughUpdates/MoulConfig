package io.github.notenoughupdates.moulconfig.annotations

/**
 * Add additional search tags to your [ConfigOption]. These search tags will not appear anywhere user facing, but
 * the [io.github.notenoughupdates.moulconfig.gui.GuiOptionEditor.fulfillsSearch] function will use them to filter
 * additional elements in the search
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
@Repeatable
annotation class SearchTag(
    val value: String
)