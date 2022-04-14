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

    /**
     * Wrapper to create a simple text node
     * @param doc the document to access the {@link Document#createElement(String)} method
     * @param tag the tag name of the created node
     * @param value content value of the node
     * @return the created text node
     */
    public static Node createTextNode(Document doc, String tag, String value) {
        Element element = doc.createElement(tag);
        element.setTextContent(value);
        return element;
    }

    /**
     * Searches the parent element for the first tag it finds
     * @param parent the parent element to search from
     * @param tag the tag name to be searched
     * @return the found element or null if no element was found
     */
    public static Node getFirstByTag(Element parent, String tag) {
        NodeList nodes = parent.getElementsByTagName(tag);
        return nodes.getLength() > 0 ?
                nodes.item(0) : null;
    }

    /**
     * Creates a XML-Root-Element
     * @param name the name of the element tag
     * @return the created element
     */
    public static Element createDefault(String name) {
        try {
            // Create default document element
            Document doc = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder().newDocument();
            Element root = doc.createElement(name);
            doc.appendChild(root);
            return root;
        }
        catch (ParserConfigurationException e) {
            // This is supposed to never happen
            throw new RuntimeException(e);
        }
    }

    /**
     * Parses the given file to an XML-Element-Tree
     * @param file the file to read the content of
     * @return the parsed Element-Tree
     * @throws IOException if any IO errors occur
     * @throws SAXException If any parse errors occur
     */
    public static Element read(File file) throws IOException, SAXException {
        try {
            // Parse default document element
            Element element = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder().parse(file)
                    .getDocumentElement();
            element.normalize();
            return element;
        }
        catch (ParserConfigurationException e) {
            // This is supposed to never happen
            throw new RuntimeException(e);
        }
    }

    /**
     * Saves the given XML-Node to a file
     * @param node the node to be saved
     * @param file the specified file to save to
     * @throws TransformerException if an unrecoverable error occurs during the course of the transformation
     * @throws IOException if an I/O error occurred
     */
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
