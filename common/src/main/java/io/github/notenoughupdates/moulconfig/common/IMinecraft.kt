package io.github.notenoughupdates.moulconfig.common

import io.github.notenoughupdates.moulconfig.gui.GuiComponent
import io.github.notenoughupdates.moulconfig.gui.GuiContext
import io.github.notenoughupdates.moulconfig.gui.GuiElement
import io.github.notenoughupdates.moulconfig.internal.MCLogger
import io.github.notenoughupdates.moulconfig.processor.MoulConfigProcessor
import org.jetbrains.annotations.ApiStatus
import java.io.InputStream
import java.util.*

/**
 * Not for manual implementation. This should be implemented by the corresponding platform.
 * @see IMinecraft.instance
 */
@ApiStatus.NonExtendable
interface IMinecraft {
    @Deprecated("Prefer using .bindTexture from RenderContext")
    fun bindTexture(resourceLocation: MyResourceLocation)
    fun loadResourceLocation(resourceLocation: MyResourceLocation): InputStream
    fun getLogger(label: String): MCLogger

    val mouseX: Int
    val mouseY: Int
    val mouseXHF: Double
    val mouseYHF: Double
    val isDevelopmentEnvironment: Boolean
    val defaultFontRenderer: IFontRenderer
    val keyboardConstants: IKeyboardConstants
    val scaledWidth: Int
    val scaledHeight: Int
    val scaleFactor: Int
    fun isMouseButtonDown(mouseButton: Int): Boolean
    fun isKeyboardKeyDown(keyboardKey: Int): Boolean

    fun addExtraBuiltinConfigProcessors(processor: MoulConfigProcessor<*>)

    fun sendClickableChatMessage(message: String, action: String, type: ClickType)

    fun getKeyName(keyCode: Int): String

    /**
     * This is a method to provide a render context. Note that constructing this context directly will potentially give
     * you an incorrect render state, leading to visual glitches. Depending on your platform, this might also require
     * additional platform specific cleanup / post rendering work to be done. Use only if you know exactly that none
     * of your rendering requires this extra functionality.
     */
    @Deprecated("This context will be at the top level, not providing any of the useful translations and scalings that might be needed to render properly. Use with care.")
    fun provideTopLevelRenderContext(): RenderContext
    fun openWrappedScreen(gui: GuiElement)
    fun openWrappedScreen(gui: GuiContext)
    fun openWrappedScreen(gui: GuiComponent) {
        openWrappedScreen(GuiContext(gui))
    }

    fun copyToClipboard(string: String)
    fun copyFromClipboard(): String


    companion object {
        @JvmField
        val instance: IMinecraft = ServiceLoader.load(IMinecraft::class.java).also { it.reload() }.first()
    }
}
