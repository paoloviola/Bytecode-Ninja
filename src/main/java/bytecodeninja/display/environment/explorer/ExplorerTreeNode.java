package bytecodeninja.display.environment.explorer;

import lombok.Getter;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

@Getter
class ExplorerTreeNode extends DefaultMutableTreeNode
{

    private final String icon;
    private final EncodedFile file;
    public ExplorerTreeNode(String icon, EncodedFile file) {
        this.icon = icon;
        this.file = file;
    }

    public ExplorerTreeNode() {
        this(null, null);
    }

    @Override
    public String toString() {
        return file == null ? super.toString() : file.getDecodedName();
    }

    public void sortTree() {
        for(int i = 0; i < getChildCount(); i++) {
            TreeNode child = getChildAt(i);
            if(child instanceof ExplorerTreeNode)
                ((ExplorerTreeNode)child).sortTree();
        }
        sortChildren();
    }

    public void sortChildren() {
        if(getChildCount() == 0) return;
        //noinspection unchecked
        children.sort((o1, o2) -> {
            if(!(o1 instanceof ExplorerTreeNode)
                    && !(o2 instanceof ExplorerTreeNode)) {
                return 0;
            }

            // Check for valid instancing
            if(!(o1 instanceof ExplorerTreeNode)) return 1;
            if(!(o2 instanceof ExplorerTreeNode)) return -1;

            ExplorerTreeNode node1 = (ExplorerTreeNode) o1;
            ExplorerTreeNode node2 = (ExplorerTreeNode) o2;

            // Compare by directory
            if(node1.isDirectory() && !node2.isDirectory()) return -1;
            if(!node1.isDirectory() && node2.isDirectory()) return 1;

            // Compare by name
            return node1.toString().compareTo(node2.toString());
        });
    }

    public boolean isDirectory() {
        return file != null && file.isDirectory();
    }

}
