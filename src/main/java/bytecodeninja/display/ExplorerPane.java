package bytecodeninja.display;

import bytecodeninja.project.NinjaProject;

import javax.swing.*;
import java.awt.*;

public class ExplorerPane extends JPanel
{

    public ExplorerPane(NinjaMenubar parent) {
        super(new BorderLayout());
        add(new JLabel("Explorer", JLabel.CENTER), BorderLayout.CENTER);
    }

    public void selectProject(NinjaProject project) { }

}
