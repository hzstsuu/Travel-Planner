package com.travelplanner.ui.gui.panels;

import com.travelplanner.model.Trip;
import com.travelplanner.model.User;
import com.travelplanner.service.ExpenseService;
import com.travelplanner.service.TripService;
import com.travelplanner.service.UserService;
import com.travelplanner.ui.gui.util.GuiUtil;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Public leaderboard shown before login.
 * Three ranking tables: most spent, most completed trips, most trips created.
 * ALL active accounts are listed in every category, even those with 0.
 */
public class LeaderboardPanel extends JPanel {

    private final UserService    userService;
    private final TripService    tripService;
    private final ExpenseService expenseService;
    private final Runnable       onLogin;
    private final Runnable       onSignUp;

    public LeaderboardPanel(UserService userService,
                            TripService tripService,
                            ExpenseService expenseService,
                            Runnable onLogin,
                            Runnable onSignUp) {
        this.userService    = userService;
        this.tripService    = tripService;
        this.expenseService = expenseService;
        this.onLogin        = onLogin;
        this.onSignUp       = onSignUp;

        setLayout(new BorderLayout(0, 0));
        setBackground(new Color(18, 20, 30));
        build();
    }

    // ── Build ─────────────────────────────────────────────────────────────

    private void build() {
        // ── Header bar ────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout(14, 0));
        header.setBackground(new Color(24, 26, 38));
        header.setBorder(BorderFactory.createEmptyBorder(18, 28, 18, 28));

        JLabel brand = new JLabel("✈  Travel Planner");
        brand.setFont(new Font("SansSerif", Font.BOLD, 22));
        brand.setForeground(Color.WHITE);

        JPanel authButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        authButtons.setOpaque(false);
        JButton loginBtn  = GuiUtil.primaryButton("Log In");
        JButton signupBtn = GuiUtil.ghostButton("Sign Up");
        loginBtn.addActionListener(e -> onLogin.run());
        signupBtn.addActionListener(e -> onSignUp.run());
        authButtons.add(signupBtn);
        authButtons.add(loginBtn);

        header.add(brand, BorderLayout.WEST);
        header.add(authButtons, BorderLayout.EAST);

        // ── Hero text ─────────────────────────────────────────────────────
        JPanel hero = new JPanel(new BorderLayout());
        hero.setBackground(new Color(18, 20, 30));
        hero.setBorder(BorderFactory.createEmptyBorder(32, 60, 20, 60));

        JLabel heroTitle = new JLabel("Community Leaderboard 🏆");
        heroTitle.setFont(new Font("SansSerif", Font.BOLD, 30));
        heroTitle.setForeground(Color.WHITE);

        JLabel heroSub = new JLabel("See who's exploring the world. Log in to track your own adventures.");
        heroSub.setFont(new Font("SansSerif", Font.PLAIN, 14));
        heroSub.setForeground(new Color(160, 170, 190));

        JPanel heroText = new JPanel();
        heroText.setOpaque(false);
        heroText.setLayout(new BoxLayout(heroText, BoxLayout.Y_AXIS));
        heroText.add(heroTitle);
        heroText.add(Box.createVerticalStrut(6));
        heroText.add(heroSub);

        hero.add(heroText, BorderLayout.CENTER);

        // ── Leaderboard tables ────────────────────────────────────────────
        JPanel body = new JPanel(new GridLayout(1, 3, 20, 0));
        body.setOpaque(false);
        body.setBorder(BorderFactory.createEmptyBorder(0, 40, 40, 40));

        List<User> users    = userService.getAllUsers();
        List<Trip> allTrips = tripService.getAllTrips();

        body.add(buildRankCard("💰 Top Spenders",
                rankBySpend(users, allTrips)));
        body.add(buildRankCard("✅ Most Trips Completed",
                rankByCompleted(users, allTrips)));
        body.add(buildRankCard("🧳 Most Trips Created",
                rankByTotal(users, allTrips)));

        // Assemble
        add(header, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 0));
        center.setOpaque(false);
        center.add(hero, BorderLayout.NORTH);
        center.add(new JScrollPane(body) {{
            setBorder(BorderFactory.createEmptyBorder());
            setOpaque(false);
            getViewport().setOpaque(false);
        }}, BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);

        // ── Footer ────────────────────────────────────────────────────────
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(new Color(24, 26, 38));
        footer.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        JLabel footerLabel = new JLabel("Join the community — sign up and start planning your next trip!");
        footerLabel.setForeground(new Color(120, 130, 150));
        footerLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        footer.add(footerLabel);
        add(footer, BorderLayout.SOUTH);
    }

    // ── Rank card builder ─────────────────────────────────────────────────

    private JPanel buildRankCard(String title, List<RankEntry> entries) {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(new Color(24, 26, 38));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(55, 62, 88), 1, true),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));

        // Header: title + user count badge
        JPanel cardHeader = new JPanel(new BorderLayout(6, 0));
        cardHeader.setOpaque(false);

        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 15));
        lbl.setForeground(Color.WHITE);
        cardHeader.add(lbl, BorderLayout.WEST);

        JLabel countBadge = new JLabel(entries.size() + " users");
        countBadge.setFont(new Font("SansSerif", Font.PLAIN, 11));
        countBadge.setForeground(new Color(100, 110, 130));
        cardHeader.add(countBadge, BorderLayout.EAST);

        card.add(cardHeader, BorderLayout.NORTH);

        // Scrollable list so it can handle many users
        JPanel list = new JPanel();
        list.setOpaque(false);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));

        String[] medals = {"🥇", "🥈", "🥉"};
        int rank = 1;
        for (RankEntry e : entries) {
            JPanel row = buildRankRow(
                    rank <= 3 ? medals[rank - 1] : rank + ".",
                    e.displayName(),
                    e.valueStr(),
                    rank
            );
            list.add(row);
            list.add(Box.createVerticalStrut(4));
            rank++;
        }

        if (entries.isEmpty()) {
            JLabel empty = new JLabel("No users yet.");
            empty.setForeground(new Color(120, 130, 150));
            list.add(empty);
        }

        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        // Show scrollbar only when needed
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildRankRow(String medal, String name, String value, int rank) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));

        JLabel medalLbl = new JLabel(medal);
        medalLbl.setFont(new Font("SansSerif", Font.PLAIN, rank <= 3 ? 16 : 12));
        medalLbl.setPreferredSize(new Dimension(32, 20));
        medalLbl.setForeground(new Color(150, 160, 175));

        JLabel nameLbl = new JLabel(name);
        nameLbl.setFont(new Font("SansSerif", Font.BOLD, 13));
        nameLbl.setForeground(rank == 1 ? new Color(255, 215, 0)
                            : rank == 2 ? new Color(192, 192, 192)
                            : rank == 3 ? new Color(205, 127, 50)
                            :             new Color(200, 210, 225));

        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        // Dim 0-value entries slightly
        valueLbl.setForeground(value.contains("₱ 0") || value.equals("0 trips")
                ? new Color(90, 100, 115)
                : new Color(150, 160, 175));
        valueLbl.setHorizontalAlignment(SwingConstants.RIGHT);

        row.add(medalLbl, BorderLayout.WEST);
        row.add(nameLbl,  BorderLayout.CENTER);
        row.add(valueLbl, BorderLayout.EAST);
        return row;
    }

    // ── Ranking logic — ALL users included, sorted descending ─────────────

    private List<RankEntry> rankBySpend(List<User> users, List<Trip> allTrips) {
        // Build spend map for all users
        Map<Integer, Double> spend = new HashMap<>();
        for (User u : users) spend.put(u.getId(), 0.0);

        for (Trip t : allTrips) {
            try {
                double amt = expenseService.getTotalExpensesForTrip(t.getId());
                spend.merge(t.getUserId(), amt, Double::sum);
            } catch (Exception ignored) {}
        }

        return users.stream()
                .sorted(Comparator.comparingDouble(u -> -spend.getOrDefault(u.getId(), 0.0)))
                .map(u -> new RankEntry(
                        displayName(u),
                        String.format("₱ %.0f", spend.getOrDefault(u.getId(), 0.0))
                ))
                .collect(Collectors.toList());
    }

    private List<RankEntry> rankByCompleted(List<User> users, List<Trip> allTrips) {
        Map<Integer, Long> counts = allTrips.stream()
                .filter(Trip::isCompleted)
                .collect(Collectors.groupingBy(Trip::getUserId, Collectors.counting()));

        return users.stream()
                .sorted(Comparator.comparingLong(u -> -counts.getOrDefault(u.getId(), 0L)))
                .map(u -> new RankEntry(
                        displayName(u),
                        counts.getOrDefault(u.getId(), 0L) + " trips"
                ))
                .collect(Collectors.toList());
    }

    private List<RankEntry> rankByTotal(List<User> users, List<Trip> allTrips) {
        Map<Integer, Long> counts = allTrips.stream()
                .collect(Collectors.groupingBy(Trip::getUserId, Collectors.counting()));

        return users.stream()
                .sorted(Comparator.comparingLong(u -> -counts.getOrDefault(u.getId(), 0L)))
                .map(u -> new RankEntry(
                        displayName(u),
                        counts.getOrDefault(u.getId(), 0L) + " trips"
                ))
                .collect(Collectors.toList());
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private String displayName(User u) {
        return u.getFullName().isBlank() ? u.getUsername() : u.getFullName();
    }

    // ── Inner types ────────────────────────────────────────────────────────

    private record RankEntry(String displayName, String valueStr) {}
}