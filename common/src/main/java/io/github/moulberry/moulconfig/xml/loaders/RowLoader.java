package io.github.moulberry.moulconfig.xml.loaders;

import io.github.moulberry.moulconfig.gui.component.RowComponent;
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

public class RowLoader implements XMLGuiLoader<RowComponent> {
    @Override
    public @NotNull RowComponent createInstance(@NotNull XMLContext<?> context, @NotNull Element element) {
        return new RowComponent(context.getChildFragments(element));
    }

    @Override
    public @NotNull QName getName() {
        return XMLUniverse.qName("Row");
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
