package com.travelplanner.ui.gui.components;
import com.travelplanner.model.ExpenseCategory;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
public class CategoryBadgeRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(
            JTable table,
            Object value,
            boolean isSelected,
            boolean hasFocus,
            int row,
            int column
    ) {
        JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        label.setHorizontalAlignment(CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        label.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        if (!isSelected && value instanceof ExpenseCategory category) {
            label.setOpaque(true);
            label.setForeground(Color.WHITE);
            label.setBackground(colorFor(category));
        }
        return label;
    }
    private Color colorFor(ExpenseCategory category) {
        return switch (category) {
            case TRANSPORTATION -> new Color(52, 152, 219);
            case FOOD -> new Color(230, 126, 34);
            case ACCOMMODATION -> new Color(155, 89, 182);
            case ENTRANCE_FEES -> new Color(46, 204, 113);
            case MISCELLANEOUS -> new Color(127, 140, 141);
        };
    }
}