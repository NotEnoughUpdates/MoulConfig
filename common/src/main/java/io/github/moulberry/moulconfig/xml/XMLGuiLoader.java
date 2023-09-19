package io.github.moulberry.moulconfig.xml;

import io.github.moulberry.moulconfig.gui.GuiComponent;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;

public interface XMLGuiLoader<T extends GuiComponent> {
    T createInstance(XMLContext<?> context, Element element);

    QName getName();
}
