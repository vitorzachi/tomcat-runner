package br.com.camtwo.intellij.tomcatrunner.ui;

import br.com.camtwo.intellij.tomcatrunner.model.Module;
import br.com.camtwo.intellij.tomcatrunner.model.Modules;
import com.google.common.base.Optional;

import javax.swing.table.DefaultTableModel;

/**
 * Table model for Tomcat modules table.
 *
 * @author Vitor Zachi Junior
 * @since 23/03/16.
 */
public class ModuleTableModel extends DefaultTableModel {
    private String[] columns = {"Context", "Document base"};
    private Modules modules;

    public ModuleTableModel() {
        modules = new Modules();
    }

    public ModuleTableModel(Modules modules) {
        this.modules = modules;
    }

    public void addModule(Module module) {
        this.modules = Optional.fromNullable(this.modules).or(new Modules());
        modules.add(module);
        fireTableDataChanged();
    }

    public void removeModule(int index) {
        this.modules = Optional.fromNullable(this.modules).or(new Modules());
        modules.remove(index);
        fireTableDataChanged();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public int getRowCount() {
        return Optional.fromNullable(this.modules).or(new Modules()).size();
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (column == 0) {
            return modules.get(row).getContext();
        } else {
            return modules.get(row).getDocumentBase();
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    public Modules getModules() {
        return modules;
    }
}
