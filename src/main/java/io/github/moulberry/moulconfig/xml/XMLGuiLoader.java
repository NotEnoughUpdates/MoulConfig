package io.github.moulberry.moulconfig.xml;

import io.github.moulberry.moulconfig.gui.GuiElementNew;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;

public interface XMLGuiLoader<T extends GuiElementNew> {
    T createInstance(XMLContext<?> context, Element element);

    QName getName();
}
