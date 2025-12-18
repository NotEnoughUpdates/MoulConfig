package io.github.notenoughupdates.moulconfig.platform;

import io.github.notenoughupdates.moulconfig.common.text.StructuredStyle;
import lombok.Value;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.NotNull;

@Value
public class MoulConfigStyle implements StructuredStyle {
    @NotNull Style style;

    public static MoulConfigStyle wrap(Style style) {
        return new MoulConfigStyle(style);
    }

    public static Style unwrap(StructuredStyle style) {
        return ((MoulConfigStyle) style).getStyle();
    }

    @NotNull
    @Override
    public StructuredStyle withColour(int rgb) {
        return new MoulConfigStyle(style.withColor(rgb));
    }

    @NotNull
    @Override
    public StructuredStyle withBold(boolean bold) {
        return new MoulConfigStyle(style.withBold(bold));
    }

    @NotNull
    @Override
    public StructuredStyle withItalic(boolean italic) {
        return new MoulConfigStyle(style.withItalic(italic));
    }

    @NotNull
    @Override
    public StructuredStyle withUnderline(boolean underline) {
        return new MoulConfigStyle(style.withUnderlined(underline));
    }

    @NotNull
    @Override
    public StructuredStyle withStrikethrough(boolean strikethrough) {
        return new MoulConfigStyle(style.withStrikethrough(strikethrough));
    }

    @NotNull
    @Override
    public StructuredStyle withObfuscated(boolean obfuscated) {
        return new MoulConfigStyle(style.withObfuscated(obfuscated));
    }
}
