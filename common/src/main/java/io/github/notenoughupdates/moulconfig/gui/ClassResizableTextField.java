package io.github.notenoughupdates.moulconfig.gui;

import io.github.notenoughupdates.moulconfig.gui.component.TextFieldComponent;
import io.github.notenoughupdates.moulconfig.observer.GetSetter;

public class ClassResizableTextField extends TextFieldComponent {
    private int width = 20;

    public ClassResizableTextField(GetSetter<String> text) {
        super(text, 20);
    }

    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public void render(GuiImmediateContext context) {
        super.render(context.translated(0, 0, width, 18));
    }

    @Override
    public boolean mouseEvent(MouseEvent mouseEvent, GuiImmediateContext context) {
        return super.mouseEvent(mouseEvent, context.translated(0, 0, width, 18));
    }

    @Override
    public boolean keyboardEvent(KeyboardEvent event, GuiImmediateContext context) {
        return super.keyboardEvent(event, context.translated(0, 0, width, 18));
    }
}
