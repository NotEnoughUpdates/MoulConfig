package io.github.notenoughupdates.moulconfig.gui

sealed interface MouseEvent {
    data class Click(val mouseButton: Int, val mouseState: Boolean) : MouseEvent
    data class Move(val dx: Float, val dy: Float) : MouseEvent
    data class Scroll(val dWheel: Float) : MouseEvent
}
