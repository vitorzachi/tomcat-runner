package br.com.camtwo.intellij.tomcatrunner.ui;

import br.com.camtwo.intellij.tomcatrunner.model.Module;
import com.intellij.execution.configuration.EnvironmentVariablesComponent;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

/**
 * View / Presentation - Created using the WYSIWYG editor.
 * Used the JGoodies Form Layout - which is BSD.
 *
 * @author Vitor Zachi Junior
 * Inspired on jetty-runner by GuiKeller.
 */
public class TomcatRunnerConfPanel {

    private JPanel mainPanel;
    private JTextField tomcatField;
    private JButton browseTomcatButton;
    private JTextField vmArgsField;
    private EnvironmentVariablesComponent environmentVariables;
    private JLabel spacerLabel;
    private JLabel vmArgsLabel;
    private JLabel firstMsgLabel;
    private JLabel webappLabel;
    private JLabel pathLabel;
    private JLabel secondMsgLabel;
    private JLabel envVarLabel;
    private JButton addContextButton;
    private JTable contexts;
    private JScrollPane tableScrollPane;


    public TomcatRunnerConfPanel() {
        // Action executed when clicked on "..." of tomcat button
        browseTomcatButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Shows a file chooser
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setMultiSelectionEnabled(Boolean.FALSE);
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                // Checking whether the user clicked okay
                int result = fileChooser.showOpenDialog(new JFrame());
                if (result == JFileChooser.APPROVE_OPTION) {
                    StringBuffer paths = new StringBuffer();
                    File selectedFile = fileChooser.getSelectedFile();
                    if (selectedFile != null) {
                        tomcatField.setText(selectedFile.getAbsolutePath());
                    }
                }
            }
        });

        addContextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final JTextField context = new JTextField();
                final JTextField location = new JTextField();
                JButton docChooser = new JButton("Locate doc. base");
                docChooser.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // Shows a file chooser
                        JFileChooser fileChooser = new JFileChooser();
                        fileChooser.setMultiSelectionEnabled(Boolean.FALSE);
                        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                        // Checking whether the user clicked okay
                        int result = fileChooser.showOpenDialog(new JFrame());
                        if (result == JFileChooser.APPROVE_OPTION) {
                            StringBuffer paths = new StringBuffer();
                            File selectedFile = fileChooser.getSelectedFile();
                            if (selectedFile != null) {
                                location.setText(selectedFile.getAbsolutePath());
                            }
                        }
                    }
                });
                final JComponent[] inputs = new JComponent[]{
                        new JLabel("Context"),
                        context,
                        new JLabel("Document base"),
                        location,
                        docChooser
                };
                JOptionPane.showMessageDialog(null, inputs, "Add module", JOptionPane.PLAIN_MESSAGE);
                if (context.getText().isEmpty() || location.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Context and Document base are required", "Add module", JOptionPane
                            .ERROR_MESSAGE);
                } else {
                    ((ModuleTableModel) (contexts.getModel())).addModule(new Module(context.getText(), location.getText()));
                }
            }
        });

        contexts.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2 && contexts.getRowCount() > 0) {
                    int dialogResult = JOptionPane.showConfirmDialog(null, "Are you sure delete selected module?",
                            "Delete module", JOptionPane.YES_NO_OPTION);
                    if (dialogResult == JOptionPane.YES_OPTION) {
                        ((ModuleTableModel) (contexts.getModel())).removeModule(contexts.getSelectedRow());
                    }
                }
            }
        });

    }


    public JPanel getMainPanel() {
        return mainPanel;
    }

    public JTextField getTomcatField() {
        return tomcatField;
    }

    public JTextField getVmArgsField() {
        return vmArgsField;
    }

    public JButton getBrowseTomcatButton() {
        return browseTomcatButton;
    }

    public JTable getContexts() {
        return contexts;
    }

    public EnvironmentVariablesComponent getEnvironmentVariables() {
        return environmentVariables;
    }


    private void createUIComponents() {
        contexts = new JBTable(new ModuleTableModel());
        contexts.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        contexts.setDefaultRenderer(String.class, new ModuleCellRenderer());
        contexts.getColumnModel().getColumn(0).setPreferredWidth(25);
        contexts.getColumnModel().getColumn(1).setPreferredWidth(375);
        tableScrollPane = new JBScrollPane();
        tableScrollPane.setViewportView(contexts);
        tableScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        tableScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        tableScrollPane.setPreferredSize(new Dimension(400, 100));
    }


}
