package io.github.notenoughupdates.moulconfig.platform

import io.github.notenoughupdates.moulconfig.common.*
import io.github.notenoughupdates.moulconfig.internal.MCLogger
import io.github.notenoughupdates.moulconfig.processor.MoulConfigProcessor
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.util.InputUtil
import net.minecraft.text.ClickEvent
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import org.apache.logging.log4j.LogManager
import org.lwjgl.glfw.GLFW
import java.io.InputStream


class ModernMinecraft : IMinecraft {
    init {
        instance = this
    }

    companion object {
        init {
            IMinecraft.instance
        }

        lateinit var instance: ModernMinecraft
        var boundTexture: Identifier? = null
        fun fromIdentifier(identifier: Identifier): MyResourceLocation {
            return MyResourceLocation(identifier.namespace, identifier.path)
        }

        fun fromMyResourceLocation(resourceLocation: MyResourceLocation): Identifier {
            return Identifier(resourceLocation.root, resourceLocation.path)
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

    override fun isMouseButtonDown(mouseButton: Int): Boolean {
        return GLFW.glfwGetMouseButton(window.handle, mouseButton) == GLFW.GLFW_PRESS
    }

    override fun isKeyboardKeyDown(keyboardKey: Int): Boolean {
        return InputUtil.isKeyPressed(window.handle, keyboardKey)
    }

    override fun addExtraBuiltinConfigProcessors(processor: MoulConfigProcessor<*>) {
    }

    override fun sendClickableChatMessage(message: String, action: String, type: ClickType) {
        MinecraftClient.getInstance().inGameHud.chatHud.addMessage(Text.literal(message).styled {
            it.withClickEvent(
                ClickEvent(
                    when (type) {
                        ClickType.OPEN_LINK -> ClickEvent.Action.OPEN_URL
                        ClickType.RUN_COMMAND -> ClickEvent.Action.RUN_COMMAND
                    },
                    action
                )
            )
        })
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