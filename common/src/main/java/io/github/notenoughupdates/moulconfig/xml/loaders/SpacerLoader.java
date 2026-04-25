package io.github.notenoughupdates.moulconfig.xml.loaders;

import io.github.notenoughupdates.moulconfig.gui.component.SpacerComponent;
import io.github.notenoughupdates.moulconfig.observer.GetSetter;
import io.github.notenoughupdates.moulconfig.xml.ChildCount;
import io.github.notenoughupdates.moulconfig.xml.XMLContext;
import io.github.notenoughupdates.moulconfig.xml.XMLGuiLoader;
import io.github.notenoughupdates.moulconfig.xml.XMLUniverse;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.LinkedHashMap;
import java.util.Map;

public class SpacerLoader implements XMLGuiLoader.Basic<SpacerComponent> {
    @Override public SpacerComponent createInstance(XMLContext<?> context, Element element) {
        GetSetter<Integer> width = context.getPropertyFromAttribute(element, new QName("width"), Integer.class);
        GetSetter<Integer> height = context.getPropertyFromAttribute(element, new QName("height"), Integer.class);
        return new SpacerComponent(width != null ? width : GetSetter.constant(0), height != null ? height : GetSetter.constant(0));
    }
    @Override public QName getName() { return XMLUniverse.qName("Spacer"); }
    @Override public ChildCount getChildCount() { return ChildCount.NONE; }
    @Override public Map<String, Boolean> getAttributeNames() {
        Map<String, Boolean> map = new LinkedHashMap<>();
        map.put("width", false);
        map.put("height", false);
        return map;
    }
}
