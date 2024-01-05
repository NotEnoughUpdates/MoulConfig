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
        get() = TODO("Not yet implemented")
    override val mouseY: Int
        get() = TODO("Not yet implemented")

    override fun loadResourceLocation(resourceLocation: MyResourceLocation): InputStream {
        return MinecraftClient.getInstance().resourceManager.getResource(fromMyResourceLocation(resourceLocation))
            .get().inputStream
    }

    override val defaultFontRenderer: IFontRenderer
        get() = ModernFontRenderer(MinecraftClient.getInstance().textRenderer)
    override val keyboardConstants: IKeyboardConstants
        get() = ModernKeyboardConstants
    override val scaledWidth: Int
        get() = TODO("Not yet implemented")
    override val scaledHeight: Int
        get() = TODO("Not yet implemented")
    override val scaleFactor: Int
        get() = TODO("Not yet implemented")
}