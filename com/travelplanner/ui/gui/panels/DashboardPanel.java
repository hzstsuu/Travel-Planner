package com.travelplanner.ui.gui.panels;
import com.travelplanner.model.Trip;
import com.travelplanner.service.ExpenseService;
import com.travelplanner.service.TripService;
import com.travelplanner.ui.gui.components.TripCardPanel;
import com.travelplanner.ui.gui.util.GuiUtil;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.function.IntConsumer;
public class DashboardPanel extends JPanel {
    private final TripService tripService;
    private final ExpenseService expenseService;
    private final IntConsumer onOpenTrip;
    private JPanel statsPanel;
    private JPanel upcomingPanel;
    private JPanel activityPanel;
    public DashboardPanel(TripService tripService, ExpenseService expenseService, IntConsumer onOpenTrip) {
        this.tripService = tripService;
        this.expenseService = expenseService;
        this.onOpenTrip = onOpenTrip;
        setLayout(new BorderLayout(0, 18));
        setOpaque(false);
        build();
        refresh();
    }
    private void build() {
        JLabel title = GuiUtil.pageTitle("🌎 Dashboard");
        add(title, BorderLayout.NORTH);
        JPanel body = new JPanel(new BorderLayout(18, 18));
        body.setOpaque(false);
        JPanel top = new JPanel(new BorderLayout(18, 18));
        top.setOpaque(false);
        statsPanel = new JPanel(new GridLayout(1, 3, 14, 14));
        statsPanel.setOpaque(false);
        top.add(statsPanel, BorderLayout.CENTER);
        body.add(top, BorderLayout.NORTH);
        upcomingPanel = new JPanel();
        upcomingPanel.setLayout(new BoxLayout(upcomingPanel, BoxLayout.Y_AXIS));
        upcomingPanel.setOpaque(false);
        JScrollPane scrollPane = new JScrollPane(upcomingPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        body.add(scrollPane, BorderLayout.CENTER);
        activityPanel = GuiUtil.cardPanel();
        activityPanel.setLayout(new BoxLayout(activityPanel, BoxLayout.Y_AXIS));
        body.add(activityPanel, BorderLayout.EAST);
        add(body, BorderLayout.CENTER);
    }
    public void refresh() {
        List<Trip> trips = tripService.getAllTrips();
        double totalSpent = trips.stream()
                .mapToDouble(trip -> {
                    try {
                        return expenseService.getTotalExpensesForTrip(trip.getId());
                    } catch (Exception e) {
                        return 0.0;
                    }
                })
                .sum();
        long completed = trips.stream().filter(Trip::isCompleted).count();
        statsPanel.removeAll();
        statsPanel.add(statCard("🧳 Total Trips", String.valueOf(trips.size())));
        statsPanel.add(statCard("💰 Total Spent", String.format("%.2f", totalSpent)));
        statsPanel.add(statCard("✅ Completed", String.valueOf(completed)));
        upcomingPanel.removeAll();
        upcomingPanel.add(GuiUtil.sectionTitle("Upcoming Trips"));
        upcomingPanel.add(javax.swing.Box.createVerticalStrut(12));
        List<Trip> upcoming = trips.stream()
                .filter(trip -> !trip.isCompleted())
                .filter(trip -> !trip.getEndDate().isBefore(LocalDate.now()))
                .sorted(Comparator.comparing(Trip::getStartDate))
                .limit(6)
                .toList();
        if (upcoming.isEmpty()) {
            upcomingPanel.add(emptyCard("No upcoming trips. Create one from My Trips."));
        } else {
            for (Trip trip : upcoming) {
                upcomingPanel.add(new TripCardPanel(trip, onOpenTrip));
                upcomingPanel.add(javax.swing.Box.createVerticalStrut(12));
            }
        }
        activityPanel.removeAll();
        activityPanel.add(GuiUtil.sectionTitle("Recent Activity"));
        activityPanel.add(javax.swing.Box.createVerticalStrut(12));
        trips.stream()
                .sorted(Comparator.comparing(Trip::getStartDate).reversed())
                .limit(6)
                .forEach(trip -> activityPanel.add(new JLabel("• " + trip.getDestination() + " — " + trip.getStatus())));
        if (trips.isEmpty()) {
            activityPanel.add(new JLabel("No activity yet."));
        }
        revalidate();
        repaint();
    }
    private JPanel statCard(String title, String value) {
        JPanel card = GuiUtil.cardPanel();
        card.setLayout(new BorderLayout());
        JLabel titleLabel = new JLabel(title);
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 30));
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }
    private JPanel emptyCard(String text) {
        JPanel card = GuiUtil.cardPanel();
        card.add(new JLabel(text));
        return card;
    }
}