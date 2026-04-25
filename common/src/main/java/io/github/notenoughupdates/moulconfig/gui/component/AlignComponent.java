package io.github.notenoughupdates.moulconfig.gui.component;

import io.github.notenoughupdates.moulconfig.gui.GuiComponent;
import io.github.notenoughupdates.moulconfig.gui.GuiImmediateContext;
import io.github.notenoughupdates.moulconfig.gui.HorizontalAlign;
import io.github.notenoughupdates.moulconfig.gui.KeyboardEvent;
import io.github.notenoughupdates.moulconfig.gui.MouseEvent;
import io.github.notenoughupdates.moulconfig.gui.VerticalAlign;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class AlignComponent extends GuiComponent {
    private final GuiComponent child;
    private final Supplier<HorizontalAlign> horizontal;
    private final Supplier<VerticalAlign> vertical;

    public AlignComponent(GuiComponent child, Supplier<HorizontalAlign> horizontal, Supplier<VerticalAlign> vertical) {
        this.child = child;
        this.horizontal = horizontal;
        this.vertical = vertical;
    }

    public GuiComponent getChild() { return child; }
    public Supplier<HorizontalAlign> getHorizontal() { return horizontal; }
    public Supplier<VerticalAlign> getVertical() { return vertical; }

    @Override
    public int getWidth() {
        return child.getWidth();
    }

    @Override
    public int getHeight() {
        return child.getHeight();
    }

    public GuiImmediateContext getChildContext(GuiImmediateContext context) {
        return context.translated(getChildOffsetX(context), getChildOffsetY(context), child.getWidth(), child.getHeight());
    }

    public int getChildOffsetX(GuiImmediateContext context) {
        switch (horizontal.get()) {
            case LEFT:
                return 0;
            case CENTER:
                return context.getWidth() / 2 - child.getWidth() / 2;
            case RIGHT:
                return context.getWidth() - child.getWidth();
            default:
                throw new IllegalStateException("Unknown horizontal alignment");
        }
    }

    public int getChildOffsetY(GuiImmediateContext context) {
        switch (vertical.get()) {
            case BOTTOM:
                return context.getHeight() - child.getHeight();
            case CENTER:
                return context.getHeight() / 2 - child.getHeight() / 2;
            case TOP:
                return 0;
            default:
                throw new IllegalStateException("Unknown vertical alignment");
        }
    }

    @Override
    public <T> T foldChildren(T initial, BiFunction<GuiComponent, T, T> visitor) {
        return visitor.apply(child, initial);
    }

    @Override
    public void render(GuiImmediateContext context) {
        context.getRenderContext().pushMatrix();
        context.getRenderContext().translate((float) getChildOffsetX(context), (float) getChildOffsetY(context));
        child.render(getChildContext(context));
        context.getRenderContext().popMatrix();
    }

    @Override
    public boolean keyboardEvent(KeyboardEvent event, GuiImmediateContext context) {
        return child.keyboardEvent(event, getChildContext(context));
    }

    @Override
    public boolean mouseEvent(MouseEvent mouseEvent, GuiImmediateContext context) {
        return child.mouseEvent(mouseEvent, getChildContext(context));
    }
}
