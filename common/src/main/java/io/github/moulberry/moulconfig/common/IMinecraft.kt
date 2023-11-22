package io.github.moulberry.moulconfig.common

import java.io.InputStream
import java.util.*

/**
 * Not for manual implementation. This should be implemented by the corresponding platform.
 * @see IMinecraft.instance
 */
interface IMinecraft {
    fun bindTexture(resourceLocation: MyResourceLocation)
    fun loadResourceLocation(resourceLocation: MyResourceLocation): InputStream
    val isDevelopmentEnvironment: Boolean
    val defaultFontRenderer: IFontRenderer
    val keyboardConstants: IKeyboardConstants

    companion object {
        @JvmField
        val instance: IMinecraft = ServiceLoader.load(IMinecraft::class.java).also { it.reload() }.first()
    }
}
