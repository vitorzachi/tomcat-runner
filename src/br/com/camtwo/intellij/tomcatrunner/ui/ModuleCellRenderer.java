package br.com.camtwo.intellij.tomcatrunner.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Cell renderer for providing tooltip.
 *
 * @author Vitor Zachi Junior
 * @since 23/03/16.
 */
public class ModuleCellRenderer extends DefaultTableCellRenderer {
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        JLabel c = null;
//        String path = String.valueOf(value);
//        if (path.startsWith(Module.INTERNAL)) {
//            String[] split = path.split("/");
//
//            c = (JLabel) super.getTableCellRendererComponent(table, "Internal module " + split[split.length - 1],
//                    isSelected, hasFocus, row, column);
//        } else {
        c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
//        }

        c.setToolTipText(c.getText());
        return c;
    }

}
