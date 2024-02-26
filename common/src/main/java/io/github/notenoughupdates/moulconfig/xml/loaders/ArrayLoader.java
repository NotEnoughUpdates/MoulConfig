package io.github.notenoughupdates.moulconfig.xml.loaders;

import io.github.notenoughupdates.moulconfig.gui.GuiComponent;
import io.github.notenoughupdates.moulconfig.gui.component.ArrayComponent;
import io.github.notenoughupdates.moulconfig.internal.MapOfs;
import io.github.notenoughupdates.moulconfig.observer.GetSetter;
import io.github.notenoughupdates.moulconfig.observer.ObservableList;
import io.github.notenoughupdates.moulconfig.xml.ChildCount;
import io.github.notenoughupdates.moulconfig.xml.XMLContext;
import io.github.notenoughupdates.moulconfig.xml.XMLGuiLoader;
import io.github.notenoughupdates.moulconfig.xml.XMLUniverse;
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
