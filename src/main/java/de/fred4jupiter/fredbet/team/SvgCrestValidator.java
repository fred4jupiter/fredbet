package de.fred4jupiter.fredbet.team;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Set;

/**
 * Checks that uploaded crest content is a well-formed SVG document without active content
 * (scripts, event handlers, javascript links, embedded HTML), because the crests are served
 * as image/svg+xml and could otherwise be used for stored XSS.
 */
class SvgCrestValidator {

    private static final Set<String> FORBIDDEN_ELEMENTS = Set.of("script", "foreignobject", "iframe", "embed", "object");

    private SvgCrestValidator() {
    }

    static boolean isValidSvg(byte[] svgContent) {
        if (svgContent == null || svgContent.length == 0) {
            return false;
        }

        try {
            Document document = createDocumentBuilder().parse(new ByteArrayInputStream(svgContent));
            Element root = document.getDocumentElement();
            return !hasEntityDeclarations(document) && "svg".equals(localName(root)) && !containsActiveContent(root);
        } catch (SAXException | IOException | ParserConfigurationException e) {
            return false;
        }
    }

    private static DocumentBuilder createDocumentBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        // many SVG editors write a DOCTYPE, so it is allowed, but nothing external is ever loaded
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        factory.setXIncludeAware(false);
        factory.setExpandEntityReferences(false);

        DocumentBuilder builder = factory.newDocumentBuilder();
        builder.setErrorHandler(new DefaultHandler());
        return builder;
    }

    private static boolean hasEntityDeclarations(Document document) {
        DocumentType doctype = document.getDoctype();
        return doctype != null && StringUtils.isNotBlank(doctype.getInternalSubset());
    }

    private static boolean containsActiveContent(Element element) {
        if (FORBIDDEN_ELEMENTS.contains(localName(element).toLowerCase(Locale.ROOT))) {
            return true;
        }

        NamedNodeMap attributes = element.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node attribute = attributes.item(i);
            String name = localName(attribute).toLowerCase(Locale.ROOT);
            String value = attribute.getNodeValue().replaceAll("\\s", "").toLowerCase(Locale.ROOT);
            if (name.startsWith("on") || value.contains("javascript:")) {
                return true;
            }
        }

        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            if (children.item(i) instanceof Element child && containsActiveContent(child)) {
                return true;
            }
        }
        return false;
    }

    private static String localName(Node node) {
        return node.getLocalName() != null ? node.getLocalName() : node.getNodeName();
    }
}
