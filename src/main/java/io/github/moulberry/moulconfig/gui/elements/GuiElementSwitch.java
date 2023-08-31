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
import io.github.moulberry.moulconfig.gui.GuiElementNew;
import io.github.moulberry.moulconfig.gui.GuiImmediateContext;
import io.github.moulberry.moulconfig.internal.LerpUtils;
import io.github.moulberry.moulconfig.internal.LerpingInteger;
import io.github.moulberry.moulconfig.observer.GetSetter;
import lombok.ToString;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;

/**
 * A gui element displaying a switch to represent a boolean value.
 */
@ToString
public class GuiElementSwitch extends GuiElementNew {
    final GetSetter<Boolean> value;
    // TODO: replace LerpingInteger with a proper percentage, so that flickering does not take longer to update.
    final LerpingInteger animation;
    private boolean lastValue;

    public GuiElementSwitch(GetSetter<Boolean> value, int timeToReachTarget) {
        this.value = value;
        this.lastValue = value.get();
        this.animation = new LerpingInteger(value.get() ? 100 : 0, timeToReachTarget);
    }

    @Override
    public int getWidth() {
        return 48;
    }

    @Override
    public int getHeight() {
        return 14;
    }

    @Override
    public void render(DrawContext drawContext, GuiImmediateContext context) {
        drawContext.drawTexture(GuiTextures.TOGGLE_BAR, 0, 0, (float)0, (float)0, context.getWidth(), context.getHeight(), context.getWidth(), context.getHeight());

        boolean val = value.get();
        if (lastValue != val) {
            animation.setTarget(val ? 100 : 0);
            animation.resetTimer();
            lastValue = val;
        } else {
            animation.tick();
        }

        float animationPercentage = LerpUtils.sigmoidZeroOne(animation.getValue() / 100F);
        Identifier buttonLocation;
        if (animationPercentage < 1 / 5F) {
            buttonLocation = GuiTextures.TOGGLE_OFF;
        } else if (animationPercentage < 2 / 5F) {
            buttonLocation = GuiTextures.TOGGLE_ONE;
        } else if (animationPercentage < 3 / 5F) {
            buttonLocation = GuiTextures.TOGGLE_TWO;
        } else if (animationPercentage < 4 / 5F) {
            buttonLocation = GuiTextures.TOGGLE_THREE;
        } else {
            buttonLocation = GuiTextures.TOGGLE_ON;
        }
        drawContext.drawTexture(buttonLocation, (int) (animationPercentage * (context.getWidth() - 12)), 0, 0, 1, 12, 14, 12, 14);
    }

    @Override
    public void mouseEvent(int button, GuiImmediateContext context) {
        System.out.println(context.getMouseX() + " " + context.getMouseY());
        super.mouseEvent(button, context);
        if (context.isHovered() && button == 0) {
            value.set(!value.get());
        }
    }
}
