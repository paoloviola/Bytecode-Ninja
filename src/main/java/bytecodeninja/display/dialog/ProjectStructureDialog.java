package bytecodeninja.display.dialog;

import bytecodeninja.project.NinjaModule;
import bytecodeninja.project.NinjaProject;
import bytecodeninja.project.RunConfig;
import bytecodeninja.util.SwingUtil;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
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

        modules_moduleList.setModel(modules_moduleListModel = new DefaultListModel<>());
        modules_libraryList.setModel(modules_libraryListModel = new DefaultListModel<>());
        configs_configList.setModel(configs_configListModel = new DefaultListModel<>());

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

    // Modules tab
    private void addModule(ActionEvent e) {
        NewModuleDialog dialog = new NewModuleDialog(this, project);
        dialog.setVisible(true);

        if(dialog.getModule() == null) return;
        if(project.addModule(dialog.getModule()))
            modules_moduleListModel.addElement(dialog.getModule());
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
        if(project.removeModule(selectedModule))
            modules_moduleListModel.removeElementAt(selectedIndex);
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
                    "Could not import Libraries!\nRead the console for more information.", "Error",
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
    // Modules tab

    // Configs tab
    private void addConfig(ActionEvent e) { // TODO:
    }

    private void removeConfig(ActionEvent e) { // TODO:
    }

    private void setConfigName() {
        RunConfig selectedConfig = configs_configList.getSelectedValue();
        if(selectedConfig != null) {
            selectedConfig.setName(configs_nameField.getText().trim());
            configs_configList.revalidate();
            configs_configList.repaint();
        }
    }

    private void setConfigModule(ItemEvent e) { // TODO:
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
    // Configs tab

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
        configs_mainClassField.setText(selectedConfig == null ? "" : selectedConfig.getMainClass());
        configs_workDirField.setText(selectedConfig == null ? "" : selectedConfig.getWorkDirectory());
        configs_programArgsField.setText(selectedConfig == null ? "" : selectedConfig.getProgramArguments());
        configs_vmArgsField.setText(selectedConfig == null ? "" : selectedConfig.getVmArguments());
    }

    private void addModuleListeners() {
        modules_addModuleButton.addActionListener(this::addModule);
        modules_removeModuleButton.addActionListener(this::removeModule);
        SwingUtil.addDocumentListener(modules_nameField, this::setModuleName);
        modules_addLibraryButton.addActionListener(this::addLibrary);
        modules_removeLibraryButton.addActionListener(this::removeLibrary);
    }

    private void addConfigListeners() {
        configs_addConfigButton.addActionListener(this::addConfig);
        configs_removeConfigButton.addActionListener(this::removeConfig);
        SwingUtil.addDocumentListener(configs_nameField, this::setConfigName);
        configs_moduleCombo.addItemListener(this::setConfigModule);
        SwingUtil.addDocumentListener(configs_mainClassField, this::setConfigMainClass);
        configs_workDirButton.addActionListener(this::setConfigWorkDir);
        SwingUtil.addDocumentListener(configs_programArgsField, this::setConfigProgramArgs);
        SwingUtil.addDocumentListener(configs_vmArgsField, this::setConfigVMArgs);
    }

    private void fillContents() {
        project_nameField.setText(project.getName());
        project_locationField.setText(project.getLocation());

        modules_moduleListModel.removeAllElements();
        configs_configListModel.removeAllElements();
        for(NinjaModule module : project.getModules()) {
            modules_moduleListModel.addElement(module);
            for(RunConfig config : module.getRunConfigs())
                configs_configListModel.addElement(config);
        }

        selectModule(null); // Update current state
        selectConfig(null); // Update current state
    }

}
