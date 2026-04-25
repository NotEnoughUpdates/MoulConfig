package io.github.notenoughupdates.moulconfig.xml.loaders;

import io.github.notenoughupdates.moulconfig.gui.component.ScaleComponent;
import io.github.notenoughupdates.moulconfig.xml.ChildCount;
import io.github.notenoughupdates.moulconfig.xml.XMLContext;
import io.github.notenoughupdates.moulconfig.xml.XMLGuiLoader;
import io.github.notenoughupdates.moulconfig.xml.XMLUniverse;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.Collections;
import java.util.Map;

public class ScaleLoader implements XMLGuiLoader.Basic<ScaleComponent> {
    @Override public ScaleComponent createInstance(XMLContext<?> context, Element element) {
        return new ScaleComponent(context.getChildFragment(element), context.getPropertyFromAttribute(element, new QName("scale"), Float.class));
    }
    @Override public QName getName() { return XMLUniverse.qName("Scale"); }
    @Override public ChildCount getChildCount() { return ChildCount.ONE; }
    @Override public Map<String, Boolean> getAttributeNames() { return Collections.singletonMap("scale", true); }
}
