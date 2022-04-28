package bytecodeninja.display.environment;

import bytecodeninja.project.NinjaProject;

import javax.swing.*;
import java.awt.*;

public class EditorPane extends JPanel
{

    public EditorPane(NinjaMenubar menu) {
        super(new BorderLayout());
        add(new JLabel("Editor", JLabel.CENTER), BorderLayout.CENTER);
    }

    void selectProject(NinjaProject project) { }

}
