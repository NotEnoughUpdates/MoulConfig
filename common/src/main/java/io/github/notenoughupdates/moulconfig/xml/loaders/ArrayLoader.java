package io.github.notenoughupdates.moulconfig.xml.loaders;

import io.github.notenoughupdates.moulconfig.gui.GuiComponent;
import io.github.notenoughupdates.moulconfig.gui.component.ArrayComponent;
import io.github.notenoughupdates.moulconfig.observer.GetSetter;
import io.github.notenoughupdates.moulconfig.observer.ObservableList;
import io.github.notenoughupdates.moulconfig.xml.ChildCount;
import io.github.notenoughupdates.moulconfig.xml.XMLContext;
import io.github.notenoughupdates.moulconfig.xml.XMLGuiLoader;
import io.github.notenoughupdates.moulconfig.xml.XMLUniverse;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.awt.Color;
import java.util.LinkedHashMap;
import java.util.Map;

public class ArrayLoader implements XMLGuiLoader.Basic<GuiComponent> {
    @Override
    @SuppressWarnings("unchecked")
    public GuiComponent createInstance(XMLContext<?> context, Element element) {
        GetSetter<ObservableList> list = context.getPropertyFromAttribute(element, new QName("data"), ObservableList.class);
        return new ArrayComponent(
            list.get(),
            item -> context.getChildFragment(element, item),
            valueOr(context.getPropertyFromAttribute(element, new QName("oddBackground"), Color.class), new Color(0, true)),
            valueOr(context.getPropertyFromAttribute(element, new QName("evenBackground"), Color.class), new Color(0, true))
        );
    }

    private static <T> GetSetter<T> valueOr(GetSetter<T> value, T def) { return value != null ? value : GetSetter.constant(def); }
    @Override public QName getName() { return XMLUniverse.qName("Array"); }
    @Override public ChildCount getChildCount() { return ChildCount.ONE; }
    @Override public Map<String, Boolean> getAttributeNames() {
        Map<String, Boolean> map = new LinkedHashMap<>();
        map.put("data", true);
        map.put("oddBackground", false);
        map.put("evenBackground", false);
        return map;
    }
}
