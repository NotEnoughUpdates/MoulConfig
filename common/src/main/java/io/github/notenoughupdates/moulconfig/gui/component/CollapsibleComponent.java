package io.github.notenoughupdates.moulconfig.gui.component;

import io.github.notenoughupdates.moulconfig.common.IFontRenderer;
import io.github.notenoughupdates.moulconfig.common.IMinecraft;
import io.github.notenoughupdates.moulconfig.gui.GuiComponent;
import io.github.notenoughupdates.moulconfig.gui.GuiImmediateContext;
import io.github.notenoughupdates.moulconfig.gui.KeyboardEvent;
import io.github.notenoughupdates.moulconfig.gui.MouseEvent;
import io.github.notenoughupdates.moulconfig.observer.GetSetter;

import java.util.function.Supplier;

public class CollapsibleComponent extends GuiComponent {
    public static final IFontRenderer fr = IMinecraft.INSTANCE.getDefaultFontRenderer();
    public static final int padding = 2;
    public static final int trim = 3;
    public static final int iconWidth = 9;

    private final Supplier<GuiComponent> title;
    private final Supplier<GuiComponent> body;
    private final GetSetter<Boolean> collapsedState;

    public CollapsibleComponent(Supplier<GuiComponent> title, Supplier<GuiComponent> body) {
        this(title, body, GetSetter.floating(true));
    }

    public CollapsibleComponent(Supplier<GuiComponent> title, Supplier<GuiComponent> body, GetSetter<Boolean> collapsedState) {
        this.title = title;
        this.body = body;
        this.collapsedState = collapsedState;
    }

    @Override
    public int getWidth() {
        return Math.max(title.get().getWidth() + padding + iconWidth, body.get().getWidth());
    }

    @Override
    public int getHeight() {
        int barHeight = Math.max(title.get().getHeight(), fr.getHeight());
        return collapsedState.get() ? barHeight : barHeight + trim + body.get().getHeight();
    }

    @Override
    public void render(GuiImmediateContext context) {
        boolean collapsed = collapsedState.get();
        context.getRenderContext().drawOpenCloseTriangle(!collapsed, 0F, 0F, (float) iconWidth, (float) iconWidth, -1);
        int barHeight = Math.max(title.get().getHeight(), fr.getHeight());
        context.getRenderContext().pushMatrix();
        context.getRenderContext().translate((float) iconWidth, 0F);
        title.get().render(context.translated(iconWidth, 0, context.getWidth() - iconWidth, barHeight));
        context.getRenderContext().popMatrix();

        if (!collapsed) {
            context.getRenderContext().drawColoredRect(0F, barHeight + 1F, (float) context.getWidth(), barHeight + 2F, 0xFF000000);
            context.getRenderContext().pushMatrix();
            context.getRenderContext().translate(0F, (float) barHeight);
            body.get().render(context.translated(0, barHeight, context.getWidth(), context.getHeight() - barHeight));
            context.getRenderContext().popMatrix();
        }
    }

    @Override
    public boolean mouseEvent(MouseEvent mouseEvent, GuiImmediateContext context) {
        int barHeight = Math.max(title.get().getHeight(), fr.getHeight());
        if (mouseEvent instanceof MouseEvent.Click && context.translated(0, 0, context.getWidth(), barHeight).isHovered()) {
            if (((MouseEvent.Click) mouseEvent).getMouseState()) {
                collapsedState.set(!collapsedState.get());
            }
            return true;
        }
        return title.get().mouseEvent(mouseEvent, context.translated(iconWidth, 0, context.getWidth() - iconWidth, barHeight))
            || body.get().mouseEvent(mouseEvent, context.translated(0, barHeight, context.getWidth(), context.getHeight() - barHeight));
    }

    @Override
    public boolean keyboardEvent(KeyboardEvent event, GuiImmediateContext context) {
        int barHeight = Math.max(title.get().getHeight(), fr.getHeight());
        return title.get().keyboardEvent(event, context.translated(iconWidth, 0, context.getWidth() - iconWidth, barHeight))
            || body.get().keyboardEvent(event, context.translated(0, barHeight, context.getWidth(), context.getHeight() - barHeight));
    }
}
