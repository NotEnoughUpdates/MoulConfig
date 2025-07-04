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

import io.github.notenoughupdates.moulconfig.common.IFontRenderer;
import io.github.notenoughupdates.moulconfig.common.IMinecraft;
import io.github.notenoughupdates.moulconfig.gui.GuiComponent;
import io.github.notenoughupdates.moulconfig.gui.GuiImmediateContext;
import io.github.notenoughupdates.moulconfig.gui.MouseEvent;
import io.github.notenoughupdates.moulconfig.processor.ProcessedOption;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Locale;

public class GuiOptionEditorDropdown extends ComponentEditor {
    private String[] values;
    private boolean useOrdinal;
    private Enum<?>[] constants;
    private String valuesForSearch;

    public GuiOptionEditorDropdown(ProcessedOption option, String[] values) {
        this(option, values, false);
    }

    public GuiOptionEditorDropdown(
        ProcessedOption option,
        String[] values,
        boolean forceGivenValues
    ) {
        super(option);
        Class<?> clazz = (Class<?>) option.getType();
        if (Enum.class.isAssignableFrom(clazz)) {
            constants = (Enum<?>[]) (clazz).getEnumConstants();
            if (forceGivenValues) {
                assert values.length == constants.length;
                this.values = values;
            } else {
                this.values = new String[constants.length];
                for (int i = 0; i < constants.length; i++) {
                    this.values[i] = constants[i].toString();;
                }
            }
        } else {
            this.values = values;
            assert values.length > 0;
        }
        this.useOrdinal = clazz == int.class || clazz == Integer.class;
    }
    int componentWidth = 0;
    private GuiComponent dropdownOverlay = new GuiComponent() {
        @Override
        public int getWidth() {
            return componentWidth;
        }

        @Override
        public int getHeight() {
            return 13 + 12 * values.length;
        }

        @Override
        public boolean mouseEvent(@NotNull MouseEvent mouseEvent, @NotNull GuiImmediateContext context) {
            if (mouseEvent instanceof MouseEvent.Click) {
                MouseEvent.Click click = ((MouseEvent.Click) mouseEvent);
                if (click.getMouseState()) {
                    closeOverlay();
                }
                if (click.getMouseState() && click.getMouseButton() == 0 && context.isHovered()) {
                    int top = 0;
                    int mouseY = context.getMouseY();
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
                        }
                        dropdownY += 12;
                    }
                }
                return true;
            }
            return super.mouseEvent(mouseEvent, context);
        }

        @Override
        public void render(@NotNull GuiImmediateContext context) {
            int selected = getSelectedIndex();
            String selectedString = " - Select - ";
            if (selected >= 0 && selected < values.length) {
                selectedString = values[selected];
            }

            int dropdownHeight = context.getHeight();
            int dropdownWidth = context.getWidth();

            int main = 0xff202026;
            int outlineColour = 0xffffffff;

            context.getRenderContext().pushMatrix();
            // TODO: do we even need that? (given the render order) context.getRenderContext().translate(0, 0, 100);
            int left = 0;
            int top = 0;
            context.getRenderContext().drawColoredRect(left, top, left + 1, top + dropdownHeight, outlineColour); //Left
            context.getRenderContext().drawColoredRect(left + 1, top, left + dropdownWidth, top + 1, outlineColour); //Top
            context.getRenderContext().drawColoredRect(left + dropdownWidth - 1, top + 1, left + dropdownWidth, top + dropdownHeight, outlineColour); //Right
            context.getRenderContext().drawColoredRect(left + 1, top + dropdownHeight - 1, left + dropdownWidth - 1, top + dropdownHeight, outlineColour); //Bottom
            context.getRenderContext().drawColoredRect(left + 1, top + 1, left + dropdownWidth - 1, top + dropdownHeight - 1, main); //Middle

            context.getRenderContext().drawColoredRect(left + 1, top + 14 - 1, left + dropdownWidth - 1, top + 14, outlineColour); //Bar
            int dropdownY = 13;
            IFontRenderer fr = IMinecraft.instance.getDefaultFontRenderer();
            for (String option : values) {
                if (option.isEmpty()) {
                    option = "<NONE>";
                }
                context.getRenderContext().drawStringScaledMaxWidth(
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
            context.getRenderContext().drawStringScaledMaxWidth(
                selectedString, fr, left + 3, top + 3, false,
                dropdownWidth - 16, 0xffa0a0a0
            );
            context.getRenderContext().drawOpenCloseTriangle(
                false, context.getWidth() - 10, 4, 6, 6, -1
            );
            context.getRenderContext().popMatrix();
        }
    };
    private GuiComponent component = wrapComponent(new GuiComponent() {
        @Override
        public int getWidth() {
            return 80;
        }

        @Override
        public int getHeight() {
            return 14;
        }

        @Override
        public boolean mouseEvent(@NotNull MouseEvent mouseEvent, @NotNull GuiImmediateContext context) {
            if (mouseEvent instanceof MouseEvent.Click && ((MouseEvent.Click) mouseEvent).getMouseState() && context.isHovered()) {
                if (!isOverlayOpen()) {
                    componentWidth = context.getWidth();
                    //Clamp the Y so that the dropdown can't go off the screen
                    int scaledHeight = context.getRenderContext().getMinecraft().getScaledHeight();
                    int clampedY;

                    if (context.getRenderOffsetY() + dropdownOverlay.getHeight() > scaledHeight) {
                        clampedY = scaledHeight - dropdownOverlay.getHeight();
                    } else {
                        clampedY = context.getRenderOffsetY();
                    }

                    openOverlay(dropdownOverlay, context.getRenderOffsetX(), clampedY);
                }
                return true;
            }
            return super.mouseEvent(mouseEvent, context);
        }

        @Override
        public void render(@NotNull GuiImmediateContext context) {
            int dropdownWidth = context.getWidth();
            int selected = getSelectedIndex();
            if (selected >= values.length) selected = values.length;
            String selectedString = " - Select - ";
            if (selected >= 0 && selected < values.length) {
                selectedString = values[selected];
            }

            context.getRenderContext().drawDarkRect(
                0, 0, dropdownWidth, context.getHeight(), false
            );
            context.getRenderContext().drawOpenCloseTriangle(
                true, context.getWidth() - 10, 4, 6, 6, -1
            );
            context.getRenderContext().drawStringScaledMaxWidth(
                selectedString, IMinecraft.instance.getDefaultFontRenderer(),
                3, 3, false, context.getWidth() - 16, 0xffa0a0a0
            );
        }
    });

    @Override
    public @NotNull GuiComponent getDelegate() {
        return component;
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
    public boolean fulfillsSearch(String word) {
        if (valuesForSearch == null) {
            valuesForSearch = String.join("", values).toLowerCase(Locale.ROOT);
        }
        return super.fulfillsSearch(word) || valuesForSearch.contains(word);
    }

}
