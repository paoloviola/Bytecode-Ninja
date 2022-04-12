package bytecodeninja.display;

import bytecodeninja.project.NinjaProject;

import javax.swing.*;
import java.awt.*;

public class EditorPane extends JPanel
{

    public EditorPane(NinjaMenubar parent) {
        super(new BorderLayout());
        add(new JLabel("Editor", JLabel.CENTER), BorderLayout.CENTER);
    }

    public void selectProject(NinjaProject project) { }

}
