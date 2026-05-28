package com.travelplanner.ui.gui.panels;

import com.travelplanner.model.Trip;
import com.travelplanner.service.SessionManager;
import com.travelplanner.service.TravelLogService;
import com.travelplanner.service.TripService;
import com.travelplanner.ui.gui.util.GuiUtil;

import javax.swing.*;
import java.awt.*;

public class LogsPanel extends JPanel {

    private final TripService      tripService;
    private final TravelLogService travelLogService;

    private JComboBox<TripComboItem> tripCombo;
    private JPanel detailHolder;

    public LogsPanel(TripService tripService, TravelLogService travelLogService) {
        this.tripService      = tripService;
        this.travelLogService = travelLogService;
        setOpaque(false);
        setLayout(new BorderLayout(0, 12));
        build();
    }

    private void build() {
        JPanel header = new JPanel(new BorderLayout(10, 0));
        header.setOpaque(false);
        header.add(GuiUtil.pageTitle("📔  Travel Diary"), BorderLayout.WEST);

        tripCombo = new JComboBox<>();
        tripCombo.addActionListener(e -> showSelectedTrip());

        JPanel sel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        sel.setOpaque(false);
        sel.add(new JLabel("Trip:"));
        sel.add(tripCombo);
        header.add(sel, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        detailHolder = new JPanel(new BorderLayout());
        detailHolder.setOpaque(false);
        add(detailHolder, BorderLayout.CENTER);
    }

    public void refresh() {
        tripCombo.removeAllItems();
        for (Trip t : tripService.getAllTrips(SessionManager.currentUserId())) {
            tripCombo.addItem(new TripComboItem(t.getId(), t.getDestination()));
        }
        showSelectedTrip();
    }

    private void showSelectedTrip() {
        detailHolder.removeAll();
        TripComboItem sel = (TripComboItem) tripCombo.getSelectedItem();
        if (sel == null) {
            detailHolder.add(GuiUtil.subLabel("No trips. Create one first."), BorderLayout.NORTH);
        } else {
            detailHolder.add(new TravelLogPanel(sel.id(), travelLogService, this::refresh), BorderLayout.CENTER);
        }
        detailHolder.revalidate();
        detailHolder.repaint();
    }

    private record TripComboItem(int id, String name) {
        @Override public String toString() { return name + "  #" + id; }
    }
}