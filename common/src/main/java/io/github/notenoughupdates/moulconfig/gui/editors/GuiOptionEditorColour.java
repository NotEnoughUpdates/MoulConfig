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

import io.github.notenoughupdates.moulconfig.ChromaColour;
import io.github.notenoughupdates.moulconfig.GuiTextures;
import io.github.notenoughupdates.moulconfig.gui.GuiComponent;
import io.github.notenoughupdates.moulconfig.gui.GuiImmediateContext;
import io.github.notenoughupdates.moulconfig.gui.MouseEvent;
import io.github.notenoughupdates.moulconfig.gui.component.ColorSelectComponent;
import io.github.notenoughupdates.moulconfig.processor.ProcessedOption;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

public class GuiOptionEditorColour extends ComponentEditor {
    private final boolean usesString;
    GuiComponent component;

    public GuiOptionEditorColour(ProcessedOption option) {
        super(option);
        Type type = option.getType();
        if (type.equals(String.class)) {
            usesString = true;
        } else if (type.equals(ChromaColour.class)) {
            usesString = false;
        } else {
            throw new IllegalArgumentException("ConfigEditorColour may only be used on a String or ChromaColour field, but is used on " + option.getDebugDeclarationLocation());
        }
        component = wrapComponent(new GuiComponent() {
            @Override
            public int getWidth() {
                return 48;
            }

            @Override
            public int getHeight() {
                return 16;
            }

            @Override
            public void render(@NotNull GuiImmediateContext context) {
                int argb = get().getEffectiveColour().getRGB();
                int r = (argb >> 16) & 0xFF;
                int g = (argb >> 8) & 0xFF;
                int b = argb & 0xFF;
                context.getRenderContext().color(r / 255f, g / 255f, b / 255f, 1);
                context.getRenderContext().bindTexture(GuiTextures.BUTTON_WHITE);
                context.getRenderContext().drawTexturedRect(0f, 0f, context.getWidth(), context.getHeight());
                context.getRenderContext().color(1, 1, 1, 1);
            }

            @Override
            public boolean mouseEvent(@NotNull MouseEvent mouseEvent, @NotNull GuiImmediateContext context) {
                if (mouseEvent instanceof MouseEvent.Click) {
                    val click = ((MouseEvent.Click) mouseEvent);
                    if (click.getMouseState() && click.getMouseButton() == 0 && context.isHovered()) {
                        ColorSelectComponent colorSelectComponent = new ColorSelectComponent(0, 0, get().toLegacyString(), newString -> set(newString), () -> {
                            closeOverlay();
                        });
                        //Clamp the Y so that the colour picker can't go off screen
                        int scaledHeight = context.getRenderContext().getMinecraft().getScaledHeight();
				        int clampedY;

				        if (context.getAbsoluteMouseY() + colorSelectComponent.getHeight() > scaledHeight) {
					        clampedY = scaledHeight - colorSelectComponent.getHeight();
				        } else {
					        clampedY = context.getAbsoluteMouseY();
				        }

                        openOverlay(colorSelectComponent, context.getAbsoluteMouseX(), clampedY);
                        return true;
                    }
                }
                return false;
            }
        });
    }

    ChromaColour get() {
        val value = option.get();
        if (usesString)
            //noinspection deprecation
            return ChromaColour.forLegacyString((String) value);
        return (ChromaColour) value;
    }

    void set(String newString) {
        if (usesString) {
            option.set(newString);
        } else {
            //noinspection deprecation
            option.set(ChromaColour.forLegacyString(newString));
        }
    }


    @Override
    public @NotNull GuiComponent getDelegate() {
        return component;
    }

}
