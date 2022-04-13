package bytecodeninja.display;

import bytecodeninja.project.NinjaProject;
import bytecodeninja.project.RunConfig;
import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class NinjaMenubar extends JMenuBar
{

    // FILE MENU
    private final JMenuItem newModuleItem;
    private final JMenuItem closeProjectItem;
    private final JMenuItem projectStructureItem;
    // FILE MENU

    // BUILD MENU
    private final JMenuItem buildItem;
    // BUILD MENU

    // RUN MENU
    private final JMenuItem runItem;
    private final JMenuItem debugItem;
    // RUN MENU

    private final NinjaDisplay parent;
    public NinjaMenubar(NinjaDisplay parent) {
        this.parent = parent;

        // File menu
        JMenu fileMenu = new JMenu("File");
        {
            JMenu newMenu = new JMenu("New");
            newMenu.add(createMenuItem("Project...", this::createNewProject)); // Should always be accessible
            newMenu.add(newModuleItem = createMenuItem("Module...", this::createNewModule));
            fileMenu.add(newMenu);
        }
        fileMenu.add(closeProjectItem = createMenuItem("Close Project", this::closeProject));
        fileMenu.addSeparator();
        fileMenu.add(projectStructureItem = createMenuItem("Project Structure...",
                new FlatSVGIcon("icons/projectStructure.svg"),
                KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK | InputEvent.ALT_MASK | InputEvent.SHIFT_MASK),
                this::openProjectStructure
        ));
        fileMenu.addSeparator();
        fileMenu.add(createMenuItem("Exit", this::exit)); // Should always be accessible
        add(fileMenu);
        // File menu

        // Build menu
        JMenu buildMenu = new JMenu("Build");
        buildMenu.add(buildItem = createMenuItem("Build Project",
                new FlatSVGIcon("icons/compile.svg"),
                KeyStroke.getKeyStroke(KeyEvent.VK_F9, InputEvent.CTRL_MASK),
                this::buildProject
        ));
        add(buildMenu);
        // Build menu

        // Run menu
        JMenu runMenu = new JMenu("Run");
        runMenu.add(runItem = createMenuItem("Run",
                new FlatSVGIcon("icons/execute.svg"),
                KeyStroke.getKeyStroke(KeyEvent.VK_F10, InputEvent.SHIFT_MASK),
                this::runConfig
        ));
        runMenu.add(debugItem = createMenuItem("Debug",
                new FlatSVGIcon("icons/startDebugger.svg"),
                KeyStroke.getKeyStroke(KeyEvent.VK_F9, InputEvent.SHIFT_MASK),
                this::debugConfig
        ));
        add(runMenu);
        // Run menu
    }

    public void selectProject(NinjaProject project) {
        // Buttons which should only be accessible if a project is loaded
        newModuleItem.setEnabled(project != null);
        closeProjectItem.setEnabled(project != null);
        projectStructureItem.setEnabled(project != null);
        buildItem.setEnabled(project != null);
    }

    public void selectConfig(RunConfig config) {
        // Buttons which should only be accessible if a config is selected
        runItem.setEnabled(config != null);
        debugItem.setEnabled(config != null);

        String postfix = config == null ? "" : " '" + config + "'";
        runItem.setText("Run" + postfix);
        debugItem.setText("Debug" + postfix);
    }

    // FILE MENU
    public void createNewProject(ActionEvent e) {
        // TODO: CREATE NEW PROJECT
    }

    public void createNewModule(ActionEvent e) {
        // TODO: CREATE NEW MODULE
    }

    public void closeProject(ActionEvent e) {
        // TODO: CLOSE PROJECT
    }

    public void openProjectStructure(ActionEvent e) {
        // TODO: OPEN PROJECT STRUCTURE VIEW
    }

    public void exit(ActionEvent e) {
        // TODO: EXIT
    }
    // FILE MENU

    // BUILD MENU
    public void buildProject(ActionEvent e) {
        // TODO: Build config
    }
    // BUILD MENU

    // RUN MENU
    public void runConfig(ActionEvent e) {
        // TODO: Run config
    }

    public void debugConfig(ActionEvent e) {
        // TODO: Debug config
    }
    // RUN MENU

    private static JMenuItem createMenuItem(String name, ActionListener action) {
        return createMenuItem(name, null, null, action);
    }

    private static JMenuItem createMenuItem(String name, KeyStroke keyStroke, ActionListener action) {
        return createMenuItem(name, null, keyStroke, action);
    }

    private static JMenuItem createMenuItem(String name, Icon icon, ActionListener action) {
        return createMenuItem(name, icon, null, action);
    }

    private static JMenuItem createMenuItem(String name, Icon icon, KeyStroke keyStroke, ActionListener action) {
        JMenuItem menuItem = new JMenuItem(name);
        menuItem.addActionListener(action);
        menuItem.setAccelerator(keyStroke);
        menuItem.setIcon(icon);
        return menuItem;
    }

}
