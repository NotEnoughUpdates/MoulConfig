package io.github.notenoughupdates.moulconfig.gui.component;

import io.github.notenoughupdates.moulconfig.gui.GuiComponent;
import io.github.notenoughupdates.moulconfig.gui.GuiImmediateContext;
import io.github.notenoughupdates.moulconfig.gui.KeyboardEvent;
import io.github.notenoughupdates.moulconfig.gui.MouseEvent;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class ScaleComponent extends GuiComponent {
    private final GuiComponent child;
    private final Supplier<Float> scaleFactor;

    public ScaleComponent(GuiComponent child, Supplier<Float> scaleFactor) {
        this.child = child;
        this.scaleFactor = scaleFactor;
    }

    @Override
    public int getWidth() {
        return (int) (scaleFactor.get() * child.getWidth());
    }

    @Override
    public int getHeight() {
        return (int) (scaleFactor.get() * child.getHeight());
    }

    @Override
    public <T> T foldChildren(T initial, BiFunction<GuiComponent, T, T> visitor) {
        return visitor.apply(child, initial);
    }

    @Override
    public void render(GuiImmediateContext context) {
        context.getRenderContext().pushMatrix();
        float scale = scaleFactor.get();
        context.getRenderContext().scale(scale, scale);
        child.render(context.scaled(scale));
        context.getRenderContext().popMatrix();
    }

    @Override
    public boolean keyboardEvent(KeyboardEvent event, GuiImmediateContext context) {
        return child.keyboardEvent(event, context.scaled(scaleFactor.get()));
    }

    @Override
    public boolean mouseEvent(MouseEvent mouseEvent, GuiImmediateContext context) {
        return child.mouseEvent(mouseEvent, context.scaled(scaleFactor.get()));
    }
}
