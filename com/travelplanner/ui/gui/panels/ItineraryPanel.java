package com.travelplanner.ui.gui.panels;
import com.travelplanner.model.Trip;
import com.travelplanner.service.ItineraryService;
import com.travelplanner.service.TripService;
import com.travelplanner.ui.gui.util.GuiUtil;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
public class ItineraryPanel extends JPanel {
    private final TripService tripService;
    private final ItineraryService itineraryService;
    private JComboBox<TripComboItem> tripCombo;
    private JPanel detailHolder;
    public ItineraryPanel(TripService tripService, ItineraryService itineraryService) {
        this.tripService = tripService;
        this.itineraryService = itineraryService;
        setOpaque(false);
        setLayout(new BorderLayout(0, 14));
        build();
    }
    private void build() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(GuiUtil.pageTitle("🗓 Itinerary Planner"), BorderLayout.WEST);
        tripCombo = new JComboBox<>();
        tripCombo.setToolTipText("Choose a trip to plan");
        tripCombo.addActionListener(e -> showSelectedTrip());
        JPanel selector = new JPanel();
        selector.setOpaque(false);
        selector.add(new JLabel("Trip"));
        selector.add(tripCombo);
        header.add(selector, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);
        detailHolder = new JPanel(new BorderLayout());
        detailHolder.setOpaque(false);
        add(detailHolder, BorderLayout.CENTER);
    }
    public void refresh() {
        tripCombo.removeAllItems();
        for (Trip trip : tripService.getAllTrips()) {
            tripCombo.addItem(new TripComboItem(trip.getId(), trip.getDestination()));
        }
        showSelectedTrip();
    }
    private void showSelectedTrip() {
        detailHolder.removeAll();
        TripComboItem selected = (TripComboItem) tripCombo.getSelectedItem();
        if (selected == null) {
            detailHolder.add(new JLabel("No trips available. Create a trip first."), BorderLayout.NORTH);
        } else {
            detailHolder.add(new ItineraryDetailPanel(selected.id(), itineraryService, this::refresh), BorderLayout.CENTER);
        }
        detailHolder.revalidate();
        detailHolder.repaint();
    }
    private record TripComboItem(int id, String name) {
        @Override
        public String toString() {
            return name + "  #" + id;
        }
    }
}