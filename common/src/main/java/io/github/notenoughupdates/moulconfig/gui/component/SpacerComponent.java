package io.github.notenoughupdates.moulconfig.gui.component;

import io.github.notenoughupdates.moulconfig.gui.GuiComponent;
import io.github.notenoughupdates.moulconfig.gui.GuiImmediateContext;

import java.util.function.Supplier;

public class SpacerComponent extends GuiComponent {
    private final Supplier<Integer> width;
    private final Supplier<Integer> height;

    public SpacerComponent(Supplier<Integer> width, Supplier<Integer> height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public int getWidth() {
        return width.get();
    }

    @Override
    public int getHeight() {
        return height.get();
    }

    @Override
    public void render(GuiImmediateContext context) {
    }
}
