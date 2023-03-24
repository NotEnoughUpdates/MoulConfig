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

/*
 *
 */
package io.github.moulberry.moulconfig;

import com.google.gson.annotations.Expose;
import io.github.moulberry.moulconfig.annotations.ConfigEditorSlider;
import io.github.moulberry.moulconfig.annotations.ConfigOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;

public abstract class Overlay {
    @Expose
    public float xPosition;
    @Expose
    public float yPosition;

    @Expose
    @ConfigOption(name = "Scale", desc = "Scale")
    @ConfigEditorSlider(minValue = 0.1F, maxValue = 6.0F, minStep = 0.1F)
    @Deprecated
    public float scale = 1F;

    public float getScale() {
        return MathHelper.clamp_float(scale, 0.1F, 6.0F);
    }

    public float getEffectiveScale() {
        return getScale() * new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor();
    }

    public float getX() {
        float viewportWidth = (float) new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth_double();
        return MathHelper.clamp_float(
            (xPosition * viewportWidth),
            0, viewportWidth - getScaledWidth());
    }

    public float getY() {
        float viewportHeight = (float) new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight_double();
        return MathHelper.clamp_float(
            (yPosition * viewportHeight),
            0, viewportHeight - getScaledHeight());
    }

    public void transform() {
        GlStateManager.translate(getX(), getY(), 0);
        GlStateManager.scale(scale, scale, 1);
    }

    public float getScaledWidth() {
        return getWidth() * scale;
    }

    public float getScaledHeight() {
        return getHeight() * scale;
    }

    public float realWorldXToLocalX(float realWorldX) {
        return realWorldX / getEffectiveScale() - getX() / getScale();
    }

    public float realWorldYToLocalY(float realWorldY) {
        return realWorldY / getEffectiveScale() - getY() / getScale();
    }

    public int getWidth() {
        return 200;
    }

    public int getHeight() {
        return 100;
    }

    public abstract String getName();

    public boolean shouldShowInEditor() {
        return true;
    }
}
