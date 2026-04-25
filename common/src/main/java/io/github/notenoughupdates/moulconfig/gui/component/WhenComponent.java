package io.github.notenoughupdates.moulconfig.gui.component;

import io.github.notenoughupdates.moulconfig.gui.GuiComponent;

import java.util.function.Supplier;

public class WhenComponent extends IndirectComponent {
    public WhenComponent(Supplier<? extends Boolean> condition, Supplier<? extends GuiComponent> ifTrue, Supplier<? extends GuiComponent> ifFalse) {
        super(() -> condition.get() ? ifTrue.get() : ifFalse.get());
    }
}
