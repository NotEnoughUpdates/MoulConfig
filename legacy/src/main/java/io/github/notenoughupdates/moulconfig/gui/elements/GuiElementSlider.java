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

package io.github.notenoughupdates.moulconfig.gui.elements;

import io.github.notenoughupdates.moulconfig.GuiTextures;
import io.github.notenoughupdates.moulconfig.common.IMinecraft;
import io.github.notenoughupdates.moulconfig.common.RenderContext;
import io.github.notenoughupdates.moulconfig.gui.GuiElement;
import io.github.notenoughupdates.moulconfig.gui.KeyboardEvent;
import io.github.notenoughupdates.moulconfig.gui.MouseEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;

import java.util.function.Consumer;

public class GuiElementSlider extends GuiElement {
    private static final int HEIGHT = 16;
    private final float minValue;
    private final float maxValue;
    private final float minStep;
    private float value;
    private final Consumer<Float> setCallback;
    public int x;
    public int y;
    public int width;
    private boolean clicked = false;

    public GuiElementSlider(
        int x, int y, int width, float minValue, float maxValue, float minStep,
        float value, Consumer<Float> setCallback
    ) {
        if (minStep < 0) minStep = 0.01f;

        this.x = x;
        this.y = y;
        this.width = width;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.minStep = minStep;
        this.value = value;
        this.setCallback = setCallback;
    }

    public void setValue(float value) {
        this.value = value;
    }

    @Override
    public void render() {
        final ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        int mouseX = Mouse.getX() * scaledResolution.getScaledWidth() / Minecraft.getMinecraft().displayWidth;

        float value = this.value;
        if (clicked) {
            value = (mouseX - x) * (maxValue - minValue) / width + minValue;
            value = Math.max(minValue, Math.min(maxValue, value));
            value = Math.round(value / minStep) * minStep;
        }

        float sliderAmount = Math.max(0, Math.min(1, (value - minValue) / (maxValue - minValue)));
        int sliderAmountI = (int) (width * sliderAmount);
        RenderContext renderContext = IMinecraft.instance.provideTopLevelRenderContext();

        renderContext.drawTexturedRect(GuiTextures.SLIDER_ON_CAP, x, y, 4, HEIGHT);
        renderContext.drawTexturedRect(GuiTextures.SLIDER_OFF_CAP, x + width - 4, y, 4, HEIGHT);

        if (sliderAmountI > 5) {
            renderContext.drawTexturedRect(GuiTextures.SLIDER_ON_SEGMENT, x + 4, y, sliderAmountI - 4, HEIGHT);
        }

        if (sliderAmountI < width - 5) {
            renderContext.drawTexturedRect(GuiTextures.SLIDER_OFF_SEGMENT, x + sliderAmountI, y, width - 4 - sliderAmountI, HEIGHT);
        }

        for (int i = 1; i < 4; i++) {
            int notchX = x + width * i / 4 - 1;
            renderContext.drawTexturedRect(notchX > x + sliderAmountI ? GuiTextures.SLIDER_OFF_NOTCH : GuiTextures.SLIDER_ON_NOTCH,
                notchX, y + (HEIGHT - 4) / 2, 2, 4);
        }

        renderContext.drawTexturedRect(GuiTextures.SLIDER_BUTTON, x + sliderAmountI - 4, y, 8, HEIGHT);
    }

    @Override
    public boolean mouseInput(int mouseX, int mouseY, MouseEvent mouseEvent) {
        if (!Mouse.isButtonDown(0)) {
            clicked = false;
        }

        if (Mouse.getEventButton() == 0) {
            clicked = Mouse.getEventButtonState() && mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + HEIGHT;
            if (clicked) {
                float value = (mouseX - x) * (maxValue - minValue) / width + minValue;
                value = Math.max(minValue, Math.min(maxValue, value));
                value = (float) (Math.round(value / minStep) * (double) minStep);
                setCallback.accept(value);
                return true;
            }
        }

        if (!Mouse.getEventButtonState() && Mouse.getEventButton() == -1 && clicked) {
            float value = (mouseX - x) * (maxValue - minValue) / width + minValue;
            value = Math.max(minValue, Math.min(maxValue, value));
            value = Math.round(value / minStep) * minStep;
            setCallback.accept(value);
            return true;
        }

        return false;
    }

    @Override
    public boolean keyboardInput(KeyboardEvent keyboardEvent) {
        return false;
    }
}
