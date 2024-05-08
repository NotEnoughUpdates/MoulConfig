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

package io.github.notenoughupdates.moulconfig.gui.editors;

import io.github.notenoughupdates.moulconfig.GuiTextures;
import io.github.notenoughupdates.moulconfig.common.IMinecraft;
import io.github.notenoughupdates.moulconfig.common.RenderContext;
import io.github.notenoughupdates.moulconfig.gui.GuiOptionEditor;
import io.github.notenoughupdates.moulconfig.gui.KeyboardEvent;
import io.github.notenoughupdates.moulconfig.gui.MouseEvent;
import io.github.notenoughupdates.moulconfig.internal.LerpUtils;
import io.github.notenoughupdates.moulconfig.internal.RenderUtils;
import io.github.notenoughupdates.moulconfig.internal.TextRenderUtils;
import io.github.notenoughupdates.moulconfig.internal.Warnings;
import io.github.notenoughupdates.moulconfig.processor.ProcessedOption;
import kotlin.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

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

    private Pair<Integer, Integer> lastMousePosition = null;

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

    private String getExampleText(Object forObject) {
        String str = exampleText.get(forObject);
        if (str == null) {
            str = "<unknown " + forObject + ">";
            Warnings.warnOnce("Could not find draggable list object for " + forObject + " on option " + option.field, forObject, option);
        }
        return str;
    }

    @Override
    public int getHeight() {
        int height = super.getHeight() + 13;

        for (Object object : activeText) {
            String str = getExampleText(object);
            height += 10 * str.split("\n").length;
        }

        return height;
    }

    public boolean canDeleteRightNow() {
        return enableDeleting && (activeText.size() > 1 || !requireNonEmpty);
    }

    @Override
    public void render(RenderContext renderContext, int x, int y, int width) {
        super.render(renderContext, x, y, width);
        int height = getHeight();

        GlStateManager.color(1, 1, 1, 1);
        IMinecraft.instance.bindTexture(GuiTextures.BUTTON);
        RenderUtils.drawTexturedRect(x + width / 6 - 24, y + 45 - 7 - 14, 48, 16);

        TextRenderUtils.drawStringCenteredScaledMaxWidth("Add", Minecraft.getMinecraft().fontRendererObj,
            x + width / 6, y + 45 - 7 - 6,
            false, 44, 0xFF303030
        );

        long currentTime = System.currentTimeMillis();
        if (trashHoverTime < 0) {
            float greenBlue = LerpUtils.clampZeroOne((currentTime + trashHoverTime) / 250f);
            GlStateManager.color(1, greenBlue, greenBlue, 1);
        } else {
            float greenBlue = LerpUtils.clampZeroOne((250 + trashHoverTime - currentTime) / 250f);
            GlStateManager.color(1, greenBlue, greenBlue, 1);
        }

        if (canDeleteRightNow()) {
            int deleteX = x + width / 6 + 27;
            int deleteY = y + 45 - 7 - 13;
            IMinecraft.instance.bindTexture(GuiTextures.DELETE);
            RenderUtils.drawTexturedRect(deleteX, deleteY, 11, 14, GL11.GL_NEAREST);
            // TODO: make use of the mouseX and mouseY from the context when switching this to a proper multi-version component
            if (lastMousePosition != null && currentDragging == null &&
                lastMousePosition.getFirst() >= deleteX && lastMousePosition.getFirst() < deleteX + 11 &&
                lastMousePosition.getSecond() >= deleteY && lastMousePosition.getSecond() < deleteY + 14) {
                renderContext.scheduleDrawTooltip(Collections.singletonList(
                    "§cDelete Item"
                ));
            }
        }

        Gui.drawRect(x + 5, y + 45, x + width - 5, y + height - 5, 0xffdddddd);
        Gui.drawRect(x + 6, y + 46, x + width - 6, y + height - 6, 0xff000000);

        int i = 0;
        int yOff = 0;
        for (Object indexObject : activeText) {
            String str = getExampleText(indexObject);

            String[] multilines = str.split("\n");

            int ySize = multilines.length * 10;

            if (i++ != dragStartIndex) {
                for (int multilineIndex = 0; multilineIndex < multilines.length; multilineIndex++) {
                    String line = multilines[multilineIndex];
                    TextRenderUtils.drawStringScaledMaxWidth(line + EnumChatFormatting.RESET, Minecraft.getMinecraft().fontRendererObj,
                        x + 20, y + 50 + yOff + multilineIndex * 10, true, width - 20, 0xffffffff
                    );
                }
                Minecraft.getMinecraft().fontRendererObj.drawString(
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

    @Override
    public void renderOverlay(int x, int y, int width) {
        super.renderOverlay(x, y, width);
        if (dropdownOpen) {
            List<Object> remaining = new ArrayList<>(exampleText.keySet());
            remaining.removeAll(activeText);

            FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
            int dropdownWidth = Math.min(width / 2 - 10, 150);
            int left = dragOffsetX;
            int top = dragOffsetY;

            int dropdownHeight = -1 + 12 * remaining.size();

            int main = 0xff202026;
            int outline = 0xff404046;
            Gui.drawRect(left, top, left + 1, top + dropdownHeight, outline); //Left
            Gui.drawRect(left + 1, top, left + dropdownWidth, top + 1, outline); //Top
            Gui.drawRect(left + dropdownWidth - 1, top + 1, left + dropdownWidth, top + dropdownHeight, outline); //Right
            Gui.drawRect(
                left + 1,
                top + dropdownHeight - 1,
                left + dropdownWidth - 1,
                top + dropdownHeight,
                outline
            ); //Bottom
            Gui.drawRect(left + 1, top + 1, left + dropdownWidth - 1, top + dropdownHeight - 1, main); //Middle

            int dropdownY = -1;
            for (Object indexObject : remaining) {
                String str = getExampleText(indexObject);
                if (str.isEmpty()) {
                    str = "<NONE>";
                }
                TextRenderUtils.drawStringScaledMaxWidth(str.replaceAll("(\n.*)+", " ..."),
                    fr, left + 3, top + 3 + dropdownY, false, dropdownWidth - 6, 0xffa0a0a0
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

            ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
            int mouseX = Mouse.getX() * scaledResolution.getScaledWidth() / Minecraft.getMinecraft().displayWidth;
            int mouseY = scaledResolution.getScaledHeight() -
                Mouse.getY() * scaledResolution.getScaledHeight() / Minecraft.getMinecraft().displayHeight - 1;

            String str = getExampleText(currentDragging);
            String[] multilines = str.split("\n");

            GlStateManager.enableBlend();
            for (int multilineIndex = 0; multilineIndex < multilines.length; multilineIndex++) {
                String line = multilines[multilineIndex];
                TextRenderUtils.drawStringScaledMaxWidth(
                    line + EnumChatFormatting.RESET,
                    Minecraft.getMinecraft().fontRendererObj,
                    dragOffsetX + mouseX + 10,
                    dragOffsetY + mouseY + multilineIndex * 10,
                    true,
                    width - 20,
                    0xffffff | (opacity << 24)
                );
            }

            int ySize = multilines.length * 10;

            Minecraft.getMinecraft().fontRendererObj.drawString("\u2261",
                dragOffsetX + mouseX,
                dragOffsetY + mouseY + ySize / 2 - 4, 0xffffff, true
            );
        }
    }

    @Override
    public boolean mouseInput(int x, int y, int width, int mouseX, int mouseY) {
        lastMousePosition = new Pair<>(mouseX, mouseY);
        if (!Mouse.getEventButtonState() && !dropdownOpen &&
            dragStartIndex >= 0 && Mouse.getEventButton() == 0 &&
            mouseX >= x + width / 6 + 27 - 3 && mouseX <= x + width / 6 + 27 + 11 + 3 &&
            mouseY >= y + 45 - 7 - 13 - 3 && mouseY <= y + 45 - 7 - 13 + 14 + 3) {
            if (canDeleteRightNow()) {
                activeText.remove(dragStartIndex);
                saveChanges();
            }
            currentDragging = null;
            dragStartIndex = -1;
            return false;
        }

        if (!Mouse.isButtonDown(0) || dropdownOpen) {
            currentDragging = null;
            dragStartIndex = -1;
            if (trashHoverTime > 0 && canDeleteRightNow()) trashHoverTime = -System.currentTimeMillis();
        } else if (currentDragging != null &&
            mouseX >= x + width / 6 + 27 - 3 && mouseX <= x + width / 6 + 27 + 11 + 3 &&
            mouseY >= y + 45 - 7 - 13 - 3 && mouseY <= y + 45 - 7 - 13 + 14 + 3) {
            if (trashHoverTime < 0 && canDeleteRightNow()) trashHoverTime = System.currentTimeMillis();
        } else if (!canDeleteRightNow()){
            trashHoverTime = Long.MAX_VALUE;
        } else if (trashHoverTime > 0) {
            trashHoverTime = -System.currentTimeMillis();
        }

        if (Mouse.getEventButtonState()) {
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
                mouseX > x + width / 6 - 24 && mouseX < x + width / 6 + 24 &&
                mouseY > y + 45 - 7 - 14 && mouseY < y + 45 - 7 + 2) {
                dropdownOpen = true;
                dragOffsetX = mouseX;
                dragOffsetY = mouseY;
                return true;
            }

            if (Mouse.getEventButton() == 0 &&
                mouseX > x + 5 && mouseX < x + width - 5 &&
                mouseY > y + 45 && mouseY < y + height - 6) {
                int yOff = 0;
                int i = 0;
                for (Object objectIndex : activeText) {
                    String str = getExampleText(objectIndex);
                    int ySize = 10 * str.split("\n").length;
                    if (mouseY < y + 50 + yOff + ySize) {
                        dragOffsetX = x + 10 - mouseX;
                        dragOffsetY = y + 50 + yOff - mouseY;

                        currentDragging = objectIndex;
                        dragStartIndex = i;
                        break;
                    }
                    yOff += ySize;
                    i++;
                }
            }
        } else if (Mouse.getEventButton() == -1 && currentDragging != null) {
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
                String str = getExampleText(objectIndex);
                yOff += 10 * str.split("\n").length;
                i++;
            }
        }

        return false;
    }

    @Override
    public boolean mouseInput(int x, int y, int width, int mouseX, int mouseY, MouseEvent mouseEvent) {
        if (mouseEvent instanceof MouseEvent.Scroll) {
            this.dropdownOpen = false;
            return false;
        }
        return super.mouseInput(x, y, width, mouseX, mouseY, mouseEvent);
    }

    @Override
    public boolean fulfillsSearch(String word) {
        if (exampleTextConcat == null) {
            exampleTextConcat = String.join("", exampleText.values()).toLowerCase(Locale.ROOT);
        }
        return super.fulfillsSearch(word) || exampleTextConcat.contains(word);
    }

    @Override
    public boolean keyboardInput(KeyboardEvent event) {
        if (event instanceof KeyboardEvent.KeyPressed) {
            int key = ((KeyboardEvent.KeyPressed) event).getKeycode();
            if (key == Keyboard.KEY_UP || key == Keyboard.KEY_DOWN) {
                dropdownOpen = false;
            }
        }
        return super.keyboardInput(event);
    }
}
