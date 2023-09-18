package io.github.moulberry.moulconfig.gui.component;

import io.github.moulberry.moulconfig.gui.GuiComponent;
import io.github.moulberry.moulconfig.gui.GuiImmediateContext;
import io.github.moulberry.moulconfig.internal.GlScissorStack;
import lombok.RequiredArgsConstructor;
import lombok.var;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;

import java.util.function.BiFunction;

@RequiredArgsConstructor
public class ScrollPanelComponent extends GuiComponent {
    final int width;
    final int height;
    final GuiComponent child;
    int scrollOffset;

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public <T> T foldChildren(T initial, BiFunction<GuiComponent, T, T> visitor) {
        return visitor.apply(child, initial);
    }

    @Override
    public void render(GuiImmediateContext context) {
        GlStateManager.pushMatrix();
        var sr = new ScaledResolution(mc);
        var x = context.getRenderOffsetX();
        var y = context.getRenderOffsetY();
        GlScissorStack.push(x, y, x + width, y + height, sr);
        GlStateManager.translate(0, -scrollOffset, 0);
        child.render(context.translatedNonRendering(0, -scrollOffset, width, height));
        GlScissorStack.pop(sr);
        GlStateManager.popMatrix();
    }

    @Override
    public void keyboardEvent(GuiImmediateContext context) {
        child.keyboardEvent(context.translatedNonRendering(0, -scrollOffset, width, height));
    }

    @Override
    public void mouseEvent(GuiImmediateContext context) {
        if (context.isHovered() && Mouse.getEventDWheel() != 0) {
            scrollOffset = Math.max(0, Math.min(scrollOffset - Mouse.getEventDWheel() * 10, child.getHeight() - height));
        }
        child.mouseEvent(context.translatedNonRendering(0, -scrollOffset, width, height));
    }
}
