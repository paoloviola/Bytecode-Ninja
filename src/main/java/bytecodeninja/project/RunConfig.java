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

    private String sdkDirectory;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RunConfig runConfig = (RunConfig) o;
        return Objects.equals(name, runConfig.name)
                && Objects.equals(sdkDirectory, runConfig.sdkDirectory)
                && Objects.equals(workDirectory, runConfig.workDirectory)
                && Objects.equals(vmArguments, runConfig.vmArguments)
                && Objects.equals(programArguments, runConfig.programArguments)
                && Objects.equals(mainClass, runConfig.mainClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                name,
                sdkDirectory, workDirectory,
                vmArguments, programArguments, mainClass
        );
    }

    void save(Element root) {
        Document doc = root.getOwnerDocument();
        Element configElement = doc.createElement("config");
        configElement.appendChild(XMLUtil.createTextNode(doc, "name", name));
        configElement.appendChild(XMLUtil.createTextNode(doc, "sdkdir", sdkDirectory));
        configElement.appendChild(XMLUtil.createTextNode(doc, "workdir", workDirectory));
        configElement.appendChild(XMLUtil.createTextNode(doc, "vmargs", vmArguments));
        configElement.appendChild(XMLUtil.createTextNode(doc, "programargs", programArguments));
        configElement.appendChild(XMLUtil.createTextNode(doc, "mainclass", mainClass));
        root.appendChild(configElement);
    }

    static RunConfig load(Node configNode) throws IOException {
        if(!(configNode instanceof Element))
            throw new IOException("Corrupted config element!");

        Node nameNode = XMLUtil.getFirstByTag((Element) configNode, "name");
        Node sdkDirNode = XMLUtil.getFirstByTag((Element) configNode, "sdkdir");
        Node workDirNode = XMLUtil.getFirstByTag((Element) configNode, "workdir");
        Node vmArgsNode = XMLUtil.getFirstByTag((Element) configNode, "vmargs");
        Node programArgsNode = XMLUtil.getFirstByTag((Element) configNode, "programargs");
        Node mainClassNode = XMLUtil.getFirstByTag((Element) configNode, "mainclass");

        if(nameNode == null
                || sdkDirNode == null || workDirNode == null
                || vmArgsNode == null || programArgsNode == null || mainClassNode == null) {
            throw new IOException("Corrupted config element!");
        }

        RunConfig runConfig = new RunConfig(nameNode.getTextContent());
        runConfig.setSdkDirectory(sdkDirNode.getTextContent());
        runConfig.setWorkDirectory(workDirNode.getTextContent());
        runConfig.setVmArguments(vmArgsNode.getTextContent());
        runConfig.setProgramArguments(programArgsNode.getTextContent());
        runConfig.setMainClass(mainClassNode.getTextContent());
        return runConfig;
    }

}
