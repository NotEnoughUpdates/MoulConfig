package io.github.moulberry.moulconfig.xml.loaders;

import io.github.moulberry.moulconfig.gui.GuiComponent;
import io.github.moulberry.moulconfig.gui.component.ArrayComponent;
import io.github.moulberry.moulconfig.internal.MapOfs;
import io.github.moulberry.moulconfig.observer.GetSetter;
import io.github.moulberry.moulconfig.observer.ObservableList;
import io.github.moulberry.moulconfig.xml.ChildCount;
import io.github.moulberry.moulconfig.xml.XMLContext;
import io.github.moulberry.moulconfig.xml.XMLGuiLoader;
import io.github.moulberry.moulconfig.xml.XMLUniverse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.Map;

public class ArrayLoader implements XMLGuiLoader<GuiComponent> {
    @Override
    public @NotNull GuiComponent createInstance(@NotNull XMLContext<?> context, @NotNull Element element) {
        GetSetter<ObservableList> list = context.getPropertyFromAttribute(element, new QName("data"), ObservableList.class);
        return new ArrayComponent<Object>(
            list.get(),
            object -> context.getChildFragment(element, object)
        );
    }

    @Override
    public @NotNull QName getName() {
        return XMLUniverse.qName("Array");
    }

    @Override
    public @NotNull ChildCount getChildCount() {
        return ChildCount.ONE;
    }

    @Override
    public @NotNull @Unmodifiable Map<String, Boolean> getAttributeNames() {
        return MapOfs.of("data", true);
    }


}
