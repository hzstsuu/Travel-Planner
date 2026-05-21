package com.travelplanner.ui.gui;
import com.travelplanner.ui.gui.util.GuiUtil;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
public class SidebarPanel extends JPanel {
    private final Consumer<String> navigator;
    private final Map<String, JButton> buttons = new LinkedHashMap<>();
    private final Color activeColor = new Color(74, 144, 226);
    private final Color inactiveColor = new Color(42, 45, 56);
    public SidebarPanel(Consumer<String> navigator) {
        this.navigator = navigator;
        build();
    }
    private void build() {
        setPreferredSize(new Dimension(250, 0));
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(22, 16, 16, 16));
        setBackground(new Color(31, 34, 43));
        JPanel brandPanel = new JPanel(new BorderLayout());
        brandPanel.setOpaque(false);
        JLabel icon = new JLabel("✈");
        icon.setFont(new Font("SansSerif", Font.BOLD, 34));
        icon.setForeground(new Color(93, 173, 226));
        JLabel title = new JLabel("<html><b>Travel</b><br><span style='color:#AEB6BF'>Planner</span></html>");
        title.setFont(new Font("SansSerif", Font.PLAIN, 22));
        title.setForeground(Color.WHITE);
        brandPanel.add(icon, BorderLayout.WEST);
        brandPanel.add(title, BorderLayout.CENTER);
        add(brandPanel, BorderLayout.NORTH);
        JPanel navPanel = new JPanel(new GridLayout(0, 1, 0, 10));
        navPanel.setOpaque(false);
        navPanel.setBorder(BorderFactory.createEmptyBorder(34, 0, 0, 0));
        addButton(navPanel, TravelPlannerApp.CARD_DASHBOARD, "🏠  Dashboard", "Ctrl+1");
        addButton(navPanel, TravelPlannerApp.CARD_TRIPS, "🧳  My Trips", "Ctrl+2");
        addButton(navPanel, TravelPlannerApp.CARD_ITINERARY, "🗓  Itinerary Planner", "Ctrl+3");
        addButton(navPanel, TravelPlannerApp.CARD_EXPENSES, "💳  Expenses & Budget", "Ctrl+4");
        addButton(navPanel, TravelPlannerApp.CARD_LOGS, "📔  Travel Diary", "Ctrl+5");
        addButton(navPanel, TravelPlannerApp.CARD_SETTINGS, "⚙  Settings", null);
        add(navPanel, BorderLayout.CENTER);
        JLabel footer = new JLabel("<html><span style='color:#9AA4B2'>Pogi Edition</span></html>");
        footer.setBorder(BorderFactory.createEmptyBorder(10, 4, 0, 4));
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.add(Box.createVerticalStrut(10), BorderLayout.NORTH);
        bottom.add(footer, BorderLayout.SOUTH);
        add(bottom, BorderLayout.SOUTH);
    }
    private void addButton(JPanel parent, String cardName, String text, String shortcut) {
        JButton button = GuiUtil.sidebarButton(text);
        if (shortcut != null) {
            button.setToolTipText(shortcut);
        }
        button.addActionListener(e -> navigator.accept(cardName));
        buttons.put(cardName, button);
        parent.add(button);
    }
    public void setActive(String cardName) {
        for (Map.Entry<String, JButton> entry : buttons.entrySet()) {
            JButton button = entry.getValue();
            boolean active = entry.getKey().equals(cardName);
            button.setBackground(active ? activeColor : inactiveColor);
            button.setForeground(Color.WHITE);
            button.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        }
    }
}