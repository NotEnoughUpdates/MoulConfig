package io.github.notenoughupdates.moulconfig.gui.editors;

import io.github.notenoughupdates.moulconfig.gui.GuiComponent;
import io.github.notenoughupdates.moulconfig.gui.GuiImmediateContext;
import io.github.notenoughupdates.moulconfig.gui.component.TextComponent;
import io.github.notenoughupdates.moulconfig.processor.ProcessedOption;
import org.jetbrains.annotations.NotNull;

public class GuiOptionEditorText extends ComponentEditor {

    GuiComponent component;

    public GuiOptionEditorText(ProcessedOption option) {
        super(option);

        component = wrapComponent(new GuiComponent() {
            @Override
            public int getWidth() {
                return 80;
            }

            @Override
            public int getHeight() {
                return 15;
            }

            @Override
            public void render(@NotNull GuiImmediateContext context) {
                if (context == null) return;
                new TextComponent((String) option.get(), 80, TextComponent.TextAlignment.CENTER).render(context);
            }


        });
    }

    @Override
    public @NotNull GuiComponent getDelegate() {
        return component;
    }
}
