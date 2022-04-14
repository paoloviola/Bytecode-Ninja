package bytecodeninja.display.dialog;

import bytecodeninja.project.NinjaModule;
import bytecodeninja.project.NinjaProject;
import com.formdev.flatlaf.FlatClientProperties;
import lombok.Getter;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class NewModuleDialog extends JDialog
{

    private JPanel contentPane;
    private JButton createButton;
    private JButton cancelButton;

    private JTextField nameField;

    @Getter private NinjaModule module;

    private final NinjaProject project;
    public NewModuleDialog(JFrame parent, NinjaProject project) {
        super(parent, "New Module", true);
        setContentPane(contentPane);
        getRootPane().setDefaultButton(createButton);
        this.project = project;

        nameField.setText("Unnamed");
        createButton.addActionListener(this::createModule);
        cancelButton.addActionListener(this::cancelModule);

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

    private void createModule(ActionEvent e) {
        if(handleIncompleteInput()) return;
        if(handleInvalidInput()) return;
        module = new NinjaModule(project, nameField.getText().trim());
        setVisible(false);
    }

    private void cancelModule(ActionEvent e) {
        module = null;
        setVisible(false);
    }

    private boolean handleInvalidInput() {
        if(project.findModule(nameField.getText().trim()) == null)
            return false;

        nameField.putClientProperty(
                FlatClientProperties.OUTLINE, FlatClientProperties.OUTLINE_ERROR
        );

        JOptionPane.showMessageDialog(this,
                "Module already exists!", "Could not create Module",
                JOptionPane.INFORMATION_MESSAGE
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
