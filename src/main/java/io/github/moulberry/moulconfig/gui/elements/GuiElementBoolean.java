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

package io.github.moulberry.moulconfig.gui.elements;/*
 * Copyright (C) 2022 NotEnoughUpdates contributors
 *
 * This file is part of NotEnoughUpdates.
 *
 * NotEnoughUpdates is free software: you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * NotEnoughUpdates is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with NotEnoughUpdates. If not, see <https://www.gnu.org/licenses/>.
 */


import io.github.moulberry.moulconfig.GuiTextures;
import io.github.moulberry.moulconfig.gui.GuiElement;
import io.github.moulberry.moulconfig.internal.LerpUtils;
import io.github.moulberry.moulconfig.internal.LerpingInteger;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class GuiElementBoolean extends GuiElement {
    public int x;
    public int y;
    private final Supplier<Boolean> value;
    private final int clickRadius;
    private final Consumer<Boolean> toggleCallback;
    private final LerpingInteger integer;
    private boolean lastValue = false;

    private static final int xSize = 48;
    private static final int ySize = 14;

    public GuiElementBoolean(int x, int y, Supplier<Boolean> value, Consumer<Boolean> toggleCallback) {
        this(x, y, value, 0, toggleCallback);
    }

    public GuiElementBoolean(int x, int y, Supplier<Boolean> value, int clickRadius, Consumer<Boolean> toggleCallback) {
        this.x = x;
        this.y = y;
        this.value = value;
        this.integer = new LerpingInteger(0, 200);
        this.clickRadius = clickRadius;
        this.toggleCallback = toggleCallback;

        if (value.get()) {
            integer.setValue(100);
            lastValue = value.get();
            previousValue = value.get();
        }
    }

    @Override
    @SuppressWarnings("DuplicatedCode")
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawTexture(GuiTextures.TOGGLE_BAR, x, y, (float) 0, (float) 0, 48, 14, 48, 14);

        boolean val = value.get();
        if (lastValue != val) {
            integer.setTarget(val ? 100 : 0);
            integer.resetTimer();
            lastValue = val;
        } else {
            integer.tick();
        }

        float animationPercentage = LerpUtils.sigmoidZeroOne(integer.getValue() / 100F);
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
        context.drawTexture(buttonLocation,  x+ (int) (animationPercentage * (48 - 12)), y, 0, 1, 12, 14, 12, 14);

    }

    boolean previousValue = false;

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (mouseX > x - clickRadius && mouseX < x + xSize + clickRadius &&
                mouseY > y - clickRadius && mouseY < y + ySize + clickRadius) {
            if (button == 0) {
                if (previousValue == !value.get()) {
                    toggleCallback.accept(!value.get());
                    return true;
                }
            }
        }
        previousValue = value.get();
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (mouseX > x - clickRadius && mouseX < x + xSize + clickRadius &&
                mouseY > y - clickRadius && mouseY < y + ySize + clickRadius) {
            if (button == 0) {
                previousValue = !value.get();
            }
        }
        return false;
    }
}
