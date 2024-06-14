package io.github.notenoughupdates.moulconfig.xml

import io.github.notenoughupdates.moulconfig.gui.component.PanelComponent
import org.w3c.dom.Element
import java.io.File
import javax.xml.namespace.QName
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

class XSDGenerator(val universe: XMLUniverse, val nameSpace: String) {
    val document = DocumentBuilderFactory.newInstance()
        .also {
            it.isNamespaceAware = true
        }
        .newDocumentBuilder()
        .newDocument()
    val XMLNS_XML_SCHEMA: String = "http://www.w3.org/2001/XMLSchema"
    val extraNamespaceMap = run {
        var nextId = 0
        universe.guiElements.values.mapTo(mutableSetOf()) { it.name.namespaceURI }.associate {
            if (it == XMLUniverse.MOULCONFIG_XML_NS) it to "moulconfig"
            else it to "extrans${nextId++}"
        }
    }
    val root: Element = document.createElementNS(XMLNS_XML_SCHEMA, "schema")
        .also {
            it.prefix = "xs"
            it.setAttribute(
                "targetNamespace",
                nameSpace
            )
            it.setAttribute("elementFormDefault", "qualified")
            it.setAttribute(
                "xmlns",
                nameSpace
            )
            extraNamespaceMap.forEach { (k, v) ->
                it.setAttribute("xmlns:$v", k)
            }
            document.appendChild(it)
        }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val universe = XMLUniverse.getDefaultUniverse()
            run {
                val generator = XSDGenerator(universe, XMLUniverse.MOULCONFIG_XML_NS)
                generator.writeAll()
                generator.dumpToFile(File("MoulConfig.xsd"))
            }
        }
    }

    fun dumpToFile(file: File) {
        val trans = TransformerFactory.newInstance().newTransformer()
        val source = DOMSource(document)
        val outputStream = StreamResult(file.outputStream())
        trans.transform(source, outputStream)
    }

    fun writeAll() {
        if (this.nameSpace == XMLUniverse.MOULCONFIG_XML_NS) {
            writeBaseCases()
        }
        for (type in universe.guiElements.values) {
            if (type.name.namespaceURI == this.nameSpace)
                writeType(type)
        }
        for (type in universe.guiElements.values) {
            if (type.name.namespaceURI == this.nameSpace)
                writeElement(type)
        }
    }

    fun createChild(base: Element, nameSpace: String, local: String): Element {
        return base.createChild(nameSpace, local)
    }

    @JvmName("createChildInternal")
    private fun Element.createChild(nameSpace: String, local: String): Element {
        val newElement = document.createElementNS(nameSpace, local)
        if (nameSpace == XMLNS_XML_SCHEMA)
            newElement.prefix = "xs"

        appendChild(newElement)
        return newElement
    }

    fun writeBaseCases() {
        val anyWidget = root.createChild(XMLNS_XML_SCHEMA, "element")
        anyWidget.setAttribute("name", "AnyWidget")
        anyWidget.setAttribute("abstract", "true")
        val widgetLess = root.createChild(XMLNS_XML_SCHEMA, "complexType")
        widgetLess.setAttribute("name", "Widgetless")
        val singleWidget = root.createChild(XMLNS_XML_SCHEMA, "complexType")
        singleWidget.setAttribute("name", "SingleWidget")
        singleWidget.createChild(XMLNS_XML_SCHEMA, "sequence").also {
            it.createChild(XMLNS_XML_SCHEMA, "element")
                .setAttribute("ref", "moulconfig:AnyWidget")
        }

        val multiWidget = root.createChild(XMLNS_XML_SCHEMA, "complexType")
        multiWidget.setAttribute("name", "MultiWidget")
        multiWidget.createChild(XMLNS_XML_SCHEMA, "sequence").also {
            it.createChild(XMLNS_XML_SCHEMA, "element")
                .setAttribute("ref", "moulconfig:AnyWidget")
            it.setAttribute("minOccurs", "0")
            it.setAttribute("maxOccurs", "unbounded")
        }

        val twoWidget = root.createChild(XMLNS_XML_SCHEMA, "complexType")
        twoWidget.setAttribute("name", "TwoWidget")
        twoWidget.createChild(XMLNS_XML_SCHEMA, "sequence").also {
            it.createChild(XMLNS_XML_SCHEMA, "element")
                .setAttribute("ref", "moulconfig:AnyWidget")
            it.setAttribute("minOccurs", "2")
            it.setAttribute("maxOccurs", "2")
        }
    }

    fun writeType(type: XMLGuiLoader<*>) {
        type.emitXSDType(this, root)
    }

    fun writeElement(type: XMLGuiLoader<*>) {
        val typeNode = root.createChild(XMLNS_XML_SCHEMA, "element")
        typeNode.setAttribute("name", type.name.localPart)
        typeNode.setAttribute("type", type.name.localPart)
        typeNode.setAttribute("substitutionGroup", "moulconfig:AnyWidget")
    }

    fun emitBasicType(type: XMLGuiLoader.Basic<*>): Element {
        val typeNode = root.createChild(XMLNS_XML_SCHEMA, "complexType")
        typeNode.setAttribute("name", type.name.localPart)
        val complexContent = typeNode.createChild(XMLNS_XML_SCHEMA, "complexContent")
        val extension = complexContent.createChild(XMLNS_XML_SCHEMA, "extension")
        extension.setAttribute(
            "base", when (type.childCount) {
                ChildCount.NONE -> "moulconfig:Widgetless"
                ChildCount.ONE -> "moulconfig:SingleWidget"
                ChildCount.ANY -> "moulconfig:MultiWidget"
                ChildCount.TWO -> "moulconfig:TwoWidget"
            }
        )
        type.attributeNames.forEach { name, required ->
            val attribute = extension.createChild(XMLNS_XML_SCHEMA, "attribute")
            attribute.setAttribute("name", name)
            if (required)
                attribute.setAttribute("use", "required")
        }
        return typeNode
    }
}
