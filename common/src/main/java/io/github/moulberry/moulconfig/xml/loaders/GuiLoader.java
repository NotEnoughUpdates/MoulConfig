package io.github.moulberry.moulconfig.xml.loaders;

import io.github.moulberry.moulconfig.gui.component.CenterComponent;
import io.github.moulberry.moulconfig.gui.component.PanelComponent;
import io.github.moulberry.moulconfig.xml.XMLContext;
import io.github.moulberry.moulconfig.xml.XMLGuiLoader;
import io.github.moulberry.moulconfig.xml.XMLUniverse;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;

public class GuiLoader implements XMLGuiLoader<CenterComponent> {
    @Override
    public CenterComponent createInstance(XMLContext<?> context, Element element) {
        return new CenterComponent(new PanelComponent(context.getChildFragment(element)));
    }

    @Override
    public QName getName() {
        return XMLUniverse.qName("Gui");
    }
}
