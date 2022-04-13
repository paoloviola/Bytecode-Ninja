package bytecodeninja.display;

import bytecodeninja.project.NinjaProject;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

@Getter
public class NinjaDisplay extends JFrame
{

    static {
        updateLookAndFeel();
    }

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

        toolbar = new ToolbarPane(menubar);
        explorer = new ExplorerPane(menubar);
        editor = new EditorPane(menubar);
        console = new ConsolePane(menubar);
        decorateContainer();

        selectProject(null);
    }

    public void selectProject(NinjaProject project) {
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