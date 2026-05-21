package com.travelplanner.ui.gui.panels;
import com.travelplanner.model.Trip;
import com.travelplanner.service.ExpenseService;
import com.travelplanner.service.TripService;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import com.travelplanner.ui.gui.util.GuiUtil;
public class ExpensesPanel extends JPanel {
    private final TripService tripService;
    private final ExpenseService expenseService;
    private JComboBox<TripComboItem> tripCombo;
    private JPanel detailHolder;
    public ExpensesPanel(TripService tripService, ExpenseService expenseService) {
        this.tripService = tripService;
        this.expenseService = expenseService;
        setOpaque(false);
        setLayout(new BorderLayout(0, 14));
        build();
    }
    private void build() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(GuiUtil.pageTitle("💳 Expenses & Budget"), BorderLayout.WEST);
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
            detailHolder.add(new ExpensePanel(selected.id(), expenseService, this::refresh), BorderLayout.CENTER);
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