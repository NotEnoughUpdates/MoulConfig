package io.github.notenoughupdates.moulconfig.platform;

import io.github.notenoughupdates.moulconfig.common.IFontRenderer;
import io.github.notenoughupdates.moulconfig.common.text.StructuredText;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record MoulConfigFontRenderer(@NotNull Font font) implements IFontRenderer {

    @Override
    public int getHeight() {
        return font.lineHeight;
    }

    @Override
    public int getStringWidth(@NotNull StructuredText string) {
        return font.width(MoulConfigText.unwrap(string));
    }

    @Override
    public int getCharWidth(char c) {
        return font.width(String.valueOf(c));
    }

    @Override
    public @NotNull List<@NotNull StructuredText> splitText(@NotNull StructuredText text, int width) {
        var list = new ArrayList<StructuredText>();
        font.getSplitter().splitLines(MoulConfigText.unwrap(text), width, Style.EMPTY, (stringVisitable, isWrapped) -> {
            var appendable = Component.empty();
            list.add(MoulConfigText.wrap(appendable));
            stringVisitable.visit((style, string) -> {
                appendable.append(Component.literal(string).setStyle(style));
                return Optional.empty();
            }, Style.EMPTY);
        });
        return list;
    }

    @Override
    @NotNull
    public String trimStringToWidth(@NotNull String string, int width, boolean reverse) {
        return font.plainSubstrByWidth(string, width, reverse);
    }
}
