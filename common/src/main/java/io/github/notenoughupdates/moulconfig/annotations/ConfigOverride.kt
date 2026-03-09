package io.github.notenoughupdates.moulconfig.annotations

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class ConfigOverride(
    /**
     * Marks this field as intentionally overriding a parent class field of the same name.
     * Optionally specify an order to control placement relative to siblings; lower values appear first.
     */
    val order: Int = 0
)
