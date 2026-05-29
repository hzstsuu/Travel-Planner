package com.travelplanner.ui.gui.panels;

import com.travelplanner.service.SessionManager;
import com.travelplanner.service.TripService;
import com.travelplanner.service.ExpenseService;
import com.travelplanner.service.ItineraryService;
import com.travelplanner.service.TravelLogService;
import com.travelplanner.service.UserService;
import com.travelplanner.ui.gui.theme.ThemeManager;
import com.travelplanner.ui.gui.util.GuiUtil;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class SettingsPanel extends JPanel {

    private final Consumer<Boolean> onThemeChange;
    private final UserService       userService;
    private final TripService       tripService;
    private final ExpenseService    expenseService;
    private final ItineraryService  itineraryService;
    private final TravelLogService  travelLogService;
    private final Runnable          onAccountDeleted;

    // ── Constructor (full — used by TravelPlannerApp) ─────────────────────
    public SettingsPanel(Consumer<Boolean> onThemeChange,
                         UserService userService,
                         TripService tripService,
                         ExpenseService expenseService,
                         ItineraryService itineraryService,
                         TravelLogService travelLogService,
                         Runnable onAccountDeleted) {
        this.onThemeChange    = onThemeChange;
        this.userService      = userService;
        this.tripService      = tripService;
        this.expenseService   = expenseService;
        this.itineraryService = itineraryService;
        this.travelLogService = travelLogService;
        this.onAccountDeleted = onAccountDeleted;
        setOpaque(false);
        setLayout(new BorderLayout(0, 14));
        build();
    }

    // ── Constructor (legacy — no delete support) ──────────────────────────
    public SettingsPanel(Consumer<Boolean> onThemeChange) {
        this(onThemeChange, null, null, null, null, null, null);
    }

    private void build() {
        add(GuiUtil.pageTitle("⚙  Settings"), BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        // ── Theme card ────────────────────────────────────────────────────
        JPanel themeCard = new JPanel(new BorderLayout(12, 0));
        themeCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 66, 88), 1, true),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)
        ));
        themeCard.setOpaque(false);
        themeCard.setMaximumSize(new Dimension(600, 90));
        themeCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel themeText = new JPanel();
        themeText.setOpaque(false);
        themeText.setLayout(new BoxLayout(themeText, BoxLayout.Y_AXIS));
        themeText.add(GuiUtil.sectionTitle("Theme"));
        themeText.add(Box.createVerticalStrut(4));
        themeText.add(GuiUtil.subLabel("Switch between dark and light mode. Takes effect immediately."));

        JToggleButton toggle = new JToggleButton(ThemeManager.isDark() ? "🌙  Dark" : "☀  Light");
        toggle.setSelected(ThemeManager.isDark());
        toggle.setFont(new Font("SansSerif", Font.BOLD, 13));
        toggle.setPreferredSize(new Dimension(110, 36));
        toggle.addActionListener(e -> {
            boolean dark = toggle.isSelected();
            toggle.setText(dark ? "🌙  Dark" : "☀  Light");
            onThemeChange.accept(dark);
        });

        themeCard.add(themeText, BorderLayout.CENTER);
        themeCard.add(toggle,    BorderLayout.EAST);
        body.add(themeCard);
        body.add(Box.createVerticalStrut(12));

        // ── Account info card ─────────────────────────────────────────────
        if (SessionManager.isLoggedIn()) {
            var user = SessionManager.current();

            JPanel accCard = new JPanel(new GridBagLayout());
            accCard.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(60, 66, 88), 1, true),
                    BorderFactory.createEmptyBorder(18, 20, 18, 20)
            ));
            accCard.setOpaque(false);
            accCard.setMaximumSize(new Dimension(600, 170));
            accCard.setAlignmentX(Component.LEFT_ALIGNMENT);

            GridBagConstraints g = GuiUtil.gbc();
            g.insets = new Insets(5, 6, 5, 6);

            GuiUtil.addDetailRow(accCard, g, 0, "Full Name", user.getFullName());
            GuiUtil.addDetailRow(accCard, g, 1, "Username",  user.getUsername());
            GuiUtil.addDetailRow(accCard, g, 2, "Email",     user.getEmail().isBlank() ? "—" : user.getEmail());
            GuiUtil.addDetailRow(accCard, g, 3, "Joined",    user.getJoinDate().toString());

            body.add(accCard);
            body.add(Box.createVerticalStrut(12));

            // ── Delete Account card ───────────────────────────────────────
            if (userService != null) {
                JPanel deleteCard = new JPanel(new BorderLayout(12, 0));
                deleteCard.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(140, 40, 40), 1, true),
                        BorderFactory.createEmptyBorder(18, 20, 18, 20)
                ));
                deleteCard.setOpaque(false);
                deleteCard.setMaximumSize(new Dimension(600, 110));
                deleteCard.setAlignmentX(Component.LEFT_ALIGNMENT);

                JPanel deleteText = new JPanel();
                deleteText.setOpaque(false);
                deleteText.setLayout(new BoxLayout(deleteText, BoxLayout.Y_AXIS));

                JLabel deleteTitle = GuiUtil.sectionTitle("Delete Account");
                deleteTitle.setForeground(new Color(220, 75, 75));
                deleteText.add(deleteTitle);
                deleteText.add(Box.createVerticalStrut(4));
                deleteText.add(GuiUtil.subLabel(
                    "Permanently removes your account and ALL associated trips, expenses,"));
                deleteText.add(GuiUtil.subLabel(
                    "itineraries, and travel logs. This action cannot be undone."));

                JButton deleteBtn = GuiUtil.dangerButton("🗑  Delete My Account");
                deleteBtn.setPreferredSize(new Dimension(180, 36));
                deleteBtn.addActionListener(e -> confirmAndDeleteAccount());

                deleteCard.add(deleteText, BorderLayout.CENTER);
                deleteCard.add(deleteBtn,  BorderLayout.EAST);
                body.add(deleteCard);
                body.add(Box.createVerticalStrut(12));
            }
        }

        // ── About card ────────────────────────────────────────────────────
        JPanel aboutCard = new JPanel(new BorderLayout(0, 6));
        aboutCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 66, 88), 1, true),
                BorderFactory.createEmptyBorder(16, 20, 16, 20)
        ));
        aboutCard.setOpaque(false);
        aboutCard.setMaximumSize(new Dimension(600, 90));
        aboutCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel appName = new JLabel("Travel Planner — pogi edition v.1.0");
        appName.setFont(new Font("SansSerif", Font.BOLD, 13));

        JLabel techStack = new JLabel("Built with Java Swing");
        techStack.setFont(new Font("SansSerif", Font.PLAIN, 12));
        techStack.setForeground(new Color(130, 140, 160));

        aboutCard.add(appName,    BorderLayout.NORTH);
        aboutCard.add(techStack,  BorderLayout.CENTER);
        body.add(aboutCard);

        body.add(Box.createVerticalGlue());

        JScrollPane scroll = new JScrollPane(body);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        add(scroll, BorderLayout.CENTER);
    }

    // ── Delete Account Logic ──────────────────────────────────────────────

    private void confirmAndDeleteAccount() {
        if (!SessionManager.isLoggedIn() || userService == null) return;

        var user = SessionManager.current();

        // Step 1: Warn the user clearly
        int first = JOptionPane.showConfirmDialog(
                this,
                "<html><b>Delete your account?</b><br><br>"
                + "This will permanently remove:<br>"
                + "• Your account (" + user.getUsername() + ")<br>"
                + "• All your trips<br>"
                + "• All expenses, itineraries, and travel logs<br><br>"
                + "<span style='color:#DC4B4B'>This action <b>cannot</b> be undone.</span></html>",
                "⚠ Delete Account",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (first != JOptionPane.YES_OPTION) return;

        // Step 2: Ask them to type their username to confirm
        String typed = JOptionPane.showInputDialog(
                this,
                "<html>Type your username <b>" + user.getUsername()
                + "</b> to confirm deletion:</html>",
                "Confirm Deletion",
                JOptionPane.WARNING_MESSAGE
        );
        if (typed == null || !typed.trim().equalsIgnoreCase(user.getUsername())) {
            JOptionPane.showMessageDialog(this,
                    "Username did not match. Account was NOT deleted.",
                    "Cancelled", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Step 3: Delete all data for this user
        int userId = user.getId();

        // Delete all trips (and their cascaded expenses, itineraries, logs)
        if (tripService != null) {
            List<com.travelplanner.model.Trip> userTrips = tripService.getAllTrips(userId);
            for (com.travelplanner.model.Trip t : userTrips) {
                if (expenseService   != null) expenseService.deleteByTripId(t.getId());
                if (itineraryService != null) itineraryService.deleteByTripId(t.getId());
                if (travelLogService != null) travelLogService.deleteByTripId(t.getId());
                tripService.deleteTrip(t.getId());
            }
        }

        // Delete the user record
        boolean deleted = userService.deleteAccount(userId);

        if (deleted) {
            SessionManager.logout();
            JOptionPane.showMessageDialog(this,
                    "Your account has been deleted. Goodbye!",
                    "Account Deleted", JOptionPane.INFORMATION_MESSAGE);
            if (onAccountDeleted != null) onAccountDeleted.run();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Something went wrong. Account could not be deleted.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}