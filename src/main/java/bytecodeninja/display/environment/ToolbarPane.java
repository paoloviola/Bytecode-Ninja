package bytecodeninja.display.environment;

import bytecodeninja.project.NinjaProject;
import bytecodeninja.project.RunConfig;
import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ToolbarPane extends JToolBar
{

    private final JButton buildBtn;
    private final JComboBox<RunConfig> configCombo;
    private final JButton runBtn;
    private final JButton debugBtn;

    private final NinjaMenubar parent;
    public ToolbarPane(NinjaMenubar parent) {
        this.parent = parent;

        setLayout(new FlowLayout(FlowLayout.RIGHT, 2, 2));
        setBorder(BorderFactory.createRaisedBevelBorder());
        setFloatable(false);

        // TODO: MAYBE ADD SOME LAYOUT
        add(Box.createHorizontalGlue());
        add(buildBtn = createButton("icons/compile.svg", "Build Project", parent::buildProject));

        configCombo = new JComboBox<>();
        configCombo.setToolTipText("Select Run config");
        configCombo.setRenderer(new IconComboRenderer(new FlatSVGIcon("icons/application.svg")));
        configCombo.addActionListener(e -> this.selectConfig((RunConfig) configCombo.getSelectedItem()));
        add(configCombo);

        add(runBtn = createButton("icons/execute.svg", "Run", parent::runConfig));
        add(debugBtn = createButton("icons/startDebugger.svg", "Debug", parent::debugConfig));
    }

    public void selectProject(NinjaProject project) {
        configCombo.removeAllItems();
        if(project != null) {
            project.getModules().forEach(
                    m -> m.getRunConfigs().forEach(configCombo::addItem)
            );
        }

        buildBtn.setEnabled(project != null);
        configCombo.setEnabled(project != null);
        selectConfig((RunConfig) configCombo.getSelectedItem());
    }

    public void selectConfig(RunConfig config) {
        runBtn.setEnabled(config != null);
        debugBtn.setEnabled(config != null);

        String postfix = config == null ? "" : " '" + config + "'";
        runBtn.setToolTipText("Run" + postfix);
        debugBtn.setToolTipText("Debug" + postfix);
        this.parent.selectConfig(config);
    }

    private static JButton createButton(String icon, String tooltip, ActionListener action) {
        JButton button = new JButton(new FlatSVGIcon(icon));
        button.addActionListener(action);
        button.setToolTipText(tooltip);
        return button;
    }

    private static class IconComboRenderer extends DefaultListCellRenderer {
        private final Icon icon;
        public IconComboRenderer(Icon icon) {
            this.icon = icon;
        }

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            setIcon(icon);
            return this;
        }
    }

}
