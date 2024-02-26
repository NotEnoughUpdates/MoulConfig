package io.github.notenoughupdates.moulconfig.common

import io.github.notenoughupdates.moulconfig.internal.MCLogger
import java.io.InputStream
import java.util.*

/**
 * Not for manual implementation. This should be implemented by the corresponding platform.
 * @see IMinecraft.instance
 */
interface IMinecraft {
    fun bindTexture(resourceLocation: MyResourceLocation)
    fun loadResourceLocation(resourceLocation: MyResourceLocation): InputStream
    fun getLogger(label: String): MCLogger

    val mouseX: Int
    val mouseY: Int
    val isDevelopmentEnvironment: Boolean
    val defaultFontRenderer: IFontRenderer
    val keyboardConstants: IKeyboardConstants
    val scaledWidth: Int
    val scaledHeight: Int
    val scaleFactor: Int

    companion object {
        @JvmField
        val instance: IMinecraft = ServiceLoader.load(IMinecraft::class.java).also { it.reload() }.first()
    }
}
