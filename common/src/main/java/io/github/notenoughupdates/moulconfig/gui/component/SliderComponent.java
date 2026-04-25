package io.github.notenoughupdates.moulconfig.gui.component;

import io.github.notenoughupdates.moulconfig.GuiTextures;
import io.github.notenoughupdates.moulconfig.gui.GuiComponent;
import io.github.notenoughupdates.moulconfig.gui.GuiImmediateContext;
import io.github.notenoughupdates.moulconfig.gui.MouseEvent;
import io.github.notenoughupdates.moulconfig.observer.GetSetter;

public class SliderComponent extends GuiComponent {
    protected final GetSetter<? extends Number> value;
    protected final float minValue;
    protected final float maxValue;
    protected final float minStep;
    private final int width;
    protected boolean clicked;

    public SliderComponent(GetSetter<? extends Number> value, float minValue, float maxValue, float minStep, int width) {
        this.value = value;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.minStep = minStep;
        this.width = width;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return 16;
    }

    @Override
    public void render(GuiImmediateContext context) {
        if (clicked) {
            setValueFromContext(context);
        }
        float currentValue = getValueAsFloat();
        context.getRenderContext().drawTexturedRect(GuiTextures.SLIDER_ON_CAP, 0F, 0F, 4F, (float) context.getHeight());
        context.getRenderContext().drawTexturedRect(GuiTextures.SLIDER_OFF_CAP, (float) (width - 4), 0F, 4F, (float) context.getHeight());
        float coerced = Math.max(minValue, Math.min(maxValue, currentValue));
        int sliderPosition = (int) ((coerced - minValue) / (maxValue - minValue) * context.getWidth());
        if (sliderPosition > 5) {
            context.getRenderContext().drawTexturedRect(GuiTextures.SLIDER_ON_SEGMENT, 4F, 0F, (float) (sliderPosition - 4), (float) context.getHeight());
        }
        if (sliderPosition < context.getWidth() - 5) {
            context.getRenderContext().drawTexturedRect(
                GuiTextures.SLIDER_OFF_SEGMENT,
                (float) sliderPosition,
                0F,
                (float) (context.getWidth() - 4 - sliderPosition),
                (float) context.getHeight()
            );
        }
        for (int i = 0; i <= 3; i++) {
            int notchX = context.getWidth() * i / 4 - 1;
            context.getRenderContext().drawTexturedRect(
                notchX > sliderPosition ? GuiTextures.SLIDER_OFF_NOTCH : GuiTextures.SLIDER_ON_NOTCH,
                (float) notchX,
                (context.getHeight() - 4) / 2F,
                2F,
                4F
            );
        }
        context.getRenderContext().drawTexturedRect(GuiTextures.SLIDER_BUTTON, (float) (sliderPosition - 4), 0F, 8F, (float) context.getHeight());
    }

    public void setValueFromContext(GuiImmediateContext context) {
        float v = context.getMouseX() * (maxValue - minValue) / context.getWidth() + minValue;
        v = Math.min(v, maxValue);
        v = Math.max(v, minValue);
        v = Math.round(v / minStep) * minStep;
        setValue(v);
    }

    @Override
    public boolean mouseEvent(MouseEvent mouseEvent, GuiImmediateContext context) {
        if (!context.getRenderContext().isMouseButtonDown(0)) {
            clicked = false;
        }
        if (context.isHovered() && mouseEvent instanceof MouseEvent.Click) {
            MouseEvent.Click click = (MouseEvent.Click) mouseEvent;
            if (click.getMouseState() && click.getMouseButton() == 0) {
                clicked = true;
            }
        }
        if (clicked) {
            setValueFromContext(context);
            return true;
        }
        return false;
    }

    protected float getValueAsFloat() {
        return value.get().floatValue();
    }

    // Slider components accept any numeric getter; config-backed setters coerce the float write to the declared field type.
    @SuppressWarnings("unchecked")
    protected void setValue(float newValue) {
        ((GetSetter<Number>) value).set(newValue);
    }
}
