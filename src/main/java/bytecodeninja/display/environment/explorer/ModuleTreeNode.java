package bytecodeninja.display.environment.explorer;

import bytecodeninja.project.NinjaModule;
import bytecodeninja.project.NinjaProject;
import lombok.Getter;

@Getter
public class ModuleTreeNode extends ExplorerTreeNode
{

    private final NinjaProject project;
    private final NinjaModule module;

    public ModuleTreeNode(NinjaProject project, NinjaModule module) {
        super("icons/moduleDirectory.svg", null);
        this.project = project;
        this.module = module;

        setUserObject(module);
    }

}
