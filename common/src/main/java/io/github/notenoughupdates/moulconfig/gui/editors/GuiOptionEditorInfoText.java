package io.github.notenoughupdates.moulconfig.gui.editors;

import io.github.notenoughupdates.moulconfig.gui.GuiComponent;
import io.github.notenoughupdates.moulconfig.gui.GuiImmediateContext;
import io.github.notenoughupdates.moulconfig.gui.component.TextComponent;
import io.github.notenoughupdates.moulconfig.processor.ProcessedOption;
import org.jetbrains.annotations.NotNull;

public class GuiOptionEditorInfoText extends ComponentEditor {

    GuiComponent component;

    public GuiOptionEditorInfoText(ProcessedOption option, String infoTitle) {
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
                new TextComponent(infoTitle, 80, TextComponent.TextAlignment.CENTER).render(context);
            }
        });
    }


    @Override
    public @NotNull GuiComponent getDelegate() {
        return component;
    }
}
