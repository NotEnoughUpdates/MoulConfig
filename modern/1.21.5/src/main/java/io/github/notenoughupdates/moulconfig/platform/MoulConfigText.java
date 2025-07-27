package io.github.notenoughupdates.moulconfig.platform;

import io.github.notenoughupdates.moulconfig.common.text.StructuredStyle;
import io.github.notenoughupdates.moulconfig.common.text.StructuredText;
import lombok.Value;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

@Value
public class MoulConfigText implements StructuredText {
    Text text;

    @Override
    public @NotNull String getText() {
        return text.getString();
    }

    public static StructuredText wrap(Text text) {
        return new MoulConfigText(text);
    }

    public static Text unwrap(StructuredText wrappedText) {
        return ((MoulConfigText) wrappedText).text;
    }

    @Override
    public @NotNull Stream<StructuredText> getChildren() {
        return text.getSiblings().stream().map(MoulConfigText::new);
    }

    @NotNull
    @Override
    public StructuredText append(StructuredText text) {
        ((MutableText) this.text).append(unwrap(text));
        return this;
    }

    @Override
    public @NotNull StructuredStyle getStyle() {
        return MoulConfigStyle.wrap(text.getStyle());
    }

    @Override
    public void setStyle(StructuredStyle style) {
        ((MutableText) this.text).setStyle(MoulConfigStyle.unwrap(style));
    }
}
