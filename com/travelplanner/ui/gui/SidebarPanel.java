package com.travelplanner.ui.gui;

import com.travelplanner.service.SessionManager;
import com.travelplanner.ui.gui.util.GuiUtil;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

import static com.travelplanner.ui.gui.TravelPlannerApp.*;

public class SidebarPanel extends JPanel {

    private static final Color BG     = new Color(22, 24, 36);
    private static final Color ACTIVE = new Color(82, 130, 255);
    private static final Color HOVER  = new Color(35, 38, 56);

    private final Consumer<String> navigator;
    private final Runnable         onLogout;
    private final Runnable         onSwitchAccount;   // NEW
    private final Map<String, JButton> buttons = new LinkedHashMap<>();

    public SidebarPanel(Consumer<String> navigator,
                        Runnable onLogout,
                        Runnable onSwitchAccount) {
        this.navigator       = navigator;
        this.onLogout        = onLogout;
        this.onSwitchAccount = onSwitchAccount;
        setBackground(BG);
        setPreferredSize(new Dimension(210, Integer.MAX_VALUE));
        setLayout(new BorderLayout());
        build();
    }

    private void build() {
        // ── Brand ─────────────────────────────────────────────────────────
        JPanel brand = new JPanel(new BorderLayout(10, 0));
        brand.setBackground(BG);
        brand.setBorder(BorderFactory.createEmptyBorder(22, 18, 22, 18));

        JLabel icon     = new JLabel("✈");
        icon.setFont(new Font("SansSerif", Font.BOLD, 26));
        icon.setForeground(ACTIVE);

        JLabel titleLbl = new JLabel(
            "<html><b style='color:white'>Travel</b><br>"
            + "<span style='color:#9AA4B2;font-size:10px'>Planner</span></html>");
        titleLbl.setFont(new Font("SansSerif", Font.PLAIN, 18));

        brand.add(icon,     BorderLayout.WEST);
        brand.add(titleLbl, BorderLayout.CENTER);
        add(brand, BorderLayout.NORTH);

        // ── Nav buttons (NO tooltips = no Ctrl hint on hover) ─────────────
        JPanel nav = new JPanel(new GridLayout(0, 1, 0, 2));
        nav.setBackground(BG);
        nav.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        addNav(nav, CARD_DASHBOARD, "🏠  Dashboard");
        addNav(nav, CARD_TRIPS,     "🧳  My Trips");
        addNav(nav, CARD_ITINERARY, "🗓  Itinerary");
        addNav(nav, CARD_EXPENSES,  "💳  Expenses");
        addNav(nav, CARD_LOGS,      "📔  Travel Diary");
        addNav(nav, CARD_SETTINGS,  "⚙   Settings");

        add(nav, BorderLayout.CENTER);

        // ── Bottom: user badge + switch account + log out ──────────────────
        JPanel bottom = new JPanel();
        bottom.setBackground(BG);
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.setBorder(BorderFactory.createEmptyBorder(8, 8, 18, 8));

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        bottom.add(sep);
        bottom.add(Box.createVerticalStrut(10));

        // User badge
        if (SessionManager.isLoggedIn()) {
            JLabel userLbl = new JLabel("👤  " + SessionManager.current().getUsername());
            userLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
            userLbl.setForeground(new Color(140, 150, 170));
            userLbl.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 4));
            bottom.add(userLbl);
            bottom.add(Box.createVerticalStrut(4));
        }

        // Switch Account button  ── NEW
        JButton switchBtn = GuiUtil.sidebarButton("🔄  Switch Account");
        switchBtn.setForeground(new Color(130, 180, 255));
        switchBtn.addActionListener(e -> onSwitchAccount.run());
        bottom.add(switchBtn);
        bottom.add(Box.createVerticalStrut(2));

        // Log Out button
        JButton logoutBtn = GuiUtil.sidebarButton("🚪  Log Out");
        logoutBtn.setForeground(new Color(220, 120, 120));
        logoutBtn.addActionListener(e -> onLogout.run());
        bottom.add(logoutBtn);

        add(bottom, BorderLayout.SOUTH);
    }

    // No tooltip parameter — removes the Ctrl+N hint that appeared on hover
    private void addNav(JPanel parent, String card, String text) {
        JButton btn = GuiUtil.sidebarButton(text);
        btn.setBackground(BG);
        btn.setToolTipText(null);   // explicitly clear any tooltip
        btn.addActionListener(e -> navigator.accept(card));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                if (!btn.getBackground().equals(ACTIVE)) btn.setBackground(HOVER);
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                if (!btn.getBackground().equals(ACTIVE)) btn.setBackground(BG);
            }
        });
        buttons.put(card, btn);
        parent.add(btn);
    }

    public void setActive(String card) {
        buttons.forEach((k, btn) -> {
            boolean active = k.equals(card);
            btn.setBackground(active ? ACTIVE : BG);
            btn.setForeground(active ? Color.WHITE : new Color(190, 198, 215));
        });
    }
}