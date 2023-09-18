package io.github.moulberry.moulconfig.xml.loaders;

import io.github.moulberry.moulconfig.gui.elements.GuiElementRow;
import io.github.moulberry.moulconfig.xml.XMLContext;
import io.github.moulberry.moulconfig.xml.XMLGuiLoader;
import io.github.moulberry.moulconfig.xml.XMLUniverse;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;

public class RowLoader implements XMLGuiLoader<GuiElementRow> {
    @Override
    public GuiElementRow createInstance(XMLContext<?> context, Element element) {
        return new GuiElementRow(context.getChildFragments(element));
    }

    @Override
    public QName getName() {
        return XMLUniverse.qName("Row");
    }
}
