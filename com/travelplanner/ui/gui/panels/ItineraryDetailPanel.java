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
        setLayout(new BorderLayout(0, 12));
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
        listPanel = new JPanel(new GridLayout(0, 1, 0, 10));
        listPanel.setOpaque(false);
        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
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
        JPanel card = GuiUtil.cardPanel();
        card.setLayout(new BorderLayout(12, 8));
        javax.swing.JLabel title = new javax.swing.JLabel("Day " + item.getDayNumber());
        title.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 18));
        javax.swing.JLabel activity = new javax.swing.JLabel("<html>" + GuiUtil.escapeHtml(item.getActivity()) + "</html>");
        JPanel actions = new JPanel();
        actions.setOpaque(false);
        JButton edit = new JButton("Edit");
        JButton delete = GuiUtil.dangerButton("Delete");
        edit.addActionListener(e -> openEditDialog(item));
        delete.addActionListener(e -> deleteItem(item));
        actions.add(edit);
        actions.add(delete);
        card.add(title, BorderLayout.NORTH);
        card.add(activity, BorderLayout.CENTER);
        card.add(actions, BorderLayout.EAST);
        return card;
    }
    private JPanel emptyCard(String text) {
        JPanel panel = GuiUtil.cardPanel();
        panel.add(new javax.swing.JLabel(text));
        return panel;
    }
    private void openAddDialog() {
        ItineraryFormDialog dialog = new ItineraryFormDialog(ownerFrame(), tripId, itineraryService, null, this::changed);
        dialog.setVisible(true);
    }
    private void openEditDialog(ItineraryItem item) {
        ItineraryFormDialog dialog = new ItineraryFormDialog(ownerFrame(), tripId, itineraryService, item, this::changed);
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