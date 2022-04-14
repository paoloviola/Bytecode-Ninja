package bytecodeninja.display.environment;

import bytecodeninja.project.NinjaProject;

import javax.swing.*;
import java.awt.*;

public class ConsolePane extends JPanel
{

    public ConsolePane(NinjaMenubar parent) {
        super(new BorderLayout());
        add(new JLabel("Console", JLabel.CENTER), BorderLayout.CENTER);
    }

    void selectProject(NinjaProject project) { }

}
