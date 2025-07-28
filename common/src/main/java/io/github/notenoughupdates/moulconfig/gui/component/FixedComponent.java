package io.github.notenoughupdates.moulconfig.gui.component;

import io.github.notenoughupdates.moulconfig.gui.GuiComponent;
import io.github.notenoughupdates.moulconfig.gui.GuiImmediateContext;
import io.github.notenoughupdates.moulconfig.gui.KeyboardEvent;
import io.github.notenoughupdates.moulconfig.gui.MouseEvent;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;

@Value
@EqualsAndHashCode(callSuper = false)
public class FixedComponent extends GuiComponent {
    GuiComponent inner;
    int width, height;

    @Override
    public <T> T foldChildren(T initial, @NotNull BiFunction<@NotNull GuiComponent, T, T> visitor) {
        return visitor.apply(inner, initial);
    }

    @Override
    public boolean mouseEvent(@NotNull MouseEvent mouseEvent, @NotNull GuiImmediateContext context) {
        return inner.mouseEvent(mouseEvent, context);
    }

    @Override
    public boolean keyboardEvent(@NotNull KeyboardEvent event, @NotNull GuiImmediateContext context) {
        return inner.keyboardEvent(event, context);
    }

    @Override
    public void render(@NotNull GuiImmediateContext context) {
        inner.render(context);
    }
}
