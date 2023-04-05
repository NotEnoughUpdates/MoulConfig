package io.github.moulberry.moulconfig.gui.elements;

import io.github.moulberry.moulconfig.gui.GuiElementNew;
import io.github.moulberry.moulconfig.gui.GuiImmediateContext;
import lombok.ToString;
import net.minecraft.client.renderer.GlStateManager;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * A gui element composing multiple other gui elements by stacking them horizontally.
 */
@ToString
public class GuiColumnRow extends GuiElementNew {
    final List<GuiElementNew> children;

    public GuiColumnRow(List<GuiElementNew> children) {
        this.children = children;
    }

    public GuiColumnRow(GuiElementNew... children) {
        this(Arrays.asList(children));
    }

    @Override
    public int getWidth() {
        return foldChildren(0, (child, width) -> child.getWidth() + width);
    }

    @Override
    public int getHeight() {
        return foldChildren(0, (child, height) -> Math.max(child.getHeight(), height));
    }

    @Override
    public <T> T foldChildren(T initial, BiFunction<GuiElementNew, T, T> visitor) {
        for (GuiElementNew child : children) {
            initial = visitor.apply(child, initial);
        }
        return initial;
    }

    public void foldWithContext(GuiImmediateContext context, BiConsumer<GuiElementNew, GuiImmediateContext> visitor) {
        foldChildren(0, (child, position) -> {
            visitor.accept(child, context.translated(position, 0, child.getWidth(), child.getHeight()));
            return child.getWidth() + position;
        });
    }

    @Override
    public void render(GuiImmediateContext context) {
        GlStateManager.pushMatrix();
        foldWithContext(context, (child, childContext) -> {
            child.render(childContext);
            GlStateManager.translate(child.getWidth(), 0, 0);
        });
        GlStateManager.popMatrix();
    }

    @Override
    public void mouseEvent(GuiImmediateContext context) {
        foldWithContext(context, GuiElementNew::mouseEvent);
    }

    @Override
    public void keyboardEvent(GuiImmediateContext context) {
        foldWithContext(context, GuiElementNew::keyboardEvent);
    }

}
