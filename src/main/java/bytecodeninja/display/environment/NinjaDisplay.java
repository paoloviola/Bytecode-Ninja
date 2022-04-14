package bytecodeninja.display.environment;

import bytecodeninja.project.NinjaProject;
import com.formdev.flatlaf.FlatDarculaLaf;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

@Getter
public class NinjaDisplay extends JFrame
{

    static {
        updateLookAndFeel();
    }

    private NinjaProject currentProject;

    private final NinjaMenubar menubar;

    private final ToolbarPane toolbar;
    private final ExplorerPane explorer;
    private final EditorPane editor;
    private final ConsolePane console;

    public NinjaDisplay() {
        setTitle("Binary Ninja");
        setSize(1280, 720);
        setLocationRelativeTo(null);
        setMinimumSize(getSize());

        menubar = new NinjaMenubar(this);
        setJMenuBar(menubar);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                menubar.exit(null);
            }
        });

        toolbar = new ToolbarPane(menubar);
        explorer = new ExplorerPane(menubar);
        editor = new EditorPane(menubar);
        console = new ConsolePane(menubar);
        decorateContainer();

        selectProject(null);
    }

    /**
     * Tells the Graphical user interface to select this project.
     * This will enable or disable all necessary items
     * @param project the project to load or null to unload the current project
     */
    public void selectProject(NinjaProject project) {
        currentProject = project;
        menubar.selectProject(project);
        toolbar.selectProject(project);
        explorer.selectProject(project);
        editor.selectProject(project);
        console.selectProject(project);
    }

    private void decorateContainer() {
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.add(toolbar, BorderLayout.NORTH);
        {
            JSplitPane splitPane0 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
            splitPane0.setResizeWeight(1d);
            { // Workspace pane
                JSplitPane splitPane1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
                splitPane1.setResizeWeight(0d);
                splitPane1.setLeftComponent(explorer);
                splitPane1.setRightComponent(editor);
                splitPane0.setTopComponent(splitPane1);
            }
            splitPane0.setBottomComponent(console);
            contentPane.add(splitPane0, BorderLayout.CENTER);
        }
        setContentPane(contentPane);
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        if(!b) dispose();
    }

    /**
     * Updates the Look and Feel of this Window based on the given settings
     * TODO: ADD SETTING
     */
    public static void updateLookAndFeel() {
        if(FlatDarculaLaf.setup())
            FlatDarculaLaf.installLafInfo();
        else {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

}
