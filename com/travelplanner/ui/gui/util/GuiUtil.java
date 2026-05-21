package com.travelplanner.ui.gui.util;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
public final class GuiUtil {
    private GuiUtil() {
    }
    public static JPanel contentPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return panel;
    }
    public static JPanel cardPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(true);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(75, 78, 90), 1, true),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));
        return panel;
    }
    public static JLabel pageTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 28));
        return label;
    }
    public static JLabel sectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 20));
        return label;
    }
    public static JButton primaryButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        button.setToolTipText(text);
        return button;
    }
    public static JButton dangerButton(String text) {
        JButton button = primaryButton(text);
        button.setBackground(new Color(192, 57, 43));
        button.setForeground(Color.WHITE);
        return button;
    }
    public static JButton sidebarButton(String text) {
        JButton button = new JButton(text);
        button.setHorizontalAlignment(JButton.LEFT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBackground(new Color(42, 45, 56));
        button.setForeground(Color.WHITE);
        return button;
    }
    public static GridBagConstraints gbc() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        return gbc;
    }
    public static void addDetailRow(JPanel panel, GridBagConstraints gbc, int row, String label, String value) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        JLabel key = new JLabel(label + ":");
        key.setFont(new Font("SansSerif", Font.BOLD, 14));
        panel.add(key, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        JLabel val = new JLabel("<html>" + escapeHtml(value).replace("\n", "<br>") + "</html>");
        val.setFont(new Font("SansSerif", Font.PLAIN, 14));
        panel.add(val, gbc);
    }
    public static Action action(Runnable runnable) {
        return new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                runnable.run();
            }
        };
    }
    public static void updateComponentTree(Component component) {
        SwingUtilities.updateComponentTreeUI(component);
        component.invalidate();
        component.validate();
        component.repaint();
    }
    public static void setFixedHeight(JComponent component, int height) {
        component.setMinimumSize(new java.awt.Dimension(component.getMinimumSize().width, height));
        component.setPreferredSize(new java.awt.Dimension(component.getPreferredSize().width, height));
        component.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, height));
    }
    public static void refreshDeep(Container container) {
        container.revalidate();
        container.repaint();
    }
    public static String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}