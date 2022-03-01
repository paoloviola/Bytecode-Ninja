package bytecodeninja.util;

import lombok.experimental.UtilityClass;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;

@UtilityClass
public class XMLUtil
{

    private static final String KEY_INDENT = "indent";
    private static final String KEY_INDENT_AMOUNT = "{http://xml.apache.org/xslt}indent-amount";

    public static Node createTextNode(Document doc, String tag, String value) {
        Element element = doc.createElement(tag);
        element.setTextContent(value);
        return element;
    }

    public static Node getFirstByTag(Element parent, String tag) {
        NodeList nodes = parent.getElementsByTagName(tag);
        return nodes.getLength() > 0 ?
                nodes.item(0) : null;
    }

    public static Element createDefault(String name) throws ParserConfigurationException {
        // Create default document element
        Document doc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().newDocument();
        Element root = doc.createElement(name);
        doc.appendChild(root);
        return root;
    }

    public static Element read(File file) throws ParserConfigurationException, IOException, SAXException {
        // Parse default document element
        Element element = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().parse(file)
                .getDocumentElement();
        element.normalize();
        return element;
    }

    public static void write(Node node, File file) throws TransformerException, IOException {
        // Create file if not exists
        IOUtil.createFile(file);

        // Create transformer with default settings
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(KEY_INDENT, "yes");
        transformer.setOutputProperty(KEY_INDENT_AMOUNT, "2");

        // Export element to file
        transformer.transform(
                new DOMSource(node),
                new StreamResult(file)
        );
    }

}
