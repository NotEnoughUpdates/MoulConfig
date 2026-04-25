package io.github.notenoughupdates.moulconfig.gui.component;

import io.github.notenoughupdates.moulconfig.gui.CloseEventListener;
import io.github.notenoughupdates.moulconfig.gui.GuiComponent;
import io.github.notenoughupdates.moulconfig.gui.GuiContext;
import io.github.notenoughupdates.moulconfig.gui.GuiImmediateContext;
import io.github.notenoughupdates.moulconfig.observer.GetSetter;

import java.util.function.Supplier;

/**
 * Component providing XML wrappers and such the ability to wrap meta operations that operate on the entire screen.
 * This component should be permanently mounted and does not impact layouting or rendering.
 */
public class MetaComponent extends GuiComponent implements CloseEventListener {
    private final Supplier<CloseAction> beforeClose;
    private final Runnable afterClose;
    private final GetSetter<Runnable> requestClose;

    public MetaComponent() {
        this(null, null, null);
    }

    public MetaComponent(Supplier<CloseAction> beforeClose, Runnable afterClose, GetSetter<Runnable> requestClose) {
        this.beforeClose = beforeClose;
        this.afterClose = afterClose;
        this.requestClose = requestClose;
    }

    @Override
    public void setContext(GuiContext context) {
        super.setContext(context);
        if (requestClose != null) {
            requestClose.set(() -> {
                if (context != null) {
                    context.requestClose();
                }
            });
        }
    }

    @Override
    public CloseAction onBeforeClose() {
        return beforeClose != null ? beforeClose.get() : CloseAction.NO_OBJECTIONS_TO_CLOSE;
    }

    @Override
    public void onAfterClose() {
        if (afterClose != null) {
            afterClose.run();
        }
    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public void render(GuiImmediateContext context) {
    }
}
