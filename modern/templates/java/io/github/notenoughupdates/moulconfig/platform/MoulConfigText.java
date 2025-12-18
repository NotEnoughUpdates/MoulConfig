package io.github.notenoughupdates.moulconfig.platform;

import io.github.notenoughupdates.moulconfig.common.text.StructuredStyle;
import io.github.notenoughupdates.moulconfig.common.text.StructuredText;
import lombok.Value;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

@Value
public class MoulConfigText implements StructuredText.Mutable {
    Component text;

    @Override
    public Mutable copyShallow() {
        return new MoulConfigText(text.copy());
    }

    @Override
    public @NotNull String getText() {
        return text.getString();
    }

    public static StructuredText.Mutable wrap(MutableComponent text) {
        return new MoulConfigText(text);
    }

    public static StructuredText wrap(Component text) {
        return new MoulConfigText(text);
    }

    public static Component unwrap(StructuredText wrappedText) {
        return ((MoulConfigText) wrappedText).text;
    }

    @Override
    public @NotNull Stream<StructuredText> getChildren() {
        return text.getSiblings().stream().map(MoulConfigText::new);
    }

    @NotNull
    @Override
    public StructuredText.Mutable append(@NotNull StructuredText text) {
        ((MutableComponent) this.text).append(unwrap(text));
        return this;
    }

    @Override
    public @NotNull StructuredStyle getStyle() {
        return MoulConfigStyle.wrap(text.getStyle());
    }

    @Override
    public void setStyle(StructuredStyle style) {
        ((MutableComponent) this.text).setStyle(MoulConfigStyle.unwrap(style));
    }
}
