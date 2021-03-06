package bytecodeninja.project;

import bytecodeninja.util.XMLUtil;
import lombok.Getter;
import lombok.Setter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.IOException;
import java.util.Objects;

@Getter @Setter
public class RunConfig
{

    private String name;
    private String workDirectory;

    private String vmArguments;
    private String programArguments;
    private String mainClass;

    public RunConfig(String name) {
        this.name = Objects.requireNonNull(name);
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Stores this object into the given element
     * @param root the element to store this object
     */
    void save(Element root) {
        Document doc = root.getOwnerDocument();
        Element configElement = doc.createElement("config");
        configElement.appendChild(XMLUtil.createTextNode(doc, "name", name));
        configElement.appendChild(XMLUtil.createTextNode(doc, "workdir", workDirectory));
        configElement.appendChild(XMLUtil.createTextNode(doc, "vmargs", vmArguments));
        configElement.appendChild(XMLUtil.createTextNode(doc, "programargs", programArguments));
        configElement.appendChild(XMLUtil.createTextNode(doc, "mainclass", mainClass));
        root.appendChild(configElement);
    }

    /**
     * Loads a RunConfig struct based on the given Node element
     * @param configNode the Node element to load
     * @return a fully parsed RunConfig
     * @throws IOException if the Node could not be parsed to a RunConfig object
     */
    static RunConfig load(Node configNode) throws IOException {
        if(!(configNode instanceof Element))
            throw new IOException("Corrupted config element!");

        Node nameNode = XMLUtil.getFirstByTag((Element) configNode, "name");
        Node workDirNode = XMLUtil.getFirstByTag((Element) configNode, "workdir");
        Node vmArgsNode = XMLUtil.getFirstByTag((Element) configNode, "vmargs");
        Node programArgsNode = XMLUtil.getFirstByTag((Element) configNode, "programargs");
        Node mainClassNode = XMLUtil.getFirstByTag((Element) configNode, "mainclass");

        if(nameNode == null || workDirNode == null
                || vmArgsNode == null || programArgsNode == null || mainClassNode == null) {
            throw new IOException("Corrupted config element!");
        }

        RunConfig runConfig = new RunConfig(nameNode.getTextContent());
        runConfig.setWorkDirectory(workDirNode.getTextContent());
        runConfig.setVmArguments(vmArgsNode.getTextContent());
        runConfig.setProgramArguments(programArgsNode.getTextContent());
        runConfig.setMainClass(mainClassNode.getTextContent());
        return runConfig;
    }

}
