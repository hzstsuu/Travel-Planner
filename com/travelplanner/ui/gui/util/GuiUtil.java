package com.travelplanner.ui.gui.util;

import javax.swing.*;
import java.awt.*;

/**
 * Central factory for themed Swing components.
 * Produces compact, FlatLaf-friendly widgets.
 */
public final class GuiUtil {

    // ── Brand palette ─────────────────────────────────────────────────────
    public static final Color ACCENT  = new Color(82,  130, 255);
    public static final Color DANGER  = new Color(220, 75,  75);
    public static final Color SUCCESS = new Color(46,  204, 113);
    public static final Color WARNING = new Color(230, 160, 30);

    // ── Sidebar palette ───────────────────────────────────────────────────
    public static final Color SIDEBAR_BG     = new Color(24, 26,  36);
    public static final Color SIDEBAR_ACTIVE = new Color(82, 130, 255);
    public static final Color SIDEBAR_HOVER  = new Color(38, 42,  58);

    private GuiUtil() {}

    // ── Page / section titles ─────────────────────────────────────────────

    public static JLabel pageTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 22));
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        return label;
    }

    public static JLabel sectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 15));
        return label;
    }

    public static JLabel subLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));
        label.setForeground(new Color(150, 160, 175));
        return label;
    }

    // ── Buttons ───────────────────────────────────────────────────────────

    public static JButton primaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(ACCENT);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.putClientProperty("JButton.buttonType", "roundRect");
        return btn;
    }

    public static JButton dangerButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(DANGER);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.putClientProperty("JButton.buttonType", "roundRect");
        return btn;
    }

    public static JButton sidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(SIDEBAR_BG);
        btn.setForeground(new Color(200, 205, 215));
        btn.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.putClientProperty("JButton.buttonType", "borderless");
        return btn;
    }

    public static JButton ghostButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btn.setFocusPainted(false);
        btn.putClientProperty("JButton.buttonType", "roundRect");
        return btn;
    }

    // ── Cards / Panels ────────────────────────────────────────────────────

    /** Standard card with a subtle border. */
    public static JPanel cardPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 66, 88), 1, true),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));
        return panel;
    }

    /** Full-width content area with padding. */
    public static JPanel contentPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        return panel;
    }

    /**
     * Compact stat card: small title on top, large value below.
     *
     * @param accent optional colour for the value label; pass null for default.
     */
    public static JPanel statCard(String title, String value, Color accent) {
        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 66, 88), 1, true),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));

        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lbl.setForeground(new Color(150, 160, 175));

        JLabel val = new JLabel(value);
        val.setFont(new Font("SansSerif", Font.BOLD, 26));
        if (accent != null) val.setForeground(accent);

        card.add(lbl, BorderLayout.NORTH);
        card.add(val, BorderLayout.CENTER);
        return card;
    }

    // ── Form helpers ──────────────────────────────────────────────────────

    public static GridBagConstraints gbc() {
        GridBagConstraints g = new GridBagConstraints();
        g.insets    = new Insets(5, 6, 5, 6);
        g.fill      = GridBagConstraints.HORIZONTAL;
        g.anchor    = GridBagConstraints.WEST;
        g.weightx   = 1;
        g.gridwidth = 1;
        return g;
    }

    /** Add a label + value detail row to a GridBagLayout panel. */
    public static void addDetailRow(JPanel panel, GridBagConstraints gbc,
                                    int row, String labelText, String valueText) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        JLabel lbl = new JLabel(labelText + ":");
        lbl.setFont(new Font("SansSerif", Font.BOLD, 13));
        lbl.setForeground(new Color(150, 160, 175));
        panel.add(lbl, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        JLabel val = new JLabel("<html>" + escapeHtml(valueText) + "</html>");
        val.setFont(new Font("SansSerif", Font.PLAIN, 13));
        panel.add(val, gbc);
    }

    // ── Keyboard shortcut helper ──────────────────────────────────────────

    public static Action action(Runnable r) {
        return new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) { r.run(); }
        };
    }

    // ── Layout helpers ────────────────────────────────────────────────────

    public static String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&",  "&amp;")
                .replace("<",  "&lt;")
                .replace(">",  "&gt;")
                .replace("\"", "&quot;");
    }

    /** Vertical spacer. */
    public static Component vgap(int h) { return Box.createVerticalStrut(h); }

    /** Horizontal spacer. */
    public static Component hgap(int w) { return Box.createHorizontalStrut(w); }

    /** Wrap a component with empty border padding. */
    public static JPanel padded(JComponent c, int top, int left, int bottom, int right) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
        p.add(c);
        return p;
    }

    /** Horizontal separator line. */
    public static JSeparator separator() {
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }
}