package io.github.notenoughupdates.moulconfig.annotations

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class ConfigOrder(
    /**
     * The order of this option within its category.
     * Lower values appear first. Options without this annotation
     * default to 0, and appear in declaration order.
     */
    val value: Int = 0
)
