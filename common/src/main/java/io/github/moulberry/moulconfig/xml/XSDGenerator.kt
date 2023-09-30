package io.github.moulberry.moulconfig.xml

import org.w3c.dom.Element
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

class XSDGenerator(val universe: XMLUniverse) {
    val document = DocumentBuilderFactory.newInstance()
        .also {
            it.isNamespaceAware = true
        }
        .newDocumentBuilder()
        .newDocument()
    val XMLNS_XML_SCHEMA: String = "http://www.w3.org/2001/XMLSchema"
    val root = document.createElementNS(XMLNS_XML_SCHEMA, "schema")
        .also {
            it.prefix = "xs"
            it.setAttribute("targetNamespace", XMLUniverse.MOULCONFIG_XML_NS)
            it.setAttribute("elementFormDefault", "qualified")
            it.setAttribute("xmlns", XMLUniverse.MOULCONFIG_XML_NS)
            document.appendChild(it)
        }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val generator = XSDGenerator(XMLUniverse.getDefaultUniverse())
            generator.writeAll()
            generator.dumpToFile(File("MoulConfig.xsd"))
        }
    }

    fun dumpToFile(file: File) {
        val trans = TransformerFactory.newInstance().newTransformer()
        val source = DOMSource(document)
        val outputStream = StreamResult(file.outputStream())
        trans.transform(source, outputStream)
    }

    fun writeAll() {
        writeBaseCases()
        writeGroup()
        writeRoot()
        for (type in universe.guiElements.values) {
            writeType(type)
        }
    }

    private fun Element.createChild(nameSpace: String, local: String): Element {
        val newElement = document.createElementNS(nameSpace, local)
        if (nameSpace == XMLNS_XML_SCHEMA)
            newElement.prefix = "xs"

        appendChild(newElement)
        return newElement
    }

    fun writeRoot() {
        val element = root.createChild(XMLNS_XML_SCHEMA, "element")
        element.setAttribute("name", "Root")
        element.setAttribute("type", "Root")
    }

    fun writeGroup() {
        val group = root.createChild(XMLNS_XML_SCHEMA, "group")
        group.setAttribute("name", "AnyWidget")
        val choice = group.createChild(XMLNS_XML_SCHEMA, "choice")
        universe.guiElements.values.forEach {
            if (it.name.localPart == "Root") return@forEach
            require(it.name.namespaceURI == XMLUniverse.MOULCONFIG_XML_NS)
            val element = choice.createChild(XMLNS_XML_SCHEMA, "element")
            element.setAttribute("name", it.name.localPart)
            element.setAttribute("type", it.name.localPart)
        }
    }

    fun writeBaseCases() {
        val widgetLess = root.createChild(XMLNS_XML_SCHEMA, "complexType")
        widgetLess.setAttribute("name", "Widgetless")
        val singleWidget = root.createChild(XMLNS_XML_SCHEMA, "complexType")
        singleWidget.setAttribute("name", "SingleWidget")
        singleWidget.createChild(XMLNS_XML_SCHEMA, "group").also {
            it.setAttribute("ref", "AnyWidget")
        }

        val multiWidget = root.createChild(XMLNS_XML_SCHEMA, "complexType")
        multiWidget.setAttribute("name", "MultiWidget")
        multiWidget.createChild(XMLNS_XML_SCHEMA, "group").also {
            it.setAttribute("ref", "AnyWidget")
            it.setAttribute("minOccurs", "0")
            it.setAttribute("maxOccurs", "unbounded")
        }
    }

    fun writeType(type: XMLGuiLoader<*>) {
        val typeNode = root.createChild(XMLNS_XML_SCHEMA, "complexType")
        typeNode.setAttribute("name", type.name.localPart)
        val complexContent = typeNode.createChild(XMLNS_XML_SCHEMA, "complexContent")
        val extension = complexContent.createChild(XMLNS_XML_SCHEMA, "extension")
        extension.setAttribute(
            "base", when (type.childCount) {
                ChildCount.NONE -> "Widgetless"
                ChildCount.ONE -> "SingleWidget"
                ChildCount.ANY -> "MultiWidget"
            }
        )
        type.attributeNames.forEach { name, required ->
            val attribute = extension.createChild(XMLNS_XML_SCHEMA, "attribute")
            attribute.setAttribute("name", name)
            if (required)
                attribute.setAttribute("use", "required")
        }
    }
}