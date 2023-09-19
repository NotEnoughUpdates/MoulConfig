package io.github.moulberry.moulconfig.xml.loaders;

import io.github.moulberry.moulconfig.gui.component.RowComponent;
import io.github.moulberry.moulconfig.xml.XMLContext;
import io.github.moulberry.moulconfig.xml.XMLGuiLoader;
import io.github.moulberry.moulconfig.xml.XMLUniverse;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;

public class RowLoader implements XMLGuiLoader<RowComponent> {
    @Override
    public @NotNull RowComponent createInstance(@NotNull XMLContext<?> context, @NotNull Element element) {
        return new RowComponent(context.getChildFragments(element));
    }

    @Override
    public QName getName() {
        return XMLUniverse.qName("Row");
    }
}
