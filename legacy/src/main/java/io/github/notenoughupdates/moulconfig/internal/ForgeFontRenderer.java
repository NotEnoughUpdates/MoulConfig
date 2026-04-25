package io.github.notenoughupdates.moulconfig.internal;

import io.github.notenoughupdates.moulconfig.common.IFontRenderer;
import io.github.notenoughupdates.moulconfig.common.text.StructuredText;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.util.IChatComponent;

import java.util.ArrayList;
import java.util.List;

public class ForgeFontRenderer implements IFontRenderer {
    public final FontRenderer font;

    public ForgeFontRenderer(FontRenderer font) {
        this.font = font;
    }

    @Override
    public int getStringWidth(StructuredText string) {
        return font.getStringWidth(StructuredTextImpl.unwrap(string).getFormattedText());
    }

    @Override
    public int getHeight() {
        return font.FONT_HEIGHT;
    }

    @Override
    public int getStringWidth(String string) {
        return font.getStringWidth(string);
    }

    @Override
    public int getCharWidth(char c) {
        return font.getCharWidth(c);
    }

    @Override
    public List<StructuredText> splitText(StructuredText text, int width) {
        List<IChatComponent> components = GuiUtilRenderComponents.splitText(StructuredTextImpl.unwrap(text), width, font, false, false);
        List<StructuredText> result = new ArrayList<>();
        for (IChatComponent component : components) {
            result.add(StructuredTextImpl.wrap(component));
        }
        return result;
    }

    @Override
    public String trimStringToWidth(String string, int width, boolean reverse) {
        return font.trimStringToWidth(string, width, reverse);
    }
}
