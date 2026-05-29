package com.travelplanner.ui.gui.panels;

import com.travelplanner.model.Trip;
import com.travelplanner.model.TripStatus;
import com.travelplanner.service.*;
import com.travelplanner.ui.gui.components.TripCardPanel;
import com.travelplanner.ui.gui.dialogs.TripFormDialog;
import com.travelplanner.ui.gui.util.GuiUtil;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.IntConsumer;

public class TripsPanel extends JPanel {

    private final TripService      tripService;
    private final ItineraryService itineraryService;
    private final ExpenseService   expenseService;
    private final TravelLogService travelLogService;
    private final IntConsumer      onOpen;

    private JPanel cardsPanel;

    public TripsPanel(TripService tripService,
                      ItineraryService itineraryService,
                      ExpenseService expenseService,
                      TravelLogService travelLogService,
                      IntConsumer onOpen) {
        this.tripService      = tripService;
        this.itineraryService = itineraryService;
        this.expenseService   = expenseService;
        this.travelLogService = travelLogService;
        this.onOpen           = onOpen;
        setOpaque(false);
        setLayout(new BorderLayout(0, 12));
        build();
    }

    private void build() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(GuiUtil.pageTitle("🧳  My Trips"), BorderLayout.WEST);
        JButton addBtn = GuiUtil.primaryButton("+ New Trip");
        addBtn.addActionListener(e -> openAddDialog());
        header.add(addBtn, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        cardsPanel = new JPanel(new GridLayout(0, 2, 12, 12));
        cardsPanel.setOpaque(false);
        JScrollPane scroll = new JScrollPane(cardsPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        add(scroll, BorderLayout.CENTER);
    }

    public void refresh() {
        cardsPanel.removeAll();
        List<Trip> trips = tripService.getAllTrips(SessionManager.currentUserId());
        if (trips.isEmpty()) {
            JLabel empty = new JLabel("No trips yet. Click \"+ New Trip\" to get started!");
            empty.setForeground(new Color(140, 150, 170));
            cardsPanel.add(empty);
        } else {
            for (Trip t : trips) {
                TripCardPanel card = new TripCardPanel(t, onOpen);

                // Right-click context menu
                JPopupMenu menu = new JPopupMenu();

                // ── Toggle status item (changes label based on current status) ──
                if (t.getStatus() == TripStatus.COMPLETED) {
                    JMenuItem revertItem = new JMenuItem("↩ Mark as Planned");
                    revertItem.addActionListener(e -> {
                        int ok = JOptionPane.showConfirmDialog(
                                this,
                                "Revert \"" + t.getDestination() + "\" back to Planned?",
                                "Confirm",
                                JOptionPane.YES_NO_OPTION
                        );
                        if (ok == JOptionPane.YES_OPTION) {
                            tripService.markTripPlanned(t.getId());
                            refresh();
                        }
                    });
                    menu.add(revertItem);
                } else {
                    JMenuItem completeItem = new JMenuItem("✅ Mark as Completed");
                    completeItem.addActionListener(e -> {
                        tripService.markTripCompleted(t.getId());
                        refresh();
                    });
                    menu.add(completeItem);
                }

                // ── Delete ────────────────────────────────────────────────────
                JMenuItem delete = new JMenuItem("🗑 Delete Trip");
                delete.setForeground(GuiUtil.DANGER);
                delete.addActionListener(e -> {
                    int ok = JOptionPane.showConfirmDialog(this,
                            "Delete trip to " + t.getDestination() + "?", "Confirm",
                            JOptionPane.YES_NO_OPTION);
                    if (ok == JOptionPane.YES_OPTION) {
                        tripService.deleteTrip(t.getId());
                        itineraryService.deleteByTripId(t.getId());
                        expenseService.deleteByTripId(t.getId());
                        travelLogService.deleteByTripId(t.getId());
                        refresh();
                    }
                });

                menu.add(delete);
                card.setComponentPopupMenu(menu);
                cardsPanel.add(card);
            }
        }
        revalidate();
        repaint();
    }

    private void openAddDialog() {
        TripFormDialog dlg = new TripFormDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                tripService, null, this::refresh
        );
        dlg.setVisible(true);
    }
}