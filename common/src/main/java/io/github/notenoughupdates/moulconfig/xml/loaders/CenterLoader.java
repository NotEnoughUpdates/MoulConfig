package io.github.notenoughupdates.moulconfig.xml.loaders;

import io.github.notenoughupdates.moulconfig.gui.component.CenterComponent;
import io.github.notenoughupdates.moulconfig.xml.ChildCount;
import io.github.notenoughupdates.moulconfig.xml.XMLContext;
import io.github.notenoughupdates.moulconfig.xml.XMLGuiLoader;
import io.github.notenoughupdates.moulconfig.xml.XMLUniverse;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.Collections;
import java.util.Map;

public class CenterLoader implements XMLGuiLoader.Basic<CenterComponent> {
    @Override public CenterComponent createInstance(XMLContext<?> context, Element element) { return new CenterComponent(context.getChildFragment(element)); }
    @Override public QName getName() { return XMLUniverse.qName("Center"); }
    @Override public ChildCount getChildCount() { return ChildCount.ONE; }
    @Override public Map<String, Boolean> getAttributeNames() { return Collections.emptyMap(); }
}
