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

    private final JMenuItem buildMenuItem;
    private final JMenuItem runMenuItem;
    private final JMenuItem debugMenuItem;

    private final NinjaDisplay parent;
    public NinjaMenubar(NinjaDisplay parent) {
        this.parent = parent;

        JMenu buildMenu = new JMenu("Build");
        buildMenu.add(buildMenuItem = createMenuItem("Build Project",
                new FlatSVGIcon("icons/compile.svg"),
                KeyStroke.getKeyStroke(KeyEvent.VK_F9, InputEvent.CTRL_MASK),
                this::buildProject
        ));
        add(buildMenu);

        JMenu runMenu = new JMenu("Run");
        runMenu.add(runMenuItem = createMenuItem("Run",
                new FlatSVGIcon("icons/execute.svg"),
                KeyStroke.getKeyStroke(KeyEvent.VK_F10, InputEvent.SHIFT_MASK),
                this::runConfig
        ));
        runMenu.add(debugMenuItem = createMenuItem("Debug",
                new FlatSVGIcon("icons/startDebugger.svg"),
                KeyStroke.getKeyStroke(KeyEvent.VK_F9, InputEvent.SHIFT_MASK),
                this::debugConfig
        ));
        add(runMenu);
    }

    public void selectProject(NinjaProject project) {
        buildMenuItem.setEnabled(project != null);
    }

    public void selectConfig(RunConfig config) {
        runMenuItem.setEnabled(config != null);
        debugMenuItem.setEnabled(config != null);

        String postfix = config == null ? "" : " '" + config + "'";
        runMenuItem.setText("Run" + postfix);
        debugMenuItem.setText("Debug" + postfix);
    }

    public void buildProject(ActionEvent e) {
        // TODO: Build config
    }

    public void runConfig(ActionEvent e) {
        // TODO: Run config
    }

    public void debugConfig(ActionEvent e) {
        // TODO: Debug config
    }

    private static JMenuItem createMenuItem(String name, Icon icon, KeyStroke keyStroke, ActionListener action) {
        JMenuItem menuItem = new JMenuItem(name);
        menuItem.addActionListener(action);
        menuItem.setAccelerator(keyStroke);
        menuItem.setIcon(icon);
        return menuItem;
    }

}
