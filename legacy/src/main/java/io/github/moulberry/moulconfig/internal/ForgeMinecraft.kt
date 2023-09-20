package io.github.moulberry.moulconfig.internal

import io.github.moulberry.moulconfig.common.IFontRenderer
import io.github.moulberry.moulconfig.common.IKeyboardConstants
import io.github.moulberry.moulconfig.common.IMinecraft
import io.github.moulberry.moulconfig.common.MyResourceLocation
import net.minecraft.client.Minecraft
import net.minecraft.util.ResourceLocation

class ForgeMinecraft : IMinecraft {
    override fun bindTexture(resourceLocation: MyResourceLocation) {
        Minecraft.getMinecraft().textureManager.bindTexture(fromMyResourceLocation(resourceLocation))
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