package com.travelplanner.ui.gui.panels;
import com.travelplanner.ui.gui.util.GuiUtil;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.function.Consumer;
public class SettingsPanel extends JPanel {
    private final Consumer<Boolean> onThemeChanged;
    public SettingsPanel(Consumer<Boolean> onThemeChanged) {
        this.onThemeChanged = onThemeChanged;
        setOpaque(false);
        setLayout(new BorderLayout(0, 14));
        build();
    }
    private void build() {
        add(GuiUtil.pageTitle("⚙ Settings"), BorderLayout.NORTH);
        JPanel card = GuiUtil.cardPanel();
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = GuiUtil.gbc();
        gbc.gridx = 0;
        gbc.gridy = 0;
        card.add(new JLabel("Theme"), gbc);
        gbc.gridx = 1;
        JToggleButton themeToggle = new JToggleButton("Dark Mode", true);
        themeToggle.setToolTipText("Toggle dark/light theme");
        themeToggle.addActionListener(e -> {
            boolean dark = themeToggle.isSelected();
            themeToggle.setText(dark ? "Dark Mode" : "Light Mode");
            onThemeChanged.accept(dark);
        });
        card.add(themeToggle, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        card.add(new JLabel("Keyboard Shortcuts"), gbc);
        gbc.gridx = 1;
        card.add(new JLabel("Ctrl+1 Dashboard, Ctrl+2 Trips, Ctrl+3 Itinerary, Ctrl+4 Expenses, Ctrl+5 Diary"), gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        card.add(new JLabel("Export"), gbc);
        gbc.gridx = 1;
        JButton exportInfo = new JButton("Coming Soon");
        exportInfo.setEnabled(false);
        card.add(exportInfo, gbc);
        add(card, BorderLayout.NORTH);
    }
}