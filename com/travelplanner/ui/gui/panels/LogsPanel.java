package com.travelplanner.ui.gui.panels;
import com.travelplanner.model.Trip;
import com.travelplanner.service.TravelLogService;
import com.travelplanner.service.TripService;
import com.travelplanner.ui.gui.util.GuiUtil;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
public class LogsPanel extends JPanel {
    private final TripService tripService;
    private final TravelLogService travelLogService;
    private JComboBox<TripComboItem> tripCombo;
    private JPanel detailHolder;
    public LogsPanel(TripService tripService, TravelLogService travelLogService) {
        this.tripService = tripService;
        this.travelLogService = travelLogService;
        setOpaque(false);
        setLayout(new BorderLayout(0, 14));
        build();
    }
    private void build() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(GuiUtil.pageTitle("📔 Travel Diary / Logs"), BorderLayout.WEST);
        tripCombo = new JComboBox<>();
        tripCombo.setToolTipText("Choose a trip");
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
            detailHolder.add(new TravelLogPanel(selected.id(), travelLogService, this::refresh), BorderLayout.CENTER);
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