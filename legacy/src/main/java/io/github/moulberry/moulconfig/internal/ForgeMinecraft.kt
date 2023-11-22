package io.github.moulberry.moulconfig.internal

import io.github.moulberry.moulconfig.common.IFontRenderer
import io.github.moulberry.moulconfig.common.IKeyboardConstants
import io.github.moulberry.moulconfig.common.IMinecraft
import io.github.moulberry.moulconfig.common.MyResourceLocation
import net.minecraft.client.Minecraft
import net.minecraft.launchwrapper.Launch
import net.minecraft.util.ResourceLocation
import java.io.InputStream

class ForgeMinecraft : IMinecraft {
    override fun bindTexture(resourceLocation: MyResourceLocation) {
        Minecraft.getMinecraft().textureManager.bindTexture(fromMyResourceLocation(resourceLocation))
    }

    override fun loadResourceLocation(resourceLocation: MyResourceLocation): InputStream {
        return Minecraft.getMinecraft().resourceManager.getResource(fromMyResourceLocation(resourceLocation))
            .inputStream
    }

    override val isDevelopmentEnvironment: Boolean
        get() = Launch.blackboard.get("fml.deobfuscatedEnvironment") as Boolean

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