package bytecodeninja.display.environment;

import bytecodeninja.display.dialog.NewModuleDialog;
import bytecodeninja.display.dialog.NewProjectDialog;
import bytecodeninja.project.NinjaProject;
import bytecodeninja.project.ProjectException;
import bytecodeninja.project.RunConfig;
import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;

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
        fileMenu.add(createMenuItem("Open Project...", this::openProject)); // Should always be accessible
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

    void selectProject(NinjaProject project) {
        // Buttons which should only be accessible if a project is loaded
        newModuleItem.setEnabled(project != null);
        closeProjectItem.setEnabled(project != null);
        projectStructureItem.setEnabled(project != null);
        buildItem.setEnabled(project != null);
    }

    void selectConfig(RunConfig config) {
        // Buttons which should only be accessible if a config is selected
        runItem.setEnabled(config != null);
        debugItem.setEnabled(config != null);

        String postfix = config == null ? "" : " '" + config + "'";
        runItem.setText("Run" + postfix);
        debugItem.setText("Debug" + postfix);
    }

    // FILE MENU
    public void createNewProject(ActionEvent e) {
        NewProjectDialog dialog = new NewProjectDialog(parent);
        dialog.setVisible(true);

        if(dialog.getProject() == null) return;
        if(dialog.getProject().save())
            parent.selectProject(dialog.getProject());
        else {
            JOptionPane.showMessageDialog(this,
                    "Could not create Project!\nRead the console for further information.", "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public void createNewModule(ActionEvent e) {
        NinjaProject project = parent.getCurrentProject();
        if(project == null)
            throw new RuntimeException("This is not supposed to happen!");

        NewModuleDialog dialog = new NewModuleDialog(parent, project);
        dialog.setVisible(true);

        if(dialog.getModule() == null) return;
        if(!project.addModule(dialog.getModule())) {
            JOptionPane.showMessageDialog(this,
                    "Could not create Module!\nRead the console for further information.", "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public void openProject(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Project Path...");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setMultiSelectionEnabled(false);

        if(fileChooser.showOpenDialog(parent) != JFileChooser.APPROVE_OPTION)
            return;

        try {
            parent.selectProject(NinjaProject.load(
                    fileChooser.getSelectedFile()
            ));
        }
        catch (ProjectException ex) {
            JOptionPane.showMessageDialog(parent,
                    "You have selected an invalid project!", "Could not load Project",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
        catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(parent,
                    "Could not open project!\nRead the console for further information.", "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public void closeProject(ActionEvent e) {
        if(parent.getCurrentProject() == null)
            throw new RuntimeException("This is not supposed to happen!");

        int confirm = JOptionPane.showConfirmDialog(parent,
                "Do you really want to close this project?", "Confirm Close",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE
        );
        if(confirm == JOptionPane.YES_OPTION)
            parent.selectProject(null);
    }

    public void openProjectStructure(ActionEvent e) {
        // TODO: OPEN PROJECT STRUCTURE VIEW
    }

    public void exit(ActionEvent e) {
        int confirm = JOptionPane.showConfirmDialog(parent,
                "Are you sure you want to exit?", "Confirm Exit",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if(confirm == JOptionPane.YES_OPTION)
            parent.setVisible(false);
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

    private static JMenuItem createMenuItem(String name, Icon icon, KeyStroke keyStroke, ActionListener action) {
        JMenuItem menuItem = new JMenuItem(name);
        menuItem.addActionListener(action);
        menuItem.setAccelerator(keyStroke);
        menuItem.setIcon(icon);
        return menuItem;
    }

}
