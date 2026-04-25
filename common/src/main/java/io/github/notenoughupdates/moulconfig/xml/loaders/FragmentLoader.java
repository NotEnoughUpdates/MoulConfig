package io.github.notenoughupdates.moulconfig.xml.loaders;

import io.github.notenoughupdates.moulconfig.common.MyResourceLocation;
import io.github.notenoughupdates.moulconfig.gui.GuiComponent;
import io.github.notenoughupdates.moulconfig.observer.GetSetter;
import io.github.notenoughupdates.moulconfig.xml.ChildCount;
import io.github.notenoughupdates.moulconfig.xml.XMLContext;
import io.github.notenoughupdates.moulconfig.xml.XMLGuiLoader;
import io.github.notenoughupdates.moulconfig.xml.XMLUniverse;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.LinkedHashMap;
import java.util.Map;

public class FragmentLoader implements XMLGuiLoader.Basic<GuiComponent> {
    @Override
    public GuiComponent createInstance(XMLContext<?> context, Element element) {
        MyResourceLocation location = context.getPropertyFromAttribute(element, new QName("value"), MyResourceLocation.class).get();
        GetSetter<Object> bind = context.getPropertyFromAttribute(element, new QName("bind"), Object.class);
        return context.getUniverse().load(bind != null ? bind.get() : element, location);
    }
    @Override public QName getName() { return XMLUniverse.qName("Fragment"); }
    @Override public ChildCount getChildCount() { return ChildCount.NONE; }
    @Override public Map<String, Boolean> getAttributeNames() {
        Map<String, Boolean> map = new LinkedHashMap<>();
        map.put("value", true);
        map.put("bind", false);
        return map;
    }
}
