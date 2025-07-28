package io.github.notenoughupdates.moulconfig.internal;

import io.github.notenoughupdates.moulconfig.common.text.DefaultFormattingColour;
import io.github.notenoughupdates.moulconfig.common.text.StructuredStyle;
import lombok.Value;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Value
public class StructuredStyleImpl implements StructuredStyle {
    ChatStyle chatStyle;

    public static @NotNull StructuredStyle wrap(@NotNull ChatStyle chatStyle) {
        return new StructuredStyleImpl(chatStyle);
    }

    public static ChatStyle unwrap(StructuredStyle style) {
        return ((StructuredStyleImpl) style).chatStyle;
    }

    @Override
    public @NotNull StructuredStyle withColour(int rgb) {
        return withColour(DefaultFormattingColour.estimate(rgb));
    }

    private static final Map<DefaultFormattingColour, EnumChatFormatting> FORMATTINGS = InitUtil.make(new HashMap<>(), colourMap -> {
        for (DefaultFormattingColour formattingColour : DefaultFormattingColour.values()) {
            for (EnumChatFormatting enumChatFormatting : EnumChatFormatting.values()) {
                if (enumChatFormatting.getColorIndex() == formattingColour.getColorHexDigit())
                    colourMap.put(formattingColour, enumChatFormatting);
            }
        }
    });

    @Override
    public @NotNull StructuredStyle withColour(@NotNull DefaultFormattingColour colour) {
        chatStyle.setColor(Objects.requireNonNull(FORMATTINGS.get(colour)));
        return this;
    }

    @Override
    public @NotNull StructuredStyle withBold(boolean bold) {
        chatStyle.setBold(bold);
        return this;
    }

    @Override
    public @NotNull StructuredStyle withItalic(boolean italic) {
        chatStyle.setItalic(italic);
        return this;
    }

    @Override
    public @NotNull StructuredStyle withUnderline(boolean underline) {
        chatStyle.setUnderlined(underline);
        return this;
    }

    @Override
    public @NotNull StructuredStyle withStrikethrough(boolean strikethrough) {
        chatStyle.setStrikethrough(strikethrough);
        return this;
    }

    @Override
    public @NotNull StructuredStyle withObfuscated(boolean obfuscated) {
        chatStyle.setObfuscated(obfuscated);
        return this;
    }
}
