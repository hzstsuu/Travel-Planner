package com.travelplanner.ui.gui.panels;

import com.travelplanner.model.Trip;
import com.travelplanner.service.ExpenseService;
import com.travelplanner.service.SessionManager;
import com.travelplanner.service.TripService;
import com.travelplanner.ui.gui.util.GuiUtil;

import javax.swing.*;
import java.awt.*;

public class ExpensesPanel extends JPanel {

    private final TripService    tripService;
    private final ExpenseService expenseService;

    private JComboBox<TripComboItem> tripCombo;
    private JPanel detailHolder;

    public ExpensesPanel(TripService tripService, ExpenseService expenseService) {
        this.tripService    = tripService;
        this.expenseService = expenseService;
        setOpaque(false);
        setLayout(new BorderLayout(0, 12));
        build();
    }

    private void build() {
        JPanel header = new JPanel(new BorderLayout(10, 0));
        header.setOpaque(false);
        header.add(GuiUtil.pageTitle("💳  Expenses & Budget"), BorderLayout.WEST);

        tripCombo = new JComboBox<>();
        tripCombo.setToolTipText("Select a trip");
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
            detailHolder.add(GuiUtil.subLabel("No trips available. Create a trip first."), BorderLayout.NORTH);
        } else {
            detailHolder.add(new ExpensePanel(sel.id(), expenseService, this::refresh), BorderLayout.CENTER);
        }
        detailHolder.revalidate();
        detailHolder.repaint();
    }

    private record TripComboItem(int id, String name) {
        @Override public String toString() { return name + "  #" + id; }
    }
}