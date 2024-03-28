package io.github.notenoughupdates.moulconfig.internal

import io.github.notenoughupdates.moulconfig.common.IFontRenderer
import io.github.notenoughupdates.moulconfig.common.IKeyboardConstants
import io.github.notenoughupdates.moulconfig.common.IMinecraft
import io.github.notenoughupdates.moulconfig.common.MyResourceLocation
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.launchwrapper.Launch
import net.minecraft.util.ResourceLocation
import org.apache.logging.log4j.LogManager
import org.lwjgl.input.Mouse
import java.io.InputStream

class ForgeMinecraft : IMinecraft {
    override fun bindTexture(resourceLocation: MyResourceLocation) {
        Minecraft.getMinecraft().textureManager.bindTexture(fromMyResourceLocation(resourceLocation))
    }

    override fun loadResourceLocation(resourceLocation: MyResourceLocation): InputStream {
        return Minecraft.getMinecraft().resourceManager.getResource(fromMyResourceLocation(resourceLocation))
            .inputStream
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

    override val isDevelopmentEnvironment: Boolean
        get() = Launch.blackboard.get("fml.deobfuscatedEnvironment") as Boolean

    override val scaledWidth
        get(): Int = ScaledResolution(Minecraft.getMinecraft()).scaledWidth

    override val scaledHeight: Int
        get() = ScaledResolution(Minecraft.getMinecraft()).scaledHeight

    override val scaleFactor: Int
        get() = ScaledResolution(Minecraft.getMinecraft()).scaleFactor

    override val mouseX: Int
        get() {
            val width = scaledWidth
            val mouseX = Mouse.getX() * width / Minecraft.getMinecraft().displayWidth
            return mouseX
        }

    override val mouseY: Int
        get() {
            val height = scaledHeight
            val mouseY = height - Mouse.getY() * height / Minecraft.getMinecraft().displayHeight - 1
            return mouseY
        }
    override val mouseXHF: Double
        get() {
            val width = ScaledResolution(Minecraft.getMinecraft()).scaledWidth_double
            val mouseX = Mouse.getX() * width / Minecraft.getMinecraft().displayWidth
            return mouseX
        }
    override val mouseYHF: Double
        get() {
            val height = ScaledResolution(Minecraft.getMinecraft()).scaledHeight_double
            val mouseY = height - Mouse.getY() * height / Minecraft.getMinecraft().displayHeight - 1
            return mouseY
        }

    companion object {
        @JvmStatic
        fun fromMyResourceLocation(resourceLocation: MyResourceLocation): ResourceLocation {
            return ResourceLocation(
                resourceLocation.root,
                resourceLocation.path
            )
        }

        @JvmStatic
        fun fromResourceLocation(resouceLocation: ResourceLocation): MyResourceLocation {
            return MyResourceLocation(resouceLocation.resourceDomain, resouceLocation.resourcePath)
        }
    }

    override val defaultFontRenderer: IFontRenderer
        get() = ForgeFontRenderer(Minecraft.getMinecraft().fontRendererObj)
    override val keyboardConstants: IKeyboardConstants
        get() = ForgeKeyboardConstants
}