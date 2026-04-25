package io.github.notenoughupdates.moulconfig.xml;

import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

public class XSDGenerator {
    private final XMLUniverse universe;
    private final String nameSpace;
    public final org.w3c.dom.Document document;
    public final String XMLNS_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
    private final Map<String, String> extraNamespaceMap;
    private final Element root;

    public XSDGenerator(XMLUniverse universe, String nameSpace) {
        this.universe = universe;
        this.nameSpace = nameSpace;
        try {
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        extraNamespaceMap = new LinkedHashMap<>();
        int nextId = 0;
        for (XMLGuiLoader<?> loader : universe.guiElements.values()) {
            String namespace = loader.getName().getNamespaceURI();
            if (!extraNamespaceMap.containsKey(namespace)) {
                extraNamespaceMap.put(namespace, XMLUniverse.MOULCONFIG_XML_NS.equals(namespace) ? "moulconfig" : "extrans" + nextId++);
            }
        }
        root = document.createElementNS(XMLNS_XML_SCHEMA, "schema");
        root.setPrefix("xs");
        root.setAttribute("targetNamespace", nameSpace);
        root.setAttribute("elementFormDefault", "qualified");
        root.setAttribute("xmlns", nameSpace);
        for (Map.Entry<String, String> entry : extraNamespaceMap.entrySet()) {
            root.setAttribute("xmlns:" + entry.getValue(), entry.getKey());
        }
        document.appendChild(root);
    }

    public static void main(String[] args) {
        XMLUniverse universe = XMLUniverse.getDefaultUniverse();
        XSDGenerator generator = new XSDGenerator(universe, XMLUniverse.MOULCONFIG_XML_NS);
        generator.writeAll();
        generator.dumpToFile(new File("MoulConfig.xsd"));
    }

    public String getXMLNS_XML_SCHEMA() {
        return XMLNS_XML_SCHEMA;
    }

    public void dumpToFile(File file) {
        try {
            TransformerFactory.newInstance().newTransformer().transform(new DOMSource(document), new StreamResult(file));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void writeAll() {
        if (XMLUniverse.MOULCONFIG_XML_NS.equals(nameSpace)) {
            writeBaseCases();
        }
        for (XMLGuiLoader<?> type : universe.guiElements.values()) {
            if (type.getName().getNamespaceURI().equals(nameSpace)) writeType(type);
        }
        for (XMLGuiLoader<?> type : universe.guiElements.values()) {
            if (type.getName().getNamespaceURI().equals(nameSpace)) writeElement(type);
        }
    }

    public Element createChild(Element base, String nameSpace, String local) {
        Element newElement = document.createElementNS(nameSpace, local);
        if (XMLNS_XML_SCHEMA.equals(nameSpace)) newElement.setPrefix("xs");
        base.appendChild(newElement);
        return newElement;
    }

    public void writeBaseCases() {
        Element anyWidget = createChild(root, XMLNS_XML_SCHEMA, "element");
        anyWidget.setAttribute("name", "AnyWidget");
        anyWidget.setAttribute("abstract", "true");
        Element widgetLess = createChild(root, XMLNS_XML_SCHEMA, "complexType");
        widgetLess.setAttribute("name", "Widgetless");
        Element singleWidget = createChild(root, XMLNS_XML_SCHEMA, "complexType");
        singleWidget.setAttribute("name", "SingleWidget");
        createChild(createChild(singleWidget, XMLNS_XML_SCHEMA, "sequence"), XMLNS_XML_SCHEMA, "element")
            .setAttribute("ref", "moulconfig:AnyWidget");

        Element multiWidget = createChild(root, XMLNS_XML_SCHEMA, "complexType");
        multiWidget.setAttribute("name", "MultiWidget");
        Element multiSequence = createChild(multiWidget, XMLNS_XML_SCHEMA, "sequence");
        createChild(multiSequence, XMLNS_XML_SCHEMA, "element").setAttribute("ref", "moulconfig:AnyWidget");
        multiSequence.setAttribute("minOccurs", "0");
        multiSequence.setAttribute("maxOccurs", "unbounded");

        Element twoWidget = createChild(root, XMLNS_XML_SCHEMA, "complexType");
        twoWidget.setAttribute("name", "TwoWidget");
        Element twoSequence = createChild(twoWidget, XMLNS_XML_SCHEMA, "sequence");
        createChild(twoSequence, XMLNS_XML_SCHEMA, "element").setAttribute("ref", "moulconfig:AnyWidget");
        twoSequence.setAttribute("minOccurs", "2");
        twoSequence.setAttribute("maxOccurs", "2");
    }

    public void writeType(XMLGuiLoader<?> type) {
        type.emitXSDType(this, root);
    }

    public void writeElement(XMLGuiLoader<?> type) {
        Element typeNode = createChild(root, XMLNS_XML_SCHEMA, "element");
        typeNode.setAttribute("name", type.getName().getLocalPart());
        typeNode.setAttribute("type", type.getName().getLocalPart());
        typeNode.setAttribute("substitutionGroup", "moulconfig:AnyWidget");
    }

    public Element emitBasicType(XMLGuiLoader.Basic<?> type) {
        Element typeNode = createChild(root, XMLNS_XML_SCHEMA, "complexType");
        typeNode.setAttribute("name", type.getName().getLocalPart());
        Element complexContent = createChild(typeNode, XMLNS_XML_SCHEMA, "complexContent");
        Element extension = createChild(complexContent, XMLNS_XML_SCHEMA, "extension");
        String base;
        switch (type.getChildCount()) {
            case NONE: base = "moulconfig:Widgetless"; break;
            case ONE: base = "moulconfig:SingleWidget"; break;
            case ANY: base = "moulconfig:MultiWidget"; break;
            case TWO: base = "moulconfig:TwoWidget"; break;
            default: throw new IllegalStateException("Unknown child count");
        }
        extension.setAttribute("base", base);
        for (Map.Entry<String, Boolean> entry : type.getAttributeNames().entrySet()) {
            Element attribute = createChild(extension, XMLNS_XML_SCHEMA, "attribute");
            attribute.setAttribute("name", entry.getKey());
            if (entry.getValue()) attribute.setAttribute("use", "required");
        }
        return typeNode;
    }
}
