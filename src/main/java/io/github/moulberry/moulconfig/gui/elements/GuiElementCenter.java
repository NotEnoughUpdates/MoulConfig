package io.github.moulberry.moulconfig.gui.elements;

import io.github.moulberry.moulconfig.gui.GuiElementNew;
import io.github.moulberry.moulconfig.gui.GuiImmediateContext;
import net.minecraft.client.renderer.GlStateManager;

import java.util.function.BiFunction;

/**
 * A gui element centers another gui element
 */
public class GuiElementCenter extends GuiElementNew {
    final GuiElementNew child;

    public GuiElementCenter(GuiElementNew child) {
        this.child = child;
    }


    @Override
    public int getWidth() {
        return child.getHeight();
    }

    @Override
    public int getHeight() {
        return child.getHeight();
    }

    GuiImmediateContext getChildContext(GuiImmediateContext context) {
        return context.translated(
            getChildOffsetX(context),
            getChildOffsetY(context),
            child.getWidth(),
            child.getHeight()
        );
    }

    public int getChildOffsetX(GuiImmediateContext context) {
        return context.getWidth() / 2 - child.getWidth() / 2;
    }

    public int getChildOffsetY(GuiImmediateContext context) {
        return context.getHeight() / 2 - child.getHeight() / 2;
    }

    @Override
    public <T> T foldChildren(T initial, BiFunction<GuiElementNew, T, T> visitor) {
        return visitor.apply(child, initial);
    }

    @Override
    public void render(GuiImmediateContext context) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(getChildOffsetX(context), getChildOffsetY(context), 0);
        child.render(getChildContext(context));
        GlStateManager.popMatrix();
    }

    @Override
    public void keyboardEvent(GuiImmediateContext context) {
        child.keyboardEvent(getChildContext(context));
    }

    @Override
    public void mouseEvent(GuiImmediateContext context) {
        child.mouseEvent(getChildContext(context));
    }
}
