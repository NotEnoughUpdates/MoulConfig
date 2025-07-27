package io.github.notenoughupdates.moulconfig.common.text;

import org.jetbrains.annotations.NotNull;

public interface StructuredStyle { // TODO: make this an interface
    default @NotNull StructuredStyle withColour(@NotNull DefaultFormattingColour colour) {
        return withColour(colour.getRgb());
    }

    @NotNull StructuredStyle withColour(int rgb);

    @NotNull StructuredStyle withBold(boolean bold);

    @NotNull StructuredStyle withItalic(boolean italic);

    @NotNull StructuredStyle withUnderline(boolean underline);

    @NotNull StructuredStyle withStrikethrough(boolean strikethrough);

    @NotNull StructuredStyle withObfuscated(boolean obfuscated);
}
