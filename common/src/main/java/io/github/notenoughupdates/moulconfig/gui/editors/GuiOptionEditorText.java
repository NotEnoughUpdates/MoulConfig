package io.github.notenoughupdates.moulconfig.gui.editors;

import io.github.notenoughupdates.moulconfig.common.IMinecraft;
import io.github.notenoughupdates.moulconfig.gui.GuiComponent;
import io.github.notenoughupdates.moulconfig.gui.GuiImmediateContext;
import io.github.notenoughupdates.moulconfig.gui.component.TextComponent;
import io.github.notenoughupdates.moulconfig.gui.component.TextFieldComponent;
import io.github.notenoughupdates.moulconfig.internal.Warnings;
import io.github.notenoughupdates.moulconfig.observer.GetSetter;
import io.github.notenoughupdates.moulconfig.processor.ProcessedOption;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.Collectors;

public class GuiOptionEditorText extends ComponentEditor {

    GuiComponent component;
    private final Set<Character> forbiddenChars;

    public GuiOptionEditorText(ProcessedOption option, String forbidden) {
        super(option);

        this.forbiddenChars = forbidden.chars()
            .mapToObj(c -> (char) c)
            .collect(Collectors.toSet());

        if (option.getType() != String.class) {
            Warnings.warn("@ConfigEditorText " + option.getDebugDeclarationLocation() + " is not a string option.");
        }
    }

    @Override
    public @NotNull GuiComponent getDelegate() {
        if (component == null) {
            component = wrapComponent(new TextFieldComponent(
                (GetSetter<String>) option.intoProperty(),
                80,
                GetSetter.constant(true),
                "",
                IMinecraft.INSTANCE.getDefaultFontRenderer(),
                forbiddenChars
            ));
        }
        return component;
    }
}
