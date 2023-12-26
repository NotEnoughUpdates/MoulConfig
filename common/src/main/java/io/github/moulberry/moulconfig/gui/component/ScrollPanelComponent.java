package io.github.moulberry.moulconfig.gui.component;

import io.github.moulberry.moulconfig.gui.GuiComponent;
import io.github.moulberry.moulconfig.gui.GuiImmediateContext;
import io.github.moulberry.moulconfig.gui.KeyboardEvent;
import io.github.moulberry.moulconfig.gui.MouseEvent;
import lombok.RequiredArgsConstructor;
import lombok.var;

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
        context.getRenderContext().pushMatrix();
        var x = context.getRenderOffsetX();
        var y = context.getRenderOffsetY();

        context.getRenderContext().pushScissor(x, y, x + width, y + height);
        context.getRenderContext().translate(0, -scrollOffset, 0);
        child.render(context.translatedNonRendering(0, -scrollOffset, width, height));
        context.getRenderContext().popScissor();
        context.getRenderContext().popMatrix();
    }

    @Override
    public boolean keyboardEvent(KeyboardEvent event, GuiImmediateContext context) {
        return child.keyboardEvent(event, context.translatedNonRendering(0, -scrollOffset, width, height));
    }

    @Override
    public boolean mouseEvent(MouseEvent mouseEvent, GuiImmediateContext context) {
        if (child.mouseEvent(mouseEvent, context.translatedNonRendering(0, -scrollOffset, width, height))) {
            return true;
        }
        if (context.isHovered() && mouseEvent instanceof MouseEvent.Scroll) {
            scrollOffset = (int) Math.max(0, Math.min(scrollOffset - (((MouseEvent.Scroll) mouseEvent).getDWheel()), child.getHeight() - height));
            return true;
        }
        return false;
    }
}
