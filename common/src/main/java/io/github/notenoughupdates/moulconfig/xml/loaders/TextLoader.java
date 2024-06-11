package io.github.notenoughupdates.moulconfig.xml.loaders;

import io.github.notenoughupdates.moulconfig.common.IMinecraft;
import io.github.notenoughupdates.moulconfig.gui.component.TextComponent;
import io.github.notenoughupdates.moulconfig.internal.MapOfs;
import io.github.notenoughupdates.moulconfig.xml.ChildCount;
import io.github.notenoughupdates.moulconfig.xml.XMLContext;
import io.github.notenoughupdates.moulconfig.xml.XMLGuiLoader;
import io.github.notenoughupdates.moulconfig.xml.XMLUniverse;
import lombok.var;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.Map;

public class TextLoader implements XMLGuiLoader.Basic<TextComponent> {
    @Override
    public @NotNull TextComponent createInstance(@NotNull XMLContext<?> context, @NotNull Element element) {
        var string = context.getPropertyFromAttribute(element, new QName("text"), String.class);
        var textAlignment = context.getPropertyFromAttribute(element, new QName("textAlign"), String.class);
        return new TextComponent(
            IMinecraft.instance.getDefaultFontRenderer(),
            string,
            context.getPropertyFromAttribute(element, new QName("width"), int.class, IMinecraft.instance.getDefaultFontRenderer().getStringWidth(string.get())),
            textAlignment == null ? TextComponent.TextAlignment.LEFT : TextComponent.TextAlignment.valueOf(textAlignment.get()),
            context.getPropertyFromAttribute(element, new QName("shadow"), boolean.class, true),
            context.getPropertyFromAttribute(element, new QName("split"), boolean.class, true)
        );
    }

    @Override
    public @NotNull QName getName() {
        return XMLUniverse.qName("Text");
    }

    @Override
    public @NotNull ChildCount getChildCount() {
        return ChildCount.NONE;
    }

    @Override
    public @NotNull @Unmodifiable Map<String, Boolean> getAttributeNames() {
        return MapOfs.of(
            "text", true,
            "textAlign", false,
            "width", false,
            "shadow", false,
            "split", false
        );
    }
}
