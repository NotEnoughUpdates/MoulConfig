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
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class GuiElementBoolean extends GuiElement {
    public int x;
    public int y;
    private Supplier<Boolean> value;
    private final int clickRadius;
    private final Consumer<Boolean> toggleCallback;

    private boolean previewValue;
    private int animation = 0;
    private long lastMillis = 0;

    private static final int xSize = 48;
    private static final int ySize = 14;

    public GuiElementBoolean(int x, int y, Supplier<Boolean> value, Consumer<Boolean> toggleCallback) {
        this(x, y, value, 0, toggleCallback);
    }

    public GuiElementBoolean(int x, int y, Supplier<Boolean> value, int clickRadius, Consumer<Boolean> toggleCallback) {
        this.x = x;
        this.y = y;
        this.value = value;
        this.previewValue = value.get();
        this.clickRadius = clickRadius;
        this.toggleCallback = toggleCallback;
        this.lastMillis = System.currentTimeMillis();

        if (previewValue) animation = 36;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawTexture(GuiTextures.TOGGLE_BAR, x, y, 0, 1, xSize, ySize);

        Identifier toggleIdentifier = GuiTextures.TOGGLE_ON;
        long currentMillis = System.currentTimeMillis();
        long deltaMillis = currentMillis - lastMillis;
        lastMillis = currentMillis;
        boolean passedLimit = false;
        if (previewValue != value.get()) {
            if ((previewValue && animation > 12) ||
                    (!previewValue && animation < 24)) {
                passedLimit = true;
            }
        }
        if (previewValue != passedLimit) {
            animation += deltaMillis / 10;
        } else {
            animation -= deltaMillis / 10;
        }
        lastMillis -= deltaMillis % 10;

        if (previewValue == value.get()) {
            animation = Math.max(0, Math.min(36, animation));
        } else if (!passedLimit) {
            if (previewValue) {
                animation = Math.max(0, Math.min(12, animation));
            } else {
                animation = Math.max(24, Math.min(36, animation));
            }
        } else {
            if (previewValue) {
                animation = Math.max(12, animation);
            } else {
                animation = Math.min(24, animation);
            }
        }

        int animation = (int) (LerpUtils.sigmoidZeroOne(this.animation / 36f) * 36);
        if (animation < 3) {
            toggleIdentifier = GuiTextures.TOGGLE_OFF;
        } else if (animation < 13) {
            toggleIdentifier = GuiTextures.TOGGLE_ONE;
        } else if (animation < 23) {
            toggleIdentifier = GuiTextures.TOGGLE_TWO;
        } else if (animation < 33) {
            toggleIdentifier = GuiTextures.TOGGLE_THREE;
        }

        context.drawTexture(toggleIdentifier, x + animation, y, 0, 1, 12, 14);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (mouseX > x - clickRadius && mouseX < x + xSize + clickRadius &&
                mouseY > y - clickRadius && mouseY < y + ySize + clickRadius) {
            if (button == 0) {
                if (previewValue == !value.get()) {
                    toggleCallback.accept(!value.get());
                }
            }
        } else {
            previewValue = value.get();
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (mouseX > x - clickRadius && mouseX < x + xSize + clickRadius &&
                mouseY > y - clickRadius && mouseY < y + ySize + clickRadius) {
            if (button == 0) {
                previewValue = !value.get();
            }
        } else {
            previewValue = value.get();
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }
}
