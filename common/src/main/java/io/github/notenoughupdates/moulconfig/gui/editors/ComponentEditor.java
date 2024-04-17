package io.github.notenoughupdates.moulconfig.gui.editors;

import io.github.notenoughupdates.moulconfig.DescriptionRendereringBehaviour;
import io.github.notenoughupdates.moulconfig.common.IMinecraft;
import io.github.notenoughupdates.moulconfig.common.RenderContext;
import io.github.notenoughupdates.moulconfig.gui.*;
import io.github.notenoughupdates.moulconfig.gui.component.CenterComponent;
import io.github.notenoughupdates.moulconfig.gui.component.PanelComponent;
import io.github.notenoughupdates.moulconfig.processor.ProcessedOption;
import lombok.Getter;
import lombok.val;
import lombok.var;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class ComponentEditor extends GuiOptionEditor {
    private static final int HEIGHT = 45;
    protected ComponentEditor(ProcessedOption option) {
        super(option);
    }

    public abstract @NotNull GuiComponent getDelegate();

    private @Nullable GuiComponent overlay;
    @Getter
    private int overlayX, overlayY;

    public void closeOverlay() {
        this.overlay = null;
    }

    public void openOverlay(GuiComponent overlay, int overlayX, int overlayY) {
        this.overlay = overlay;
        this.overlayX = overlayX;
        this.overlayY = overlayY;
    }

    public @Nullable GuiComponent getOverlayDelegate() {
        return overlay;
    }

    public GuiImmediateContext getImmContext(
        int x, int y, int width, int height, RenderContext renderContext
    ) {
        IMinecraft instance = IMinecraft.instance;
        return new GuiImmediateContext(
            renderContext,
            x, y,
            width, height,
            instance.getMouseX() - x,
            instance.getMouseY() - y,
            instance.getMouseX(),
            instance.getMouseY(),
            (float) instance.getMouseXHF() - x,
            (float) instance.getMouseYHF() - y
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
            int descriptionHeight = option.config.getDescriptionBehaviour(option) != DescriptionRendereringBehaviour.EXPAND_PANEL ? HEIGHT : context.getHeight();
            while (true) {
                lines = fr.splitText(option.desc, (int) (width * 2 / 3 / scale - 10));
                if (lines.size() * scale * (fr.getHeight() + 1) + 10 < descriptionHeight)
                    break;
                scale -= 1 / 8f;
                if (scale < 1 / 16f) break;
            }
            context.getRenderContext().pushMatrix();
            context.getRenderContext().translate(5 + width / 3, 5, 0);
            context.getRenderContext().scale(scale, scale, 1);
            context.getRenderContext().translate(0, ((descriptionHeight - 10) - (fr.getHeight() + 1) * (lines.size() - 1) * scale) / 2F, 0);
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

    private int lastRenderX, lastRenderY, lastRenderWidth, lastRenderHeight;

    @Override
    public final boolean mouseInput(int x, int y, int width, int mouseX, int mouseY, MouseEvent mouseEvent) {
        return getDelegate().mouseEvent(mouseEvent, getImmContext(x, y, width, getHeight(), IMinecraft.instance.provideTopLevelRenderContext()));
    }

    @Override
    public final boolean keyboardInput(KeyboardEvent keyboardEvent) {
        val ctx = getImmContext(lastRenderX, lastRenderY, lastRenderWidth, lastRenderHeight, IMinecraft.instance.provideTopLevelRenderContext());
        val overlay = getOverlayDelegate();
        if (overlay != null) {
            overlay.foldRecursive((Void) null, (comp, _void) -> {
                comp.setContext(getDelegate().getContext());
                return _void;
            });
            if (overlay.keyboardEvent(keyboardEvent, ctx))
                return true;
        }
        if (getDelegate().keyboardEvent(keyboardEvent, ctx))
            return true;
        return false;
    }

    @Override
    public void setGuiContext(GuiContext guiContext) {
        getDelegate().setContext(guiContext);
    }

    @Override
    public final void render(RenderContext renderContext, int x, int y, int width) {
        // TODO: remove this
        lastRenderX = x;
        lastRenderY = y;
        lastRenderWidth = width;
        lastRenderHeight = getHeight();

        var context = getImmContext(x, y, width, getHeight(), renderContext);
        context.getRenderContext().pushMatrix();
        context.getRenderContext().translate(context.getRenderOffsetX(), context.getRenderOffsetY(), 0);
        getDelegate().render(context);
        context.getRenderContext().popMatrix();
    }

    @Override
    public final boolean mouseInputOverlay(int x, int y, int width, int mouseX, int mouseY, MouseEvent event) {
        if (overlay == null) return false;
        overlay.foldRecursive((Void) null, (comp, _void) -> {
            comp.setContext(getDelegate().getContext());
            return _void;
        });
        return overlay.mouseEvent(event, getImmContext(overlayX, overlayY, overlay.getWidth(), overlay.getHeight(), IMinecraft.instance.provideTopLevelRenderContext()));
    }

    @Override
    public final void renderOverlay(int x, int y, int width) {
        if (overlay == null) return;
        overlay.foldRecursive((Void) null, (comp, _void) -> {
            comp.setContext(getDelegate().getContext());
            return _void;
        });
        val ctx = getImmContext(overlayX, overlayY, overlay.getWidth(), overlay.getHeight(), IMinecraft.instance.provideTopLevelRenderContext());
        ctx.getRenderContext().translate(overlayX, overlayY, 0);
        overlay.render(ctx);
    }
}
