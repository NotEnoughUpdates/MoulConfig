/*
 * Copyright (C) 2023 NotEnoughUpdates contributors
 *
 * This file is part of MoulConfig.
 *
 * MoulConfig is free software: you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * MoulConfig is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MoulConfig. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package io.github.moulberry.moulconfig.gui.elements;

import io.github.moulberry.moulconfig.internal.StringUtils;
import io.github.moulberry.moulconfig.internal.TextRenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GuiElementTextField {
    public static final int SCISSOR_TEXT = 0b10000000;
    public static final int DISABLE_BG = 0b1000000;
    public static final int SCALE_TEXT = 0b100000;
    public static final int NUM_ONLY = 0b10000;
    public static final int NO_SPACE = 0b01000;
    public static final int FORCE_CAPS = 0b00100;
    public static final int COLOUR = 0b00010;
    public static final int MULTILINE = 0b00001;

    private int searchBarYSize;
    private int searchBarXSize;
    private static final int searchBarPadding = 2;

    private int options;

    private boolean focus = false;

    private int x;
    private int y;

    private String prependText = "";
    private String masterStarUnicode = "";
    private int customTextColour = 0xffffffff;

    private final TextFieldWidget textField = new TextFieldWidget(MinecraftClient.getInstance().textRenderer,
            0, 0, 0, 0, Text.literal("")
    );

    private int customBorderColour = -1;

    public GuiElementTextField(String initialText, int options) {
        this(initialText, 100, 20, options);
    }

    public GuiElementTextField(String initialText, int sizeX, int sizeY, int options) {
        textField.setFocused(true);
        textField.setFocusUnlocked(true);
        textField.setMaxLength(999);
        textField.setText(initialText);
        this.searchBarXSize = sizeX;
        this.searchBarYSize = sizeY;
        this.options = options;
    }

    public void setMaxStringLength(int len) {
        textField.setMaxLength(len);
    }

    public void setCustomBorderColour(int colour) {
        this.customBorderColour = colour;
    }

    public void setCustomTextColour(int colour) {
        this.customTextColour = colour;
    }

    public String getText() {
        return textField.getText();
    }

    public String getTextDisplay() {
        String textNoColour = getText();
        while (true) {
            Matcher matcher = PATTERN_CONTROL_CODE.matcher(textNoColour);
            if (!matcher.find()) break;
            String code = matcher.group(1);
            textNoColour = matcher.replaceFirst("\u00B6" + code);
        }

        return textNoColour;
    }

    public void setPrependText(String text) {
        this.prependText = text;
    }

    public void setText(String text) {
        if (textField.getText() == null || !textField.getText().equals(text)) {
            textField.setText(text);
        }
    }

    public void setSize(int searchBarXSize, int searchBarYSize) {
        this.searchBarXSize = searchBarXSize;
        this.searchBarYSize = searchBarYSize;
    }

    public void setOptions(int options) {
        this.options = options;
    }

    @Override
    public String toString() {
        return textField.getText();
    }

    public void setFocus(boolean focus) {
        this.focus = focus;
        if (!focus) {
            textField.setCursor(textField.getCursor());
        }
    }

    public boolean getFocus() {
        return focus;
    }

    public int getHeight() {
        int paddingUnscaled = (int) (searchBarPadding / MinecraftClient.getInstance().getWindow().getScaleFactor());

        int numLines = org.apache.commons.lang3.StringUtils.countMatches(textField.getText(), "\n") + 1;
        int extraSize = (searchBarYSize - 8) / 2 + 8;
        int bottomTextBox = searchBarYSize + extraSize * (numLines - 1);

        return bottomTextBox + paddingUnscaled * 2;
    }

    public int getWidth() {
        int paddingUnscaled = (int) (searchBarPadding / MinecraftClient.getInstance().getWindow().getScaleFactor());

        return searchBarXSize + paddingUnscaled * 2;
    }

    private float getScaleFactor(String str) {
        return Math.min(1, (searchBarXSize - 2) / (float) MinecraftClient.getInstance().textRenderer.getWidth(str));
    }

    private boolean isScaling() {
        return (options & SCALE_TEXT) != 0;
    }

    private static final Pattern PATTERN_CONTROL_CODE = Pattern.compile("(?i)\\u00A7([^\\u00B6]|$)(?!\\u00B6)");

    public int getCursorPos(int mouseX, int mouseY) {
        int xComp = mouseX - x;
        int yComp = mouseY - y;

        int extraSize = (searchBarYSize - 8) / 2 + 8;

        String renderText = prependText + textField.getText();

        int lineNum = Math.round(((yComp - (searchBarYSize - 8) / 2)) / extraSize);

        String text = renderText;
        String textNoColour = renderText;
        if ((options & COLOUR) != 0) {
            while (true) {
                Matcher matcher = PATTERN_CONTROL_CODE.matcher(text);
                if (!matcher.find() || matcher.groupCount() < 1) break;
                String code = matcher.group(1);
                if (code.isEmpty()) {
                    text = matcher.replaceFirst("\u00A7r\u00B6");
                } else {
                    text = matcher.replaceFirst("\u00A7" + code + "\u00B6" + code);
                }
            }
        }
        while (true) {
            Matcher matcher = PATTERN_CONTROL_CODE.matcher(textNoColour);
            if (!matcher.find() || matcher.groupCount() < 1) break;
            String code = matcher.group(1);
            textNoColour = matcher.replaceFirst("\u00B6" + code);
        }

        int currentLine = 0;
        int cursorIndex = 0;
        for (; cursorIndex < textNoColour.length(); cursorIndex++) {
            if (currentLine == lineNum) break;
            if (textNoColour.charAt(cursorIndex) == '\n') {
                currentLine++;
            }
        }

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        String textNC = textNoColour.substring(0, cursorIndex);
        int colorCodes = org.apache.commons.lang3.StringUtils.countMatches(textNC, "\u00B6");
        String line = text.substring(cursorIndex + (((options & COLOUR) != 0) ? colorCodes * 2 : 0)).split("\n")[0];
        int padding = Math.min(5, searchBarXSize - strLenNoColor(line)) / 2;
        String trimmed = textRenderer.trimToWidth(line, xComp - padding);
        int linePos = strLenNoColor(trimmed);
        if (linePos != strLenNoColor(line)) {
            char after = line.charAt(linePos);
            int trimmedWidth = textRenderer.getWidth(trimmed);
            int charWidth = textRenderer.getWidth(String.valueOf(after));
            if (trimmedWidth + charWidth / 2 < xComp - padding) {
                linePos++;
            }
        }
        cursorIndex += linePos;

        int pre = StringUtils.cleanColour(prependText).length();
        if (cursorIndex < pre) {
            cursorIndex = 0;
        } else {
            cursorIndex -= pre;
        }

        return cursorIndex;
    }

    public void mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (mouseButton == 1) {
            textField.setText("");
        } else {
            textField.setCursor(getCursorPos((int) mouseX, (int) mouseY));
        }
        focus = true;
    }

    public void unfocus() {
        focus = false;
        //textField.setSelectionStart(textField.getCursor());
    }

    public int strLenNoColor(String str) {
        return StringUtils.cleanColour(str).length();
    }

    public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (focus) {
            textField.setSelectionStart(getCursorPos(mouseX, mouseY));
        }
    }

    public void keyTyped(int keyCode, int scanCode, int modifier) {
        char typedChar;
        if (Screen.hasShiftDown()) typedChar = (char) keyCode; else typedChar = Character.toLowerCase((char) keyCode);
        if (focus) {
            if ((options & MULTILINE) != 0) { //Carriage return
                TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

                Pattern patternControlCode = Pattern.compile("(?i)\\u00A7([^\\u00B6\n]|$)(?!\\u00B6)");

                String text = textField.getText();
                String textNoColour = textField.getText();
                while (true) {
                    Matcher matcher = patternControlCode.matcher(text);
                    if (!matcher.find() || matcher.groupCount() < 1) break;
                    String code = matcher.group(1);
                    if (code.isEmpty()) {
                        text = matcher.replaceFirst("\u00A7r\u00B6");
                    } else {
                        text = matcher.replaceFirst("\u00A7" + code + "\u00B6" + code);
                    }
                }
                while (true) {
                    Matcher matcher = patternControlCode.matcher(textNoColour);
                    if (!matcher.find() || matcher.groupCount() < 1) break;
                    String code = matcher.group(1);
                    textNoColour = matcher.replaceFirst("\u00B6" + code);
                }

                if (keyCode == 28) {
                    String before = textField.getText().substring(0, textField.getCursor());
                    String after = textField.getText().substring(textField.getCursor());
                    int pos = textField.getCursor();
                    textField.setText(before + "\n" + after);
                    textField.setCursor(pos + 1);
                    return;
                } else if (keyCode == 200) { //Up
                    String textNCBeforeCursor = textField.getSelectedText();
                    int colorCodes = org.apache.commons.lang3.StringUtils.countMatches(textNCBeforeCursor, "\u00B6");
                    String textBeforeCursor = text.substring(0, textNCBeforeCursor.length() + colorCodes * 2);

                    int numLinesBeforeCursor = org.apache.commons.lang3.StringUtils.countMatches(textBeforeCursor, "\n");

                    String[] split = textBeforeCursor.split("\n");
                    int textBeforeCursorWidth;
                    String lineBefore;
                    String thisLineBeforeCursor;
                    if (split.length == numLinesBeforeCursor && split.length > 0) {
                        textBeforeCursorWidth = 0;
                        lineBefore = split[split.length - 1];
                        thisLineBeforeCursor = "";
                    } else if (split.length > 1) {
                        thisLineBeforeCursor = split[split.length - 1];
                        lineBefore = split[split.length - 2];
                        textBeforeCursorWidth = textRenderer.getWidth(thisLineBeforeCursor);
                    } else {
                        return;
                    }
                    String trimmed = textRenderer
                            .trimToWidth(lineBefore, textBeforeCursorWidth);
                    int linePos = strLenNoColor(trimmed);
                    if (linePos != strLenNoColor(lineBefore)) {
                        char after = lineBefore.charAt(linePos);
                        int trimmedWidth = textRenderer.getWidth(trimmed);
                        int charWidth = textRenderer.getWidth(String.valueOf(after));
                        if (trimmedWidth + charWidth / 2 < textBeforeCursorWidth) {
                            linePos++;
                        }
                    }
                    int newPos = textNCBeforeCursor.length() - strLenNoColor(thisLineBeforeCursor)
                            - strLenNoColor(lineBefore) - 1 + linePos;

                    if (modifier == 1) {
                        //textField.setSelectionEnd(newPos);
                    } else {
                        //textField.setCursor(newPos);
                    }
                } else if (keyCode == 208) { //Down
                    String textNCBeforeCursor = textField.getSelectedText();
                    int colorCodes = org.apache.commons.lang3.StringUtils.countMatches(textNCBeforeCursor, "\u00B6");
                    String textBeforeCursor = text.substring(0, textNCBeforeCursor.length() + colorCodes * 2);

                    int numLinesBeforeCursor = org.apache.commons.lang3.StringUtils.countMatches(textBeforeCursor, "\n");

                    String[] split = textBeforeCursor.split("\n");
                    String thisLineBeforeCursor;
                    int textBeforeCursorWidth;
                    if (split.length == numLinesBeforeCursor) {
                        thisLineBeforeCursor = "";
                        textBeforeCursorWidth = 0;
                    } else if (split.length > 0) {
                        thisLineBeforeCursor = split[split.length - 1];
                        textBeforeCursorWidth = textRenderer.getWidth(thisLineBeforeCursor);
                    } else {
                        return;
                    }

                    String[] split2 = textNoColour.split("\n");
                    if (split2.length > numLinesBeforeCursor + 1) {
                        String lineAfter = split2[numLinesBeforeCursor + 1];
                        String trimmed = textRenderer.trimToWidth(lineAfter, textBeforeCursorWidth);
                        int linePos = strLenNoColor(trimmed);
                        if (linePos != strLenNoColor(lineAfter)) {
                            char after = lineAfter.charAt(linePos);
                            int trimmedWidth = textRenderer.getWidth(trimmed);
                            int charWidth = textRenderer.getWidth(String.valueOf(after));
                            if (trimmedWidth + charWidth / 2 < textBeforeCursorWidth) {
                                linePos++;
                            }
                        }
                        int newPos = textNCBeforeCursor.length() - strLenNoColor(thisLineBeforeCursor)
                                + strLenNoColor(split2[numLinesBeforeCursor]) + 1 + linePos;

                        if (modifier == 1) {
                            //textField.setSelectionEnd(newPos);
                        } else {
                            //textField.setCursor(newPos);
                        }
                    }
                }
            }

            String old = textField.getText();
            if ((options & FORCE_CAPS) != 0) typedChar = Character.toUpperCase(typedChar);
            if ((options & NO_SPACE) != 0 && typedChar == ' ') return;

            if (typedChar == '\u00B6') {
                typedChar = '\u00A7';
            }

            textField.setFocused(true);
            if (textField.keyPressed(keyCode, scanCode, modifier)) return;
            if (typedChar <= 126) textField.charTyped(typedChar, modifier);


            if ((options & COLOUR) != 0) {
                if (typedChar == '&') {
                    int pos = textField.getCursor() - 2;
                    if (pos >= 0 && pos < textField.getText().length()) {
                        if (textField.getText().charAt(pos) == '&') {
                            String before = textField.getText().substring(0, pos);
                            String after = "";
                            if (pos + 2 < textField.getText().length()) {
                                after = textField.getText().substring(pos + 2);
                            }
                            textField.setText(before + "\u00A7" + after);
                            textField.setCursor(pos + 1);
                        }
                    }
                } else if (typedChar == '*') {
                    int pos = textField.getCursor() - 2;
                    if (pos >= 0 && pos < textField.getText().length()) {
                        if (textField.getText().charAt(pos) == '*') {
                            String before = textField.getText().substring(0, pos);
                            String after = "";
                            if (pos + 2 < textField.getText().length()) {
                                after = textField.getText().substring(pos + 2);
                            }
                            textField.setText(before + "\u272A" + after);
                            textField.setCursor(pos + 1);
                        }
                    }
                } else {
                    for (int i = 0; i < 10; i++) {
                        if (typedChar == Integer.toString(i + 1).charAt(0)) {
                            int pos = textField.getCursor() - 2;
                            if (pos >= 0 && pos < textField.getText().length()) {
                                if (textField.getText().charAt(pos) == '*') {
                                    switch (i) {
                                        case 0 -> masterStarUnicode = "\u278A";
                                        case 1 -> masterStarUnicode = "\u278B";
                                        case 2 -> masterStarUnicode = "\u278C";
                                        case 3 -> masterStarUnicode = "\u278D";
                                        case 4 -> masterStarUnicode = "\u278E";
                                        case 5 -> masterStarUnicode = "\u278F";
                                        case 6 -> masterStarUnicode = "\u2790";
                                        case 7 -> masterStarUnicode = "\u2791";
                                        case 8 -> masterStarUnicode = "\u2792";
                                        case 9 -> masterStarUnicode = "\u2793";
                                    }
                                    String before = textField.getText().substring(0, pos);
                                    String after = "";
                                    if (pos + 2 < textField.getText().length()) {
                                        after = textField.getText().substring(pos + 2);
                                    }
                                    textField.setText(before + masterStarUnicode + after);
                                    textField.setCursor(pos + 1);
                                }
                            }
                        }
                    }
                }
            }

            if ((options & NUM_ONLY) != 0 && textField.getText().matches("[^0-9.]")) textField.setText(old);
        }
    }

    public void render(DrawContext context, int x, int y) {
        this.x = x;
        this.y = y;
        drawTextbox(context, x, y, searchBarXSize, searchBarYSize, searchBarPadding, textField, focus);
    }

    private void drawTextbox(
            DrawContext context,
            int x, int y, int searchBarXSize, int searchBarYSize, int searchBarPadding,
            TextFieldWidget textField, boolean focus
    ) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        String renderText = prependText + textField.getText();

        //GL11.glDisable(GL11.GL_LIGHTING); //TODO check if needed

        /*
         * Search bar
         */
        int paddingUnscaled = (int) (searchBarPadding / MinecraftClient.getInstance().getWindow().getScaleFactor());
        if (paddingUnscaled < 1) paddingUnscaled = 1;

        int numLines = org.apache.commons.lang3.StringUtils.countMatches(renderText, "\n") + 1;
        int extraSize = (searchBarYSize - 8) / 2 + 8;
        int bottomTextBox = y + searchBarYSize + extraSize * (numLines - 1);

        int borderColour = focus ? Color.GREEN.getRGB() : Color.WHITE.getRGB();
        if (customBorderColour != -1) {
            borderColour = customBorderColour;
        }
        if ((options & DISABLE_BG) == 0) {
            //bar background
            context.fill(x - paddingUnscaled,
                    y - paddingUnscaled,
                    x + searchBarXSize + paddingUnscaled,
                    bottomTextBox + paddingUnscaled, borderColour
            );
            context.fill(x,
                    y,
                    x + searchBarXSize,
                    bottomTextBox, Color.BLACK.getRGB()
            );
        }

        //bar text
        String text = renderText;
        String textNoColor = renderText;
        if ((options & COLOUR) != 0) {
            while (true) {
                Matcher matcher = PATTERN_CONTROL_CODE.matcher(text);
                if (!matcher.find() || matcher.groupCount() < 1) break;
                String code = matcher.group(1);
                if (code.isEmpty()) {
                    text = matcher.replaceFirst("\u00A7r\u00B6");
                } else {
                    text = matcher.replaceFirst("\u00A7" + code + "\u00B6" + code);
                }
            }
        }
        while (true) {
            Matcher matcher = PATTERN_CONTROL_CODE.matcher(textNoColor);
            if (!matcher.find() || matcher.groupCount() < 1) break;
            String code = matcher.group(1);
            textNoColor = matcher.replaceFirst("\u00B6" + code);
        }

        int xStartOffset = 5;
        float scale = 1;
        String[] texts = text.split("\n");
        for (int yOffI = 0; yOffI < texts.length; yOffI++) {
            int yOff = yOffI * extraSize;

            if (isScaling() && textRenderer.getWidth(texts[yOffI]) > searchBarXSize - 10) {
                scale = (searchBarXSize - 2) / (float) textRenderer.getWidth(texts[yOffI]);
                if (scale > 1) scale = 1;
                float newLen = textRenderer.getWidth(texts[yOffI]) * scale;
                xStartOffset = (int) ((searchBarXSize - newLen) / 2f);

                TextRenderUtils.drawStringCenteredScaledMaxWidth(
                        StringUtils.chromaStringByColourCode(texts[yOffI]),
                        context,
                        x + searchBarXSize / 2f,
                        y + searchBarYSize / 2f + yOff,
                        false,
                        searchBarXSize - 2,
                        customTextColour
                );
            } else {
                if ((options & SCISSOR_TEXT) != 0) {
                    context.enableScissor(x + 5, 0, x + searchBarXSize, context.getScaledWindowHeight());
                    context.drawText(textRenderer, StringUtils.chromaStringByColourCode(texts[yOffI]), x + 5,
                            y + (searchBarYSize - 8) / 2 + yOff, customTextColour, true
                    );
                    context.disableScissor();
                } else {
                    String toRender = textRenderer.trimToWidth(StringUtils.chromaStringByColourCode(
                            texts[yOffI]), searchBarXSize - 10);
                    context.drawText(textRenderer, toRender, x + 5,
                            y + (searchBarYSize - 8) / 2 + yOff, customTextColour, false
                    );
                }

            }
        }

        if (focus && System.currentTimeMillis() % 1000 > 500) {
            String textNCBeforeCursor = textNoColor.substring(0, textField.getCursor() + prependText.length());
            int colorCodes = org.apache.commons.lang3.StringUtils.countMatches(textNCBeforeCursor, "\u00B6");
            String textBeforeCursor = text.substring(
                    0,
                    Math.min(
                            text.length(),
                            textField.getCursor() + prependText.length() + (((options & COLOUR) != 0) ? colorCodes * 2 : 0)
                    )
            );

            int numLinesBeforeCursor = org.apache.commons.lang3.StringUtils.countMatches(textBeforeCursor, "\n");
            int yOff = numLinesBeforeCursor * extraSize;

            String[] split = textBeforeCursor.split("\n");
            int textBeforeCursorWidth;
            if (split.length <= numLinesBeforeCursor || split.length == 0) {
                textBeforeCursorWidth = 0;
            } else {
                textBeforeCursorWidth = (int) (textRenderer.getWidth(split[split.length -
                        1]) * scale);
            }
            context.fill(x + xStartOffset + textBeforeCursorWidth,
                    y + (searchBarYSize - 8) / 2 - 1 + yOff,
                    x + xStartOffset + textBeforeCursorWidth + 1,
                    y + (searchBarYSize - 8) / 2 + 9 + yOff, Color.WHITE.getRGB()
            );
        }

        String selectedText = textField.getSelectedText();
        if (!selectedText.isEmpty()) {
            int leftIndex = Math.min(
                    textField.getCursor() + prependText.length(),
                    textField.getCursor() + textField.getSelectedText().length() + prependText.length()
            );
            int rightIndex = Math.max( // TODO might cause problems with left selected text
                    textField.getCursor() + prependText.length(),
                    textField.getCursor() + textField.getSelectedText().length() + prependText.length()
            );

            float texX = 0;
            int texY = 0;
            boolean sectionSignPrev = false;
            boolean ignoreNext = false;
            boolean bold = false;
            for (int i = 0; i < textNoColor.length(); i++) {
                if (ignoreNext) {
                    ignoreNext = false;
                    continue;
                }

                char c = textNoColor.charAt(i);
                if (sectionSignPrev) {
                    if (c != 'k' && c != 'K'
                            && c != 'm' && c != 'M'
                            && c != 'n' && c != 'N'
                            && c != 'o' && c != 'O') {
                        bold = c == 'l' || c == 'L';
                    }
                    sectionSignPrev = false;
                    if (i < prependText.length()) continue;
                }
                if (c == '\u00B6') {
                    sectionSignPrev = true;
                    if (i < prependText.length()) continue;
                }

                if (c == '\n') {
                    if (i >= leftIndex && i < rightIndex) {
                        context.fill(x + xStartOffset + (int) texX,
                                y + (searchBarYSize - 8) / 2 - 1 + texY,
                                x + xStartOffset + (int) texX + 3,
                                y + (searchBarYSize - 8) / 2 + 9 + texY, Color.LIGHT_GRAY.getRGB()
                        );
                    }

                    texX = 0;
                    texY += extraSize;
                    continue;
                }

                int len = textRenderer.getWidth(String.valueOf(c));
                if (bold) len++;
                if (i >= leftIndex && i < rightIndex) {
                    context.fill(x + xStartOffset + (int) texX,
                            y + (searchBarYSize - 8) / 2 - 1 + texY,
                            x + xStartOffset + (int) (texX + len * scale),
                            y + (searchBarYSize - 8) / 2 + 9 + texY, Color.LIGHT_GRAY.getRGB()
                    );

                    TextRenderUtils.drawStringScaled(String.valueOf(c), context,
                            x + xStartOffset + texX,
                            y + searchBarYSize / 2f - scale * 8 / 2f + texY, false, Color.BLACK.getRGB(), scale
                    );
                    if (bold) {
                        TextRenderUtils.drawStringScaled(String.valueOf(c), context,
                                x + xStartOffset + texX + 1,
                                y + searchBarYSize / 2f - scale * 8 / 2f + texY, false, Color.BLACK.getRGB(), scale
                        );
                    }
                }

                texX += len * scale;
            }
        }
    }
}
