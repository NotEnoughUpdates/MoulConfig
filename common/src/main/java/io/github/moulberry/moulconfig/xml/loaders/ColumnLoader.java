package io.github.moulberry.moulconfig.xml.loaders;

import io.github.moulberry.moulconfig.gui.component.ColumnComponent;
import io.github.moulberry.moulconfig.internal.MapOfs;
import io.github.moulberry.moulconfig.xml.ChildCount;
import io.github.moulberry.moulconfig.xml.XMLContext;
import io.github.moulberry.moulconfig.xml.XMLGuiLoader;
import io.github.moulberry.moulconfig.xml.XMLUniverse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.Map;

public class ColumnLoader implements XMLGuiLoader<ColumnComponent> {
    @Override
    public @NotNull ColumnComponent createInstance(@NotNull XMLContext<?> context, @NotNull Element element) {
        return new ColumnComponent(context.getChildFragments(element));
    }

    @Override
    public @NotNull QName getName() {
        return XMLUniverse.qName("Column");
    }

    @Override
    public @NotNull ChildCount getChildCount() {
        return ChildCount.ANY;
    }

    @Override
    public @NotNull @Unmodifiable Map<String, Boolean> getAttributeNames() {
        return MapOfs.of();
    }
}
