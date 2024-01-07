package io.github.moulberry.moulconfig.gui.editors;

import io.github.moulberry.moulconfig.DescriptionRendereringBehaviour;
import io.github.moulberry.moulconfig.common.IMinecraft;
import io.github.moulberry.moulconfig.gui.GuiComponent;
import io.github.moulberry.moulconfig.gui.GuiImmediateContext;
import io.github.moulberry.moulconfig.gui.GuiOptionEditor;
import io.github.moulberry.moulconfig.gui.MouseEvent;
import io.github.moulberry.moulconfig.gui.component.CenterComponent;
import io.github.moulberry.moulconfig.gui.component.PanelComponent;
import io.github.moulberry.moulconfig.internal.ForgeRenderContext;
import io.github.moulberry.moulconfig.processor.ProcessedOption;
import lombok.var;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.input.Mouse;

import java.util.List;

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

    public class EditorComponentWrapper extends PanelComponent {
        public EditorComponentWrapper(GuiComponent component) {
            super(component);
        }

        @Override
        public int getWidth() {
            return super.getWidth() + 150;
        }

        @Override
        public int getHeight() {
            if (option.config.getDescriptionBehaviour(option) != DescriptionRendereringBehaviour.EXPAND_PANEL)
                return super.getHeight();
            var fr = IMinecraft.instance.getDefaultFontRenderer();
            return Math.max(45, fr.splitText(option.desc, 250 * 2 / 3 - 10).size() * (fr.getHeight() + 1) + 10);
        }

        @Override
        protected GuiImmediateContext getChildContext(GuiImmediateContext context) {
            return context.translated(5, 13, context.getWidth() / 3 - 10, context.getHeight() - 13);
        }

        @Override
        public void render(@NotNull GuiImmediateContext context) {
            context.getRenderContext().drawDarkRect(0, 0, context.getWidth(), context.getHeight() - 2);

            renderTitle(context);

            renderDescription(context);

            renderElement(context);
        }

        protected void renderElement(@NotNull GuiImmediateContext context) {
            context.getRenderContext().pushMatrix();
            context.getRenderContext().translate(5, 13, 0);
            this.getElement().render(getChildContext(context));
            context.getRenderContext().popMatrix();
        }

        protected void renderTitle(@NotNull GuiImmediateContext context) {
            int width = context.getWidth();
            var minecraft = context.getRenderContext().getMinecraft();
            var fr = minecraft.getDefaultFontRenderer();
            context.getRenderContext().drawStringCenteredScaledMaxWidth(
                option.name, fr, width / 6, 13, true, width / 3 - 10, 0xc0c0c0
            );
        }

        protected void renderDescription(@NotNull GuiImmediateContext context) {
            int width = context.getWidth();
            var minecraft = context.getRenderContext().getMinecraft();
            var fr = minecraft.getDefaultFontRenderer();
            float scale = 1;
            List<String> lines;
            while (true) {
                lines = fr.splitText(option.desc, (int) (width * 2 / 3 / scale - 10));
                if (lines.size() * scale * (fr.getHeight() + 1) + 10 < context.getHeight())
                    break;
                scale -= 1 / 8f;
                if (scale < 1 / 16f) break;
            }
            context.getRenderContext().pushMatrix();
            context.getRenderContext().translate(5 + width / 3, 5, 0);
            context.getRenderContext().scale(scale, scale, 1);
            context.getRenderContext().translate(0, ((context.getHeight() - 10) - (fr.getHeight() + 1) * (lines.size() - 1) * scale) / 2F, 0);
            for (String line : lines) {
                context.getRenderContext().drawString(fr, line, 0, 0, 0xc0c0c0, false);
                context.getRenderContext().translate(0, fr.getHeight() + 1, 0);
            }
            context.getRenderContext().popMatrix();
        }
    }

    protected GuiComponent wrapComponent(GuiComponent component) {
        return new EditorComponentWrapper(
            new CenterComponent(component)
        );
    }

    @Override
    public int getHeight() {
        return Math.max(getDelegate().getHeight(), super.getHeight());
    }

    @Override
    public final boolean mouseInput(int x, int y, int width, int mouseX, int mouseY) {
        if (Mouse.getEventButton() == -1) return false;
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
