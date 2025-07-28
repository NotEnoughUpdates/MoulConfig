package io.github.notenoughupdates.moulconfig.gui.editors;

import io.github.notenoughupdates.moulconfig.DescriptionRendereringBehaviour;
import io.github.notenoughupdates.moulconfig.TitleRenderingBehaviour;
import io.github.notenoughupdates.moulconfig.common.IMinecraft;
import io.github.notenoughupdates.moulconfig.common.RenderContext;
import io.github.notenoughupdates.moulconfig.common.text.StructuredText;
import io.github.notenoughupdates.moulconfig.gui.*;
import io.github.notenoughupdates.moulconfig.gui.component.CenterComponent;
import io.github.notenoughupdates.moulconfig.gui.component.PanelComponent;
import io.github.notenoughupdates.moulconfig.processor.ProcessedOption;
import lombok.Getter;
import lombok.val;
import lombok.var;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

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

    public boolean isOverlayOpen() {
        return overlay != null;
    }

    // TODO: close overlay on scroll

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
        IMinecraft instance = IMinecraft.INSTANCE;
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

    @NullMarked
    public class EditorComponentWrapper extends PanelComponent {
        public final @Nullable GuiComponent bottomComponent;

        public EditorComponentWrapper(GuiComponent component) {
            this(component, null);
        }

        public EditorComponentWrapper(GuiComponent component, @Nullable GuiComponent bottomComponent) {
            super(component);
            this.bottomComponent = bottomComponent;
        }

        @Override
        public int getWidth() {
            return super.getWidth() + 150;
        }

        protected int getDescriptionHeight() {
            if (option.getConfig().getDescriptionBehaviour(option) == DescriptionRendereringBehaviour.SCALE_TEXT)
                return super.getHeight();
            var fr = IMinecraft.INSTANCE.getDefaultFontRenderer();
            return Math.max(45, fr.splitText(option.getDescription(), 250 * 2 / 3 - 10).size() * (fr.getHeight() + 1) + 10);
        }

        public int getTopHeight() {
            int height = getDescriptionHeight();
            if (option.getConfig().getTitleRenderingBehaviour(option) != TitleRenderingBehaviour.LEFT)
                height += IMinecraft.INSTANCE.getDefaultFontRenderer().getHeight() + 1;
            return Math.max(HEIGHT, height);
        }

        @Override
        public int getHeight() {
            return getTopHeight() + (bottomComponent != null ? bottomComponent.getHeight() + 10 : 0);
        }

        @Override
        protected GuiImmediateContext getChildContext(GuiImmediateContext context) {
            return context.translated(5, 15, context.getWidth() / 3 - 10, context.getHeight() - 15);
        }

        protected int getEffectiveTopHeight(GuiImmediateContext context) {
            return Math.min(context.getHeight(), (getTopHeight()));
        }

        protected GuiImmediateContext getBottomContext(GuiImmediateContext context) {
            int effectiveTopHeight = getEffectiveTopHeight(context);
            return context.translated(5, effectiveTopHeight + bottomOffset, context.getWidth() - 10, context.getHeight() - effectiveTopHeight - bottomOffset - 8);
        }

        protected GuiImmediateContext getTopContext(GuiImmediateContext context) {
            return context.translated(0, 0, context.getWidth(), getEffectiveTopHeight(context));
        }

        int bottomOffset = 0;

        @Override
        public void render(GuiImmediateContext context) {
            context.getRenderContext().drawDarkRect(0, 0, context.getWidth(), context.getHeight() - 2);

            var topContext = getTopContext(context);
            renderTitle(topContext);

            renderDescription(topContext);

            renderElement(topContext);

            context.getRenderContext().pushMatrix();
            context.getRenderContext().translate(5, getEffectiveTopHeight(context) + bottomOffset);
            renderBottomElement(getBottomContext(context));
            context.getRenderContext().popMatrix();
        }

        protected void renderBottomElement(GuiImmediateContext context) {
            if (bottomComponent != null)
                bottomComponent.render(context);
        }

        protected void renderElement(GuiImmediateContext context) {
            context.getRenderContext().pushMatrix();
            context.getRenderContext().translate(5, 15);
            this.getElement().render(getChildContext(context));
            context.getRenderContext().popMatrix();
        }

        protected void renderTitle(GuiImmediateContext context) {
            int width = context.getWidth();
            var minecraft = context.getRenderContext().getMinecraft();
            var fr = minecraft.getDefaultFontRenderer();
            switch (option.getConfig().getTitleRenderingBehaviour(option)) {
                case WIDE_CENTERED_UNDERLINED:
                    context.getRenderContext().drawHorizontalLine(16, 10, width - 10, 0xFF404040);
                    // fallthrough;
                case WIDE_CENTERED:
                    context.getRenderContext().drawStringCenteredScaledMaxWidth(
                        option.getName(), fr, width / 2, 10, true, width - 10, 0xe0e0e0
                    );
                    break;
                case LEFT:
                    context.getRenderContext().drawStringCenteredScaledMaxWidth(
                        option.getName(), fr, width / 6, 10, true, width / 3 - 10, 0xe0e0e0
                    );
                    break;
            }
        }

        @Override
        public boolean mouseEvent(MouseEvent mouseEvent, GuiImmediateContext context) {
            if (super.mouseEvent(mouseEvent, getTopContext(context)))
                return true;
            if (bottomComponent != null && bottomComponent.mouseEvent(mouseEvent, getBottomContext(context)))
                return true;
            return false;
        }

        @Override
        public boolean keyboardEvent(KeyboardEvent event, GuiImmediateContext context) {
            if (super.keyboardEvent(event, getTopContext(context)))
                return true;
            if (bottomComponent != null && bottomComponent.keyboardEvent(event, getBottomContext(context)))
                return true;
            return false;
        }

        protected void renderDescription(@NotNull GuiImmediateContext context) {
            int width = context.getWidth();
            var minecraft = context.getRenderContext().getMinecraft();
            var fr = minecraft.getDefaultFontRenderer();
            int yOffset = option.getConfig().getTitleRenderingBehaviour(option) != TitleRenderingBehaviour.LEFT ? fr.getHeight() + 13 : 5;
            float scale = 1;
            List<StructuredText> lines;
            int descriptionHeight = context.getHeight() - yOffset;
            while (true) {
                lines = fr.splitText(option.getDescription(), (int) (width * 2 / 3 / scale - 10));
                if (lines.size() * scale * (fr.getHeight() + 1) < descriptionHeight)
                    break;
                scale -= 1 / 8f;
                if (scale < 1 / 16f) break;
            }
            context.getRenderContext().pushMatrix();
            context.getRenderContext().translate(5 + width / 3, yOffset);
            context.getRenderContext().scale(scale, scale);
            for (var line : lines) {
                context.getRenderContext().drawString(fr, line, 0, 0, 0xc0c0c0, false);
                context.getRenderContext().translate(0, fr.getHeight() + 1);
            }
            context.getRenderContext().popMatrix();
        }
    }

    protected GuiComponent wrapComponent(GuiComponent component, @Nullable GuiComponent bottomComponent) {
        return new EditorComponentWrapper(
            new CenterComponent(component),
            bottomComponent
        );
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
        return getDelegate().mouseEvent(mouseEvent, getImmContext(x, y, width, getHeight(), IMinecraft.INSTANCE.provideTopLevelRenderContext()));
    }

    @Override
    public final boolean keyboardInput(KeyboardEvent keyboardEvent) {
        val ctx = getImmContext(lastRenderX, lastRenderY, lastRenderWidth, lastRenderHeight, IMinecraft.INSTANCE.provideTopLevelRenderContext());
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
        getDelegate().foldRecursive((Void) null, (comp, _void) -> {
            comp.setContext(guiContext);
            return _void;
        });
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
        context.getRenderContext().translate(context.getRenderOffsetX(), context.getRenderOffsetY());
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
        return overlay.mouseEvent(event, getImmContext(overlayX, overlayY, overlay.getWidth(), overlay.getHeight(), IMinecraft.INSTANCE.provideTopLevelRenderContext()));
    }

    @Override
    public final void renderOverlay(RenderContext context, int x, int y, int width) {
        if (overlay == null) return;
        overlay.foldRecursive((Void) null, (comp, _void) -> {
            comp.setContext(getDelegate().getContext());
            return _void;
        });
        val ctx = getImmContext(overlayX, overlayY, overlay.getWidth(), overlay.getHeight(), context);
        ctx.getRenderContext().translate(overlayX, overlayY);
        overlay.render(ctx);
    }
}
