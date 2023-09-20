package io.github.moulberry.moulconfig.gui.component;

import io.github.moulberry.moulconfig.common.IFontRenderer;
import io.github.moulberry.moulconfig.common.KeyboardConstants;
import io.github.moulberry.moulconfig.gui.GuiComponent;
import io.github.moulberry.moulconfig.gui.GuiImmediateContext;
import io.github.moulberry.moulconfig.gui.KeyboardEvent;
import io.github.moulberry.moulconfig.gui.MouseEvent;
import io.github.moulberry.moulconfig.internal.ClipboardUtils;
import io.github.moulberry.moulconfig.observer.GetSetter;
import lombok.RequiredArgsConstructor;
import lombok.var;

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
    final IFontRenderer font = mc.getDefaultFontRenderer();

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
        renderBox(context);
        renderText(context, visibleText);
        if (text.get().isEmpty() && !isFocused()) {
            context.getRenderContext().drawString(font, suggestion, TEXT_PADDING_X, TEXT_PADDING_Y, SUGGESTION_COLOR, false);
        }
        if (isFocused()) {
            renderCursor(context);
        }
        renderSelection(context);
    }

    public void validateCursor() {
        cursor = Math.max(0, Math.min(text.get().length(), cursor));
    }

    private void renderSelection(GuiImmediateContext context) {
        if (selection == cursor || selection == -1) return;
        int left = Math.min(cursor, selection);
        int right = Math.max(cursor, selection);
        if (right < scrollOffset || left > scrollOffset + visibleText.length()) return;
        int normalizedLeft = Math.max(scrollOffset, left) - scrollOffset;
        int normalizedRight = Math.min(scrollOffset + visibleText.length(), right) - scrollOffset;
        int leftPos = font.getStringWidth(safeSubString(visibleText, 0, normalizedLeft));
        int rightPos = leftPos + font.getStringWidth(safeSubString(visibleText, normalizedLeft, normalizedRight));
        context.getRenderContext().invertedRect(TEXT_PADDING_X + leftPos, TEXT_PADDING_Y, TEXT_PADDING_X + rightPos, getHeight() - TEXT_PADDING_Y);
    }

    private void renderCursor(GuiImmediateContext context) {
        if (System.currentTimeMillis() / 1000 % 2 == 0) {
            return;
        }
        if (cursor < scrollOffset) return;
        if (cursor > scrollOffset + visibleText.length()) return;
        int cursorOffset = font.getStringWidth(safeSubString(visibleText, 0, cursor - scrollOffset));
        context.getRenderContext().drawColoredRect(TEXT_PADDING_X + cursorOffset, TEXT_PADDING_Y, TEXT_PADDING_X + cursorOffset + 1, getHeight() - TEXT_PADDING_Y, CURSOR_COLOR);
    }


    private void renderText(GuiImmediateContext context, String visibleText) {
        int textColor = editable.get() ? ENABLED_COLOR : DISABLED_COLOR;
        context.getRenderContext().drawString(font, visibleText, TEXT_PADDING_X, TEXT_PADDING_Y, textColor, true);
    }

    private void renderBox(GuiImmediateContext context) {
        int borderColor = isFocused() ? BORDER_COLOR_SELECTED : BORDER_COLOR_UNSELECTED;
        context.getRenderContext().drawColoredRect(0, 0, width, getHeight(), borderColor);
        context.getRenderContext().drawColoredRect(1, 1, width - 1, getHeight() - 1, BACKGROUND_COLOR);
    }

    @Override
    public void keyboardEvent(KeyboardEvent event, GuiImmediateContext context) {
        if (!editable.get())
            return;
        if (!event.getPress()) return;
        switch (event.getKey()) {
            case KeyboardConstants.KEY_LEFT:
                onDirectionalKey(context, -1);
                return;
            case KeyboardConstants.KEY_RIGHT:
                onDirectionalKey(context, 1);
                return;
            case KeyboardConstants.KEY_HOME:
            case KeyboardConstants.KEY_UP:
                if (context.getRenderContext().isShiftDown()) {
                    if (selection == -1) selection = cursor;
                } else {
                    selection = -1;
                }
                cursor = 0;
                scrollCursorIntoView();
                return;
            case KeyboardConstants.KEY_DOWN:
            case KeyboardConstants.KEY_END:
                if (context.getRenderContext().isShiftDown()) {
                    if (selection == -1) selection = cursor;
                } else {
                    selection = -1;
                }
                cursor = text.get().length();
                scrollCursorIntoView();
                return;
            case KeyboardConstants.KEY_C:
                if (context.getRenderContext().isCtrlDown()) {
                    ClipboardUtils.copyToClipboard(getSelection());
                    return;
                }
                break;
            case KeyboardConstants.KEY_X:
                if (context.getRenderContext().isCtrlDown()) {
                    ClipboardUtils.copyToClipboard(getSelection());
                    writeText("");
                    return;
                }
                break;
            case KeyboardConstants.KEY_V:
                if (context.getRenderContext().isCtrlDown()) {
                    writeText(ClipboardUtils.getClipboardContent());
                    return;
                }
                break;
            case KeyboardConstants.KEY_A:
                if (context.getRenderContext().isCtrlDown()) {
                    cursor = text.get().length();
                    selection = 0;
                    scrollCursorIntoView();
                    return;
                }
                break;
            case KeyboardConstants.KEY_BACK:
                if (selection == -1)
                    selection = skipCharacters(context.getRenderContext().isCtrlDown(), -1);
                writeText("");
                return;
            case KeyboardConstants.KEY_DELETE:
                if (selection == -1)
                    selection = skipCharacters(context.getRenderContext().isCtrlDown(), 1);
                writeText("");
                return;

        }
        char c = event.getChar();
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
    public void mouseEvent(MouseEvent mouseEvent, GuiImmediateContext context) {
        super.mouseEvent(mouseEvent, context);
        if (context.isHovered() && mouseEvent instanceof MouseEvent.Click) {
            var click = ((MouseEvent.Click) mouseEvent);
            if (click.getMouseState() && click.getMouseButton() == 0) {
                requestFocus();
            }
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

    void onDirectionalKey(GuiImmediateContext context, int i) {
        if (context.getRenderContext().isShiftDown()) {
            if (selection == -1) selection = cursor;
            cursor = skipCharacters(context.getRenderContext().isCtrlDown(), i);
        } else {
            if (selection != -1) {
                cursor = i < 0 ? Math.min(cursor, selection) : Math.max(cursor, selection);
                selection = -1;
            } else {
                cursor = skipCharacters(context.getRenderContext().isCtrlDown(), i);
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
