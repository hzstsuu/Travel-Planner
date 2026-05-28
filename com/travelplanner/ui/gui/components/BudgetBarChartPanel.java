package com.travelplanner.ui.gui.components;
 
import com.travelplanner.model.ExpenseCategory;
 
import javax.swing.*;
import java.awt.*;
import java.util.EnumMap;
import java.util.Map;
 
/**
 * Horizontal bar chart showing expenses per category.
 * Auto-scales bars to fit available width. Theme-aware foreground.
 */
public class BudgetBarChartPanel extends JPanel {
 
    private Map<ExpenseCategory, Double> data = new EnumMap<>(ExpenseCategory.class);
 
    public BudgetBarChartPanel() {
        setPreferredSize(new Dimension(200, 200));   // FIX: increased height 180 → 200 (more breathing room)
        for (ExpenseCategory c : ExpenseCategory.values()) data.put(c, 0.0);
    }
 
    public void setData(Map<ExpenseCategory, Double> data) {
        this.data = data;
        repaint();
    }
 
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (data == null || data.isEmpty()) return;
 
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
 
        double max = data.values().stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
        if (max <= 0) {
            g2.setColor(UIManager.getColor("Label.disabledForeground"));
            g2.setFont(new Font("SansSerif", Font.PLAIN, 13));
            g2.drawString("No expenses yet.", 10, 28);
            g2.dispose();
            return;
        }
 
        int w       = getWidth();
        int labelW  = 68;                               // FIX: shorter label column (was 130)
        int amtW    = 80;                               // FIX: wider amount column (was 72)
        int barMaxW = Math.max(40, w - labelW - amtW - 16);
        int barH    = 13;
        int rowH    = 30;                               // FIX: slightly more row spacing (was 28)
        int y       = 18;
 
        g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
 
        for (ExpenseCategory cat : ExpenseCategory.values()) {
            double amt = data.getOrDefault(cat, 0.0);
            int bw     = (int) ((amt / max) * barMaxW);
 
            // Background track
            g2.setColor(new Color(55, 62, 88, 80));
            g2.fillRoundRect(labelW, y, barMaxW, barH, 8, 8);
 
            // Filled bar
            if (bw > 0) {
                g2.setColor(colorFor(cat));
                g2.fillRoundRect(labelW, y, bw, barH, 8, 8);
            }
 
            // Category label (left column)
            g2.setColor(UIManager.getColor("Label.foreground"));
            g2.drawString(label(cat), 2, y + 11);
 
            // FIX: amount text — use a much brighter, high-contrast colour so it is always readable
            // Previously used new Color(160,170,190) which was too dim against the light-themed bg
            String amtStr = String.format("₱ %.0f", amt);
            FontMetrics fm = g2.getFontMetrics();
            int sw = fm.stringWidth(amtStr);
 
            // Draw a subtle dark shadow first for contrast on any background
            g2.setColor(new Color(0, 0, 0, 60));
            g2.drawString(amtStr, w - sw - 3, y + 12);
 
            // FIX: bright white/light text on dark bg; falls back to dark on light bg via UIManager
            Color fg = UIManager.getColor("Label.foreground");
            if (fg == null) fg = Color.DARK_GRAY;
            // Always render in the theme's label colour so it's legible in both dark and light mode
            g2.setColor(fg);
            g2.drawString(amtStr, w - sw - 4, y + 11);
 
            y += rowH;
        }
        g2.dispose();
    }
 
    private String label(ExpenseCategory c) {
        return switch (c) {
            case TRANSPORTATION -> "Transport";
            case FOOD           -> "Food";
            case ACCOMMODATION  -> "Lodging";
            case ENTRANCE_FEES  -> "Entrance";
            case MISCELLANEOUS  -> "Other";
        };
    }
 
    private Color colorFor(ExpenseCategory c) {
        return switch (c) {
            case TRANSPORTATION -> new Color(82, 130, 255);
            case FOOD           -> new Color(230, 126, 34);
            case ACCOMMODATION  -> new Color(155, 89, 182);
            case ENTRANCE_FEES  -> new Color(46, 204, 113);
            case MISCELLANEOUS  -> new Color(127, 140, 141);
        };
    }
}