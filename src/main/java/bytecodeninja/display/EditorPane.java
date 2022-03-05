package bytecodeninja.display;

import javax.swing.*;
import java.awt.*;

public class EditorPane extends JPanel
{

    public EditorPane() {
        super(new BorderLayout());
        add(new JLabel("Editor", JLabel.CENTER), BorderLayout.CENTER);
    }

}
