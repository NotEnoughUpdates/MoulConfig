package io.github.notenoughupdates.moulconfig.common

import io.github.notenoughupdates.moulconfig.GuiTextures
import juuxel.libninepatch.NinePatch

object NinePatches {
    fun createButton(): NinePatch<MyResourceLocation> {
        return NinePatch.builder(GuiTextures.BUTTON)
            .cornerSize(10)
            .cornerUv(10 / 32f, 10 / 96F)
            .mode(NinePatch.Mode.STRETCHING)
            .build()
    }
    fun createWhiteButton(): NinePatch<MyResourceLocation> {
        return NinePatch.builder(GuiTextures.BUTTON_WHITE)
            .cornerSize(14)
            .cornerUv(14 / 32f, 14 / 96F)
            .mode(NinePatch.Mode.STRETCHING)
            .build()
    }

    fun createVanillaPanel(): NinePatch<MyResourceLocation> {
        return NinePatch.builder(GuiTextures.VANILLA_PANEL)
            .cornerSize(4)
            .cornerUv(4 / 16F)
            .mode(NinePatch.Mode.STRETCHING)
            .build()
    }
}
