package io.github.notenoughupdates.moulconfig.internal;

import io.github.notenoughupdates.moulconfig.common.text.StructuredStyle;
import io.github.notenoughupdates.moulconfig.common.text.StructuredText;
import lombok.Value;
import net.minecraft.util.IChatComponent;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

@Value
public class StructuredTextImpl implements StructuredText.Mutable {
    IChatComponent chatComponent;

    public static @NotNull IChatComponent unwrap(@NotNull StructuredText structuredText) {
        return ((StructuredTextImpl) structuredText).chatComponent;
    }

    public static @NotNull StructuredText.Mutable wrap(@NotNull IChatComponent it) {
        return new StructuredTextImpl(it);
    }

    @Override
    public Mutable copyShallow() {
        return new StructuredTextImpl(chatComponent.createCopy());
    }

    @Override
    public @NotNull String getText() {
        return chatComponent.getUnformattedText();
    }

    @Override
    public @NotNull Stream<@NotNull StructuredText> getChildren() {
        return chatComponent.getSiblings().stream().map(StructuredTextImpl::wrap);
    }

    @Override
    public @NotNull StructuredText.Mutable append(@NotNull StructuredText text) {
        chatComponent.appendSibling(unwrap(text));
        return this;
    }

    @Override
    public @NotNull StructuredStyle getStyle() {
        return StructuredStyleImpl.wrap(chatComponent.getChatStyle());
    }

    @Override
    public void setStyle(StructuredStyle style) {
        chatComponent.setChatStyle(StructuredStyleImpl.unwrap(style));
    }
}
