package br.com.camtwo.intellij.tomcatrunner.ui;

import br.com.camtwo.intellij.tomcatrunner.model.Module;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.*;
import java.awt.event.*;

public class NewModule extends JDialog {
    private AddModuleInterface addModuleInterface;
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField context;
    private JTextField docBase;
    private JCheckBox reloadableModule;
    private JButton locateBase;

    public NewModule(AddModuleInterface addModuleInterface) {
        this.addModuleInterface = addModuleInterface;
        this.setLocationRelativeTo(null);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        locateBase.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
                descriptor.setTitle("Select document base");
                VirtualFile virtualFile = FileChooser.chooseFile(descriptor, null, null);
                if (virtualFile != null) {
                    docBase.setText(virtualFile.getPath());
                }
            }
        });
// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        this.pack();
        this.setVisible(true);
    }

    private void onOK() {
        if (context.getText().isEmpty() || docBase.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Context and Document base are required", "Add module", JOptionPane
                    .ERROR_MESSAGE);
        } else {
            Module module = new Module(context.getText(), docBase.getText(), true, reloadableModule.isSelected());
            addModuleInterface.addModule(module);
            dispose();
        }
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

}
