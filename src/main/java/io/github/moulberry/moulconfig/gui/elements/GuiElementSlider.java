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

import io.github.moulberry.moulconfig.GuiTextures;
import io.github.moulberry.moulconfig.gui.GuiElement;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

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
    public void render(DrawContext context, int ignored, int ignored_, float ignored__) {
        float value = this.value;
        float sliderAmount = Math.max(0, Math.min(1, (value - minValue) / (maxValue - minValue)));
        int sliderAmountI = (int) (width * sliderAmount);

        //GL11.glColor4f(1, 1, 1, 1);
        context.drawTexture(GuiTextures.SLIDER_ON_CAP, x, y, 0, 0, 4, HEIGHT, 4, HEIGHT);
        context.drawTexture(GuiTextures.SLIDER_OFF_CAP, x + width - 4, y, 0, 0, 4, HEIGHT, 4, HEIGHT);

        if (sliderAmountI > 5) {
            context.drawTexture(GuiTextures.SLIDER_ON_SEGMENT, x + 4, y, 0, 0, sliderAmountI - 4, HEIGHT, sliderAmountI - 4, HEIGHT);
        }

        if (sliderAmountI < width - 5) {
            context.drawTexture(GuiTextures.SLIDER_OFF_SEGMENT, x + sliderAmountI, y, 0, 0, width - 4 - sliderAmountI, HEIGHT, (width - 4) - (x + sliderAmountI), HEIGHT);
        }

        for (int i = 1; i < 4; i++) {
            int notchX = x + width * i / 4 - 1;
            Identifier identifier = (
                    notchX > x + sliderAmountI ? GuiTextures.SLIDER_OFF_NOTCH : GuiTextures.SLIDER_ON_NOTCH);
            context.drawTexture(identifier, notchX, y + (HEIGHT - 4) / 2, 0, 0, 2, 4, 2, 4);
        }

        context.drawTexture(GuiTextures.SLIDER_BUTTON, x + sliderAmountI - 4, y, 0, 0, 8, HEIGHT, 8, HEIGHT);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        clicked = mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + HEIGHT;

        //if (button == -1 && clicked) {
        //    float value = (float) ((mouseX - x) * (maxValue - minValue) / width + minValue);
        //    value = Math.max(minValue, Math.min(maxValue, value));
        //    value = Math.round(value / minStep) * minStep;
        //    setCallback.accept(value);
        //    return true;
        //}

        return moveIfClicked(mouseX, mouseY, button);
    }

    public boolean moveIfClicked(double mouseX, double mouseY, int button) {
        if (clicked) {
            double value = (mouseX - x) * (maxValue - minValue) / width + minValue;
            value = Math.max(minValue, Math.min(maxValue, value));
            value = (float) (Math.round(value / minStep) * (double) minStep);
            this.value = (float) value;
            setCallback.accept((float) value);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return moveIfClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        clicked = false;
        return false;
    }

    double lastMouseX = 0, lastMouseY = 0;

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        lastMouseX = mouseX;
        lastMouseY = mouseY;
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }
}
