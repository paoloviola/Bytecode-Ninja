package bytecodeninja.display;

import javax.swing.*;
import java.awt.*;

public class ExplorerPane extends JPanel
{

    public ExplorerPane() {
        super(new BorderLayout());
        add(new JLabel("Explorer", JLabel.CENTER), BorderLayout.CENTER);
    }

}
