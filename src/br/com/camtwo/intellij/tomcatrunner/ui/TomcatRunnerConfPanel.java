package br.com.camtwo.intellij.tomcatrunner.ui;

import br.com.camtwo.intellij.tomcatrunner.model.Module;
import com.intellij.execution.configuration.EnvironmentVariablesComponent;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * View / Presentation - Created using the WYSIWYG editor.
 * Used the JGoodies Form Layout - which is BSD.
 *
 * @author Vitor Zachi Junior
 *         Inspired on jetty-runner by GuiKeller.
 */
public class TomcatRunnerConfPanel implements AddModuleInterface {

    private TomcatRunnerEditor editor;

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

    public TomcatRunnerConfPanel(TomcatRunnerEditor editor) {
        this.editor = editor;

        // Action executed when clicked on "..." of tomcat button
        browseTomcatButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
                descriptor.setTitle("Select tomcat installation");
                VirtualFile virtualFile = FileChooser.chooseFile(descriptor, null, null);
                if (virtualFile != null) {
                    tomcatField.setText(virtualFile.getPath());
                }
            }
        });

        addContextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new NewModule(TomcatRunnerConfPanel.this);
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

//                evt.getClickCount() == 2 && contexts.getRowCount() > 0
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
        contexts.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        contexts.setDefaultRenderer(String.class, new ModuleCellRenderer());
        contexts.getColumnModel().getColumn(0).setPreferredWidth(25);
        tableScrollPane = new JBScrollPane();
        tableScrollPane.setViewportView(contexts);
        tableScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        tableScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        tableScrollPane.setPreferredSize(new Dimension(400, 100));
        tableScrollPane.setSize(new Dimension(400, 100));
    }

    @Override
    public void addModule(Module module) {
        ((ModuleTableModel) (contexts.getModel())).addModule(module);
    }
}
