package io.github.moulberry.moulconfig.gui.elements;

import io.github.moulberry.moulconfig.gui.GuiElementNew;
import io.github.moulberry.moulconfig.gui.GuiImmediateContext;
import io.github.moulberry.moulconfig.internal.RenderUtils;
import net.minecraft.client.renderer.GlStateManager;

/**
 * Renders an element with a floating rect.
 */
public class GuiElementPanel extends GuiElementNew {
    private final GuiElementNew element;
    private final int insets;

    /**
     * @param element the child element to render the panels contents
     * @param insets  the padding size of this panel
     */
    public GuiElementPanel(GuiElementNew element, int insets) {
        this.element = element;
        this.insets = insets;
    }

    public GuiElementPanel(GuiElementNew element) {
        this(element, 2);
    }

    @Override
    public int getWidth() {
        return element.getWidth() + insets * 2;
    }

    public int getInsets() {
        return insets;
    }

    @Override
    public int getHeight() {
        return element.getHeight() + insets * 2;
    }

    GuiImmediateContext getChildContext(GuiImmediateContext context) {
        return context.translated(insets, insets, element.getWidth(), element.getHeight());
    }

    @Override
    public void render(GuiImmediateContext context) {
        GlStateManager.pushMatrix();
        RenderUtils.drawFloatingRectDark(0, 0, getWidth(), getHeight());
        GlStateManager.translate(insets, insets, 0);
        element.render(getChildContext(context));
        GlStateManager.popMatrix();
    }

    @Override
    public void keyboardEvent(GuiImmediateContext context) {
        element.keyboardEvent(getChildContext(context));
    }

    @Override
    public void mouseEvent(GuiImmediateContext context) {
        element.mouseEvent(getChildContext(context));
    }
}
