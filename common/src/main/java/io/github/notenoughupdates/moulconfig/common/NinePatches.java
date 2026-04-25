package io.github.notenoughupdates.moulconfig.common;

import io.github.notenoughupdates.moulconfig.GuiTextures;
import juuxel.libninepatch.NinePatch;

public final class NinePatches {
    public static final NinePatches INSTANCE = new NinePatches();

    private NinePatches() {
    }

    public static NinePatch<MyResourceLocation> createButton() {
        return NinePatch.builder(GuiTextures.BUTTON)
            .cornerSize(10)
            .cornerUv(10 / 32F, 10 / 96F)
            .mode(NinePatch.Mode.STRETCHING)
            .build();
    }

    public static NinePatch<MyResourceLocation> createWhiteButton() {
        return NinePatch.builder(GuiTextures.BUTTON_WHITE)
            .cornerSize(14)
            .cornerUv(14 / 32F, 14 / 96F)
            .mode(NinePatch.Mode.STRETCHING)
            .build();
    }

    public static NinePatch<MyResourceLocation> createVanillaPanel() {
        return NinePatch.builder(GuiTextures.VANILLA_PANEL)
            .cornerSize(4)
            .cornerUv(4 / 16F)
            .mode(NinePatch.Mode.STRETCHING)
            .build();
    }
}
