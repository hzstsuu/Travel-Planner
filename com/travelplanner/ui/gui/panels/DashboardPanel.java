package com.travelplanner.ui.gui.panels;

import com.travelplanner.model.Trip;
import com.travelplanner.service.ExpenseService;
import com.travelplanner.service.SessionManager;
import com.travelplanner.service.TripService;
import com.travelplanner.ui.gui.components.TripCardPanel;
import com.travelplanner.ui.gui.util.GuiUtil;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.function.IntConsumer;

public class DashboardPanel extends JPanel {

    private final TripService    tripService;
    private final ExpenseService expenseService;
    private final IntConsumer    onOpenTrip;

    private JPanel statsPanel;
    private JPanel upcomingList;
    private JPanel activityPanel;

    public DashboardPanel(TripService tripService, ExpenseService expenseService,
                          IntConsumer onOpenTrip) {
        this.tripService    = tripService;
        this.expenseService = expenseService;
        this.onOpenTrip     = onOpenTrip;
        setOpaque(false);
        setLayout(new BorderLayout(0, 14));
        build();
        refresh();
    }

    private void build() {
        // ── Header ─────────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = GuiUtil.pageTitle("🌎  Dashboard");
        JLabel sub   = GuiUtil.subLabel(getGreeting());

        JPanel headLeft = new JPanel();
        headLeft.setOpaque(false);
        headLeft.setLayout(new BoxLayout(headLeft, BoxLayout.Y_AXIS));
        headLeft.add(title);
        headLeft.add(sub);
        header.add(headLeft, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // ── Body ────────────────────────────────────────────────────────────
        JPanel body = new JPanel(new BorderLayout(14, 14));
        body.setOpaque(false);

        // Stats row
        statsPanel = new JPanel(new GridLayout(1, 3, 12, 0));
        statsPanel.setOpaque(false);
        body.add(statsPanel, BorderLayout.NORTH);

        // Centre: upcoming trips
        JPanel upcomingWrap = new JPanel(new BorderLayout(0, 8));
        upcomingWrap.setOpaque(false);
        upcomingWrap.add(GuiUtil.sectionTitle("Upcoming Trips"), BorderLayout.NORTH);

        upcomingList = new JPanel();
        upcomingList.setLayout(new BoxLayout(upcomingList, BoxLayout.Y_AXIS));
        upcomingList.setOpaque(false);

        JScrollPane scroll = new JScrollPane(upcomingList);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        upcomingWrap.add(scroll, BorderLayout.CENTER);
        body.add(upcomingWrap, BorderLayout.CENTER);

        // East: recent activity
        activityPanel = new JPanel();
        activityPanel.setLayout(new BoxLayout(activityPanel, BoxLayout.Y_AXIS));
        activityPanel.setOpaque(false);
        activityPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(55, 62, 88), 1, true),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));

        JScrollPane actScroll = new JScrollPane(activityPanel);
        actScroll.setBorder(BorderFactory.createEmptyBorder());
        actScroll.setPreferredSize(new Dimension(220, 400));
        actScroll.setOpaque(false);
        actScroll.getViewport().setOpaque(false);
        body.add(actScroll, BorderLayout.EAST);

        add(body, BorderLayout.CENTER);
    }

    public void refresh() {
        int userId       = SessionManager.currentUserId();
        List<Trip> trips = tripService.getAllTrips(userId);

        double totalSpent = trips.stream()
                .mapToDouble(t -> {
                    try { return expenseService.getTotalExpensesForTrip(t.getId()); }
                    catch (Exception e) { return 0.0; }
                }).sum();
        long completed = trips.stream().filter(Trip::isCompleted).count();

        // ── Stats ──────────────────────────────────────────────────────────
        statsPanel.removeAll();
        statsPanel.add(GuiUtil.statCard("🧳  Total Trips",
                String.valueOf(trips.size()), null));
        statsPanel.add(GuiUtil.statCard("💰  Total Spent",
                String.format("₱ %.2f", totalSpent), GuiUtil.WARNING));
        statsPanel.add(GuiUtil.statCard("✅  Completed",
                String.valueOf(completed), GuiUtil.SUCCESS));

        // ── Upcoming ───────────────────────────────────────────────────────
        upcomingList.removeAll();
        List<Trip> upcoming = trips.stream()
                .filter(t -> !t.isCompleted())
                .filter(t -> !t.getEndDate().isBefore(LocalDate.now()))
                .sorted(Comparator.comparing(Trip::getStartDate))
                .limit(5)
                .toList();

        if (upcoming.isEmpty()) {
            JLabel empty = new JLabel("No upcoming trips. Create one from My Trips.");
            empty.setForeground(new Color(140, 150, 170));
            upcomingList.add(empty);
        } else {
            for (Trip t : upcoming) {
                upcomingList.add(new TripCardPanel(t, onOpenTrip));
                upcomingList.add(Box.createVerticalStrut(8));
            }
        }

        // ── Activity ───────────────────────────────────────────────────────
        activityPanel.removeAll();
        activityPanel.add(GuiUtil.sectionTitle("Recent Activity"));
        activityPanel.add(Box.createVerticalStrut(8));
        if (trips.isEmpty()) {
            activityPanel.add(GuiUtil.subLabel("No activity yet."));
        } else {
            trips.stream()
                    .sorted(Comparator.comparing(Trip::getStartDate).reversed())
                    .limit(8)
                    .forEach(t -> {
                        JLabel lbl = new JLabel("• " + t.getDestination()
                                + " — " + t.getStatus());
                        lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
                        lbl.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));
                        activityPanel.add(lbl);
                    });
        }

        revalidate();
        repaint();
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    private String getGreeting() {
        int hour = LocalTime.now().getHour();
        String tod  = hour < 12 ? "Good morning"
                    : hour < 18 ? "Good afternoon"
                    :             "Good evening";
        String name = "";
        if (SessionManager.isLoggedIn()) {
            String full = SessionManager.current().getFullName();
            name = ", " + (full.isBlank() ? SessionManager.current().getUsername()
                                           : full.split(" ")[0]);
        }
        return tod + name + "! Here's your travel overview.";
    }
}