package io.github.notenoughupdates.moulconfig.platform

import io.github.notenoughupdates.moulconfig.common.IKeyboardConstants
import net.minecraft.client.util.InputUtil

object ModernKeyboardConstants : IKeyboardConstants {
    override val backSpace: Int
        get() = InputUtil.GLFW_KEY_BACKSPACE
    override val ctrlLeft: Int
        get() = InputUtil.GLFW_KEY_LEFT_CONTROL
    override val ctrlRight: Int
        get() = InputUtil.GLFW_KEY_RIGHT_CONTROL
    override val shiftLeft: Int
        get() = InputUtil.GLFW_KEY_LEFT_SHIFT
    override val shiftRight: Int
        get() = InputUtil.GLFW_KEY_RIGHT_SHIFT
    override val escape: Int
        get() = InputUtil.GLFW_KEY_ESCAPE
    override val none: Int
        get() = -1
    override val enter: Int
        get() = InputUtil.GLFW_KEY_ENTER
    override val delete: Int
        get() = InputUtil.GLFW_KEY_DELETE
    override val up: Int
        get() = InputUtil.GLFW_KEY_UP
    override val down: Int
        get() = InputUtil.GLFW_KEY_DOWN
    override val right: Int
        get() = InputUtil.GLFW_KEY_RIGHT
    override val left: Int
        get() = InputUtil.GLFW_KEY_LEFT
    override val home: Int
        get() = InputUtil.GLFW_KEY_HOME
    override val end: Int
        get() = InputUtil.GLFW_KEY_END
    override val keyA: Int
        get() = InputUtil.GLFW_KEY_A
    override val keyC: Int
        get() = InputUtil.GLFW_KEY_C
    override val keyX: Int
        get() = InputUtil.GLFW_KEY_X
    override val keyV: Int
        get() = InputUtil.GLFW_KEY_V
    override val keyN: Int
        get() = InputUtil.GLFW_KEY_N
    override val keyF: Int
        get() = InputUtil.GLFW_KEY_F
}
