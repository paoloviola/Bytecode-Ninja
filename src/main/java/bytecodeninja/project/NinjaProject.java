package bytecodeninja.project;

import bytecodeninja.util.XMLUtil;
import lombok.Getter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public class NinjaProject
{

    protected static final String MODULES_DIRECTORY = ".ninja/modules/";
    protected static final String WORKSPACE_FILE = ".ninja/workspace.xml";

    private String name;
    private String location;

    private final List<NinjaModule> modules;
    public NinjaProject(String name, String location) {
        this.name = Objects.requireNonNull(name);
        this.location = Objects.requireNonNull(location);
        this.modules = new ArrayList<>();
    }

    private NinjaProject() {
        this.modules = new ArrayList<>();
    }

    public boolean addModule(NinjaModule module) {
        if(findModule(module.getName()) != null)
            return false;

        if(module.save()) {
            modules.add(module);
            save(); // Save project silently, since we want to ignore the already known errors
            return true;
        }

        return false;
    }

    public boolean removeModule(NinjaModule module) {
        if(findModule(module.getName()) == null)
            return false;

        modules.remove(module);
        save(); // Save project silently, since we want to ignore the already known errors
        return true;
    }

    public NinjaModule findModule(String name) {
        for (NinjaModule module : modules) {
            if(module.getName().equals(name))
                return module;
        }

        return null;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NinjaProject project = (NinjaProject) o;
        return name.equals(project.name)
                && location.equals(project.location)
                && modules.equals(project.modules);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, location, modules);
    }

    public boolean save() {
        try {
            Element root = XMLUtil.createDefault("project");
            Document doc = root.getOwnerDocument();
            {
                root.appendChild(XMLUtil.createTextNode(doc, "name", name));
            }
            XMLUtil.write(doc, new File(location, WORKSPACE_FILE));

            // Go through every module and try to save it
            // If any of them fails, continue saving but return false
            boolean containsErrors = false;
            for(NinjaModule module : modules) {
                if(!module.save())
                    containsErrors = true;
            }
            return !containsErrors;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static NinjaProject load(File projectDir) throws ProjectException, IOException {
        if(!projectDir.exists() || !projectDir.isDirectory())
            throw new ProjectException();

        Element root;
        try {
            File workspaceFile = new File(projectDir, WORKSPACE_FILE);
            if(!workspaceFile.exists() || !workspaceFile.isFile())
                throw new ProjectException();
            root = XMLUtil.read(workspaceFile);
        }
        catch (ParserConfigurationException | SAXException e) {
            throw new IOException(e);
        }

        Node nameNode = XMLUtil.getFirstByTag(root, "name");
        if(nameNode == null) throw new IOException("Corrupted workspace file!");

        NinjaProject project = new NinjaProject();
        project.location = projectDir.getAbsolutePath();
        project.name = nameNode.getTextContent();
        { // Load modules
            File modulesDir = new File(project.getLocation(), MODULES_DIRECTORY);

            File[] moduleFiles = modulesDir.listFiles();
            if(moduleFiles == null)
                return project; // Don't load modules if not accessible

            for(File moduleFile : moduleFiles) {
                try {
                    project.modules.add(
                            NinjaModule.load(project, moduleFile)
                    );
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return project;
    }

}
