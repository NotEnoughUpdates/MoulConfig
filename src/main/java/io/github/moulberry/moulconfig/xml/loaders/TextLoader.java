package io.github.moulberry.moulconfig.xml.loaders;

import io.github.moulberry.moulconfig.gui.component.TextComponent;
import io.github.moulberry.moulconfig.xml.XMLContext;
import io.github.moulberry.moulconfig.xml.XMLGuiLoader;
import io.github.moulberry.moulconfig.xml.XMLUniverse;
import lombok.var;
import net.minecraft.client.Minecraft;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;

public class TextLoader implements XMLGuiLoader<TextComponent> {
    @Override
    public TextComponent createInstance(XMLContext<?> context, Element element) {
        var string = context.getPropertyFromAttribute(element, new QName("text"), String.class);
        var textAlignment = context.getPropertyFromAttribute(element, new QName("textAlign"), String.class);
        return new TextComponent(
                Minecraft.getMinecraft().fontRendererObj,
                string,
                context.getPropertyFromAttribute(element, new QName("width"), int.class, Minecraft.getMinecraft().fontRendererObj.getStringWidth(string.get())),
                textAlignment == null ? TextComponent.TextAlignment.LEFT : TextComponent.TextAlignment.valueOf(textAlignment.get()),
                context.getPropertyFromAttribute(element, new QName("shadow"), boolean.class, true),
                context.getPropertyFromAttribute(element, new QName("split"), boolean.class, true)
        );
    }

    @Override
    public QName getName() {
        return XMLUniverse.qName("Text");
    }
}
