package bytecodeninja.display.environment;

import bytecodeninja.project.NinjaProject;

import javax.swing.*;
import java.awt.*;

public class ExplorerPane extends JPanel
{

    public ExplorerPane(NinjaMenubar parent) {
        super(new BorderLayout());
        add(new JLabel("Explorer", JLabel.CENTER), BorderLayout.CENTER);
    }

    void selectProject(NinjaProject project) { }

}
