package io.github.notenoughupdates.moulconfig.platform;

import io.github.notenoughupdates.moulconfig.common.IMinecraft;
import io.github.notenoughupdates.moulconfig.gui.*;
import lombok.Getter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
#if MC > 12107
import net.minecraft.client.gui.Click;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
#endif
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class MoulConfigScreenComponent extends Screen {
    @Getter
    final GuiContext guiContext;
    @Getter
    final @Nullable Screen previousScreen;

    public MoulConfigScreenComponent(
        Text title,
        GuiContext guiContext,
        @Nullable Screen previousScreen) {
        super(title);
        this.guiContext = guiContext;
        this.previousScreen = previousScreen;
        guiContext.setCloseRequestHandler(this::close);
    }

    public GuiImmediateContext createContext() {
        return createContext(null);
    }

    public GuiImmediateContext createContext(@Nullable DrawContext drawContext) {
        assert client != null;
        var im = IMinecraft.INSTANCE;
        var mousePos = im.getMousePositionHF();
        var x = mousePos.getFirst().intValue();
        var y = mousePos.getSecond().intValue();
        return new GuiImmediateContext(
            new MoulConfigRenderContext(drawContext != null ? drawContext : MoulConfigPlatform.makeDrawContext()),
            0, 0,
            im.getScaledWidth(),
            im.getScaledHeight(),
            x, y, x, y,
            mousePos.getFirst().floatValue(),
            mousePos.getSecond().floatValue()
        );
    }

    @Override
    public void close() {
        if (guiContext.onBeforeClose() == CloseEventListener.CloseAction.NO_OBJECTIONS_TO_CLOSE)
            super.close();
    }

    @Override
    public void removed() {
        super.removed();
        guiContext.onAfterClose();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        var ctx = createContext(context);
        guiContext.getRoot().render(ctx);
        ctx.getRenderContext().renderExtraLayers();
    }

    #if MC < 12109
    @Override
    public boolean charTyped(char chr, int modifiers) {
        return guiContext.getRoot().keyboardEvent(new KeyboardEvent.CharTyped(chr), createContext());
    }
    #else
    @Override
    public boolean charTyped(CharInput input){
        return guiContext.getRoot().keyboardEvent(new KeyboardEvent.CharTyped((char) input.codepoint()), createContext());
    }
    #endif

    #if MC < 12109
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (guiContext.root.keyboardEvent(new KeyboardEvent.KeyPressed(keyCode, scanCode, true), createContext()))
            return true;
        if (keyCode == InputUtil.GLFW_KEY_ESCAPE) {
            if (guiContext.getFocusedElement() != null) {
                guiContext.setFocusedElement(null);
            } else {
                close();
            }
            return true;
        }
        return false;
    }
    #else
    @Override
    public boolean keyPressed(KeyInput input) {
        if (guiContext.root.keyboardEvent(new KeyboardEvent.KeyPressed(input.key(), input.scancode(), true), createContext()))
            return true;
        if (input.key() == InputUtil.GLFW_KEY_ESCAPE) {
            if (guiContext.getFocusedElement() != null) {
                guiContext.setFocusedElement(null);
            } else {
                close();
            }
            return true;
        }
        return false;
    }
    #endif

    #if MC < 12109
    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return guiContext.root.keyboardEvent(
            new KeyboardEvent.KeyPressed(keyCode, scanCode, false),
            createContext()
        );
    }
    #else
    @Override
    public boolean keyReleased(KeyInput input) {
        return guiContext.root.keyboardEvent(
            new KeyboardEvent.KeyPressed(input.key(), input.scancode(), false),
            createContext()
        );
    }
    #endif

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        var ctx = createContext();
        var dx = (int) mouseX;
        var dy = (int) mouseY;
        var event = new MouseEvent.Move(
            ((float) mouseX) - ctx.getMouseXHF(),
            ((float) mouseY) - ctx.getMouseYHF()
        );
        ctx = new GuiImmediateContext(
            ctx.getRenderContext(),
            ctx.getRenderOffsetX(),
            ctx.getRenderOffsetY(),
            ctx.getWidth(),
            ctx.getHeight(),
            dx, dy,
            dx, dy,
            (float) mouseX, (float) mouseY
        );

        guiContext.getRoot().mouseEvent(event, ctx);
    }

    #if MC < 12109
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return guiContext.root.mouseEvent(
            new MouseEvent.Click(button, true), createContext()
        );
    }
    #else
    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        return guiContext.root.mouseEvent(
            new MouseEvent.Click(click.button(), true), createContext()
        );
    }
    #endif

    #if MC < 12109
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return guiContext.root.mouseEvent(
            new MouseEvent.Click(button, false), createContext()
        );
    }
    #else
    @Override
    public boolean mouseReleased(Click click) {
        return guiContext.root.mouseEvent(
            new MouseEvent.Click(click.button(), false), createContext()
        );
    }
    #endif

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return guiContext.root.mouseEvent(
            new MouseEvent.Scroll(
                ((float) verticalAmount)
            ),
            createContext()
        );
    }

    #if MC < 12109
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return true;
    }
    #else
    @Override
    public boolean mouseDragged(Click click, double offsetX, double offsetY) {
        return true;
    }
    #endif
}
