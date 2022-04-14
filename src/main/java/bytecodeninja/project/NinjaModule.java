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
import java.util.*;

@Getter
public class NinjaModule
{

    protected static final String SOURCE_DIRECTORY = "src";
    protected static final String RESOURCE_DIRECTORY = "res";

    private NinjaProject project;

    private String name;

    private final Set<String> libraries;
    private final List<RunConfig> runConfigs;
    public NinjaModule(NinjaProject project, String name) {
        this.project = Objects.requireNonNull(project);
        this.name = Objects.requireNonNull(name);

        this.libraries = new HashSet<>();
        this.runConfigs = new ArrayList<>();
    }

    private NinjaModule() {
        this.libraries = new HashSet<>();
        this.runConfigs = new ArrayList<>();
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NinjaModule module = (NinjaModule) o;
        return name.equals(module.name)
                && runConfigs.equals(module.runConfigs)
                && libraries.equals(module.libraries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, runConfigs, libraries);
    }

    public boolean save() {
        try {
            Element root = XMLUtil.createDefault("module");
            Document doc = root.getOwnerDocument();
            {
                root.appendChild(XMLUtil.createTextNode(doc, "name", name));

                Element configsElement = doc.createElement("configs");
                runConfigs.forEach(config -> config.save(configsElement));
                root.appendChild(configsElement);

                Element libsElement = doc.createElement("libraries");
                libraries.forEach(lib -> libsElement.appendChild(XMLUtil.createTextNode(doc, "library", lib)));
                root.appendChild(libsElement);
            }
            XMLUtil.write(doc, new File(project.getLocation(), NinjaProject.MODULES_DIRECTORY + name + ".xml"));
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
        Node configsNode = XMLUtil.getFirstByTag(root, "configs");
        Node libsNode = XMLUtil.getFirstByTag(root, "libraries");

        if(nameNode == null
                || !(configsNode instanceof Element)
                || !(libsNode instanceof Element)) {
            throw new IOException("Corrupted module file!");
        }

        NinjaModule module = new NinjaModule();
        module.project = project;

        module.name = nameNode.getTextContent();
        { // Load run configurations
            NodeList configNodes = ((Element)configsNode).getElementsByTagName("config");
            for (int i = 0; i < configNodes.getLength(); i++) {
                try {
                    module.runConfigs.add(
                            RunConfig.load(configNodes.item(i))
                    );
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        { // Load libraries
            NodeList libNodes = ((Element)libsNode).getElementsByTagName("library");
            for (int i = 0; i < libNodes.getLength(); i++) {
                module.libraries.add(
                        libNodes.item(i).getTextContent()
                );
            }
        }
        return module;
    }

}
