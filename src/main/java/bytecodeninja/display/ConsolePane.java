package bytecodeninja.display;

import javax.swing.*;
import java.awt.*;

public class ConsolePane extends JPanel
{

    public ConsolePane() {
        super(new BorderLayout());
        add(new JLabel("Console", JLabel.CENTER), BorderLayout.CENTER);
    }

}
