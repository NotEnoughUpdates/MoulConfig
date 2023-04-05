package io.github.moulberry.moulconfig.gui.elements;

import io.github.moulberry.moulconfig.gui.GuiElementNew;
import io.github.moulberry.moulconfig.gui.GuiImmediateContext;
import io.github.moulberry.moulconfig.internal.TextRenderUtils;
import lombok.AllArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import java.util.function.Supplier;

/**
 * A gui element which renders a string in a single line
 */
@AllArgsConstructor
public class GuiElementText extends GuiElementNew {
    final FontRenderer fontRenderer;
    final Supplier<String> string;
    final int width;
    final TextAlignment alignment;
    final boolean shadow;

    public GuiElementText(String string, int width) {
        this(Minecraft.getMinecraft().fontRendererObj, () -> string, width, TextAlignment.LEFT, false);
    }
    public GuiElementText(String string) {
        this(string, Minecraft.getMinecraft().fontRendererObj.getStringWidth(string));
    }

    @Override
    public int getWidth() {
        return width + 4;
    }

    @Override
    public int getHeight() {
        return fontRenderer.FONT_HEIGHT + 4;
    }

    @Override
    public void render(GuiImmediateContext context) {
        String text = string.get();
        int length = fontRenderer.getStringWidth(text);
        if (length > width) {
            TextRenderUtils.drawStringScaledMaxWidth(text, fontRenderer, 2, 2, shadow, width, -1);
        }
        switch (alignment) {
            case LEFT:
                fontRenderer.drawString(text, 2, 2, -1, shadow);
                break;
            case CENTER:
                fontRenderer.drawString(text, width / 2 - length / 2 + 2, 2, -1, shadow);
                break;
            case RIGHT:
                fontRenderer.drawString(text, width - length + 2, 2, -1, shadow);
                break;
        }
    }

    public enum TextAlignment {
        LEFT, CENTER, RIGHT;
    }
}
