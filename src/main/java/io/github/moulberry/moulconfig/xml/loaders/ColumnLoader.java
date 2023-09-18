package io.github.moulberry.moulconfig.xml.loaders;

import io.github.moulberry.moulconfig.gui.component.ColumnComponent;
import io.github.moulberry.moulconfig.xml.XMLContext;
import io.github.moulberry.moulconfig.xml.XMLGuiLoader;
import io.github.moulberry.moulconfig.xml.XMLUniverse;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;

public class ColumnLoader implements XMLGuiLoader<ColumnComponent> {
    @Override
    public ColumnComponent createInstance(XMLContext<?> context, Element element) {
        return new ColumnComponent(context.getChildFragments(element));
    }

    @Override
    public QName getName() {
        return XMLUniverse.qName("Column");
    }
}
