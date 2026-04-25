package io.github.notenoughupdates.moulconfig.gui.component;

import io.github.notenoughupdates.moulconfig.common.IMinecraft;
import io.github.notenoughupdates.moulconfig.gui.GuiComponent;
import io.github.notenoughupdates.moulconfig.gui.GuiImmediateContext;
import io.github.notenoughupdates.moulconfig.gui.KeyboardEvent;
import io.github.notenoughupdates.moulconfig.gui.MouseEvent;
import io.github.notenoughupdates.moulconfig.observer.GetSetter;

import java.util.function.BiFunction;

public class SliderWithTextComponent extends SliderComponent {
    private TextFieldComponent componentNumberInput;

    public SliderWithTextComponent(GetSetter<? extends Number> value, float minValue, float maxValue, float minStep, int width) {
        super(value, minValue, maxValue, minStep, width);
    }

    @Override
    public void render(GuiImmediateContext context) {
        context.getRenderContext().translate(-(getWidth() / 3F), 0F);
        super.render(context);
        context.getRenderContext().translate(60F, -5F);
        getComponentNumberInput().render(context.translated(60, -5, getComponentNumberInput().getWidth(), 18));
    }

    private boolean isHovered(GuiImmediateContext context) {
        return context.getMouseX() >= -getWidth() / 3
            && context.getMouseX() < getWidth() - 13
            && context.getMouseY() >= 0
            && context.getMouseY() < context.getHeight();
    }

    @Override
    public boolean mouseEvent(MouseEvent mouseEvent, GuiImmediateContext context) {
        if (!context.getRenderContext().isMouseButtonDown(0)) {
            clicked = false;
        }
        if (isHovered(context) && mouseEvent instanceof MouseEvent.Click) {
            MouseEvent.Click click = (MouseEvent.Click) mouseEvent;
            if (click.getMouseState() && click.getMouseButton() == 0) {
                clicked = true;
            }
        }
        if (clicked) {
            setValueFromContext(context);
            return true;
        }
        return getComponentNumberInput().mouseEvent(mouseEvent, context.translated(45, -5, getComponentNumberInput().getWidth(), 18));
    }

    @Override
    public boolean keyboardEvent(KeyboardEvent event, GuiImmediateContext context) {
        getComponentNumberInput().setShouldExpandToFit(true);
        return getComponentNumberInput().keyboardEvent(event, context);
    }

    @Override
    public void setValueFromContext(GuiImmediateContext context) {
        float v = (context.getMouseX() + getWidth() / 3F) * (maxValue - minValue) / context.getWidth() + minValue;
        v = Math.min(v, maxValue);
        v = Math.max(v, minValue);
        v = Math.round(v / minStep) * minStep;
        setValue(v);
    }

    private TextFieldComponent getComponentNumberInput() {
        if (componentNumberInput == null) {
            componentNumberInput = new TextFieldComponent(new GetSetter<String>() {
                private String editingBuffer = "";

                @Override
                public String get() {
                    if (isInFocus()) {
                        return editingBuffer;
                    }
                    float num;
                    try {
                        num = getValueAsFloat();
                    } catch (NumberFormatException e) {
                        num = 0F;
                    }
                    String stringNum = Float.toString(num);
                    if (stringNum.endsWith(".0")) {
                        stringNum = stringNum.substring(0, stringNum.length() - 2);
                    }
                    editingBuffer = stringNum;
                    return stringNum;
                }

                @Override
                public void set(String newValue) {
                    editingBuffer = newValue;
                    float num;
                    try {
                        num = Float.parseFloat(editingBuffer);
                    } catch (NumberFormatException e) {
                        num = 0F;
                    }
                    setValue(num);
                    editingBuffer = Float.toString(num);
                }
            }, 20, GetSetter.constant(true), "", IMinecraft.INSTANCE.getDefaultFontRenderer());
        }
        return componentNumberInput;
    }

    @Override
    public <T> T foldChildren(T initial, BiFunction<GuiComponent, T, T> visitor) {
        return visitor.apply(getComponentNumberInput(), initial);
    }
}
