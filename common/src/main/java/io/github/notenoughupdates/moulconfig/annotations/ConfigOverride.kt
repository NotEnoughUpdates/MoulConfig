package io.github.notenoughupdates.moulconfig.annotations

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class ConfigOverride(
    /**
     * Controls placement of this field relative to its siblings, inheriting the overridden parent
     * field's [ConfigOrder] value by default. Set explicitly to override that behaviour.
     *
     * Uses [Int.MIN_VALUE] as a sentinel to indicate inheritance — do not use that value directly.
     */
    val overrideOrder: Int = Int.MIN_VALUE
)
