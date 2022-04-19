package bytecodeninja.display.dialog;

import bytecodeninja.project.NinjaModule;
import bytecodeninja.project.NinjaProject;
import bytecodeninja.project.RunConfig;
import bytecodeninja.util.SwingUtil;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class ProjectStructureDialog extends JDialog
{

    private JPanel contentPane;
    private JButton okButton;

    // Project tab
    private JTextField project_nameField;
    private JTextField project_locationField;

    // Modules tab
    private JList<NinjaModule> modules_moduleList;
    private JButton modules_removeModuleButton;
    private JButton modules_addModuleButton;
    private JList<String> modules_libraryList;
    private JButton modules_addLibraryButton;
    private JButton modules_removeLibraryButton;
    private JTextField modules_nameField;

    // Configs tab
    private JList<RunConfig> configs_configList;
    private JButton configs_addConfigButton;
    private JButton configs_removeConfigButton;
    private JTextField configs_nameField;
    private JComboBox<NinjaModule> configs_moduleCombo;
    private JTextField configs_mainClassField;
    private JTextField configs_vmArgsField;
    private JTextField configs_programArgsField;
    private JTextField configs_workDirField;
    private JButton configs_workDirButton;

    private final NinjaProject project;
    private final DefaultListModel<NinjaModule> modules_moduleListModel;
    private final DefaultListModel<String> modules_libraryListModel;
    private final DefaultListModel<RunConfig> configs_configListModel;
    public ProjectStructureDialog(Window parent, NinjaProject project) {
        super(parent, "Project Structure", DEFAULT_MODALITY_TYPE);
        setContentPane(contentPane);
        getRootPane().setDefaultButton(okButton);
        this.project = project;

        { // Decorate dialog
            final FlatSVGIcon addIcon = new FlatSVGIcon("icons/add.svg");
            final FlatSVGIcon removeIcon = new FlatSVGIcon("icons/remove.svg");
            final FlatSVGIcon moduleIcon = new FlatSVGIcon("icons/moduleDirectory.svg");

            // Add and remove buttons
            modules_addModuleButton.setIcon(addIcon);
            modules_removeModuleButton.setIcon(removeIcon);
            modules_addLibraryButton.setIcon(addIcon);
            modules_removeLibraryButton.setIcon(removeIcon);
            configs_addConfigButton.setIcon(addIcon);
            configs_removeConfigButton.setIcon(removeIcon);

            // Lists and Combos
            modules_moduleList.setCellRenderer(new IconCellRenderer(moduleIcon));
            modules_libraryList.setCellRenderer(new IconCellRenderer(new FlatSVGIcon("icons/archive.svg")));
            configs_configList.setCellRenderer(new IconCellRenderer(new FlatSVGIcon("icons/application.svg")));
            configs_moduleCombo.setRenderer(new IconCellRenderer(moduleIcon));
            modules_moduleList.setModel(modules_moduleListModel = new DefaultListModel<>());
            modules_libraryList.setModel(modules_libraryListModel = new DefaultListModel<>());
            configs_configList.setModel(configs_configListModel = new DefaultListModel<>());
        }

        contentPane.registerKeyboardAction(e -> setVisible(false),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        modules_moduleList.addListSelectionListener(this::selectModule);
        modules_libraryList.addListSelectionListener(this::selectLibrary);
        configs_configList.addListSelectionListener(this::selectConfig);
        okButton.addActionListener(e -> setVisible(false));
        addModuleListeners();
        addConfigListeners();
        fillContents();

        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(parent);
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        if(!b) dispose();
    }

    // Modules tab (changes)
    private void addModule(ActionEvent e) {
        NewModuleDialog dialog = new NewModuleDialog(this, project);
        dialog.setVisible(true);

        if(dialog.getModule() == null) return;
        if(project.addModule(dialog.getModule())) {
            modules_moduleListModel.addElement(dialog.getModule());
            configs_moduleCombo.addItem(dialog.getModule());
        }
        else {
            JOptionPane.showMessageDialog(this,
                    "Could not create Module!\nRead the console for further information.", "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void removeModule(ActionEvent e) {
        int selectedIndex = modules_moduleList.getSelectedIndex();
        if(selectedIndex == -1) throw new RuntimeException("This is not supposed to happen!");

        NinjaModule selectedModule = modules_moduleListModel.getElementAt(selectedIndex);
        if(project.removeModule(selectedModule)) {
            modules_moduleListModel.removeElementAt(selectedIndex);
            configs_moduleCombo.removeItem(selectedModule);
        }
        else
            throw new RuntimeException("This is not supposed to happen!");
    }

    private void setModuleName() {
        NinjaModule selectedModule = modules_moduleList.getSelectedValue();
        if(selectedModule != null) {
            selectedModule.setName(modules_nameField.getText().trim());
            modules_moduleList.revalidate();
            modules_moduleList.repaint();
        }
    }

    private void addLibrary(ActionEvent e) {
        NinjaModule selectedModule = modules_moduleList.getSelectedValue();
        if(selectedModule == null) throw new RuntimeException("This is not supposed to happen!");

        JFileChooser fileChooser = new JFileChooser(project.getLocation());
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setDialogTitle("Select Libraries...");
        fileChooser.setMultiSelectionEnabled(true);

        if(fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
            return;

        try {
            // Quick'n'dirty way to add libraries
            final Consumer<String> addLibraryEvent = lib -> {
                if(selectedModule.addLibrary(project, lib))
                    modules_libraryListModel.addElement(lib);
                else {
                    JOptionPane.showMessageDialog(this,
                            "Could not add Library \"" + lib + "\"!\nRead the console for further information.", "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            };

            for(File file : fileChooser.getSelectedFiles()) {
                BasicFileAttributes fileInfo = Files.readAttributes(
                        file.toPath(), BasicFileAttributes.class
                );

                if(fileInfo.isDirectory()) {
                    try(Stream<Path> stream = Files.walk(file.toPath())) {
                        stream.filter(Files::isRegularFile)
                                .map(Path::toString)
                                .filter(p -> p.endsWith(".jar") || p.endsWith(".zip"))
                                .forEach(addLibraryEvent);
                    }
                }
                else if(fileInfo.isRegularFile())
                    addLibraryEvent.accept(file.getAbsolutePath());
            }
        }
        catch (IOException e0) {
            e0.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Could not import Libraries!\nRead the console for futher information.", "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void removeLibrary(ActionEvent e) {
        NinjaModule selectedModule = modules_moduleList.getSelectedValue();
        int[] selectedIndices = modules_libraryList.getSelectedIndices();
        if(selectedModule == null || selectedIndices.length == 0)
            throw new RuntimeException("This is not supposed to happen!");

        for(int i = selectedIndices.length - 1; i >= 0; i--) {
            String lib = modules_libraryListModel.getElementAt(selectedIndices[i]);
            if(selectedModule.removeLibrary(project, lib))
                modules_libraryListModel.removeElementAt(selectedIndices[i]);
            else {
                JOptionPane.showMessageDialog(this,
                        "Could not remove Library \"" + lib + "\"!\nRead the console for further information.", "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
    // Modules tab (changes)

    // Configs tab (changes)
    private void addConfig(ActionEvent e) {
        RunConfig config = new RunConfig("Unnamed");
        configs_configListModel.addElement(config);
    }

    private void removeConfig(ActionEvent e) {
        int selectedIndex = configs_configList.getSelectedIndex();
        if(selectedIndex == -1) throw new RuntimeException("This is not supposed to happen!");
        RunConfig selectedConfig = configs_configListModel.getElementAt(selectedIndex);

        NinjaModule correspondingModule = findModuleOf(selectedConfig);
        if(correspondingModule == null || correspondingModule.removeConfig(project, selectedConfig))
            configs_configListModel.removeElementAt(selectedIndex);
        else {
            JOptionPane.showMessageDialog(this,
                    "Could not remove Config!\nRead the console for further information.", "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void setConfigName() {
        RunConfig selectedConfig = configs_configList.getSelectedValue();
        if(selectedConfig != null) {
            selectedConfig.setName(configs_nameField.getText().trim());
            configs_configList.revalidate();
            configs_configList.repaint();
        }
    }

    private void setConfigModule(ActionEvent e) {
        RunConfig selectedConfig = configs_configList.getSelectedValue();
        if(selectedConfig == null) return;

        NinjaModule oldModule = findModuleOf(selectedConfig);
        if(oldModule != null) oldModule.removeConfig(project, selectedConfig);
        NinjaModule newModule = (NinjaModule) configs_moduleCombo.getSelectedItem();
        if(newModule != null) newModule.addConfig(project, selectedConfig);
    }

    private void setConfigMainClass() {
        RunConfig selectedConfig = configs_configList.getSelectedValue();
        if(selectedConfig != null)
            selectedConfig.setMainClass(configs_mainClassField.getText().trim());
    }

    private void setConfigWorkDir(ActionEvent e) {
        RunConfig selectedConfig = configs_configList.getSelectedValue();
        if(selectedConfig == null) throw new RuntimeException("This is not supposed to happen!");

        JFileChooser fileChooser = new JFileChooser(selectedConfig.getWorkDirectory());
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle("Select Working directory...");
        fileChooser.setMultiSelectionEnabled(false);

        if(fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            selectedConfig.setWorkDirectory(selectedFile.getAbsolutePath());
            configs_workDirField.setText(selectedConfig.getWorkDirectory());
        }
    }

    private void setConfigProgramArgs() {
        RunConfig selectedConfig = configs_configList.getSelectedValue();
        if(selectedConfig != null)
            selectedConfig.setProgramArguments(configs_programArgsField.getText().trim());
    }

    private void setConfigVMArgs() {
        RunConfig selectedConfig = configs_configList.getSelectedValue();
        if(selectedConfig != null)
            selectedConfig.setVmArguments(configs_vmArgsField.getText().trim());
    }
    // Configs tab (changes)

    // Apply region
    private void applyModuleName(FocusEvent e) {
        NinjaModule selectedModule = modules_moduleList.getSelectedValue();
        if(selectedModule == null) throw new RuntimeException("This is not supposed to happen!");

        // Check if module name is empty
        if(selectedModule.getName().isEmpty()) {
            // Set outline error
            modules_nameField.putClientProperty(FlatClientProperties.OUTLINE, FlatClientProperties.OUTLINE_ERROR);
            modules_nameField.requestFocusInWindow();
            return;
        }

        // Check if module exists twice
        if(findModuleExcept(selectedModule, selectedModule.getName()) == null) {
            modules_nameField.putClientProperty(FlatClientProperties.OUTLINE, null); // Clear outline
            if(!project.save()) { // When saving failed
                modules_nameField.requestFocusInWindow();
                JOptionPane.showMessageDialog(this,
                        "Could not apply changes!\nRead the console for further information.", "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
        else {
            JOptionPane.showMessageDialog(this,
                    "Module name already exists!", "Error",
                    JOptionPane.ERROR_MESSAGE
            );

            // Set outline error
            modules_nameField.putClientProperty(FlatClientProperties.OUTLINE, FlatClientProperties.OUTLINE_ERROR);
            modules_nameField.requestFocusInWindow();
        }
    }

    private void applyConfigName(FocusEvent focusEvent) {
        RunConfig selectedConfig = configs_configList.getSelectedValue();
        if(selectedConfig == null) throw new RuntimeException("This is not supposed to happen!");

        // Check if config name is empty
        if(selectedConfig.getName().isEmpty()) {
            // Set outline error
            modules_nameField.putClientProperty(FlatClientProperties.OUTLINE, FlatClientProperties.OUTLINE_ERROR);
            modules_nameField.requestFocusInWindow();
            return;
        }

        // Check if config exists twice
        if(findConfigExcept(selectedConfig, selectedConfig.getName()) == null) {
            configs_nameField.putClientProperty(FlatClientProperties.OUTLINE, null); // Clear outline

            NinjaModule selectedModule = findModuleOf(selectedConfig);
            if(selectedModule != null && !selectedModule.save(project)) {
                configs_nameField.requestFocusInWindow();
                JOptionPane.showMessageDialog(this,
                        "Could not apply changes!\nRead the console for further information.", "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
        else {
            JOptionPane.showMessageDialog(this,
                    "Config name already exists!", "Error",
                    JOptionPane.ERROR_MESSAGE
            );

            // Set outline error
            configs_nameField.putClientProperty(FlatClientProperties.OUTLINE, FlatClientProperties.OUTLINE_ERROR);
            configs_nameField.requestFocusInWindow();
        }
    }

    private void applyAllSettings(FocusEvent focusEvent) {
        if(!project.save()) {
            JOptionPane.showMessageDialog(this,
                    "Could not apply Settings!\nRead the console for further information.", "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
    // Apply region

    // Select region
    private void selectModule(ListSelectionEvent e) {
        NinjaModule selectedModule = modules_moduleList.getSelectedValue();
        modules_removeModuleButton.setEnabled(selectedModule != null);

        modules_nameField.setEnabled(selectedModule != null);
        modules_addLibraryButton.setEnabled(selectedModule != null);
        modules_libraryList.setEnabled(selectedModule != null);

        modules_nameField.setText(selectedModule == null ? "" : selectedModule.getName());

        modules_libraryListModel.removeAllElements();
        if(selectedModule != null) {
            selectedModule.getLibraries()
                    .forEach(modules_libraryListModel::addElement);
        }
        selectLibrary(null); // Update current state
    }

    private void selectLibrary(ListSelectionEvent e) {
        int selectedIndex = modules_libraryList.getSelectedIndex();
        modules_removeLibraryButton.setEnabled(selectedIndex >= 0);
    }

    private void selectConfig(ListSelectionEvent e) {
        RunConfig selectedConfig = configs_configList.getSelectedValue();
        configs_removeConfigButton.setEnabled(selectedConfig != null);

        configs_nameField.setEnabled(selectedConfig != null);
        configs_moduleCombo.setEnabled(selectedConfig != null);
        configs_mainClassField.setEnabled(selectedConfig != null);
        configs_workDirButton.setEnabled(selectedConfig != null);
        configs_programArgsField.setEnabled(selectedConfig != null);
        configs_vmArgsField.setEnabled(selectedConfig != null);

        configs_nameField.setText(selectedConfig == null ? "" : selectedConfig.getName());
        configs_moduleCombo.setSelectedItem(selectedConfig == null ? null : findModuleOf(selectedConfig));
        configs_mainClassField.setText(selectedConfig == null ? "" : selectedConfig.getMainClass());
        configs_workDirField.setText(selectedConfig == null ? "" : selectedConfig.getWorkDirectory());
        configs_programArgsField.setText(selectedConfig == null ? "" : selectedConfig.getProgramArguments());
        configs_vmArgsField.setText(selectedConfig == null ? "" : selectedConfig.getVmArguments());
    }
    // Select region

    private void addModuleListeners() {
        modules_addModuleButton.addActionListener(this::addModule);
        modules_removeModuleButton.addActionListener(this::removeModule);
        SwingUtil.addDocumentListener(modules_nameField, this::setModuleName);
        modules_addLibraryButton.addActionListener(this::addLibrary);
        modules_removeLibraryButton.addActionListener(this::removeLibrary);

        modules_nameField.addFocusListener((FocusListenerAdapter) this::applyModuleName);
    }

    private void addConfigListeners() {
        configs_addConfigButton.addActionListener(this::addConfig);
        configs_removeConfigButton.addActionListener(this::removeConfig);
        SwingUtil.addDocumentListener(configs_nameField, this::setConfigName);
        configs_moduleCombo.addActionListener(this::setConfigModule);
        SwingUtil.addDocumentListener(configs_mainClassField, this::setConfigMainClass);
        configs_workDirButton.addActionListener(this::setConfigWorkDir);
        SwingUtil.addDocumentListener(configs_programArgsField, this::setConfigProgramArgs);
        SwingUtil.addDocumentListener(configs_vmArgsField, this::setConfigVMArgs);

        configs_nameField.addFocusListener((FocusListenerAdapter) this::applyConfigName);
        configs_mainClassField.addFocusListener((FocusListenerAdapter) this::applyAllSettings);
        configs_programArgsField.addFocusListener((FocusListenerAdapter) this::applyAllSettings);
        configs_vmArgsField.addFocusListener((FocusListenerAdapter) this::applyAllSettings);
    }

    private void fillContents() {
        project_nameField.setText(project.getName());
        project_locationField.setText(project.getLocation());

        modules_moduleListModel.removeAllElements();
        configs_configListModel.removeAllElements();
        for(NinjaModule module : project.getModules()) {
            modules_moduleListModel.addElement(module);
            configs_moduleCombo.addItem(module);

            for(RunConfig config : module.getRunConfigs())
                configs_configListModel.addElement(config);
        }

        selectModule(null); // Update current state
        selectConfig(null); // Update current state
    }

    private NinjaModule findModuleExcept(NinjaModule exception, String name) {
        try(Stream<NinjaModule> stream = project.getModules().stream()) {
            return stream.filter(m -> m.getName().equals(name))
                    .filter(m -> !m.equals(exception))
                    .findAny().orElse(null);
        }
    }

    private RunConfig findConfigExcept(RunConfig exception, String name) {
        for(NinjaModule module : project.getModules()) {
            for(RunConfig config : module.getRunConfigs()) {
                if(!config.equals(exception) && config.getName().equals(name))
                    return config;
            }
        }

        return null;
    }

    private NinjaModule findModuleOf(RunConfig config) {
        try(Stream<NinjaModule> stream = project.getModules().stream()) {
            return stream.filter(m -> m.getRunConfigs().contains(config))
                    .findAny().orElse(null);
        }
    }

    private static class IconCellRenderer extends DefaultListCellRenderer {
        private final Icon icon;
        public IconCellRenderer(Icon icon) {
            this.icon = icon;
        }

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            setIcon(icon);
            return this;
        }
    }

    private interface FocusListenerAdapter extends FocusListener {
        @Override
        default void focusGained(FocusEvent e) { }
    }

}
