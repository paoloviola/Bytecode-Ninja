package bytecodeninja.project;

import bytecodeninja.util.XMLUtil;
import lombok.Getter;
import lombok.Setter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

@Getter
public class NinjaModule
{

    protected static final String SOURCE_DIRECTORY = "src";
    protected static final String RESOURCE_DIRECTORY = "res";

    @Setter private String name;

    private final Set<String> libraries;
    private final Set<RunConfig> runConfigs;
    public NinjaModule(String name) {
        this.name = Objects.requireNonNull(name);

        this.libraries = new HashSet<>();
        this.runConfigs = new HashSet<>();
    }

    /**
     * Adds a specific library to this module instance
     * @param library the library to add
     * @return if the library was successfully added or not
     */
    public boolean addLibrary(String library) {
        return libraries.add(library);
    }

    /**
     * Removes the specific library from this module instance
     * @param library the identical library in the stored in this module
     * @return if the library was successfully removed or not
     */
    public boolean removeLibrary(String library) {
        return libraries.remove(library);
    }

    /**
     * Adds a specific config to this module instance and saves it
     * @param config the config to add
     * @return if the config was successfully added or not
     */
    public boolean addConfig(RunConfig config) {
        if(findConfig(config.getName()) != null)
            return false;

        return runConfigs.add(config);
    }

    /**
     * Removes the specific config instance
     * @param config the identical config in the stored in this module
     * @return if the config was successfully removed or not
     */
    public boolean removeConfig(RunConfig config) {
        return runConfigs.remove(config);
    }

    /**
     * Searches for a config instance with the given name
     * @param name the name to search in the config list
     * @return the found config object or null if not found
     */
    public RunConfig findConfig(String name) {
        try(Stream<RunConfig> stream = runConfigs.stream()) {
            return stream.filter(c -> c.getName().equals(name))
                    .findAny().orElse(null);
        }
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

    /**
     * Saves this module to the predefined Path "${PROJECT_LOCATION}/.ninja/modules/${MODULE_NAME}.xml"
     * @return if the module has been saved successfully or not
     */
    public boolean save(NinjaProject project) {
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

        try {
            XMLUtil.write(doc, new File(
                    project.getLocation(),
                    NinjaProject.MODULES_DIRECTORY + name + ".xml"
            ));
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Loads the module file and stores it into a NinjaModule struct
     * @param moduleFile the file to load from
     * @return the created NinjaModule object
     * @throws IOException if any IO errors occur
     */
    public static NinjaModule load(File moduleFile) throws IOException {
        if(!moduleFile.exists() || !moduleFile.isFile())
            throw new IOException("Not a module file!");

        Element root;
        try {
            root = XMLUtil.read(moduleFile);
        }
        catch (SAXException e) {
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

        NinjaModule module = new NinjaModule(nameNode.getTextContent());
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
