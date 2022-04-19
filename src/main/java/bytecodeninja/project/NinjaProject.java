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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Getter
public class NinjaProject
{

    protected static final String MODULES_DIRECTORY = ".ninja/modules/";
    protected static final String WORKSPACE_FILE = ".ninja/workspace.xml";

    @Setter private String name;

    private String location;

    // TODO: ADD SDK DIRECTORY

    private final List<NinjaModule> modules;
    public NinjaProject(String name, String location) {
        this.name = Objects.requireNonNull(name);
        this.location = Objects.requireNonNull(location);
        this.modules = new ArrayList<>();
    }

    private NinjaProject() {
        this.modules = new ArrayList<>();
    }

    /**
     * Adds a specific module to this project instance and saves it
     * @param module the module to add
     * @return if the module was successfully added or not
     */
    public boolean addModule(NinjaModule module) {
        if(findModule(module.getName()) != null)
            return false;

        if(module.save(this)) {
            modules.add(module);
            save(); // Save project silently, since we want to ignore the already known errors
            return true;
        }

        return false;
    }

    /**
     * Removes the specific module instance
     * @param module the identical module in the stored in this project
     * @return if the module was successfully removed or not
     */
    public boolean removeModule(NinjaModule module) {
        if(modules.remove(module)) {
            save(); // Save project silently, since we want to ignore the already known errors
            return true;
        }

        return false;
    }

    /**
     * Searches for a module instance with the given name
     * @param name the name to search in the module list
     * @return the found module object or null if not found
     */
    public NinjaModule findModule(String name) {
        try(Stream<NinjaModule> stream = modules.stream()) {
            return stream.filter(m -> m.getName().equals(name))
                    .findAny().orElse(null);
        }
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Saves the project on the predefined path
     * @return true if the project was saved without any errors, otherwise false
     */
    public boolean save() {
        try {
            Element root = XMLUtil.createDefault("project");
            Document doc = root.getOwnerDocument();

            root.appendChild(XMLUtil.createTextNode(doc, "name", name));
            Element modulesElement = doc.createElement("modules");

            // Go through every module and try to save it
            // If any of them fails, continue saving but return false
            boolean containsErrors = false;
            for(NinjaModule module : modules) {
                modulesElement.appendChild(XMLUtil.createTextNode(
                        doc, "module", module.getName()
                ));

                if(!module.save(this))
                    containsErrors = true;
            }

            root.appendChild(modulesElement);
            XMLUtil.write(doc, new File(location, WORKSPACE_FILE));
            return !containsErrors;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Load a specific module directory
     * @param projectDir the directory to be parsed
     * @return the loaded NinjaProject object
     * @throws ProjectException if the directory is not a project directory
     * @throws IOException if the project could not be loaded
     */
    public static NinjaProject load(File projectDir) throws ProjectException, IOException {
        if(!projectDir.exists() || !projectDir.isDirectory())
            throw new ProjectException("Not a project directory!");

        Element root;
        try {
            File workspaceFile = new File(projectDir, WORKSPACE_FILE);
            if(!workspaceFile.exists() || !workspaceFile.isFile())
                throw new ProjectException("Not a project directory!");
            root = XMLUtil.read(workspaceFile);
        }
        catch (SAXException e) {
            throw new IOException(e);
        }

        Node nameNode = XMLUtil.getFirstByTag(root, "name");
        Node modulesNode = XMLUtil.getFirstByTag(root, "modules");
        if(nameNode == null || modulesNode == null)
            throw new IOException("Corrupted workspace file!");

        NinjaProject project = new NinjaProject();
        project.location = projectDir.getAbsolutePath();
        project.name = nameNode.getTextContent();
        { // Load modules
            File modulesDir = new File(project.getLocation(), MODULES_DIRECTORY);

            NodeList moduleNodes = ((Element)modulesNode).getElementsByTagName("module");
            for(int i = 0; i < moduleNodes.getLength(); i++) {
                try {
                    project.modules.add(NinjaModule.load(new File(
                            modulesDir, moduleNodes.item(i).getTextContent() + ".xml"
                    )));
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return project;
    }

}
