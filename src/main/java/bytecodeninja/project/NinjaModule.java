package bytecodeninja.project;

import bytecodeninja.util.XMLUtil;
import lombok.Getter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public class NinjaModule
{

    protected static final String MODULES_DIRECTORY = ".ninja/modules/";

    private NinjaProject project;

    private String name;
    private String location;
    private String srcDir;
    private String resDir;

    private final List<ProjectLibrary> libraries;
    public NinjaModule(NinjaProject project, String name, String location) {
        this.project = Objects.requireNonNull(project);
        this.name = Objects.requireNonNull(name);
        this.location = Objects.requireNonNull(location);
        this.srcDir = "src";
        this.resDir = "res";

        this.libraries = new ArrayList<>();
    }

    private NinjaModule() {
        this.libraries = new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NinjaModule module = (NinjaModule) o;
        return name.equals(module.name)
                && location.equals(module.location)
                && srcDir.equals(module.srcDir)
                && resDir.equals(module.resDir)
                && libraries.equals(module.libraries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, location, srcDir, resDir, libraries);
    }

    public boolean save() {
        try {
            Element root = XMLUtil.createDefault("module");
            Document doc = root.getOwnerDocument();
            {
                root.appendChild(XMLUtil.createTextNode(doc, "name", name));
                root.appendChild(XMLUtil.createTextNode(doc, "location", location));
                root.appendChild(XMLUtil.createTextNode(doc, "srcDir", srcDir));
                root.appendChild(XMLUtil.createTextNode(doc, "resDir", resDir));

                Element libsElement = doc.createElement("libraries");
                libraries.forEach(lib -> lib.save(libsElement));
                root.appendChild(libsElement);
            }
            XMLUtil.write(doc, new File(project.getLocation(), MODULES_DIRECTORY + name + ".xml"));
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static NinjaModule load(NinjaProject project, File moduleFile) throws IOException {
        if(!moduleFile.exists() || !moduleFile.isFile())
            throw new IOException("Not a module file!");

        Element root;
        try {
            root = XMLUtil.read(moduleFile);
        }
        catch (ParserConfigurationException | SAXException e) {
            throw new IOException(e);
        }

        Node nameNode = XMLUtil.getFirstByTag(root, "name");
        Node locationNode = XMLUtil.getFirstByTag(root, "location");
        Node srcNode = XMLUtil.getFirstByTag(root, "srcDir");
        Node resNode = XMLUtil.getFirstByTag(root, "resDir");
        Node libsNode = XMLUtil.getFirstByTag(root, "libraries");

        if(nameNode == null || locationNode == null
                || srcNode == null || resNode == null
                || !(libsNode instanceof Element)) {
            throw new IOException("Corrupted module file!");
        }

        NinjaModule module = new NinjaModule();
        module.project = project;

        module.name = nameNode.getTextContent();
        module.location = locationNode.getTextContent();
        module.srcDir = srcNode.getTextContent();
        module.resDir = resNode.getTextContent();
        { // Load libraries
            NodeList libNodes = ((Element)libsNode).getElementsByTagName("library");
            for (int i = 0; i < libNodes.getLength(); i++) {
                try {
                    module.libraries.add(
                            ProjectLibrary.load(libNodes.item(i))
                    );
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return module;
    }

}
