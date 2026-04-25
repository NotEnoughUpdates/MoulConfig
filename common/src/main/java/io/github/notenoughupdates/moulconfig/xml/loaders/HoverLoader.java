package io.github.notenoughupdates.moulconfig.xml.loaders;

import io.github.notenoughupdates.moulconfig.common.text.StructuredText;
import io.github.notenoughupdates.moulconfig.gui.component.HoverComponent;
import io.github.notenoughupdates.moulconfig.internal.StructuredTextHelper;
import io.github.notenoughupdates.moulconfig.observer.GetSetter;
import io.github.notenoughupdates.moulconfig.xml.ChildCount;
import io.github.notenoughupdates.moulconfig.xml.XMLContext;
import io.github.notenoughupdates.moulconfig.xml.XMLGuiLoader;
import io.github.notenoughupdates.moulconfig.xml.XMLUniverse;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class HoverLoader implements XMLGuiLoader.Basic<HoverComponent> {
    @Override
    @SuppressWarnings("unchecked")
    public HoverComponent createInstance(XMLContext<?> context, Element element) {
        GetSetter<List> list = context.getPropertyFromAttribute(element, new QName("lines"), List.class);
        return new HoverComponent(context.getChildFragment(element), () -> {
            List<StructuredText> result = new ArrayList<>();
            for (Object item : list.get()) {
                result.add(StructuredTextHelper.mapStringOrStructuredText(item));
            }
            return result;
        });
    }
    @Override public QName getName() { return XMLUniverse.qName("Hover"); }
    @Override public ChildCount getChildCount() { return ChildCount.ONE; }
    @Override public Map<String, Boolean> getAttributeNames() { return Collections.singletonMap("lines", true); }
}
