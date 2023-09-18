package io.github.moulberry.moulconfig.xml.loaders;

import io.github.moulberry.moulconfig.gui.elements.GuiElementText;
import io.github.moulberry.moulconfig.xml.XMLContext;
import io.github.moulberry.moulconfig.xml.XMLGuiLoader;
import io.github.moulberry.moulconfig.xml.XMLUniverse;
import lombok.var;
import net.minecraft.client.Minecraft;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;

public class TextLoader implements XMLGuiLoader<GuiElementText> {
    @Override
    public GuiElementText createInstance(XMLContext<?> context, Element element) {
        var string = context.getPropertyFromAttribute(element, new QName("text"), String.class);
        var width = context.getPropertyFromAttribute(element, new QName("width"), int.class);
        var textAlignment = context.getPropertyFromAttribute(element, new QName("textAlign"), String.class);
        var shadow = context.getPropertyFromAttribute(element, new QName("shadow"), boolean.class);
        return new GuiElementText(
                Minecraft.getMinecraft().fontRendererObj,
                string,
                width != null ? width.get() : Minecraft.getMinecraft().fontRendererObj.getStringWidth(string.get()),
                textAlignment == null ? GuiElementText.TextAlignment.LEFT : GuiElementText.TextAlignment.valueOf(textAlignment.get()),
                shadow != null ? shadow.get() : true
        );
    }

    @Override
    public QName getName() {
        return XMLUniverse.qName("Text");
    }
}