package bytecodeninja.display.environment;

import bytecodeninja.display.environment.explorer.ExplorerTree;
import bytecodeninja.project.NinjaProject;

import javax.swing.*;
import java.awt.*;

// TODO: Maybe add more components, like path selector
public class ExplorerPane extends JPanel
{

    private final ExplorerTree explorerTree;
    public ExplorerPane(NinjaMenubar menu) {
        super(new BorderLayout());

        explorerTree = new ExplorerTree(this, menu);
        add(new JScrollPane(explorerTree), BorderLayout.CENTER);
    }

    void selectProject(NinjaProject project) {
        explorerTree.rebuildTree(project);
    }

}
