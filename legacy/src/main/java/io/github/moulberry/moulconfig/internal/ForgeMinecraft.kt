package io.github.moulberry.moulconfig.internal

import io.github.moulberry.moulconfig.common.IFontRenderer
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
    }

    override val defaultFontRenderer: IFontRenderer
        get() = ForgeFontRenderer(Minecraft.getMinecraft().fontRendererObj)
}