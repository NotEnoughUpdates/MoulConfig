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

import io.github.notenoughupdates.moulconfig.common.RenderContext;
import io.github.notenoughupdates.moulconfig.gui.GuiOptionEditor;
import io.github.notenoughupdates.moulconfig.gui.MouseEvent;
import io.github.notenoughupdates.moulconfig.internal.RenderUtils;
import io.github.notenoughupdates.moulconfig.internal.TextRenderUtils;
import io.github.notenoughupdates.moulconfig.processor.ProcessedOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;
import java.util.Locale;

public class GuiOptionEditorDropdown extends GuiOptionEditor {
    private String[] values;
    private boolean useOrdinal;
    private boolean open = false;
    private Enum<?>[] constants;
    private String valuesForSearch;

    public GuiOptionEditorDropdown(
        ProcessedOption option,
        String[] values
    ) {
        super(option);
        Class<?> clazz = (Class<?>) option.getType();
        if (Enum.class.isAssignableFrom(clazz)) {
            constants = (Enum<?>[]) (clazz).getEnumConstants();
            this.values = new String[constants.length];
            for (int i = 0; i < constants.length; i++) {
                this.values[i] = constants[i].toString();
            }
        } else {
            this.values = values;
            assert values.length > 0;
        }
        this.useOrdinal = clazz == int.class || clazz == Integer.class;
    }

    @Override
    public void render(RenderContext renderContext, int x, int y, int width) {
        super.render(renderContext, x, y, width);
        if (!open) {
            int height = getHeight();

            FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
            int dropdownWidth = Math.min(width / 3 - 10, 80);
            int left = x + width / 6 - dropdownWidth / 2;
            int top = y + height - 7 - 14;
            int selected = getSelectedIndex();
            if (selected >= values.length) selected = values.length;
            String selectedString = " - Select - ";
            if (selected >= 0 && selected < values.length) {
                selectedString = values[selected];
            }

            RenderUtils.drawFloatingRectDark(left, top, dropdownWidth, 14, false);
            TextRenderUtils.drawStringScaled(
                "▼",
                fr,
                left + dropdownWidth - 10,
                y + height - 7 - 15,
                false,
                0xffa0a0a0,
                2
            );

            TextRenderUtils.drawStringScaledMaxWidth(selectedString, fr, left + 3, top + 3, false,
                dropdownWidth - 16, 0xffa0a0a0
            );
        }
    }

    private int getSelectedIndex() {
        Object selectedObject = option.get();
        if (selectedObject == null) return -1;
        if (useOrdinal) {
            return (int) selectedObject;
        } else if (constants != null) {
            return ((Enum) selectedObject).ordinal();
        } else {
            return Arrays.asList(values).indexOf(selectedObject);
        }
    }

    @Override
    public void renderOverlay(int x, int y, int width) {
        if (open) {
            int selected = getSelectedIndex();
            String selectedString = " - Select - ";
            if (selected >= 0 && selected < values.length) {
                selectedString = values[selected];
            }

            int height = getHeight();

            FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
            int dropdownWidth = Math.min(width / 3 - 10, 80);
            int left = x + width / 6 - dropdownWidth / 2;
            int top = y + height - 7 - 14;

            int dropdownHeight = 13 + 12 * values.length;

            int main = 0xff202026;
            int blue = 0xff2355ad;

            GlStateManager.pushMatrix();
            GL11.glTranslated(0, 0, 100);
            Gui.drawRect(left, top, left + 1, top + dropdownHeight, blue); //Left
            Gui.drawRect(left + 1, top, left + dropdownWidth, top + 1, blue); //Top
            Gui.drawRect(left + dropdownWidth - 1, top + 1, left + dropdownWidth, top + dropdownHeight, blue); //Right
            Gui.drawRect(left + 1, top + dropdownHeight - 1, left + dropdownWidth - 1, top + dropdownHeight, blue); //Bottom
            Gui.drawRect(left + 1, top + 1, left + dropdownWidth - 1, top + dropdownHeight - 1, main); //Middle

            Gui.drawRect(left + 1, top + 14 - 1, left + dropdownWidth - 1, top + 14, blue); //Bar
            int dropdownY = 13;
            for (String option : values) {
                if (option.isEmpty()) {
                    option = "<NONE>";
                }
                TextRenderUtils.drawStringScaledMaxWidth(
                    option,
                    fr,
                    left + 3,
                    top + 3 + dropdownY,
                    false,
                    dropdownWidth - 6,
                    0xffa0a0a0
                );
                dropdownY += 12;
            }

            TextRenderUtils.drawStringScaled(
                "\u25B2",
                fr,
                left + dropdownWidth - 10,
                y + height - 7 - 15,
                false,
                0xffa0a0a0,
                2
            );

            TextRenderUtils.drawStringScaledMaxWidth(selectedString, fr, left + 3, top + 3, false,
                dropdownWidth - 16, 0xffa0a0a0
            );
            GlStateManager.popMatrix();
        }
    }

    @Override
    public boolean mouseInput(int x, int y, int width, int mouseX, int mouseY) {
        int height = getHeight();

        int left = x + width / 6 - 40;
        int top = y + height - 7 - 14;

        if (Mouse.getEventButtonState() && Mouse.getEventButton() == 0) {
            if (mouseX >= left && mouseX <= left + 80 &&
                mouseY >= top && mouseY <= top + 14) {
                open = !open;
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean mouseInputOverlay(int x, int y, int width, int mouseX, int mouseY, MouseEvent mouseEvent) {
        int height = getHeight();

        int left = x + width / 6 - 40;
        int top = y + height - 7 - 14;

        if (Mouse.getEventButtonState() && Mouse.getEventButton() == 0) {
            if (!(mouseX >= left && mouseX <= left + 80 &&
                mouseY >= top && mouseY <= top + 14) && open) {
                open = false;
                if (mouseX >= left && mouseX <= left + 80) {
                    int dropdownY = 13;
                    for (int ordinal = 0; ordinal < values.length; ordinal++) {
                        if (mouseY >= top + 3 + dropdownY && mouseY <= top + 3 + dropdownY + 12) {
                            int selected = ordinal;
                            if (constants != null) {
                                option.set(constants[selected]);
                            } else if (useOrdinal) {
                                option.set(selected);
                            } else {
                                option.set(values[selected]);
                            }
                            return true;
                        }
                        dropdownY += 12;
                    }
                }
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean fulfillsSearch(String word) {
        if (valuesForSearch == null) {
            valuesForSearch = String.join("", values).toLowerCase(Locale.ROOT);
        }
        return super.fulfillsSearch(word) || valuesForSearch.contains(word);
    }

    @Override
    public boolean keyboardInput() {
        return false;
    }
}
