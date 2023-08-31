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
package io.github.moulberry.moulconfig.gui;

import io.github.moulberry.moulconfig.Overlay;
import io.github.moulberry.moulconfig.internal.TextRenderUtils;
import io.github.moulberry.moulconfig.processor.MoulConfigProcessor;
import io.github.moulberry.moulconfig.processor.ProcessedOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.Map;

public class MoulGuiOverlayEditor extends Screen {
    final List<Overlay> overlays;
    final Map<Overlay, List<ProcessedOption>> processedOptions;

    Overlay grabbedOverlay;
    float grabbedOverlayAnchorX, grabbedOverlayAnchorY;

    public MoulGuiOverlayEditor(List<Overlay> overlays, Map<Overlay, List<ProcessedOption>> processedOptions) {
        super(Text.of("Overlay editor"));
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
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        boolean hasHoveredAny = grabbedOverlay != null;
        for (Overlay overlay : overlays) {
            if (!overlay.shouldShowInEditor()) continue;
            context.getMatrices().push();
            overlay.transform(context);
            boolean hovered = overlay == grabbedOverlay;
            if (!hasHoveredAny && isOverlayHovered(overlay, mouseX, mouseY)) {
                hovered = true;
                hasHoveredAny = true;
            }
            context.fill(0, 0, overlay.getWidth(), overlay.getHeight(),
                hovered ? 0xB0808080 : 0x80101010);
            TextRenderUtils.drawStringCenteredScaledMaxWidth(overlay.getName(), context,
                overlay.getWidth() / 2F, overlay.getHeight() / 2F,
                true, overlay.getWidth(),
                0xFFFFFFFF);
            context.getMatrices().pop();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) tryGrabOverlay((int) mouseX, (int) mouseY);
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (button == 0) tryMoveGrabbedOverlay((int) mouseX, (int) mouseY);
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) tryReleaseOverlay();
        return true;
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
        overlay.xPosition = (mouseX - grabbedOverlayAnchorX) / (float) MinecraftClient.getInstance().getWindow().getScaledWidth();
        overlay.yPosition = (mouseY - grabbedOverlayAnchorY) / (float) MinecraftClient.getInstance().getWindow().getScaledHeight();
    }

    public void tryReleaseOverlay() {
        this.grabbedOverlay = null;
    }
}
