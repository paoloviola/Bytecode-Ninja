package bytecodeninja.display.environment.explorer;

import bytecodeninja.display.StaticIcon;
import bytecodeninja.display.environment.ExplorerPane;
import bytecodeninja.display.environment.NinjaMenubar;
import bytecodeninja.project.NinjaProject;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;

public class ExplorerTree extends JTree
{

    private final ExplorerTreeNode treeRoot;
    private final DefaultTreeModel treeModel;
    public ExplorerTree(ExplorerPane parent, NinjaMenubar menu) {
        treeRoot = new ExplorerTreeNode();
        treeModel = new DefaultTreeModel(treeRoot);

        setModel(treeModel);
        setCellRenderer(new ExplorerCellRenderer());
        setRootVisible(false);
    }

    public void rebuildTree(NinjaProject project) {
        setEnabled(false); // Remove access to this component
        treeRoot.removeAllChildren(); // Clear out all nodes

        if(project != null) {
            project.getModules().forEach(m ->
                    treeRoot.add(new ModuleTreeNode(project, m)));

            // Sort node tree
            treeRoot.sortTree();
            treeModel.reload();
            setEnabled(true);
        }
    }

    private static class ExplorerCellRenderer extends DefaultTreeCellRenderer {
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            if(value instanceof ExplorerTreeNode) {
                String iconPath = ((ExplorerTreeNode)value).getIcon();
                setIcon(StaticIcon.get(iconPath));
            }
            return this;
        }
    }

}
