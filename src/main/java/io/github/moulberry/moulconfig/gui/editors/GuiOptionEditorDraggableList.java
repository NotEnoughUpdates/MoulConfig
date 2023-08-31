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

package io.github.moulberry.moulconfig.gui.editors;

import io.github.moulberry.moulconfig.GuiTextures;
import io.github.moulberry.moulconfig.gui.GuiOptionEditor;
import io.github.moulberry.moulconfig.internal.LerpUtils;
import io.github.moulberry.moulconfig.internal.TextRenderUtils;
import io.github.moulberry.moulconfig.processor.ProcessedOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Formatting;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class GuiOptionEditorDraggableList extends GuiOptionEditor {
    private Map<Object, String> exampleText = new HashMap<>();
    private boolean enableDeleting;
    private List<Object> activeText;
    private final boolean requireNonEmpty;
    private Object currentDragging = null;
    private int dragStartIndex = -1;

    private long trashHoverTime = -1;

    private int dragOffsetX = -1;
    private int dragOffsetY = -1;
    private boolean dropdownOpen = false;
    private Enum<?>[] enumConstants;
    private String exampleTextConcat;

    public GuiOptionEditorDraggableList(
            ProcessedOption option,
            String[] exampleText,
            boolean enableDeleting
    ) {
        this(option, exampleText, enableDeleting, false);
    }

    public GuiOptionEditorDraggableList(
            ProcessedOption option,
            String[] exampleText,
            boolean enableDeleting,
            boolean requireNonEmpty
    ) {
        super(option);

        this.enableDeleting = enableDeleting;
        this.activeText = (List) option.get();
        this.requireNonEmpty = requireNonEmpty;

        Type elementType = ((ParameterizedType) option.getType()).getActualTypeArguments()[0];

        if (Enum.class.isAssignableFrom((Class<?>) elementType)) {
            Class<? extends Enum<?>> enumType = (Class<? extends Enum<?>>) ((ParameterizedType) option.getType()).getActualTypeArguments()[0];
            enumConstants = enumType.getEnumConstants();
            for (int i = 0; i < enumConstants.length; i++) {
                this.exampleText.put(enumConstants[i], enumConstants[i].toString());
            }
        } else {
            for (int i = 0; i < exampleText.length; i++) {
                this.exampleText.put(i, exampleText[i]);
            }
        }
    }

    private void saveChanges() {
        option.explicitNotifyChange();
    }

    @Override
    public int getHeight() {
        int height = super.getHeight() + 13;

        for (Object object : activeText) {
            String str = exampleText.get(object);
            height += 10 * str.split("\n").length;
        }

        return height;
    }

    public boolean canDeleteRightNow() {
        return enableDeleting && (activeText.size() > 1 || !requireNonEmpty);
    }

    @Override
    public void render(DrawContext context, int x, int y, int width) {
        super.render(context, x, y, width);
        int height = getHeight();

        context.drawTexture(GuiTextures.BUTTON, x + width / 6 - 24, y + 45 - 7 - 14, 0, 0, 48, 16, 48, 17);

        TextRenderUtils.drawStringCenteredScaledMaxWidth("Add", context,
                x + (float) width / 6, y + 45 - 7 - 6,
                false, 44, 0xFF303030
        );

        long currentTime = System.currentTimeMillis();
        if (trashHoverTime < 0) {
            float greenBlue = LerpUtils.clampZeroOne((currentTime + trashHoverTime) / 250f);
            context.setShaderColor(1, greenBlue, greenBlue, 1);
            //GL11.glColor4f(1, greenBlue, greenBlue, 1);
        } else {
            float greenBlue = LerpUtils.clampZeroOne((250 + trashHoverTime - currentTime) / 250f);
            context.setShaderColor(1, greenBlue, greenBlue, 1);
            //GL11.glColor4f(1, greenBlue, greenBlue, 1);
        }

        if (canDeleteRightNow()) {
            context.drawTexture(GuiTextures.DELETE, x + width / 6 + 27, y + 45 - 7 - 13, 0, 0, 11, 14, 11, 14);
        }

        context.setShaderColor(1, 1, 1, 1);

        context.fill(x + 5, y + 45, x + width - 5, y + height - 5, 0xffdddddd);
        context.fill(x + 6, y + 46, x + width - 6, y + height - 6, 0xff000000);

        int i = 0;
        int yOff = 0;
        for (Object indexObject : activeText) {
            String str = exampleText.get(indexObject);

            String[] multilines = str.split("\n");

            int ySize = multilines.length * 10;

            if (i++ != dragStartIndex) {
                for (int multilineIndex = 0; multilineIndex < multilines.length; multilineIndex++) {
                    String line = multilines[multilineIndex];
                    TextRenderUtils.drawStringScaledMaxWidth(line + Formatting.RESET, context,
                            x + 20, y + 50 + yOff + multilineIndex * 10, true, width - 20, 0xffffffff
                    );
                }
                context.drawText(
                        MinecraftClient.getInstance().textRenderer,
                        "≡",
                        x + 10,
                        y + 50 + yOff + ySize / 2 - 4,
                        0xffffff,
                        true
                );
            }

            yOff += ySize;
        }
    }

    float lastMouseX = 0, lastMouseY = 0;

    @Override
    public void renderOverlay(DrawContext context, int x, int y, int width) {
        super.renderOverlay(context, x, y, width);
        if (dropdownOpen) {
            List<Object> remaining = new ArrayList<>(exampleText.keySet());
            remaining.removeAll(activeText);

            int dropdownWidth = Math.min(width / 2 - 10, 150);
            int left = dragOffsetX;
            int top = dragOffsetY;

            int dropdownHeight = -1 + 12 * remaining.size();

            int main = 0xff202026;
            int outline = 0xff404046;
            context.fill(left, top, left + 1, top + dropdownHeight, outline); //Left
            context.fill(left + 1, top, left + dropdownWidth, top + 1, outline); //Top
            context.fill(left + dropdownWidth - 1, top + 1, left + dropdownWidth, top + dropdownHeight, outline); //Right
            context.fill(
                    left + 1,
                    top + dropdownHeight - 1,
                    left + dropdownWidth - 1,
                    top + dropdownHeight,
                    outline
            ); //Bottom
            context.fill(left + 1, top + 1, left + dropdownWidth - 1, top + dropdownHeight - 1, main); //Middle

            int dropdownY = -1;
            for (Object indexObject : remaining) {
                String str = exampleText.get(indexObject);
                if (str.isEmpty()) {
                    str = "<NONE>";
                }
                TextRenderUtils.drawStringScaledMaxWidth(str.replaceAll("(\n.*)+", " ..."),
                        context, left + 3, top + 3 + dropdownY, false, dropdownWidth - 6, 0xffa0a0a0
                );
                dropdownY += 12;
            }
        } else if (currentDragging != null) {
            int opacity = 0x80;
            long currentTime = System.currentTimeMillis();
            if (trashHoverTime < 0) {
                float greenBlue = LerpUtils.clampZeroOne((currentTime + trashHoverTime) / 250f);
                opacity = (int) (opacity * greenBlue);
            } else {
                float greenBlue = LerpUtils.clampZeroOne((250 + trashHoverTime - currentTime) / 250f);
                opacity = (int) (opacity * greenBlue);
            }

            if (opacity < 20) return;

            Mouse mouse = MinecraftClient.getInstance().mouse;

            int mouseX = (int) (mouse.getX() * MinecraftClient.getInstance().getWindow().getScaledWidth() / MinecraftClient.getInstance().getWindow().getWidth());
            int mouseY = (int) (mouse.getY() * MinecraftClient.getInstance().getWindow().getScaledHeight() / MinecraftClient.getInstance().getWindow().getHeight() - 1);

            String str = exampleText.get(currentDragging);

            String[] multilines = str.split("\n");

            //GL11.glEnable(GL11.GL_BLEND);
            for (int multilineIndex = 0; multilineIndex < multilines.length; multilineIndex++) {
                String line = multilines[multilineIndex];
                TextRenderUtils.drawStringScaledMaxWidth(
                        line + Formatting.RESET,
                        context,
                        dragOffsetX + mouseX + 10,
                        dragOffsetY + mouseY + multilineIndex * 10,
                        true,
                        width - 20,
                        0xffffff | (opacity << 24)
                );
            }

            int ySize = multilines.length * 10;

            context.drawText(MinecraftClient.getInstance().textRenderer, "\u2261",
                    dragOffsetX + mouseX,
                    dragOffsetY + mouseY + ySize / 2 - 4, 0xffffff, true
            );
        }
    }

    @Override
    public boolean mouseDragged(int x, int y, int width, double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (button != 0 || dropdownOpen) {
            currentDragging = null;
            dragStartIndex = -1;
            if (trashHoverTime > 0 && canDeleteRightNow()) trashHoverTime = -System.currentTimeMillis();
        } else if (currentDragging != null &&
                mouseX >= x + width / 6.0 + 27 - 3 && mouseX <= x + (double) width / 6-0 + 27 + 11 + 3 &&
                mouseY >= y + 45 - 7 - 13 - 3 && mouseY <= y + 45 - 7 - 13 + 14 + 3) {
            if (trashHoverTime < 0 && canDeleteRightNow()) trashHoverTime = System.currentTimeMillis();
        } else {
            if (trashHoverTime > 0 && canDeleteRightNow()) trashHoverTime = -System.currentTimeMillis();
        }

        if (currentDragging != null) {
            int yOff = 0;
            int i = 0;
            for (Object objectIndex : activeText) {
                if (dragOffsetY + mouseY + 4 < y + 50 + yOff + 10) {
                    activeText.remove(dragStartIndex);
                    activeText.add(i, currentDragging);
                    saveChanges();
                    dragStartIndex = i;
                    break;
                }
                yOff += 10 * exampleText.get(objectIndex).split("\n").length;
                i++;
            }
        }


        return false;
    }

    @Override
    public boolean mouseReleased(int x, int y, int width, double mouseX, double mouseY, int button) {
        if (!dropdownOpen &&
                dragStartIndex >= 0 && button == 0 &&
                mouseX >= x + width / 6.0 + 27 - 3 && mouseX <= x + width / 6.0 + 27 + 11 + 3 &&
                mouseY >= y + 45 - 7 - 13 - 3 && mouseY <= y + 45 - 7 - 13 + 14 + 3) {
            if (canDeleteRightNow()) {
                activeText.remove(dragStartIndex);
                saveChanges();
            }
            currentDragging = null;
            dragStartIndex = -1;
            return false;
        }

        currentDragging = null;
        dragStartIndex = -1;

        if (button != 0 || dropdownOpen) {
            if (trashHoverTime > 0 && canDeleteRightNow()) trashHoverTime = -System.currentTimeMillis();
        } else if (mouseX >= x + width / 6.0 + 27 - 3 && mouseX <= x + (double) width / 6-0 + 27 + 11 + 3 &&
                mouseY >= y + 45 - 7 - 13 - 3 && mouseY <= y + 45 - 7 - 13 + 14 + 3) {
            if (trashHoverTime > 0 && canDeleteRightNow()) trashHoverTime = -System.currentTimeMillis();
        }

        return false;
    }

    @Override
    public boolean mouseInput(int x, int y, int width, double mouseX, double mouseY, int button) {
        int height = getHeight();

        if (dropdownOpen) {
            List<Object> remaining = new ArrayList<>(exampleText.keySet());
            remaining.removeAll(activeText);

            int dropdownWidth = Math.min(width / 2 - 10, 150);
            int left = dragOffsetX;
            int top = dragOffsetY;

            int dropdownHeight = -1 + 12 * remaining.size();

            if (mouseX > left && mouseX < left + dropdownWidth &&
                    mouseY > top && mouseY < top + dropdownHeight) {
                int dropdownY = -1;
                for (Object objectIndex : remaining) {
                    if (mouseY < top + dropdownY + 12) {
                        activeText.add(0, objectIndex);
                        saveChanges();
                        if (remaining.size() == 1) dropdownOpen = false;
                        return true;
                    }

                    dropdownY += 12;
                }
            }

            dropdownOpen = false;
            return true;
        }

        if (activeText.size() < exampleText.size() &&
                mouseX > x + width / 6.0 - 24 && mouseX < x + width / 6.0 + 24 &&
                mouseY > y + 45 - 7 - 14 && mouseY < y + 45 - 7 + 2) {
            dropdownOpen = true;
            dragOffsetX = (int) mouseX;
            dragOffsetY = (int) mouseY;
            return true;
        }

        if (button == 0 &&
                mouseX > x + 5 && mouseX < x + width - 5 &&
                mouseY > y + 45 && mouseY < y + height - 6) {
            int yOff = 0;
            int i = 0;
            for (Object objectIndex: activeText) {
                int ySize = 10 * exampleText.get(objectIndex).split("\n").length;
                if (mouseY < y + 50 + yOff + ySize) {
                    dragOffsetX = (int) (x + 10 - mouseX);
                    dragOffsetY = (int) (y + 50 + yOff - mouseY);

                    currentDragging = objectIndex;
                    dragStartIndex = i;
                    break;
                }
                yOff += ySize;
                i++;
            }
        }

        return false;
    }

    @Override
    public boolean fulfillsSearch(String word) {
        if (exampleTextConcat == null) {
            exampleTextConcat = String.join("", exampleText.values()).toLowerCase(Locale.ROOT);
        }
        return super.fulfillsSearch(word) || exampleTextConcat.contains(word);
    }

    @Override
    public boolean keyboardInput(int keyCode, int scanCode, int modifiers) {
        return false;
    }
}
