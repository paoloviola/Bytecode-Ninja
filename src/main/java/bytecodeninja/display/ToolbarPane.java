package bytecodeninja.display;

import javax.swing.*;
import java.awt.*;

public class ToolbarPane extends JPanel
{

    public ToolbarPane() {
        super(new BorderLayout());
        add(new JLabel("Toolbar", JLabel.CENTER), BorderLayout.CENTER);
    }

}
