package io.github.notenoughupdates.moulconfig.common

import io.github.notenoughupdates.moulconfig.internal.MCLogger

/**
 * Minimal [IMinecraft] implementation for use in unit tests.
 * Only implements what is required for [io.github.notenoughupdates.moulconfig.internal.Warnings] to initialise.
 * All other methods throw [UnsupportedOperationException].
 */
class MockIMinecraft : IMinecraft {

    override fun isDevelopmentEnvironment() = false

    override fun getLogger(label: String) = object : MCLogger {
        override fun warn(text: String) = Unit
        override fun info(text: String) = Unit
        override fun error(text: String, throwable: Throwable) = Unit
    }

    override fun loadResourceLocation(resourceLocation: MyResourceLocation) = TODO()
    override fun isGeneratedSentinel(resourceLocation: MyResourceLocation) = TODO()
    override fun generateDynamicTexture(image: java.awt.image.BufferedImage) = TODO()
    override fun getMousePositionHF() = TODO()
    override fun getDefaultFontRenderer() = TODO()
    override fun getKeyboardConstants() = TODO()
    override fun getScaledWidth() = TODO()
    override fun getScaledHeight() = TODO()
    override fun getScaleFactor() = TODO()
    override fun isOnMacOs() = TODO()
    override fun isMouseButtonDown(mouseButton: Int) = TODO()
    override fun isKeyboardKeyDown(keyCode: Int) = TODO()
    override fun addExtraBuiltinConfigProcessors(processor: io.github.notenoughupdates.moulconfig.processor.MoulConfigProcessor<*>) = TODO()
    override fun sendClickableChatMessage(message: io.github.notenoughupdates.moulconfig.common.text.StructuredText, action: String, clickType: ClickType?) = TODO()
    override fun getKeyName(keyCode: Int) = TODO()
    override fun createLiteral(text: String) = TODO()
    override fun createTranslatable(key: String, vararg args: io.github.notenoughupdates.moulconfig.common.text.StructuredText) = TODO()
    override fun createStructuredTextInternal(`object`: Any) = TODO()
    override fun registerPlatformTypeMorphisms(universe: io.github.notenoughupdates.moulconfig.xml.XMLUniverse) = TODO()
    @Deprecated("See parent deprecation")
    override fun provideTopLevelRenderContext() = TODO()
    override fun openWrappedScreen(guiContext: io.github.notenoughupdates.moulconfig.gui.GuiContext) = TODO()
    override fun copyToClipboard(string: String) = TODO()
    override fun copyFromClipboard() = TODO()
}
