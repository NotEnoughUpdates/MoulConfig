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

/**/
package io.github.notenoughupdates.moulconfig.gui;

import io.github.notenoughupdates.moulconfig.Overlay;
import io.github.notenoughupdates.moulconfig.internal.TextRenderUtils;
import io.github.notenoughupdates.moulconfig.processor.MoulConfigProcessor;
import io.github.notenoughupdates.moulconfig.processor.ProcessedOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

import java.util.List;
import java.util.Map;

public class MoulGuiOverlayEditor extends GuiScreen {
    final List<Overlay> overlays;
    final Map<Overlay, List<ProcessedOption>> processedOptions;

    Overlay grabbedOverlay;
    float grabbedOverlayAnchorX, grabbedOverlayAnchorY;

    public MoulGuiOverlayEditor(List<Overlay> overlays, Map<Overlay, List<ProcessedOption>> processedOptions) {
        this.overlays = overlays;
        this.processedOptions = processedOptions;
    }

    public MoulGuiOverlayEditor(MoulConfigProcessor<?> testConfigMoulConfigProcessor) {
        this(testConfigMoulConfigProcessor.getAllOverlays(), testConfigMoulConfigProcessor.getOverlayOptions());
    }

    private boolean isOverlayHovered(Overlay overlay, int mouseX, int mouseY) {
        return overlay.getX() < mouseX && mouseX < overlay.getX() + overlay.getScaledWidth()
            && overlay.getY() < mouseY && mouseY < overlay.getY() + overlay.getScaledHeight();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        drawDefaultBackground();
        boolean hasHoveredAny = grabbedOverlay != null;
        for (Overlay overlay : overlays) {
            if (!overlay.shouldShowInEditor()) continue;
            GlStateManager.pushMatrix();
            overlay.transform();
            boolean hovered = overlay == grabbedOverlay;
            if (!hasHoveredAny && isOverlayHovered(overlay, mouseX, mouseY)) {
                hovered = true;
                hasHoveredAny = true;
            }
            drawRect(0, 0, overlay.getWidth(), overlay.getHeight(),
                hovered ? 0xB0808080 : 0x80101010);
            TextRenderUtils.drawStringCenteredScaledMaxWidth(overlay.getName(), mc.fontRendererObj,
                overlay.getWidth() / 2F, overlay.getHeight() / 2F,
                true, overlay.getWidth(),
                0xFFFFFFFF);
            GlStateManager.popMatrix();
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0) tryGrabOverlay(mouseX, mouseY);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (clickedMouseButton == 0)
            tryMoveGrabbedOverlay(mouseX, mouseY);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (state == 0)
            tryReleaseOverlay();
    }

    public void tryGrabOverlay(int mouseX, int mouseY) {
        for (Overlay overlay : overlays) {
            if (!overlay.shouldShowInEditor()) continue;
            if (isOverlayHovered(overlay, mouseX, mouseY)) {
                this.grabbedOverlay = overlay;
                this.grabbedOverlayAnchorX = mouseX - overlay.getX();
                this.grabbedOverlayAnchorY = mouseY - overlay.getY();
                break;
            }
        }
    }

    public void tryMoveGrabbedOverlay(int mouseX, int mouseY) {
        Overlay overlay = this.grabbedOverlay;
        if (overlay == null) return;
        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        overlay.xPosition = (mouseX - grabbedOverlayAnchorX) / (float) scaledResolution.getScaledWidth_double();
        overlay.yPosition = (mouseY - grabbedOverlayAnchorY) / (float) scaledResolution.getScaledHeight_double();
    }

    public void tryReleaseOverlay() {
        this.grabbedOverlay = null;
    }
}
