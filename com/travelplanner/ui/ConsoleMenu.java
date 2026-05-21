package com.travelplanner.ui;

import com.travelplanner.model.Expense;
import com.travelplanner.model.ExpenseCategory;
import com.travelplanner.model.ItineraryItem;
import com.travelplanner.model.TravelLog;
import com.travelplanner.model.Trip;
import com.travelplanner.service.ExpenseService;
import com.travelplanner.service.ItineraryService;
import com.travelplanner.service.TravelLogService;
import com.travelplanner.service.TripService;
import com.travelplanner.util.InputUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class ConsoleMenu {
    private final TripService tripService;
    private final ItineraryService itineraryService;
    private final ExpenseService expenseService;
    private final TravelLogService travelLogService;

    public ConsoleMenu(
            TripService tripService,
            ItineraryService itineraryService,
            ExpenseService expenseService,
            TravelLogService travelLogService
    ) {
        this.tripService = tripService;
        this.itineraryService = itineraryService;
        this.expenseService = expenseService;
        this.travelLogService = travelLogService;
    }

    public void start() {
        int choice;

        do {
            printMainMenu();
            choice = InputUtil.readInt("Choose an option: ");

            try {
                switch (choice) {
                    case 1 -> createTrip();
                    case 2 -> viewAllTrips();
                    case 3 -> editTrip();
                    case 4 -> deleteTrip();
                    case 5 -> manageItineraryAndNotes();
                    case 6 -> manageExpenses();
                    case 7 -> manageTravelHistory();
                    case 0 -> System.out.println("Exiting Travel Planner System. Goodbye!");
                    default -> System.out.println("Invalid option.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }

        } while (choice != 0);
    }

    private void printMainMenu() {
        System.out.println("\n==================================");
        System.out.println("       TRAVEL PLANNER SYSTEM       ");
        System.out.println("==================================");
        System.out.println("1. Create New Trip");
        System.out.println("2. View All Trips");
        System.out.println("3. Edit Trip");
        System.out.println("4. Delete Trip");
        System.out.println("5. Itinerary & Notes Management");
        System.out.println("6. Billing & Expense Tracking");
        System.out.println("7. Travel History & Diary");
        System.out.println("0. Exit");
        System.out.println("==================================");
    }

    private void createTrip() {
        System.out.println("\n--- Create New Trip ---");

        String destination = InputUtil.readLine("Destination: ");
        LocalDate startDate = InputUtil.readDate("Start date (yyyy-MM-dd): ");
        LocalDate endDate = InputUtil.readDate("End date (yyyy-MM-dd): ");
        String description = InputUtil.readOptionalLine("Description: ");

        Trip trip = tripService.createTrip(destination, startDate, endDate, description);

        System.out.println("Trip created successfully with ID: " + trip.getId());
    }

    private void viewAllTrips() {
        System.out.println("\n--- All Trips ---");

        List<Trip> trips = tripService.getAllTrips();

        if (trips.isEmpty()) {
            System.out.println("No trips found.");
            return;
        }

        for (Trip trip : trips) {
            System.out.println("----------------------------------");
            System.out.println(trip);
        }
    }

    private void editTrip() {
        System.out.println("\n--- Edit Trip ---");

        int id = InputUtil.readPositiveInt("Trip ID to edit: ");

        if (tripService.getTripById(id).isEmpty()) {
            System.out.println("Trip not found.");
            return;
        }

        String destination = InputUtil.readLine("New destination: ");
        LocalDate startDate = InputUtil.readDate("New start date (yyyy-MM-dd): ");
        LocalDate endDate = InputUtil.readDate("New end date (yyyy-MM-dd): ");
        String description = InputUtil.readOptionalLine("New description: ");
        String notes = InputUtil.readOptionalLine("New notes: ");

        boolean updated = tripService.updateTrip(
                id,
                destination,
                startDate,
                endDate,
                description,
                notes
        );

        System.out.println(updated ? "Trip updated successfully." : "Trip update failed.");
    }

    private void deleteTrip() {
        System.out.println("\n--- Delete Trip ---");

        int id = InputUtil.readPositiveInt("Trip ID to delete: ");

        boolean deleted = tripService.deleteTrip(id);

        if (deleted) {
            itineraryService.deleteByTripId(id);
            expenseService.deleteByTripId(id);
            travelLogService.deleteByTripId(id);
            System.out.println("Trip and related records deleted successfully.");
        } else {
            System.out.println("Trip not found.");
        }
    }

    private void manageItineraryAndNotes() {
        int choice;

        do {
            System.out.println("\n--- Itinerary & Notes Management ---");
            System.out.println("1. Add Daily Itinerary");
            System.out.println("2. View Itinerary and Notes");
            System.out.println("3. Update Itinerary Item");
            System.out.println("4. Delete Itinerary Item");
            System.out.println("5. Update General Notes");
            System.out.println("0. Back");

            choice = InputUtil.readInt("Choose an option: ");

            switch (choice) {
                case 1 -> addItineraryItem();
                case 2 -> viewItineraryAndNotes();
                case 3 -> updateItineraryItem();
                case 4 -> deleteItineraryItem();
                case 5 -> updateGeneralNotes();
                case 0 -> {
                }
                default -> System.out.println("Invalid option.");
            }

        } while (choice != 0);
    }

    private void addItineraryItem() {
        int tripId = InputUtil.readPositiveInt("Trip ID: ");
        int dayNumber = InputUtil.readPositiveInt("Day number: ");
        String activity = InputUtil.readLine("Activity: ");

        ItineraryItem item = itineraryService.addItineraryItem(tripId, dayNumber, activity);

        System.out.println("Itinerary item added with ID: " + item.getId());
    }

    private void viewItineraryAndNotes() {
        int tripId = InputUtil.readPositiveInt("Trip ID: ");

        Trip trip = tripService.getTripById(tripId).orElse(null);

        if (trip == null) {
            System.out.println("Trip not found.");
            return;
        }

        System.out.println("\nTrip Notes:");
        System.out.println(trip.getNotes().isEmpty() ? "No notes added." : trip.getNotes());

        List<ItineraryItem> items = itineraryService.getItineraryByTripId(tripId);

        System.out.println("\nItinerary:");

        if (items.isEmpty()) {
            System.out.println("No itinerary items found.");
            return;
        }

        for (ItineraryItem item : items) {
            System.out.println("----------------------------------");
            System.out.println(item);
        }
    }

    private void updateItineraryItem() {
        int id = InputUtil.readPositiveInt("Itinerary item ID: ");
        int dayNumber = InputUtil.readPositiveInt("New day number: ");
        String activity = InputUtil.readLine("New activity: ");

        boolean updated = itineraryService.updateItineraryItem(id, dayNumber, activity);

        System.out.println(updated ? "Itinerary item updated." : "Itinerary item not found.");
    }

    private void deleteItineraryItem() {
        int id = InputUtil.readPositiveInt("Itinerary item ID to delete: ");

        boolean deleted = itineraryService.deleteItineraryItem(id);

        System.out.println(deleted ? "Itinerary item deleted." : "Itinerary item not found.");
    }

    private void updateGeneralNotes() {
        int tripId = InputUtil.readPositiveInt("Trip ID: ");

        if (tripService.getTripById(tripId).isEmpty()) {
            System.out.println("Trip not found.");
            return;
        }

        String notes = InputUtil.readOptionalLine("New notes/reflections: ");
        boolean updated = tripService.updateNotes(tripId, notes);

        System.out.println(updated ? "Notes updated." : "Notes update failed.");
    }

    private void manageExpenses() {
        int choice;

        do {
            System.out.println("\n--- Billing & Expense Tracking ---");
            System.out.println("1. Add Expense");
            System.out.println("2. View Expenses for Trip");
            System.out.println("3. Update Expense");
            System.out.println("4. Delete Expense");
            System.out.println("5. Show Budget Summary");
            System.out.println("0. Back");

            choice = InputUtil.readInt("Choose an option: ");

            switch (choice) {
                case 1 -> addExpense();
                case 2 -> viewExpensesForTrip();
                case 3 -> updateExpense();
                case 4 -> deleteExpense();
                case 5 -> showBudgetSummary();
                case 0 -> {
                }
                default -> System.out.println("Invalid option.");
            }

        } while (choice != 0);
    }

    private void addExpense() {
        int tripId = InputUtil.readPositiveInt("Trip ID: ");
        ExpenseCategory category = InputUtil.readExpenseCategory();
        String description = InputUtil.readOptionalLine("Description: ");
        double amount = InputUtil.readNonNegativeDouble("Amount: ");
        LocalDate date = InputUtil.readDate("Expense date (yyyy-MM-dd): ");

        Expense expense = expenseService.addExpense(
                tripId,
                category,
                description,
                amount,
                date
        );

        System.out.println("Expense added with ID: " + expense.getId());
    }

    private void viewExpensesForTrip() {
        int tripId = InputUtil.readPositiveInt("Trip ID: ");

        List<Expense> expenses = expenseService.getExpensesByTripId(tripId);

        if (expenses.isEmpty()) {
            System.out.println("No expenses found.");
            return;
        }

        for (Expense expense : expenses) {
            System.out.println("----------------------------------");
            System.out.println(expense);
        }

        double total = expenseService.getTotalExpensesForTrip(tripId);
        System.out.printf("Total Expenses: %.2f%n", total);
    }

    private void updateExpense() {
        int id = InputUtil.readPositiveInt("Expense ID: ");
        ExpenseCategory category = InputUtil.readExpenseCategory();
        String description = InputUtil.readOptionalLine("New description: ");
        double amount = InputUtil.readNonNegativeDouble("New amount: ");
        LocalDate date = InputUtil.readDate("New expense date (yyyy-MM-dd): ");

        boolean updated = expenseService.updateExpense(
                id,
                category,
                description,
                amount,
                date
        );

        System.out.println(updated ? "Expense updated." : "Expense not found.");
    }

    private void deleteExpense() {
        int id = InputUtil.readPositiveInt("Expense ID to delete: ");

        boolean deleted = expenseService.deleteExpense(id);

        System.out.println(deleted ? "Expense deleted." : "Expense not found.");
    }

    private void showBudgetSummary() {
        int tripId = InputUtil.readPositiveInt("Trip ID: ");

        Map<ExpenseCategory, Double> summary = expenseService.getBudgetSummaryByCategory(tripId);
        double total = expenseService.getTotalExpensesForTrip(tripId);

        System.out.println("\n--- Budget Summary ---");

        for (Map.Entry<ExpenseCategory, Double> entry : summary.entrySet()) {
            System.out.printf("%-20s : %.2f%n", entry.getKey(), entry.getValue());
        }

        System.out.printf("%-20s : %.2f%n", "TOTAL", total);
    }

    private void manageTravelHistory() {
        int choice;

        do {
            System.out.println("\n--- Travel History & Diary ---");
            System.out.println("1. Mark Trip as Completed");
            System.out.println("2. Add Travel Reflection / Experience");
            System.out.println("3. View Past Trips");
            System.out.println("4. View Logs for Trip");
            System.out.println("5. Update Travel Log");
            System.out.println("6. Delete Travel Log");
            System.out.println("0. Back");

            choice = InputUtil.readInt("Choose an option: ");

            switch (choice) {
                case 1 -> markTripCompleted();
                case 2 -> addTravelLog();
                case 3 -> viewPastTrips();
                case 4 -> viewLogsForTrip();
                case 5 -> updateTravelLog();
                case 6 -> deleteTravelLog();
                case 0 -> {
                }
                default -> System.out.println("Invalid option.");
            }

        } while (choice != 0);
    }

    private void markTripCompleted() {
        int tripId = InputUtil.readPositiveInt("Trip ID: ");
        boolean updated = tripService.markTripCompleted(tripId);

        System.out.println(updated ? "Trip marked as completed." : "Trip not found.");
    }

    private void addTravelLog() {
        int tripId = InputUtil.readPositiveInt("Trip ID: ");
        String reflection = InputUtil.readLine("Reflection / experience: ");
        String imagePath = InputUtil.readOptionalLine("Optional image file path: ");
        LocalDate logDate = InputUtil.readDate("Log date (yyyy-MM-dd): ");

        TravelLog log = travelLogService.addTravelLog(
                tripId,
                reflection,
                imagePath,
                logDate
        );

        System.out.println("Travel log added with ID: " + log.getId());
    }

    private void viewPastTrips() {
        List<Trip> completedTrips = tripService.getCompletedTrips();

        if (completedTrips.isEmpty()) {
            System.out.println("No completed trips found.");
            return;
        }

        for (Trip trip : completedTrips) {
            System.out.println("----------------------------------");
            System.out.println(trip);
        }
    }

    private void viewLogsForTrip() {
        int tripId = InputUtil.readPositiveInt("Trip ID: ");

        List<TravelLog> logs = travelLogService.getLogsByTripId(tripId);

        if (logs.isEmpty()) {
            System.out.println("No logs found.");
            return;
        }

        for (TravelLog log : logs) {
            System.out.println("----------------------------------");
            System.out.println(log);
        }
    }

    private void updateTravelLog() {
        int id = InputUtil.readPositiveInt("Travel log ID: ");
        String reflection = InputUtil.readLine("New reflection: ");
        String imagePath = InputUtil.readOptionalLine("New image path: ");
        LocalDate logDate = InputUtil.readDate("New log date (yyyy-MM-dd): ");

        boolean updated = travelLogService.updateTravelLog(
                id,
                reflection,
                imagePath,
                logDate
        );

        System.out.println(updated ? "Travel log updated." : "Travel log not found.");
    }

    private void deleteTravelLog() {
        int id = InputUtil.readPositiveInt("Travel log ID to delete: ");

        boolean deleted = travelLogService.deleteTravelLog(id);

        System.out.println(deleted ? "Travel log deleted." : "Travel log not found.");
    }
}
