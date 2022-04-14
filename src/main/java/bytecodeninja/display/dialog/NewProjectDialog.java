package bytecodeninja.display.dialog;

import bytecodeninja.project.NinjaProject;
import com.formdev.flatlaf.FlatClientProperties;
import lombok.Getter;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.event.ActionEvent;
import java.io.File;

public class NewProjectDialog extends JDialog
{

    private JPanel contentPane;
    private JButton createButton;
    private JButton cancelButton;

    private JTextField nameField;
    private JTextField locationField;
    private JButton locationButton;

    @Getter private NinjaProject project;

    public NewProjectDialog(JFrame parent) {
        super(parent, "New Project", true);
        setContentPane(contentPane);
        getRootPane().setDefaultButton(createButton);

        nameField.setText("Unnamed");
        locationField.setText(new File(
                FileSystemView.getFileSystemView().getDefaultDirectory(),
                "Ninja Projects/" + nameField.getText()
        ).getAbsolutePath());

        locationButton.addActionListener(this::selectLocation);
        createButton.addActionListener(this::createProject);
        cancelButton.addActionListener(this::cancelProject);

        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(parent);
    }

    @Override
    public void setVisible(boolean b) {
        if(b) project = null;
        super.setVisible(b);
        if(!b) dispose();
    }

    private void selectLocation(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Project Path...");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setMultiSelectionEnabled(false);

        if(fileChooser.showOpenDialog(getParent()) == JFileChooser.APPROVE_OPTION) {
            locationField.setText(new File(
                    fileChooser.getSelectedFile(),
                    nameField.getText()
            ).getAbsolutePath());
        }
    }

    private void createProject(ActionEvent e) {
        if(handleIncompleteInput()) return;
        if(handleInvalidInput()) return;
        project = new NinjaProject(
                nameField.getText().trim(),
                locationField.getText().trim()
        );
        setVisible(false);
    }

    private void cancelProject(ActionEvent e) {
        project = null;
        setVisible(false);
    }

    private boolean handleInvalidInput() {
        File location = new File(locationField.getText().trim());

        String[] files = location.list();
        if(files != null && files.length > 0) {
            locationField.putClientProperty(
                    FlatClientProperties.OUTLINE, FlatClientProperties.OUTLINE_ERROR
            );

            JOptionPane.showMessageDialog(this,
                    "Please select an empty directory!", "Could not load Project",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return true;
        }
        else {
            locationField.putClientProperty(
                    FlatClientProperties.OUTLINE, null
            );
        }

        return false;
    }

    private boolean handleIncompleteInput() {
        boolean error = false;

        // Name-field checking
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

        // Location-field checking
        if(locationField.getText().trim().isEmpty()) {
            locationField.putClientProperty(
                    FlatClientProperties.OUTLINE, FlatClientProperties.OUTLINE_ERROR
            );
            error = true;
        }
        else {
            locationField.putClientProperty(
                    FlatClientProperties.OUTLINE, null
            );
        }

        return error;
    }

}
