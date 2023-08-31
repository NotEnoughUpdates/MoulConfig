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

package io.github.moulberry.moulconfig.gui.editors;

import io.github.moulberry.moulconfig.gui.GuiOptionEditor;
import io.github.moulberry.moulconfig.internal.RenderUtils;
import io.github.moulberry.moulconfig.internal.TextRenderUtils;
import io.github.moulberry.moulconfig.processor.ProcessedOption;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import org.lwjgl.opengl.GL11;

public class GuiOptionEditorAccordion extends GuiOptionEditor {
    private final int accordionId;
    private boolean accordionToggled = false;

    public GuiOptionEditorAccordion(ProcessedOption option, int accordionId) {
        super(option);
        this.accordionId = accordionId;
    }

    @Override
    public int getHeight() {
        return 20;
    }

    public int getAccordionId() {
        return accordionId;
    }

    public boolean getToggled() {
        return accordionToggled;
    }

    int lastX = 0, lastY = 0, lastWidth = 0, lastMouseX = 0, lastMouseY = 0;

    @Override
    public void render(DrawContext context, int x, int y, int width) {
        int height = getHeight();
        RenderUtils.drawFloatingRectDark(context, x, y, width, height, true);


        Tessellator tessellator = Tessellator.getInstance();

        //GL11.glEnable(GL11.GL_BLEND);
        //GL11.glEnable(GL11.GL_4D_COLOR_TEXTURE);
        //GL11.glDisable(GL11.GL_TEXTURE_2D);
        //GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        //GL11.glColor4f(1, 1, 1, 1);
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION);
        if (accordionToggled) {
            buffer.vertex((double) x + 6, (double) y + 6, 0.0D).next();
            buffer.vertex((double) x + 9.75f, (double) y + 13.5f, 0.0D).next();
            buffer.vertex((double) x + 13.5f, (double) y + 6, 0.0D).next();
        } else {
            buffer.vertex((double) x + 6, (double) y + 13.5f, 0.0D).next();
            buffer.vertex((double) x + 13.5f, (double) y + 9.75f, 0.0D).next();
            buffer.vertex((double) x + 6, (double) y + 6, 0.0D).next();
        }
        tessellator.draw();
        //GL11.glEnable(GL11.GL_TEXTURE_2D);
        //GL11.glDisable(GL11.GL_BLEND);

        TextRenderUtils.drawStringScaledMaxWidth(option.name, context,
                x + 18, y + 6, false, width - 10, 0xc0c0c0
        );

        //context.fill(lastX - 10, lastY - 1, lastX + lastWidth, lastY + 10, 0xFF000000);
        //context.fill(lastMouseX - 10, lastMouseY - 1, lastMouseX + 10, lastMouseY + 10, 0xFF000000);
    }

    @Override
    public boolean mouseInput(int x, int y, int width, double mouseX, double mouseY, int button) {
        this.lastX = x;
        this.lastY = y;
        this.lastWidth = width;
        this.lastMouseX = (int) mouseX;
        this.lastMouseY = (int) mouseY;

        if (mouseX > x && mouseX < x + width &&
                mouseY > y && mouseY < y + getHeight()) {
            accordionToggled = !accordionToggled;
            return true;
        }

        return false;
    }

    @Override
    public boolean keyboardInput(int keyCode, int scanCode, int modifiers) {
        return false;
    }
}
