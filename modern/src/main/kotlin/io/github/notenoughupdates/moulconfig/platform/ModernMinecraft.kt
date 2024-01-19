package io.github.notenoughupdates.moulconfig.platform

import io.github.moulberry.moulconfig.common.IFontRenderer
import io.github.moulberry.moulconfig.common.IKeyboardConstants
import io.github.moulberry.moulconfig.common.IMinecraft
import io.github.moulberry.moulconfig.common.MyResourceLocation
import io.github.moulberry.moulconfig.internal.MCLogger
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import net.minecraft.util.Identifier
import org.apache.logging.log4j.LogManager
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

    override val mouseX: Int
        get() {
            val mouse = MinecraftClient.getInstance().mouse
            val window = MinecraftClient.getInstance().window
            val x = (mouse.x * window.scaledWidth.toDouble() / window.width.toDouble()).toInt()
            return x
        }
    override val mouseY: Int
        get() {
            val mouse = MinecraftClient.getInstance().mouse
            val window = MinecraftClient.getInstance().window
            val y = (mouse.y * window.scaledHeight.toDouble() / window.height.toDouble()).toInt()
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
}