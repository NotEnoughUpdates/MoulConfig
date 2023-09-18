package io.github.moulberry.moulconfig.xml.loaders;

import io.github.moulberry.moulconfig.gui.elements.GuiElementCenter;
import io.github.moulberry.moulconfig.gui.elements.GuiElementPanel;
import io.github.moulberry.moulconfig.xml.XMLContext;
import io.github.moulberry.moulconfig.xml.XMLGuiLoader;
import io.github.moulberry.moulconfig.xml.XMLUniverse;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;

public class GuiLoader implements XMLGuiLoader<GuiElementCenter> {
    @Override
    public GuiElementCenter createInstance(XMLContext<?> context, Element element) {
        return new GuiElementCenter(new GuiElementPanel(context.getChildFragment(element)));
    }

    @Override
    public QName getName() {
        return XMLUniverse.qName("Gui");
    }
}
