package io.github.notenoughupdates.moulconfig.gui.component;

import io.github.notenoughupdates.moulconfig.common.IItemStack;
import io.github.notenoughupdates.moulconfig.common.text.StructuredText;
import io.github.notenoughupdates.moulconfig.gui.GuiComponent;
import io.github.notenoughupdates.moulconfig.gui.GuiImmediateContext;
import io.github.notenoughupdates.moulconfig.observer.GetSetter;

public class ItemStackComponent extends GuiComponent {
    private final GetSetter<IItemStack> itemStack;

    public ItemStackComponent(GetSetter<IItemStack> itemStack) {
        this.itemStack = itemStack;
    }

    public GetSetter<IItemStack> getItemStack() {
        return itemStack;
    }

    @Override
    public int getWidth() {
        return 18;
    }

    @Override
    public int getHeight() {
        return 18;
    }

    @Override
    public void render(GuiImmediateContext context) {
        context.getRenderContext().renderItemStack(itemStack.get(), 1, 1, StructuredText.empty());
    }
}
