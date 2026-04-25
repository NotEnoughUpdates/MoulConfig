package io.github.notenoughupdates.moulconfig.gui.component;

import io.github.notenoughupdates.moulconfig.common.IFontRenderer;
import io.github.notenoughupdates.moulconfig.common.IMinecraft;
import io.github.notenoughupdates.moulconfig.common.KeyboardConstants;
import io.github.notenoughupdates.moulconfig.common.text.StructuredText;
import io.github.notenoughupdates.moulconfig.gui.GuiComponent;
import io.github.notenoughupdates.moulconfig.gui.GuiImmediateContext;
import io.github.notenoughupdates.moulconfig.gui.KeyboardEvent;
import io.github.notenoughupdates.moulconfig.gui.MouseEvent;
import io.github.notenoughupdates.moulconfig.observer.GetSetter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class TextFieldComponent extends GuiComponent {
    private static final int TEXT_PADDING_X = 4;
    private static final int BACKGROUND_COLOR = 0xFF000000;
    private static final int BORDER_COLOR_SELECTED = 0xFF00FF00;
    private static final int BORDER_COLOR_UNSELECTED = 0xFFFFFFFF;
    private static final int ENABLED_COLOR = 0xFFE0E0E0;
    private static final int SUGGESTION_COLOR = 0xFF808080;
    private static final int DISABLED_COLOR = 0xFF707070;
    private static final int CURSOR_COLOR = 0xFFD0D0D0;
    private static final int TEXT_PADDING_Y = 2;

    protected final GetSetter<String> text;
    private final int preferredWidth;
    protected final Supplier<Boolean> editable;
    protected final String suggestion;
    protected final IFontRenderer font;
    protected final Set<Character> forbiddenChars;

    private int cursor;
    private int selection = -1;
    private int scrollOffset;
    private String visibleText;
    private boolean shouldExpandToFit;
    private boolean initializedCursor;

    public TextFieldComponent(GetSetter<String> text, int preferredWidth) {
        this(text, preferredWidth, GetSetter.constant(true), "", IMinecraft.INSTANCE.getDefaultFontRenderer(), singletonCharSet('\u00a7'));
    }

    public TextFieldComponent(GetSetter<String> text, int preferredWidth, Supplier<Boolean> editable, String suggestion) {
        this(text, preferredWidth, editable, suggestion, IMinecraft.INSTANCE.getDefaultFontRenderer(), singletonCharSet('\u00a7'));
    }

    public TextFieldComponent(GetSetter<String> text, int preferredWidth, Supplier<Boolean> editable, String suggestion, IFontRenderer font) {
        this(text, preferredWidth, editable, suggestion, font, singletonCharSet('\u00a7'));
    }

    public TextFieldComponent(GetSetter<String> text, int preferredWidth, Supplier<Boolean> editable, String suggestion, IFontRenderer font, Set<Character> forbiddenChars) {
        this.text = text;
        this.preferredWidth = preferredWidth;
        this.editable = editable;
        this.suggestion = suggestion;
        this.font = font;
        this.forbiddenChars = forbiddenChars;
    }

    private static Set<Character> singletonCharSet(char c) {
        return Collections.singleton(c);
    }

    public GetSetter<String> getText() {
        return text;
    }

    @Override
    public int getWidth() {
        if (isFocused() && shouldExpandToFit) {
            return Math.max(preferredWidth, font.getStringWidth(StructuredText.of(text.get())) + 10);
        }
        return preferredWidth;
    }

    @Override
    public int getHeight() {
        return 14;
    }

    public void scrollCursorIntoView(int width) {
        validateCursor();
        if (scrollOffset > cursor) {
            scrollOffset = cursor;
        }
        if (scrollOffset < cursor
            && font.trimStringToWidth(safeSubString(text.get(), scrollOffset), width - TEXT_PADDING_X * 2).length() + scrollOffset < cursor) {
            scrollOffset = cursor;
        }
        checkScrollOffset(width);
    }

    public void checkScrollOffset(int width) {
        String value = text.get();
        int rightMostScrollOffset = value.length() - font.trimStringToWidth(value, width - TEXT_PADDING_X * 2, true).length();
        scrollOffset = Math.max(0, Math.min(rightMostScrollOffset, scrollOffset));
    }

    public void updateVisibleText(int width) {
        visibleText = font.trimStringToWidth(safeSubString(text.get(), scrollOffset), width - TEXT_PADDING_X * 2);
    }

    @Override
    public void render(GuiImmediateContext context) {
        validateCursor();
        checkScrollOffset(context.getWidth());
        updateVisibleText(context.getWidth());
        renderBox(context);
        renderText(context, visibleText);
        if (text.get().isEmpty() && !isFocused()) {
            context.getRenderContext().drawString(
                font,
                StructuredText.of(suggestion),
                TEXT_PADDING_X,
                context.getHeight() / 2 - font.getHeight() / 2,
                SUGGESTION_COLOR,
                false
            );
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
        context.getRenderContext().invertedRect(
            (float) (TEXT_PADDING_X + leftPos),
            (float) TEXT_PADDING_Y,
            (float) (TEXT_PADDING_X + rightPos),
            (float) (context.getHeight() - TEXT_PADDING_Y),
            0xFF0000FF
        );
    }

    private void renderCursor(GuiImmediateContext context) {
        if (System.currentTimeMillis() / 1000 % 2 == 0) return;
        if (cursor < scrollOffset) return;
        if (cursor > scrollOffset + visibleText.length()) return;
        int cursorOffset = font.getStringWidth(safeSubString(visibleText, 0, cursor - scrollOffset));
        context.getRenderContext().drawColoredRect(
            (float) (TEXT_PADDING_X + cursorOffset),
            (float) TEXT_PADDING_Y,
            (float) (TEXT_PADDING_X + cursorOffset + 1),
            (float) (context.getHeight() - TEXT_PADDING_Y),
            CURSOR_COLOR
        );
    }

    private void renderText(GuiImmediateContext context, String visibleText) {
        int textColor = editable.get() ? ENABLED_COLOR : DISABLED_COLOR;
        context.getRenderContext().drawString(
            font,
            StructuredText.of(visibleText),
            TEXT_PADDING_X,
            context.getHeight() / 2 - font.getHeight() / 2,
            textColor,
            true
        );
    }

    private void renderBox(GuiImmediateContext context) {
        int borderColor = isFocused() ? BORDER_COLOR_SELECTED : BORDER_COLOR_UNSELECTED;
        context.getRenderContext().drawColoredRect(0F, 0F, (float) context.getWidth(), (float) context.getHeight(), borderColor);
        context.getRenderContext().drawColoredRect(1F, 1F, (float) (context.getWidth() - 1), (float) (context.getHeight() - 1), BACKGROUND_COLOR);
    }

    @Override
    public boolean keyboardEvent(KeyboardEvent event, GuiImmediateContext context) {
        if (!editable.get()) return false;
        if (!isFocused()) return false;
        if (event instanceof KeyboardEvent.KeyPressed) {
            KeyboardEvent.KeyPressed keyPressed = (KeyboardEvent.KeyPressed) event;
            if (!keyPressed.getPressed()) return false;
            int keycode = keyPressed.getKeycode();
            if (keycode == KeyboardConstants.INSTANCE.getLeft()) {
                onDirectionalKey(context, -1);
                return true;
            } else if (keycode == KeyboardConstants.INSTANCE.getRight()) {
                onDirectionalKey(context, 1);
                return true;
            } else if (keycode == KeyboardConstants.INSTANCE.getHome() || keycode == KeyboardConstants.INSTANCE.getUp()) {
                if (context.getRenderContext().isShiftDown()) {
                    if (selection == -1) selection = cursor;
                } else {
                    selection = -1;
                }
                cursor = 0;
                scrollCursorIntoView(context.getWidth());
                return true;
            } else if (keycode == KeyboardConstants.INSTANCE.getDown() || keycode == KeyboardConstants.INSTANCE.getEnd()) {
                if (context.getRenderContext().isShiftDown()) {
                    if (selection == -1) selection = cursor;
                } else {
                    selection = -1;
                }
                cursor = text.get().length();
                scrollCursorIntoView(context.getWidth());
                return true;
            } else if (keycode == KeyboardConstants.INSTANCE.getBackSpace()) {
                if (selection == -1) selection = skipCharacters(context.getRenderContext().isLogicalCtrlDown(), -1);
                writeText("", context.getWidth());
                return true;
            } else if (keycode == KeyboardConstants.INSTANCE.getDelete()) {
                if (selection == -1) selection = skipCharacters(context.getRenderContext().isLogicalCtrlDown(), 1);
                writeText("", context.getWidth());
                return true;
            } else if (keycode == KeyboardConstants.INSTANCE.getKeyC()) {
                if (context.getRenderContext().isLogicalCtrlDown()) {
                    IMinecraft.INSTANCE.copyToClipboard(getSelection());
                    return true;
                }
                return false;
            } else if (keycode == KeyboardConstants.INSTANCE.getKeyX()) {
                if (context.getRenderContext().isLogicalCtrlDown()) {
                    IMinecraft.INSTANCE.copyToClipboard(getSelection());
                    writeText("", context.getWidth());
                    return true;
                }
                return false;
            } else if (keycode == KeyboardConstants.INSTANCE.getKeyV()) {
                if (context.getRenderContext().isLogicalCtrlDown()) {
                    writeText(IMinecraft.INSTANCE.copyFromClipboard(), context.getWidth());
                    return true;
                }
                return false;
            } else if (keycode == KeyboardConstants.INSTANCE.getKeyA()) {
                if (context.getRenderContext().isLogicalCtrlDown()) {
                    cursor = text.get().length();
                    selection = 0;
                    scrollCursorIntoView(context.getWidth());
                    return true;
                }
                return false;
            }
            return false;
        } else if (event instanceof KeyboardEvent.CharTyped) {
            char c = ((KeyboardEvent.CharTyped) event).getChar();
            if (c < ' ' || c == 127) return false;
            if (forbiddenChars.contains(c)) return true;
            writeText(Character.toString(c), context.getWidth());
            return true;
        }
        return false;
    }

    private String getSelection() {
        if (selection == -1) return "";
        int left = Math.min(cursor, selection);
        int right = Math.max(cursor, selection);
        return safeSubString(text.get(), left, right);
    }

    @Override
    public boolean mouseEvent(MouseEvent mouseEvent, GuiImmediateContext context) {
        super.mouseEvent(mouseEvent, context);
        checkScrollOffset(context.getWidth());
        updateVisibleText(context.getWidth());
        if (mouseEvent instanceof MouseEvent.Click && ((MouseEvent.Click) mouseEvent).getMouseState()) {
            if (context.isHovered()) {
                requestFocus();
                if (!initializedCursor) {
                    initializedCursor = true;
                    cursor = Integer.MAX_VALUE;
                    validateCursor();
                    scrollCursorIntoView(context.getWidth());
                }
                return true;
            } else {
                setFocus(false);
            }
        }
        return false;
    }

    private String safeSubString(String str, int startIndex) {
        return str.substring(Math.min(startIndex, str.length()));
    }

    private String safeSubString(String str, int startIndex, int endIndex) {
        return str.substring(Math.min(startIndex, str.length()), Math.min(Math.max(startIndex, endIndex), str.length()));
    }

    public void writeText(String s, int width) {
        StringBuilder filtered = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (!forbiddenChars.contains(c)) {
                filtered.append(c);
            }
        }
        String filteredString = filtered.toString();
        if (filteredString.isEmpty() && !s.isEmpty()) return;

        String current = text.get();
        if (selection == -1) {
            text.set(safeSubString(current, 0, cursor) + filteredString + safeSubString(current, cursor));
            cursor += filteredString.length();
        } else {
            int left = Math.min(cursor, selection);
            int right = Math.max(cursor, selection);
            text.set(safeSubString(current, 0, left) + filteredString + safeSubString(current, right));
            cursor = left + filteredString.length();
            selection = -1;
        }
        scrollCursorIntoView(width);
    }

    public void onDirectionalKey(GuiImmediateContext context, int i) {
        if (context.getRenderContext().isShiftDown()) {
            if (selection == -1) selection = cursor;
            cursor = skipCharacters(context.getRenderContext().isLogicalCtrlDown(), i);
        } else {
            if (selection != -1) {
                cursor = i < 0 ? Math.min(cursor, selection) : Math.max(cursor, selection);
                selection = -1;
            } else {
                cursor = skipCharacters(context.getRenderContext().isLogicalCtrlDown(), i);
            }
        }
        scrollCursorIntoView(context.getWidth());
    }

    private int skipCharacters(boolean skipWords, int i) {
        if (i != -1 && i != 1) return cursor;
        int position = cursor;
        while (true) {
            position += i;
            if (position < 0) return 0;
            if (position > text.get().length()) return text.get().length();
            if (!skipWords) return position;
            if (position < text.get().length() && Character.isWhitespace(text.get().charAt(position))) return position;
        }
    }

    public void setShouldExpandToFit(boolean shouldExpandToFit) {
        this.shouldExpandToFit = shouldExpandToFit;
    }
}
