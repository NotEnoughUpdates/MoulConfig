package io.github.moulberry.moulconfig.common

import lombok.Getter
import java.util.*

interface IMinecraft {
    fun bindTexture(resourceLocation: MyResourceLocation)
    val defaultFontRenderer: IFontRenderer

    companion object {
        @JvmField
        @Getter
        val instance: IMinecraft = ServiceLoader.load(IMinecraft::class.java).also { it.reload() }.first()
    }
}
