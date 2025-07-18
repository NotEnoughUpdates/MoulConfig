package io.github.notenoughupdates.moulconfig.platform

import io.github.notenoughupdates.moulconfig.common.ClickType
import io.github.notenoughupdates.moulconfig.common.IFontRenderer
import io.github.notenoughupdates.moulconfig.common.IKeyboardConstants
import io.github.notenoughupdates.moulconfig.common.IMinecraft
import io.github.notenoughupdates.moulconfig.common.MyResourceLocation
import io.github.notenoughupdates.moulconfig.common.RenderContext
import io.github.notenoughupdates.moulconfig.gui.GuiComponentWrapper
import io.github.notenoughupdates.moulconfig.gui.GuiContext
import io.github.notenoughupdates.moulconfig.gui.GuiElement
import io.github.notenoughupdates.moulconfig.gui.GuiElementWrapper
import io.github.notenoughupdates.moulconfig.internal.MCLogger
import io.github.notenoughupdates.moulconfig.processor.MoulConfigProcessor
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.InputUtil
import net.minecraft.text.ClickEvent
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import org.apache.logging.log4j.LogManager
import org.lwjgl.glfw.GLFW
import java.io.InputStream
import java.net.URI


class MoulConfigPlatform : IMinecraft {
    init {
        instance = this
    }

    companion object {
        init {
            IMinecraft.instance
        }

        lateinit var instance: MoulConfigPlatform
            private set
        var boundTexture: Identifier? = null
        fun fromIdentifier(identifier: Identifier): MyResourceLocation {
            return MyResourceLocation(identifier.namespace, identifier.path)
        }

        fun fromMyResourceLocation(resourceLocation: MyResourceLocation): Identifier {
            return Identifier.of(resourceLocation.root, resourceLocation.path)
        }
    }

    override val isDevelopmentEnvironment: Boolean
        get() = FabricLoader.getInstance().isDevelopmentEnvironment

    override fun bindTexture(resourceLocation: MyResourceLocation) {
        boundTexture = fromMyResourceLocation(resourceLocation)
    }

    override fun getLogger(label: String): MCLogger {
        val logger = LogManager.getLogger(label)
        return object : MCLogger {
            override fun warn(text: String) {
                logger.warn(text)
            }

            override fun info(text: String) {
                logger.info(text)
            }

            override fun error(text: String, throwable: Throwable) {
                logger.error(text, throwable)
            }
        }
    }

    val window get() = MinecraftClient.getInstance().window

    override val mouseX: Int
        get() = mouseXHF.toInt()
    override val mouseY: Int
        get() = mouseYHF.toInt()

    override val mouseXHF: Double
        get() {
            val mouse = MinecraftClient.getInstance().mouse
            val window = MinecraftClient.getInstance().window
            val x = (mouse.x * window.scaledWidth.toDouble() / window.width.toDouble())
            return x
        }
    override val mouseYHF: Double
        get() {
            val mouse = MinecraftClient.getInstance().mouse
            val window = MinecraftClient.getInstance().window
            val y = (mouse.y * window.scaledHeight.toDouble() / window.height.toDouble())
            return y
        }

    override fun loadResourceLocation(resourceLocation: MyResourceLocation): InputStream {
        return MinecraftClient.getInstance().resourceManager.getResource(fromMyResourceLocation(resourceLocation))
            .get().inputStream
    }

    override val defaultFontRenderer: IFontRenderer
        get() = ModernFontRenderer(MinecraftClient.getInstance().textRenderer)
    override val keyboardConstants: IKeyboardConstants
        get() = ModernKeyboardConstants
    override val scaledWidth: Int
        get() {
            val window = MinecraftClient.getInstance().window
            return window.scaledWidth
        }
    override val scaledHeight: Int
        get() {
            val window = MinecraftClient.getInstance().window
            return window.scaledHeight
        }
    override val scaleFactor: Int
        get() {
            val window = MinecraftClient.getInstance().window
            return window.scaleFactor.toInt()
        }

    override val isOnMacOS: Boolean
        get() = MinecraftClient.IS_SYSTEM_MAC

    override fun isMouseButtonDown(mouseButton: Int): Boolean {
        return GLFW.glfwGetMouseButton(window.handle, mouseButton) == GLFW.GLFW_PRESS
    }

    override fun isKeyboardKeyDown(keyboardKey: Int): Boolean {
        return InputUtil.isKeyPressed(window.handle, keyboardKey)
    }

    override fun addExtraBuiltinConfigProcessors(processor: MoulConfigProcessor<*>) {
    }

    fun displayGuiScreen(gui: Screen) {
        MinecraftClient.getInstance().setScreen(gui)
    }

    override fun openWrappedScreen(gui: GuiElement) {
        displayGuiScreen(GuiElementWrapper(gui))
    }

    override fun openWrappedScreen(gui: GuiContext) {
        displayGuiScreen(GuiComponentWrapper(gui))
    }

    override fun copyToClipboard(string: String) {
        MinecraftClient.getInstance().keyboard.clipboard = string
    }

    override fun copyFromClipboard(): String {
        return MinecraftClient.getInstance().keyboard.clipboard ?: ""
    }

    override fun sendClickableChatMessage(message: String, action: String, type: ClickType) {
        MinecraftClient.getInstance().inGameHud.chatHud.addMessage(Text.literal(message).styled {
            it.withClickEvent(
                when (type) {
                    ClickType.OPEN_LINK -> ClickEvent.OpenUrl(URI(action))
                    ClickType.RUN_COMMAND -> ClickEvent.RunCommand(action)
                },
            )
        })
    }

    override fun getKeyName(keyCode: Int): String {
        return ModernKeybindHelper.getKeyName(keyCode)
    }

    override fun provideTopLevelRenderContext(): RenderContext {
        return ModernRenderContext(
            DrawContext(
                MinecraftClient.getInstance(),
                MinecraftClient.getInstance().bufferBuilders.entityVertexConsumers
            )
        )
    }
}
