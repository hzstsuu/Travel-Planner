package com.travelplanner;

import com.travelplanner.repository.*;
import com.travelplanner.service.*;
import com.travelplanner.ui.gui.TravelPlannerApp;
import com.travelplanner.ui.gui.theme.ThemeManager;
import com.travelplanner.util.FileUtil;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        FileUtil.initializeDataFiles();

        // ── MUST happen before invokeLater ────────────────────────────────
        // Nimbus reads UIManager at install time to seed derived colours.
        // Calling this here — before any Swing component exists — guarantees
        // the correct palette is in place from the very first paint.
        ThemeManager.installDarkTheme();

        // Repositories
        UserRepository      userRepo    = new UserRepository("data/users.txt");
        TripRepository      tripRepo    = new TripRepository("data/trips.txt");
        ItineraryRepository itinRepo    = new ItineraryRepository("data/itineraries.txt");
        ExpenseRepository   expenseRepo = new ExpenseRepository("data/expenses.txt");
        TravelLogRepository logRepo     = new TravelLogRepository("data/travel_logs.txt");

        // Services
        UserService      userService    = new UserService(userRepo);
        TripService      tripService    = new TripService(tripRepo);
        ItineraryService itinService    = new ItineraryService(itinRepo, tripRepo);
        ExpenseService   expenseService = new ExpenseService(expenseRepo, tripRepo);
        TravelLogService logService     = new TravelLogService(logRepo, tripRepo);

        SwingUtilities.invokeLater(() -> {
            TravelPlannerApp app = new TravelPlannerApp(
                    userService, tripService, itinService, expenseService, logService
            );
            app.setVisible(true);
        });
    }
}