package bytecodeninja.project;

import bytecodeninja.util.XMLUtil;
import lombok.Getter;
import lombok.Setter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ProjectLibrary
{

    @Setter private String name;
    @Getter private final Set<String> paths;

    public ProjectLibrary(String name) {
        this(name, new HashSet<>());
    }

    public ProjectLibrary(String name, Set<String> paths) {
        this.name = Objects.requireNonNull(name);
        this.paths = Objects.requireNonNull(paths);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectLibrary that = (ProjectLibrary) o;
        return name.equals(that.name) && paths.equals(that.paths);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, paths);
    }

    void save(Element root) {
        Document doc = root.getOwnerDocument();

        Element libElement = doc.createElement("library");
        libElement.appendChild(XMLUtil.createTextNode(doc, "name", name));

        Element pathsElement = doc.createElement("paths");
        paths.forEach(path -> pathsElement.appendChild(XMLUtil.createTextNode(doc, "path", path)));
        libElement.appendChild(pathsElement);
        root.appendChild(libElement);
    }

    static ProjectLibrary load(Node libNode) throws IOException {
        if(!(libNode instanceof Element))
            throw new IOException("Corrupted library element!");

        Node nameNode = XMLUtil.getFirstByTag((Element) libNode, "name");
        Node pathsNode = XMLUtil.getFirstByTag((Element) libNode, "paths");
        if(nameNode == null || !(pathsNode instanceof Element))
            throw new IOException("Corrupted library element!");

        ProjectLibrary library = new ProjectLibrary(nameNode.getTextContent());
        { // Load paths
            NodeList pathNodes = ((Element) pathsNode).getElementsByTagName("path");
            for(int i = 0; i < pathNodes.getLength(); i++)
                library.paths.add(pathNodes.item(i).getTextContent());
        }
        return library;
    }

}
