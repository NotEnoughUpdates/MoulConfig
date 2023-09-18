package io.github.moulberry.moulconfig.xml.loaders;

import io.github.moulberry.moulconfig.gui.elements.GuiElementButton;
import io.github.moulberry.moulconfig.xml.XMLContext;
import io.github.moulberry.moulconfig.xml.XMLGuiLoader;
import io.github.moulberry.moulconfig.xml.XMLUniverse;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;

public class ButtonLoader implements XMLGuiLoader<GuiElementButton> {
    @Override
    public GuiElementButton createInstance(XMLContext<?> context, Element element) {
        return new GuiElementButton(
                context.getChildFragment(element),
                context.getPropertyFromAttribute(element, new QName("margin"), int.class, 2),
                context.getMethodFromAttribute(element, new QName("onClick"))
        );
    }

    @Override
    public QName getName() {
        return XMLUniverse.qName("Button");
    }
}
