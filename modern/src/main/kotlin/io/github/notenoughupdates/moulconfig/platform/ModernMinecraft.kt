package io.github.notenoughupdates.moulconfig.platform

import io.github.moulberry.moulconfig.common.IFontRenderer
import io.github.moulberry.moulconfig.common.IKeyboardConstants
import io.github.moulberry.moulconfig.common.IMinecraft
import io.github.moulberry.moulconfig.common.MyResourceLocation
import net.minecraft.client.MinecraftClient
import net.minecraft.util.Identifier
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

    override fun bindTexture(resourceLocation: MyResourceLocation) {
        boundTexture = fromMyResourceLocation(resourceLocation)
    }

    override fun loadResourceLocation(resourceLocation: MyResourceLocation): InputStream {
        return MinecraftClient.getInstance().resourceManager.getResource(fromMyResourceLocation(resourceLocation))
            .get().inputStream
    }

    override val defaultFontRenderer: IFontRenderer
        get() = ModernFontRenderer(MinecraftClient.getInstance().textRenderer)
    override val keyboardConstants: IKeyboardConstants
        get() = ModernKeyboardConstants
}