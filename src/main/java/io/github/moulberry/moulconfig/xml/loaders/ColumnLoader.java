package io.github.moulberry.moulconfig.xml.loaders;

import io.github.moulberry.moulconfig.gui.elements.GuiElementColumn;
import io.github.moulberry.moulconfig.xml.XMLContext;
import io.github.moulberry.moulconfig.xml.XMLGuiLoader;
import io.github.moulberry.moulconfig.xml.XMLUniverse;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;

public class ColumnLoader implements XMLGuiLoader<GuiElementColumn> {
    @Override
    public GuiElementColumn createInstance(XMLContext<?> context, Element element) {
        return new GuiElementColumn(context.getChildFragments(element));
    }

    @Override
    public QName getName() {
        return XMLUniverse.qName("Column");
    }
}
