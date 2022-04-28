package bytecodeninja.display.environment;

import bytecodeninja.display.StaticIcon;
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

    private final NinjaMenubar menu;
    public ToolbarPane(NinjaMenubar menu) {
        this.menu = menu;

        setLayout(new FlowLayout(FlowLayout.RIGHT, 2, 2));
        setBorder(BorderFactory.createRaisedBevelBorder());
        setFloatable(false);

        // TODO: MAYBE ADD SOME LAYOUT
        add(Box.createHorizontalGlue());
        add(buildBtn = createButton("icons/compile.svg", "Build Project", menu::buildProject));

        configCombo = new JComboBox<>();
        configCombo.setToolTipText("Select Run config");
        configCombo.setRenderer(new IconComboRenderer(StaticIcon.get("icons/application.svg")));
        configCombo.addActionListener(e -> this.selectConfig((RunConfig) configCombo.getSelectedItem()));
        add(configCombo);

        add(runBtn = createButton("icons/execute.svg", "Run", menu::runConfig));
        add(debugBtn = createButton("icons/startDebugger.svg", "Debug", menu::debugConfig));
    }

    void selectProject(NinjaProject project) {
        configCombo.removeAllItems();
        if(project != null) {
            project.getModules().forEach(
                    m -> m.getRunConfigs().forEach(configCombo::addItem)
            );
        }

        buildBtn.setEnabled(project != null);
        configCombo.setEnabled(project != null && configCombo.getItemCount() > 0);
        selectConfig((RunConfig) configCombo.getSelectedItem());
    }

    void selectConfig(RunConfig config) {
        runBtn.setEnabled(config != null);
        debugBtn.setEnabled(config != null);

        String postfix = config == null ? "" : " '" + config + "'";
        runBtn.setToolTipText("Run" + postfix);
        debugBtn.setToolTipText("Debug" + postfix);
        this.menu.selectConfig(config);
    }

    private static JButton createButton(String icon, String tooltip, ActionListener action) {
        JButton button = new JButton(StaticIcon.get(icon));
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
