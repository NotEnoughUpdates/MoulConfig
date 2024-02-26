package io.github.notenoughupdates.moulconfig.internal

import java.lang.reflect.Field

data class BoundField(
    val field: Field,
    val boundTo: Any
) {
    override fun toString(): String {
        return "$field bound to $boundTo"
    }
}
