package com.travelplanner.ui.gui;
import com.travelplanner.model.Trip;
import com.travelplanner.service.ExpenseService;
import com.travelplanner.service.ItineraryService;
import com.travelplanner.service.TravelLogService;
import com.travelplanner.service.TripService;
import com.travelplanner.ui.gui.panels.ExpensePanel;
import com.travelplanner.ui.gui.panels.ItineraryDetailPanel;
import com.travelplanner.ui.gui.panels.TravelLogPanel;
import com.travelplanner.ui.gui.util.GuiUtil;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Optional;
public class TripDetailDialog extends JDialog {
    private final int tripId;
    private final TripService tripService;
    private final Runnable onChange;
    private JPanel overviewPanel;
    private ItineraryDetailPanel itineraryDetailPanel;
    private ExpensePanel expensePanel;
    private TravelLogPanel travelLogPanel;
    public TripDetailDialog(
            Frame owner,
            int tripId,
            TripService tripService,
            ItineraryService itineraryService,
            ExpenseService expenseService,
            TravelLogService travelLogService,
            Runnable onChange
    ) {
        super(owner, "Trip Details", true);
        this.tripId = tripId;
        this.tripService = tripService;
        this.onChange = onChange;
        setSize(1050, 720);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        Optional<Trip> optionalTrip = tripService.getTripById(tripId);
        if (optionalTrip.isEmpty()) {
            JOptionPane.showMessageDialog(owner, "Trip not found.", "Not Found", JOptionPane.WARNING_MESSAGE);
            dispose();
            return;
        }
        Trip trip = optionalTrip.get();
        JLabel title = GuiUtil.pageTitle("🧭 " + trip.getDestination());
        title.setBorder(BorderFactory.createEmptyBorder(18, 22, 8, 22));
        add(title, BorderLayout.NORTH);
        JTabbedPane tabs = new JTabbedPane();
        overviewPanel = createOverviewPanel();
        itineraryDetailPanel = new ItineraryDetailPanel(tripId, itineraryService, this::refreshAll);
        expensePanel = new ExpensePanel(tripId, expenseService, this::refreshAll);
        travelLogPanel = new TravelLogPanel(tripId, travelLogService, this::refreshAll);
        tabs.addTab("Overview", overviewPanel);
        tabs.addTab("Itinerary", itineraryDetailPanel);
        tabs.addTab("Expenses", expensePanel);
        tabs.addTab("Diary", travelLogPanel);
        add(tabs, BorderLayout.CENTER);
    }
    private JPanel createOverviewPanel() {
        JPanel wrapper = GuiUtil.contentPanel();
        wrapper.setLayout(new BorderLayout());
        JPanel card = GuiUtil.cardPanel();
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        Trip trip = tripService.getTripById(tripId).orElseThrow();
        GridBagConstraints gbc = GuiUtil.gbc();
        GuiUtil.addDetailRow(card, gbc, 0, "Destination", trip.getDestination());
        GuiUtil.addDetailRow(card, gbc, 1, "Dates", trip.getStartDate() + " → " + trip.getEndDate());
        GuiUtil.addDetailRow(card, gbc, 2, "Status", trip.getStatus().toString());
        GuiUtil.addDetailRow(card, gbc, 3, "Description", trip.getDescription().isBlank() ? "No description." : trip.getDescription());
        GuiUtil.addDetailRow(card, gbc, 4, "Notes", trip.getNotes().isBlank() ? "No notes yet." : trip.getNotes());
        wrapper.add(card, BorderLayout.NORTH);
        return wrapper;
    }
    private void refreshAll() {
        remove(overviewPanel);
        itineraryDetailPanel.refresh();
        expensePanel.refresh();
        travelLogPanel.refresh();
        onChange.run();
        revalidate();
        repaint();
    }
}