package com.travelplanner.ui.gui.components;
import com.travelplanner.model.ExpenseCategory;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.EnumMap;
import java.util.Map;
public class BudgetBarChartPanel extends JPanel {
    private Map<ExpenseCategory, Double> data = new EnumMap<>(ExpenseCategory.class);
    public BudgetBarChartPanel() {
        for (ExpenseCategory category : ExpenseCategory.values()) {
            data.put(category, 0.0);
        }
    }
    public void setData(Map<ExpenseCategory, Double> data) {
        this.data = data;
        repaint();
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (data == null || data.isEmpty()) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int width = getWidth();
        int y = 24;
        int labelWidth = 145;
        int barMaxWidth = Math.max(100, width - labelWidth - 90);
        int barHeight = 18;
        int gap = 18;
        double max = data.values().stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
        if (max <= 0) {
            g2.setColor(new Color(145, 155, 170));
            g2.setFont(new Font("SansSerif", Font.PLAIN, 14));
            g2.drawString("No expenses yet.", 20, 35);
            g2.dispose();
            return;
        }
        g2.setFont(new Font("SansSerif", Font.PLAIN, 13));
        for (ExpenseCategory category : ExpenseCategory.values()) {
            double amount = data.getOrDefault(category, 0.0);
            int barWidth = (int) ((amount / max) * barMaxWidth);
            g2.setColor(new Color(180, 190, 205));
            g2.drawString(label(category), 18, y + 14);
            g2.setColor(colorFor(category));
            g2.fillRoundRect(labelWidth, y, barWidth, barHeight, 12, 12);
            g2.setColor(new Color(180, 190, 205));
            g2.drawString(String.format("%.2f", amount), labelWidth + barWidth + 12, y + 14);
            y += barHeight + gap;
        }
        g2.dispose();
    }
    private String label(ExpenseCategory category) {
        return category.toString().replace("_", " ");
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