package bytecodeninja.display;

import com.formdev.flatlaf.FlatDarkLaf;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;

public class NinjaDisplay extends JFrame
{

    static {
        updateLookAndFeel();
    }

    @Getter private final ToolbarPane toolbar;
    @Getter private final ExplorerPane explorer;
    @Getter private final EditorPane editor;
    @Getter private final ConsolePane console;

    public NinjaDisplay() {
        setTitle("Binary Ninja");
        setSize(1280, 720);
        setLocationRelativeTo(null);
        setMinimumSize(getSize());

        toolbar = new ToolbarPane();
        explorer = new ExplorerPane();
        editor = new EditorPane();
        console = new ConsolePane();

        decorateContainer();
    }

    private void decorateContainer() {
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.add(toolbar, BorderLayout.NORTH);
        {
            JSplitPane splitPane0 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
            { // Workspace pane
                JSplitPane splitPane1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
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
        if(FlatDarkLaf.setup())
            FlatDarkLaf.installLafInfo();
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
