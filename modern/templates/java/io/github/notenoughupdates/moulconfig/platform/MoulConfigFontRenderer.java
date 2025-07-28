package io.github.notenoughupdates.moulconfig.platform;

import io.github.notenoughupdates.moulconfig.common.IFontRenderer;
import io.github.notenoughupdates.moulconfig.common.text.StructuredText;
import lombok.Value;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Value
public class MoulConfigFontRenderer implements IFontRenderer {
    @NotNull TextRenderer font;

    @Override
    public int getHeight() {
        return font.fontHeight;
    }

    @Override
    public int getStringWidth(@NotNull StructuredText string) {
        return font.getWidth(MoulConfigText.unwrap(string));
    }

    @Override
    public int getCharWidth(char c) {
        return font.getWidth(String.valueOf(c));
    }

    @Override
    public @NotNull List<@NotNull StructuredText> splitText(@NotNull StructuredText text, int width) {
        var list = new ArrayList<StructuredText>();
        font.getTextHandler().wrapLines(MoulConfigText.unwrap(text), width, Style.EMPTY, (stringVisitable, isWrapped) -> {
            var appendable = Text.empty();
            list.add(MoulConfigText.wrap(appendable));
            stringVisitable.visit((style, string) -> {
                appendable.append(Text.literal(string).setStyle(style));
                return Optional.empty();
            }, Style.EMPTY);
        });
        return list;
    }

    @Override
    @NotNull
    public String trimStringToWidth(@NotNull String string, int width, boolean reverse) {
        return font.trimToWidth(string, width, reverse);
    }
}
