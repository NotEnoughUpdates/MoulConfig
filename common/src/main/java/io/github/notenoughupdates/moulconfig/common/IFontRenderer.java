package io.github.notenoughupdates.moulconfig.common;

import io.github.notenoughupdates.moulconfig.common.text.StructuredText;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public interface IFontRenderer {
    int getHeight();

    default int getStringWidth(String string) {
        return getStringWidth(StructuredText.of(string));
    }

    int getStringWidth(StructuredText structuredText);

    default int getCharWidth(char c) {
        return getStringWidth(Character.toString(c));
    }

    List<StructuredText> splitText(StructuredText structuredText, int width);

    default List<StructuredText> splitLines(StructuredText structuredText) {
        return splitText(structuredText, Integer.MAX_VALUE);
    }

    default String trimStringToWidth(String string, int width) {
        return trimStringToWidth(string, width, false);
    }

    String trimStringToWidth(String string, int width, boolean reversed);
}
