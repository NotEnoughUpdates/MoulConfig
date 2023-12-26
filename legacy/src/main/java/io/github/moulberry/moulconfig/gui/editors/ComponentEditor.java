package io.github.moulberry.moulconfig.gui.editors;

import io.github.moulberry.moulconfig.common.IMinecraft;
import io.github.moulberry.moulconfig.gui.GuiComponent;
import io.github.moulberry.moulconfig.gui.GuiImmediateContext;
import io.github.moulberry.moulconfig.gui.GuiOptionEditor;
import io.github.moulberry.moulconfig.gui.MouseEvent;
import io.github.moulberry.moulconfig.internal.ForgeRenderContext;
import io.github.moulberry.moulconfig.processor.ProcessedOption;
import lombok.var;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.input.Mouse;

public abstract class ComponentEditor extends GuiOptionEditor {
    protected ComponentEditor(ProcessedOption option) {
        super(option);
    }

    public abstract @NotNull GuiComponent getDelegate();

    public @Nullable GuiComponent getOverlayDelegate() {
        return null;
    }

    public GuiImmediateContext getImmContext(
        int x, int y, int width, int height
    ) {
        IMinecraft instance = IMinecraft.instance;
        return new GuiImmediateContext(
            new ForgeRenderContext(),
            x, y,
            width, height,
            instance.getMouseX() - x,
            instance.getMouseY() - y,
            instance.getMouseX(),
            instance.getMouseY()
        );
    }

    @Override
    public int getHeight() {
        return Math.max(getDelegate().getHeight(), super.getHeight());
    }

    @Override
    public final boolean mouseInput(int x, int y, int width, int mouseX, int mouseY) {
        return getDelegate().mouseEvent(new MouseEvent.Click(Mouse.getEventButton(), Mouse.getEventButtonState()), getImmContext(x, y, width, getHeight()));
    }

    @Override
    public final boolean keyboardInput() {
        return false; // TODO: handle keyboard input here
    }

    @Override
    public final void render(int x, int y, int width) {
        var context = getImmContext(x, y, width, getHeight());
        context.getRenderContext().pushMatrix();
        context.getRenderContext().translate(context.getRenderOffsetX(), context.getRenderOffsetY(), 0);
        getDelegate().render(context);
        context.getRenderContext().popMatrix();
    }

    @Override
    public final boolean mouseInputOverlay(int x, int y, int width, int mouseX, int mouseY) {
        if (getOverlayDelegate() == null) return false;
        return getOverlayDelegate().mouseEvent(new MouseEvent.Click(Mouse.getEventButton(), Mouse.getEventButtonState()), getImmContext(x, y, width, getOverlayDelegate().getHeight()));
    }

    @Override
    public final void renderOverlay(int x, int y, int width) {
        if (getOverlayDelegate() == null) return;
        getOverlayDelegate().render(getImmContext(x, y, width, getOverlayDelegate().getHeight()));
    }
}
