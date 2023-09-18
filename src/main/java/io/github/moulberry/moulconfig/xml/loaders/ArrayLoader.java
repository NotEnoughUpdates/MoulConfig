package io.github.moulberry.moulconfig.xml.loaders;

import io.github.moulberry.moulconfig.gui.GuiElementNew;
import io.github.moulberry.moulconfig.gui.elements.GuiElementArray;
import io.github.moulberry.moulconfig.observer.GetSetter;
import io.github.moulberry.moulconfig.observer.ObservableList;
import io.github.moulberry.moulconfig.xml.XMLContext;
import io.github.moulberry.moulconfig.xml.XMLGuiLoader;
import io.github.moulberry.moulconfig.xml.XMLUniverse;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;

public class ArrayLoader implements XMLGuiLoader<GuiElementNew> {
    @Override
    public GuiElementNew createInstance(XMLContext<?> context, Element element) {
        GetSetter<ObservableList> list = context.getPropertyFromAttribute(element, new QName("data"), ObservableList.class);
        return new GuiElementArray<Object>(
                list.get(),
                object -> context.getChildFragment(element, object)
        );
    }

    @Override
    public QName getName() {
        return XMLUniverse.qName("Array");
    }
}
