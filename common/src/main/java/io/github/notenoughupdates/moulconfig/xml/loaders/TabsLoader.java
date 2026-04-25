package io.github.notenoughupdates.moulconfig.xml.loaders;

import io.github.notenoughupdates.moulconfig.gui.component.TabComponent;
import io.github.notenoughupdates.moulconfig.observer.GetSetter;
import io.github.notenoughupdates.moulconfig.xml.XMLContext;
import io.github.notenoughupdates.moulconfig.xml.XMLGuiLoader;
import io.github.notenoughupdates.moulconfig.xml.XMLUniverse;
import io.github.notenoughupdates.moulconfig.xml.XSDGenerator;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;

public class TabsLoader implements XMLGuiLoader<TabComponent> {
    @Override
    public TabComponent createInstance(XMLContext<?> context, Element element) {
        NodeList tabElements = element.getElementsByTagName("Tab");
        List<TabComponent.Tab> tabs = new ArrayList<>();
        for (int i = 0; i < tabElements.getLength(); i++) {
            Element tabElement = (Element) tabElements.item(i);
            Element body = (Element) tabElement.getElementsByTagName("Tab.Body").item(0);
            Element header = (Element) tabElement.getElementsByTagName("Tab.Header").item(0);
            tabs.add(new TabComponent.Tab(context.getChildFragment(header), context.getChildFragment(body)));
        }
        GetSetter<Integer> selectedTabIndex = context.getPropertyFromAttribute(element, new QName("selectedTabIndex"), Integer.class);
        if (selectedTabIndex == null) {
            selectedTabIndex = GetSetter.floating(context.getPropertyFromAttribute(element, new QName("initialSelectedTabIndex"), Integer.class, 0));
        }
        return new TabComponent(tabs, selectedTabIndex);
    }

    @Override
    public QName getName() {
        return XMLUniverse.qName("Tabs");
    }

    @Override
    public Element emitXSDType(XSDGenerator generator, Element root) {
        Element typeNode = generator.createChild(root, generator.XMLNS_XML_SCHEMA, "complexType");
        typeNode.setAttribute("name", getName().getLocalPart());
        Element complexContent = generator.createChild(typeNode, generator.XMLNS_XML_SCHEMA, "complexContent");
        Element extension = generator.createChild(complexContent, generator.XMLNS_XML_SCHEMA, "extension");
        extension.setAttribute("base", "Tabs.Content");
        Element attributeTab = generator.createChild(extension, generator.XMLNS_XML_SCHEMA, "attribute");
        attributeTab.setAttribute("name", "selectedTabIndex");
        Element attributeDefaultTab = generator.createChild(extension, generator.XMLNS_XML_SCHEMA, "attribute");
        attributeDefaultTab.setAttribute("name", "initialSelectedTabIndex");

        Element childTypeNode = generator.createChild(root, generator.XMLNS_XML_SCHEMA, "complexType");
        childTypeNode.setAttribute("name", "Tabs.Content");
        Element sequence = generator.createChild(childTypeNode, generator.XMLNS_XML_SCHEMA, "sequence");
        sequence.setAttribute("maxOccurs", "unbounded");
        Element sequenceElement = generator.createChild(sequence, generator.XMLNS_XML_SCHEMA, "element");
        sequenceElement.setAttribute("name", "Tab");

        Element tabType = generator.createChild(sequenceElement, generator.XMLNS_XML_SCHEMA, "complexType");
        Element tabSequence = generator.createChild(tabType, generator.XMLNS_XML_SCHEMA, "sequence");
        Element tabHeader = generator.createChild(tabSequence, generator.XMLNS_XML_SCHEMA, "element");
        tabHeader.setAttribute("name", "Tab.Header");
        tabHeader.setAttribute("type", "SingleWidget");
        Element tabBody = generator.createChild(tabSequence, generator.XMLNS_XML_SCHEMA, "element");
        tabBody.setAttribute("name", "Tab.Body");
        tabBody.setAttribute("type", "SingleWidget");
        return typeNode;
    }
}
