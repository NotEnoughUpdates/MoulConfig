package io.github.moulberry.moulconfig.gui;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

import java.io.IOException;

@RequiredArgsConstructor
@ToString
public class GuiScreenElementWrapperNew extends GuiScreen {
    @NonNull
    public GuiContext context;

    GuiImmediateContext createContext() {
        int x = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int y = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
        return new GuiImmediateContext(
            width, height,
            x, y, x, y
        );
    }

    @Override
    public void handleKeyboardInput() throws IOException {
        super.handleKeyboardInput();
        context.getRoot().keyboardEvent(createContext());
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        context.getRoot().render(new GuiImmediateContext(
            width, height, mouseX, mouseY, mouseX, mouseY
        ));
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        context.getRoot().mouseEvent(createContext());
    }
}
