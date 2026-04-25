package io.github.notenoughupdates.moulconfig.gui.component;

import io.github.notenoughupdates.moulconfig.gui.GuiComponent;
import io.github.notenoughupdates.moulconfig.gui.GuiImmediateContext;
import io.github.notenoughupdates.moulconfig.gui.KeyboardEvent;
import io.github.notenoughupdates.moulconfig.gui.MouseEvent;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class IndirectComponent extends GuiComponent {
    private final Supplier<? extends GuiComponent> component;

    public IndirectComponent(Supplier<? extends GuiComponent> component) {
        this.component = component;
    }

    public Supplier<? extends GuiComponent> getComponent() {
        return component;
    }

    @Override
    public int getWidth() {
        return component.get().getWidth();
    }

    @Override
    public int getHeight() {
        return component.get().getHeight();
    }

    @Override
    public void render(GuiImmediateContext context) {
        component.get().render(context);
    }

    @Override
    public boolean keyboardEvent(KeyboardEvent event, GuiImmediateContext context) {
        return component.get().keyboardEvent(event, context);
    }

    @Override
    public boolean mouseEvent(MouseEvent mouseEvent, GuiImmediateContext context) {
        return component.get().mouseEvent(mouseEvent, context);
    }

    @Override
    public <T> T foldChildren(T initial, BiFunction<GuiComponent, T, T> visitor) {
        return visitor.apply(component.get(), initial);
    }
}
