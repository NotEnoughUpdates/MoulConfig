package io.github.notenoughupdates.moulconfig.internal

import io.github.notenoughupdates.moulconfig.common.IKeyboardConstants
import org.lwjgl.input.Keyboard

object ForgeKeyboardConstants : IKeyboardConstants {
    override val backSpace: Int
        get() = Keyboard.KEY_BACK
    override val ctrlLeft: Int
        get() = Keyboard.KEY_LCONTROL
    override val ctrlRight: Int
        get() = Keyboard.KEY_RCONTROL
    override val shiftLeft: Int
        get() = Keyboard.KEY_LSHIFT
    override val shiftRight: Int
        get() = Keyboard.KEY_RSHIFT
    override val escape: Int
        get() = Keyboard.KEY_ESCAPE

    override val enter: Int
        get() = Keyboard.KEY_RETURN
    override val delete: Int
        get() = Keyboard.KEY_DELETE
    override val up: Int
        get() = Keyboard.KEY_UP
    override val down: Int
        get() = Keyboard.KEY_DOWN
    override val right: Int
        get() = Keyboard.KEY_RIGHT
    override val left: Int
        get() = Keyboard.KEY_LEFT
    override val home: Int
        get() = Keyboard.KEY_HOME
    override val end: Int
        get() = Keyboard.KEY_END
    override val keyA: Int
        get() = Keyboard.KEY_A
    override val keyC: Int
        get() = Keyboard.KEY_C
    override val keyX: Int
        get() = Keyboard.KEY_X
    override val keyV: Int
        get() = Keyboard.KEY_V
    override val keyN: Int
        get() = Keyboard.KEY_N
    override val keyF: Int
        get() = Keyboard.KEY_F
}