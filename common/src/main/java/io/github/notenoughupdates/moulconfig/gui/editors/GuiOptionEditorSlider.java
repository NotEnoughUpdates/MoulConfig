package io.github.notenoughupdates.moulconfig.gui.editors;

import io.github.notenoughupdates.moulconfig.gui.GuiComponent;
import io.github.notenoughupdates.moulconfig.gui.component.SliderComponent;
import io.github.notenoughupdates.moulconfig.observer.GetSetter;
import io.github.notenoughupdates.moulconfig.processor.ProcessedOption;
import org.jetbrains.annotations.NotNull;

public class GuiOptionEditorSlider extends ComponentEditor {
    GuiComponent component;

    public GuiOptionEditorSlider(ProcessedOption option, float minValue, float maxValue, float minStep) {
        super(option);
        if (minStep < 0) minStep = 0.01f;

        float floatVal = getFloatValue();

        component = wrapComponent(new SliderComponent(GetSetter.floating(floatVal), minValue, maxValue, minStep, 60));
    }

    public float getFloatValue() {
        return ((Number) option.get()).floatValue();
    }


    @Override
    public @NotNull GuiComponent getDelegate() {
        return component;
    }
}
