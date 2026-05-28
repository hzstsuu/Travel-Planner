package com.travelplanner.ui.gui.panels;
 
import com.travelplanner.model.ItineraryItem;
import com.travelplanner.service.ItineraryService;
import com.travelplanner.ui.gui.dialogs.ItineraryFormDialog;
import com.travelplanner.ui.gui.util.GuiUtil;
 
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Window;
import java.util.List;
 
public class ItineraryDetailPanel extends JPanel {
 
    private final int tripId;
    private final ItineraryService itineraryService;
    private final Runnable onChanged;
    private JPanel listPanel;
 
    public ItineraryDetailPanel(int tripId, ItineraryService itineraryService, Runnable onChanged) {
        this.tripId = tripId;
        this.itineraryService = itineraryService;
        this.onChanged = onChanged;
        setOpaque(false);
        setLayout(new BorderLayout(0, 8));   // FIX: reduced gap 12 → 8
        build();
        refresh();
    }
 
    private void build() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(GuiUtil.sectionTitle("Day-by-Day Plan"), BorderLayout.WEST);
        JButton addButton = GuiUtil.primaryButton("+ Add Activity");
        addButton.addActionListener(e -> openAddDialog());
        header.add(addButton, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);
 
        listPanel = new JPanel(new GridLayout(0, 1, 0, 6));  // FIX: reduced row gap 10 → 6
        listPanel.setOpaque(false);
        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setOpaque(false);
        add(scrollPane, BorderLayout.CENTER);
    }
 
    public void refresh() {
        listPanel.removeAll();
        List<ItineraryItem> items;
        try {
            items = itineraryService.getItineraryByTripId(tripId);
        } catch (IllegalArgumentException e) {
            listPanel.add(new javax.swing.JLabel(e.getMessage()));
            return;
        }
        if (items.isEmpty()) {
            listPanel.add(emptyCard("No itinerary yet. Add your first activity."));
        } else {
            for (ItineraryItem item : items) {
                listPanel.add(itemCard(item));
            }
        }
        revalidate();
        repaint();
    }
 
    private JPanel itemCard(ItineraryItem item) {
        // FIX: replaced GuiUtil.cardPanel() (FlowLayout) with BorderLayout for tight control
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new java.awt.Color(60, 66, 88), 1, true),
                BorderFactory.createEmptyBorder(7, 12, 7, 12)   // FIX: tight padding (was 12,14)
        ));
 
        // Left: day badge + activity on same row
        JPanel left = new JPanel(new BorderLayout(8, 0));
        left.setOpaque(false);
 
        javax.swing.JLabel dayBadge = new javax.swing.JLabel("Day " + item.getDayNumber());
        dayBadge.setFont(new Font("SansSerif", Font.BOLD, 13));  // FIX: reduced from 18 → 13
        dayBadge.setForeground(new java.awt.Color(82, 130, 255));
        dayBadge.setPreferredSize(new java.awt.Dimension(50, 20));
 
        javax.swing.JLabel activity = new javax.swing.JLabel(
                "<html>" + GuiUtil.escapeHtml(item.getActivity()) + "</html>");
        activity.setFont(new Font("SansSerif", Font.PLAIN, 12));
 
        left.add(dayBadge, BorderLayout.WEST);
        left.add(activity, BorderLayout.CENTER);
 
        // Right: action buttons — compact
        JPanel actions = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 4, 0));
        actions.setOpaque(false);
        JButton edit   = new JButton("Edit");
        edit.setFont(new Font("SansSerif", Font.PLAIN, 11));
        edit.setMargin(new java.awt.Insets(2, 8, 2, 8));
        JButton delete = GuiUtil.dangerButton("Delete");
        delete.setFont(new Font("SansSerif", Font.PLAIN, 11));
        delete.setMargin(new java.awt.Insets(2, 8, 2, 8));
        edit.addActionListener(e -> openEditDialog(item));
        delete.addActionListener(e -> deleteItem(item));
        actions.add(edit);
        actions.add(delete);
 
        card.add(left,    BorderLayout.CENTER);
        card.add(actions, BorderLayout.EAST);
        return card;
    }
 
    private JPanel emptyCard(String text) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(8, 4, 8, 4));
        panel.add(new javax.swing.JLabel(text));
        return panel;
    }
 
    private void openAddDialog() {
        ItineraryFormDialog dialog = new ItineraryFormDialog(
                ownerFrame(), tripId, itineraryService, null, this::changed);
        dialog.setVisible(true);
    }
 
    private void openEditDialog(ItineraryItem item) {
        ItineraryFormDialog dialog = new ItineraryFormDialog(
                ownerFrame(), tripId, itineraryService, item, this::changed);
        dialog.setVisible(true);
    }
 
    private void deleteItem(ItineraryItem item) {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete this itinerary item?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm == JOptionPane.YES_OPTION) {
            itineraryService.deleteItineraryItem(item.getId());
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