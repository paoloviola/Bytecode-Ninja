package bytecodeninja.display.dialog;

import bytecodeninja.project.NinjaModule;
import bytecodeninja.project.NinjaProject;
import com.formdev.flatlaf.FlatClientProperties;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class NewModuleDialog extends JDialog
{

    private JPanel contentPane;
    private JButton createButton;
    private JButton cancelButton;

    private JTextField nameField;
    private JLabel importLabel;

    @Getter private NinjaModule module;

    private final NinjaProject project;
    public NewModuleDialog(Window parent, NinjaProject project) {
        super(parent, "New Module", DEFAULT_MODALITY_TYPE);
        setContentPane(contentPane);
        getRootPane().setDefaultButton(createButton);
        this.project = project;

        StringBuilder name = new StringBuilder("Unnamed");
        for(int i = 0; project.findModule(name.toString()) != null; i++) {
            name.setLength(0);
            name.append("Unnamed (").append(i).append(')');
        }
        nameField.setText(name.toString());
        importLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        contentPane.registerKeyboardAction(this::cancelModule,
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        createButton.addActionListener(this::createModule);
        cancelButton.addActionListener(this::cancelModule);
        importLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                importModule();
            }
        });

        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(parent);
    }

    @Override
    public void setVisible(boolean b) {
        if(b) module = null;
        super.setVisible(b);
        if(!b) dispose();
    }

    private void importModule() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Module File...");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setMultiSelectionEnabled(false);

        if(fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
            return;

        try {
            NinjaModule module = NinjaModule.load(fileChooser.getSelectedFile());
            if(handleModuleExistence(module.getName())) return;
            this.module = module;
            setVisible(false);
        }
        catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not load module!\nRead the console for further information.", "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void createModule(ActionEvent e) {
        if(handleIncompleteInput()) return;
        if(handleInvalidInput()) return;
        module = new NinjaModule(nameField.getText().trim());
        setVisible(false);
    }

    private void cancelModule(ActionEvent e) {
        module = null;
        setVisible(false);
    }

    private boolean handleModuleExistence(String name) {
        if(project.findModule(name) == null)
            return false;

        JOptionPane.showMessageDialog(this,
                "Module already exists!", "Could not create Module",
                JOptionPane.INFORMATION_MESSAGE
        );
        return true;
    }

    private boolean handleInvalidInput() {
        if(!handleModuleExistence(nameField.getText().trim()))
            return false;

        nameField.putClientProperty(
                FlatClientProperties.OUTLINE, FlatClientProperties.OUTLINE_ERROR
        );
        return true;
    }

    private boolean handleIncompleteInput() {
        boolean error = false;
        if(nameField.getText().trim().isEmpty()) {
            nameField.putClientProperty(
                    FlatClientProperties.OUTLINE, FlatClientProperties.OUTLINE_ERROR
            );
            error = true;
        }
        else {
            nameField.putClientProperty(
                    FlatClientProperties.OUTLINE, null
            );
        }
        return error;
    }

}
