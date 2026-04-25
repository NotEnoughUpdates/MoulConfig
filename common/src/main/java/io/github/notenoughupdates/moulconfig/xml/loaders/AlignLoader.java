package io.github.notenoughupdates.moulconfig.xml.loaders;

import io.github.notenoughupdates.moulconfig.gui.HorizontalAlign;
import io.github.notenoughupdates.moulconfig.gui.VerticalAlign;
import io.github.notenoughupdates.moulconfig.gui.component.AlignComponent;
import io.github.notenoughupdates.moulconfig.observer.GetSetter;
import io.github.notenoughupdates.moulconfig.xml.ChildCount;
import io.github.notenoughupdates.moulconfig.xml.XMLContext;
import io.github.notenoughupdates.moulconfig.xml.XMLGuiLoader;
import io.github.notenoughupdates.moulconfig.xml.XMLUniverse;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.LinkedHashMap;
import java.util.Map;

public class AlignLoader implements XMLGuiLoader.Basic<AlignComponent> {
    @Override
    public AlignComponent createInstance(XMLContext<?> context, Element element) {
        return new AlignComponent(
            context.getChildFragment(element),
            valueOr(context.getPropertyFromAttribute(element, new QName("horizontal"), HorizontalAlign.class), HorizontalAlign.LEFT),
            valueOr(context.getPropertyFromAttribute(element, new QName("vertical"), VerticalAlign.class), VerticalAlign.TOP)
        );
    }

    private static <T> GetSetter<T> valueOr(GetSetter<T> value, T def) {
        return value != null ? value : GetSetter.constant(def);
    }

    @Override public QName getName() { return XMLUniverse.qName("Align"); }
    @Override public ChildCount getChildCount() { return ChildCount.ONE; }
    @Override public Map<String, Boolean> getAttributeNames() {
        Map<String, Boolean> map = new LinkedHashMap<>();
        map.put("horizontal", false);
        map.put("vertical", false);
        return map;
    }
}
