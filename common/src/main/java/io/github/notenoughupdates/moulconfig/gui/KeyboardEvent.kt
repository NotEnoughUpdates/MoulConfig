package io.github.notenoughupdates.moulconfig.gui

sealed interface KeyboardEvent {
    data class CharTyped(val char: Char) : KeyboardEvent
    data class KeyPressed(val keycode: Int, val scancode: Int, val pressed: Boolean) : KeyboardEvent
}
