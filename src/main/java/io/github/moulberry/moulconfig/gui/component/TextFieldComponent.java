package io.github.moulberry.moulconfig.gui.component;

import io.github.moulberry.moulconfig.gui.GuiComponent;
import io.github.moulberry.moulconfig.gui.GuiImmediateContext;
import io.github.moulberry.moulconfig.internal.ClipboardUtils;
import io.github.moulberry.moulconfig.internal.KeybindHelper;
import io.github.moulberry.moulconfig.internal.RenderUtils;
import io.github.moulberry.moulconfig.observer.GetSetter;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

@RequiredArgsConstructor
public class TextFieldComponent extends GuiComponent {
    private static final int TEXT_PADDING_X = 2;
    private static final int BACKGROUND_COLOR = 0xFF000000;
    private static final int BORDER_COLOR_SELECTED = 0xFFFFFFA0;
    private static final int BORDER_COLOR_UNSELECTED = 0xFFA0A0A0;
    private static final int ENABLED_COLOR = 0xFFE0E0E0;
    private static final int SUGGESTION_COLOR = 0xFF808080;
    private static final int DISABLED_COLOR = 0xFF707070;
    private static final int CURSOR_COLOR = 0xFFD0D0D0;
    private static final int TEXT_PADDING_Y = 2;
    final GetSetter<String> text;
    final GetSetter<Boolean> editable;
    final String suggestion;
    final int width;
    final FontRenderer font = Minecraft.getMinecraft().fontRendererObj;

    int cursor = 0;
    int selection = -1;
    int scrollOffset = 0;
    String visibleText;

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return 14;
    }


    public void scrollCursorIntoView() {
        validateCursor();
        if (scrollOffset > cursor)
            scrollOffset = cursor;
        if (scrollOffset < cursor && font.trimStringToWidth(safeSubString(text.get(), scrollOffset), width - TEXT_PADDING_X * 2).length() + scrollOffset < cursor) {
            scrollOffset = cursor;
        }
        checkScrollOffset();
    }

    public void checkScrollOffset() {
        String text = this.text.get();
        int rightMostScrollOffset = text.length() - font.trimStringToWidth(text, width - TEXT_PADDING_X * 2, true).length();
        scrollOffset = Math.max(0, Math.min(rightMostScrollOffset, scrollOffset));
    }

    @Override
    public void render(GuiImmediateContext context) {
        validateCursor();
        checkScrollOffset();
        visibleText = font.trimStringToWidth(safeSubString(text.get(), scrollOffset), width - TEXT_PADDING_X * 2);
        renderBox();
        renderText(visibleText);
        if (text.get().isEmpty() && !isFocused()) {
            font.drawString(suggestion, TEXT_PADDING_X, TEXT_PADDING_Y, SUGGESTION_COLOR);
        }
        if (isFocused()) {
            renderCursor();
        }
        renderSelection();
    }

    public void validateCursor() {
        cursor = Math.max(0, Math.min(text.get().length(), cursor));
    }

    private void renderSelection() {
        if (selection == cursor || selection == -1) return;
        int left = Math.min(cursor, selection);
        int right = Math.max(cursor, selection);
        if (right < scrollOffset || left > scrollOffset + visibleText.length()) return;
        int normalizedLeft = Math.max(scrollOffset, left) - scrollOffset;
        int normalizedRight = Math.min(scrollOffset + visibleText.length(), right) - scrollOffset;
        int leftPos = font.getStringWidth(safeSubString(visibleText, 0, normalizedLeft));
        int rightPos = leftPos + font.getStringWidth(safeSubString(visibleText, normalizedLeft, normalizedRight));
        invertedRect(TEXT_PADDING_X + leftPos, TEXT_PADDING_Y, TEXT_PADDING_X + rightPos, getHeight() - TEXT_PADDING_Y);
    }

    private void invertedRect(int left, int top, int right, int bottom) {
        GlStateManager.enableColorLogic();
        GlStateManager.colorLogicOp(GL11.GL_OR_REVERSE);
        GlStateManager.disableTexture2D();
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.color(0.0F, 0.0F, 255.0F, 255.0F);
        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        worldrenderer.pos(right, top, 0).endVertex();
        worldrenderer.pos(left, top, 0).endVertex();
        worldrenderer.pos(left, bottom, 0).endVertex();
        worldrenderer.pos(right, bottom, 0).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableColorLogic();
    }

    private void renderCursor() {
        if (System.currentTimeMillis() / 1000 % 2 == 0) {
            return;
        }
        if (cursor < scrollOffset) return;
        if (cursor > scrollOffset + visibleText.length()) return;
        int cursorOffset = font.getStringWidth(safeSubString(visibleText, 0, cursor - scrollOffset));
        RenderUtils.drawGradientRect(0, TEXT_PADDING_X + cursorOffset, TEXT_PADDING_Y, TEXT_PADDING_X + cursorOffset + 1, getHeight() - TEXT_PADDING_Y, CURSOR_COLOR, CURSOR_COLOR);
    }


    private void renderText(String visibleText) {
        int textColor = editable.get() ? ENABLED_COLOR : DISABLED_COLOR;
        font.drawString(visibleText, TEXT_PADDING_X, TEXT_PADDING_Y, textColor, true);
    }

    private void renderBox() {
        int borderColor = isFocused() ? BORDER_COLOR_SELECTED : BORDER_COLOR_UNSELECTED;
        RenderUtils.drawGradientRect(0, 0, 0, width, getHeight(), borderColor, borderColor);
        RenderUtils.drawGradientRect(0, 1, 1, width - 1, getHeight() - 1, BACKGROUND_COLOR, BACKGROUND_COLOR);
    }

    @Override
    public void keyboardEvent(GuiImmediateContext context) {
        if (!editable.get())
            return;
        if (!Keyboard.getEventKeyState()) return;
        switch (Keyboard.getEventKey()) {
            case Keyboard.KEY_LEFT:
                onDirectionalKey(-1);
                return;
            case Keyboard.KEY_RIGHT:
                onDirectionalKey(1);
                return;
            case Keyboard.KEY_HOME:
            case Keyboard.KEY_UP:
                if (KeybindHelper.isShiftDown()) {
                    if (selection == -1) selection = cursor;
                } else {
                    selection = -1;
                }
                cursor = 0;
                scrollCursorIntoView();
                return;
            case Keyboard.KEY_DOWN:
            case Keyboard.KEY_END:
                if (KeybindHelper.isShiftDown()) {
                    if (selection == -1) selection = cursor;
                } else {
                    selection = -1;
                }
                cursor = text.get().length();
                scrollCursorIntoView();
                return;
            case Keyboard.KEY_C:
                if (KeybindHelper.isCtrlDown()) {
                    ClipboardUtils.copyToClipboard(getSelection());
                    return;
                }
                break;
            case Keyboard.KEY_X:
                if (KeybindHelper.isCtrlDown()) {
                    ClipboardUtils.copyToClipboard(getSelection());
                    writeText("");
                    return;
                }
                break;
            case Keyboard.KEY_V:
                if (KeybindHelper.isCtrlDown()) {
                    writeText(ClipboardUtils.getClipboardContent());
                    return;
                }
                break;
            case Keyboard.KEY_A:
                if (KeybindHelper.isCtrlDown()) {
                    cursor = text.get().length();
                    selection = 0;
                    scrollCursorIntoView();
                    return;
                }
                break;
            case Keyboard.KEY_BACK:
                if (selection == -1)
                    selection = skipCharacters(KeybindHelper.isCtrlDown(), -1);
                writeText("");
                return;
            case Keyboard.KEY_DELETE:
                if (selection == -1)
                    selection = skipCharacters(KeybindHelper.isCtrlDown(), 1);
                writeText("");
                return;

        }
        char c = Keyboard.getEventCharacter();
        if (c >= ' ' && c != '§' && c != 127) {
            writeText(c + "");
        }
    }

    private String getSelection() {
        if (selection == -1) return "";
        int l = Math.min(cursor, selection);
        int r = Math.max(cursor, selection);
        return safeSubString(text.get(), l, r);
    }

    @Override
    public void mouseEvent(GuiImmediateContext context) {
        super.mouseEvent(context);
        if (context.isHovered() && Mouse.getEventButtonState() && Mouse.getEventButton() == 0) {
            requestFocus();
        }
    }

    private String safeSubString(String str, int startIndex) {
        return str.substring(Math.min(startIndex, str.length()));
    }

    private String safeSubString(String str, int startIndex, int endIndex) {
        return str.substring(Math.min(startIndex, str.length()), Math.min(Math.max(startIndex, endIndex), str.length()));
    }

    public void writeText(String s) {
        String t = text.get();
        if (selection == -1) {
            text.set(safeSubString(t, 0, cursor) + s + safeSubString(t, cursor));
            cursor = cursor + s.length();
        } else {
            int l = Math.min(cursor, selection);
            int r = Math.max(cursor, selection);
            text.set(safeSubString(t, 0, l) + s + safeSubString(t, r));
            cursor = l + s.length();
            selection = -1;
        }
        scrollCursorIntoView();
    }

    void onDirectionalKey(int i) {
        if (KeybindHelper.isShiftDown()) {
            if (selection == -1) selection = cursor;
            cursor = skipCharacters(KeybindHelper.isCtrlDown(), i);
        } else {
            if (selection != -1) {
                cursor = i < 0 ? Math.min(cursor, selection) : Math.max(cursor, selection);
                selection = -1;
            } else {
                cursor = skipCharacters(KeybindHelper.isCtrlDown(), i);
            }
        }
        scrollCursorIntoView();
    }

    private int skipCharacters(boolean skipWords, int i) {
        if (i != -1 && i != 1) return cursor;
        int position = cursor;
        while (true) {
            position += i;
            if (position < 0) return 0;
            if (position > text.get().length()) return text.get().length();
            if (!skipWords) return position;
            if (position < text.get().length() && Character.isWhitespace(text.get().charAt(position)))
                return position;
        }
    }
}
