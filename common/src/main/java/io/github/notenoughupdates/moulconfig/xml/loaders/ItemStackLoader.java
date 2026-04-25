package io.github.notenoughupdates.moulconfig.xml.loaders;

import io.github.notenoughupdates.moulconfig.common.IItemStack;
import io.github.notenoughupdates.moulconfig.gui.component.ItemStackComponent;
import io.github.notenoughupdates.moulconfig.xml.ChildCount;
import io.github.notenoughupdates.moulconfig.xml.XMLContext;
import io.github.notenoughupdates.moulconfig.xml.XMLGuiLoader;
import io.github.notenoughupdates.moulconfig.xml.XMLUniverse;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.Collections;
import java.util.Map;

public class ItemStackLoader implements XMLGuiLoader.Basic<ItemStackComponent> {
    @Override public ItemStackComponent createInstance(XMLContext<?> context, Element element) {
        return new ItemStackComponent(context.getPropertyFromAttribute(element, new QName("value"), IItemStack.class));
    }
    @Override public QName getName() { return XMLUniverse.qName("ItemStack"); }
    @Override public ChildCount getChildCount() { return ChildCount.NONE; }
    @Override public Map<String, Boolean> getAttributeNames() { return Collections.singletonMap("value", true); }
}
