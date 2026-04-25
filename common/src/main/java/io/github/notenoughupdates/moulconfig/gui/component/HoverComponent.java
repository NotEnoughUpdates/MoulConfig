package io.github.notenoughupdates.moulconfig.gui.component;

import io.github.notenoughupdates.moulconfig.common.text.StructuredText;
import io.github.notenoughupdates.moulconfig.gui.GuiComponent;
import io.github.notenoughupdates.moulconfig.gui.GuiImmediateContext;
import io.github.notenoughupdates.moulconfig.gui.KeyboardEvent;
import io.github.notenoughupdates.moulconfig.gui.MouseEvent;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class HoverComponent extends GuiComponent {
    private final GuiComponent child;
    private final Supplier<List<StructuredText>> hoverLines;

    public HoverComponent(GuiComponent child, Supplier<List<StructuredText>> hoverLines) {
        this.child = child;
        this.hoverLines = hoverLines;
    }

    public GuiComponent getChild() { return child; }
    public Supplier<List<StructuredText>> getHoverLines() { return hoverLines; }

    @Override
    public int getWidth() {
        return child.getWidth();
    }

    @Override
    public int getHeight() {
        return child.getHeight();
    }

    @Override
    public <T> T foldChildren(T initial, BiFunction<GuiComponent, T, T> visitor) {
        return visitor.apply(child, initial);
    }

    @Override
    public void render(GuiImmediateContext context) {
        if (context.isHovered()) {
            context.getRenderContext().scheduleDrawTooltip(context.getMouseX(), context.getMouseY(), hoverLines.get());
        }
        child.render(context);
    }

    @Override
    public boolean mouseEvent(MouseEvent mouseEvent, GuiImmediateContext context) {
        return child.mouseEvent(mouseEvent, context);
    }

    @Override
    public boolean keyboardEvent(KeyboardEvent event, GuiImmediateContext context) {
        return child.keyboardEvent(event, context);
    }
}
