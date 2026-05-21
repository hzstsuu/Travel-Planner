package com.travelplanner.ui.gui.panels;
import com.travelplanner.model.TravelLog;
import com.travelplanner.service.TravelLogService;
import com.travelplanner.ui.gui.components.ImagePreviewPanel;
import com.travelplanner.ui.gui.dialogs.TravelLogFormDialog;
import com.travelplanner.ui.gui.util.GuiUtil;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.util.Comparator;
import java.util.List;
public class TravelLogPanel extends JPanel {
    private final int tripId;
    private final TravelLogService travelLogService;
    private final Runnable onChanged;
    private JPanel listPanel;
    public TravelLogPanel(int tripId, TravelLogService travelLogService, Runnable onChanged) {
        this.tripId = tripId;
        this.travelLogService = travelLogService;
        this.onChanged = onChanged;
        setOpaque(false);
        setLayout(new BorderLayout(0, 12));
        build();
        refresh();
    }
    private void build() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(GuiUtil.sectionTitle("Diary Entries"), BorderLayout.WEST);
        JButton addButton = GuiUtil.primaryButton("+ Add Log");
        addButton.addActionListener(e -> openAddDialog());
        header.add(addButton, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);
        listPanel = new JPanel(new GridLayout(0, 1, 0, 12));
        listPanel.setOpaque(false);
        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);
    }
    public void refresh() {
        listPanel.removeAll();
        List<TravelLog> logs;
        try {
            logs = travelLogService.getLogsByTripId(tripId)
                    .stream()
                    .sorted(Comparator.comparing(TravelLog::getLogDate).reversed())
                    .toList();
        } catch (IllegalArgumentException e) {
            listPanel.add(new javax.swing.JLabel(e.getMessage()));
            return;
        }
        if (logs.isEmpty()) {
            listPanel.add(emptyCard("No diary entries yet."));
        } else {
            for (TravelLog log : logs) {
                listPanel.add(logCard(log));
            }
        }
        revalidate();
        repaint();
    }
    private JPanel logCard(TravelLog log) {
        JPanel card = GuiUtil.cardPanel();
        card.setLayout(new BorderLayout(16, 8));
        javax.swing.JLabel date = new javax.swing.JLabel("📅 " + log.getLogDate());
        date.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 18));
        javax.swing.JLabel text = new javax.swing.JLabel("<html>" + GuiUtil.escapeHtml(log.getReflection()) + "</html>");
        ImagePreviewPanel preview = new ImagePreviewPanel();
        preview.setImagePath(log.getImagePath());
        JPanel center = new JPanel(new BorderLayout(8, 8));
        center.setOpaque(false);
        center.add(date, BorderLayout.NORTH);
        center.add(text, BorderLayout.CENTER);
        JPanel actions = new JPanel();
        actions.setOpaque(false);
        JButton edit = new JButton("Edit");
        JButton delete = GuiUtil.dangerButton("Delete");
        edit.addActionListener(e -> openEditDialog(log));
        delete.addActionListener(e -> deleteLog(log));
        actions.add(edit);
        actions.add(delete);
        card.add(preview, BorderLayout.WEST);
        card.add(center, BorderLayout.CENTER);
        card.add(actions, BorderLayout.EAST);
        return card;
    }
    private JPanel emptyCard(String text) {
        JPanel panel = GuiUtil.cardPanel();
        panel.add(new javax.swing.JLabel(text));
        return panel;
    }
    private void openAddDialog() {
        TravelLogFormDialog dialog = new TravelLogFormDialog(ownerFrame(), tripId, travelLogService, null, this::changed);
        dialog.setVisible(true);
    }
    private void openEditDialog(TravelLog log) {
        TravelLogFormDialog dialog = new TravelLogFormDialog(ownerFrame(), tripId, travelLogService, log, this::changed);
        dialog.setVisible(true);
    }
    private void deleteLog(TravelLog log) {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete this travel log?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm == JOptionPane.YES_OPTION) {
            travelLogService.deleteTravelLog(log.getId());
            changed();
        }
    }
    private void changed() {
        refresh();
        onChanged.run();
    }
    private JFrame ownerFrame() {
        Window window = javax.swing.SwingUtilities.getWindowAncestor(this);
        return window instanceof JFrame frame ? frame : null;
    }
}